// 회원 목록 데이터 (서버에서 채움)
let users = [];

// DOM 요소
const tableBody = document.querySelector("#admin_user_table tbody");
const userTotalCount = document.getElementById("admin_tot_cnt");
// 게임 진행도 관련 DOM
const gameProgressFill = document.querySelector(".game_progress_fill");
const gameProgressText = document.querySelector(".game_progress_wrap span.game_progress_text");
const gameCertBtn = document.getElementById("game_cert_btn");

// 모달 관련 요소
const modal = document.getElementById("aUser_detail");
const closeBtn = document.querySelector(".modal_close_btn");
const modalUserName = document.getElementById("dm_user_name");
const detailTableBody = document.querySelector("#aUser_detail_table tbody");

// 수강 정보 (모달)
let courseData = [];

/**
 * 서버에서 회원 목록 가져오기
 * (role = 'User' 인 회원만 반환됨)
 */
async function loadUsersFromServer() {
    try {
        const response = await fetch("/adminUser/api/users");
        if (!response.ok) {
            throw new Error("회원 목록 조회 실패");
        }

        const data = await response.json();
        users = Array.isArray(data) ? data : [];

        // currentPage 가 adminCommon.js 에서 전역으로 관리되지만,
        // 혹시 undefined 인 경우 기본값을 1로 세팅
        if (typeof currentPage === "undefined") {
            window.currentPage = 1;
        }

        renderTable(currentPage);
        renderPagination(users.length);
    } catch (error) {
        console.error(error);
        tableBody.innerHTML = `
      <tr>
        <td colspan="6">회원 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
        userTotalCount.textContent = "00";
    }
}

// 행 개수 드롭다운 (페이지당 표시 개수 변경)
// rowSelect, rowsPerPage, rowCurrentPage 는 adminCommon.js 에서 전역 관리 중
rowSelect.addEventListener("change", function () {
    rowsPerPage = parseInt(this.value, 10);
    rowCurrentPage = 1;
    renderTable(rowCurrentPage);
    renderPagination(users.length);
});

/**
 * 회원 목록 테이블 렌더링
 */
function renderTable(page) {
    tableBody.innerHTML = "";

    if (!Array.isArray(users) || users.length === 0) {
        tableBody.innerHTML = `
      <tr>
        <td colspan="6">등록된 회원이 없습니다.</td>
      </tr>
    `;
        userTotalCount.textContent = "00";
        return;
    }

    const totalCount = users.length;              // 전체 회원 수
    const start = (page - 1) * rowsPerPage;       // 현재 페이지의 시작 인덱스
    const end = start + rowsPerPage;
    const pageData = users.slice(start, end);

    // 현재 페이지의 첫 번째 행이 가져야 할 No (전체 기준 최신순)
    // 예) totalCount=12, rowsPerPage=10
    // 1페이지: start=0  → startNo=12  → 12,11,...,3
    // 2페이지: start=10 → startNo=2   → 2,1
    let startNo = totalCount - start;
    if (startNo < 1) startNo = 1;

    pageData.forEach((u, idx) => {
        const no = startNo - idx;

        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${no}</td>
      <td>
        <button class="admin_user_link" data-user="${u.userId}">
          ${u.userName} (${u.userId})
        </button>
      </td>
      <td>${u.userEmail || ""}</td>
      <td>${u.userPhone || ""}</td>
      <td>${u.userBirth || ""}</td>
      <td>${u.userAddr || ""}</td>
    `;
        tableBody.appendChild(tr);
    });

    userTotalCount.textContent = totalCount.toString().padStart(2, "0");
}

/**
 * 회원 목록 페이징 처리 (공통 페이징에서 호출)
 */
function changePage(page) {
    currentPage = page;
    renderTable(currentPage);
    renderPagination(users.length);
}

/**
 * 회원 수강 정보 모달 열기
 * userId 파라미터는 "로그인 아이디(username)" 기준
 */
function openUserModal(userId) {
    const user = users.find(u => u.userId === userId);
    if (!user) return;

    // 회원 이름 표시
    modalUserName.textContent = `${user.userName} (${user.userId})`;
    modalCurrentPage = 1;
    // ✅ 여기서 게임 진행도 로딩
    loadGameProgress(user.userNo);

    // 서버에서 해당 회원의 수강 정보 조회
    fetch(`/adminUser/api/users/${user.userNo}/courses`)
        .then(response => {
            if (!response.ok) {
                throw new Error("수강 정보 조회 실패");
            }
            return response.json();
        })
        .then(data => {
            courseData = Array.isArray(data) ? data : [];

            if (courseData.length === 0) {
                detailTableBody.innerHTML = `
          <tr>
            <td colspan="8">수강 정보가 없습니다.</td>
          </tr>
        `;
                renderModalPagination(0);
            } else {
                renderModalTable(1);
                renderModalPagination(courseData.length);
            }

            // 모달 표시
            modal.style.display = "flex";
            document.body.style.overflow = "hidden";
        })
        .catch(error => {
            console.error(error);
            courseData = [];
            detailTableBody.innerHTML = `
        <tr>
          <td colspan="8">수강 정보를 불러오지 못했습니다.</td>
        </tr>
      `;
            renderModalPagination(0);

            modal.style.display = "flex";
            document.body.style.overflow = "hidden";
        });
}

/**
 * 게임 진행도 조회 후 모달 헤더 갱신
 * userNo = users 테이블 PK (user_id)
 */
function loadGameProgress(userNo) {
    if (!gameProgressFill || !gameProgressText || !gameCertBtn) return;

    // 기본값 초기화
    gameProgressFill.style.width = "0%";
    gameProgressText.textContent = "0 / 4 클리어";
    gameCertBtn.disabled = true;
    gameCertBtn.classList.remove("active");

    fetch(`/adminUser/api/users/${userNo}/game`)
        .then(res => {
            if (!res.ok) throw new Error("게임 진행도 조회 실패");
            return res.json();
        })
        .then(data => {
            // starLevels: "[4,0,0,0]" 형식
            let levelsStr = data.starLevels || "[]";
            let levels;

            try {
                levels = JSON.parse(levelsStr);
            } catch (e) {
                console.error("starLevels JSON 파싱 실패", e);
                levels = [];
            }

            if (!Array.isArray(levels)) {
                levels = [];
            }

            // 총 게임 수 (기본 4개)
            const total = levels.length > 0 ? levels.length : 4;

            // 값이 4 이상인 게임만 클리어로 취급
            const clearCount = levels.filter(v => v >= 4).length;

            // 프로그레스 바 너비 (0, 25, 50, 75, 100%)
            const ratio = total > 0 ? (clearCount / total) * 100 : 0;
            gameProgressFill.style.width = ratio + "%";

            // 텍스트 "X / 4 클리어"
            gameProgressText.textContent = `${clearCount} / ${total} 클리어`;

            // ✅ 3개 이상 클리어 시 이수증 버튼 활성화 (총 4게임 기준)
            if (clearCount >= 3 && total > 0) {
                gameCertBtn.disabled = false;
                gameCertBtn.classList.add("active");
            } else {
                gameCertBtn.disabled = true;
                gameCertBtn.classList.remove("active");
            }
        })
        .catch(err => {
            console.error(err);
            // 에러 시 기본값 유지
            gameProgressFill.style.width = "0%";
            gameProgressText.textContent = "0 / 4 클리어";
            gameCertBtn.disabled = true;
            gameCertBtn.classList.remove("active");
        });
}

/**
 * 수강 정보 테이블 렌더링 (모달 내부)
 */
function renderModalTable(page) {
    detailTableBody.innerHTML = "";

    if (!Array.isArray(courseData) || courseData.length === 0) {
        detailTableBody.innerHTML = `
      <tr>
        <td colspan="8">수강 정보가 없습니다.</td>
      </tr>
    `;
        return;
    }

    const start = (page - 1) * modalRowsPerPage;
    const end = start + modalRowsPerPage;
    const rows = courseData.slice(start, end);

    rows.forEach(c => {
        // 진도가 100%가 아니면 상태를 '진행중'으로, 100%면 '이수'
        let courseStatus = c.courseStatus || (c.courseProgress !== "100%" ? "진행중" : "이수");

        // 이수증 버튼 활성화 조건
        let certBtn = "";
        if (c.courseCert === "발급 완료") {
            certBtn = `<button class="cert_btn" disabled>발급 완료</button>`;
        } else if (courseStatus === "이수" && (c.courseScore || 0) >= 80) {
            certBtn = `<button class="cert_btn active">발급 승인</button>`;
        } else {
            certBtn = `<button class="cert_btn" disabled>발급 불가</button>`;
        }

        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${c.courseNo}</td>
      <td>${c.courseTitle}</td>
      <td>${c.courseStart || ""}</td>
      <td>${c.courseEnd || ""}</td>
      <td>${c.courseProgress || ""}</td>
      <td>${c.courseScore || ""}</td>
      <td>${courseStatus}</td>
      <td>${certBtn}</td>
    `;
        detailTableBody.appendChild(tr);
    });
}

/**
 * 모달 닫기
 */
function closeModal() {
    modal.style.display = "none";
    document.body.style.overflow = "auto";
}

// 닫기 버튼 및 배경 클릭
closeBtn.addEventListener("click", closeModal);
modal.addEventListener("click", (e) => {
    if (e.target === modal) {
        closeModal();
    }
});

// 테이블 클릭 시 모달 열기
tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("admin_user_link")) {
        const userId = e.target.dataset.user;
        openUserModal(userId);
    }
});

// ‘발급 승인’ 버튼 클릭 이벤트 (모달 내부)
detailTableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("cert_btn") && e.target.classList.contains("active")) {
        e.target.textContent = "발급 완료";
        e.target.disabled = true;
        e.target.classList.remove("active");
    }
});

// 회원 수강 목록 페이지 변경 (모달 페이징에서 호출)
function changeModalPage(page) {
    modalCurrentPage = page;
    renderModalTable(modalCurrentPage);
    renderModalPagination(courseData.length);
}

// 초기 로딩 시 서버에서 회원 목록 조회
loadUsersFromServer();
