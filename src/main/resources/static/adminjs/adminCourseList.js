// 요소
const tbody = document.querySelector("#admin_courseList_table tbody");
const courseTotalCount = document.getElementById("admin_tot_cnt");
const searchInput = document.querySelector("input[name='courseList_keyword']");
// 필터 카테고리 저장용
let categoryFilter = null;


// 1️⃣ 페이지 로드시 DB 데이터 불러오기
async function loadCourseList() {
    try {
        const res = await fetch("/admin/api/courses");
        courseList = await res.json();

        applyFilter(); // 필터 반영해서 출력

    } catch (err) {
        console.error("강의 리스트 로드 실패:", err);
    }
}


function getFilteredList() {

    let list = courseList;

    // 1) 카테고리 필터 적용
    if (categoryFilter) {
        list = list.filter(c => c.top_category === categoryFilter);
    }

    // 2) 제목 검색 적용
    const keyword = searchInput.value.trim();
    if (keyword !== "") {
        list = list.filter(c => c.title.includes(keyword));
    }
    list = list.sort((a, b) => new Date(b.reg_date) - new Date(a.reg_date));
    return list;
}
document.querySelector(".admin_srch_icon").addEventListener("click", () => {
    currentPage = 1;
    applyFilter();
});


// 필터 적용 후 화면 다시 렌더링
function applyFilter() {
    const list = getFilteredList();

    renderPagination(list.length);
    renderTable(currentPage);
}


// 2️⃣ 테이블 렌더링
function renderTable(page) {
    tbody.innerHTML = "";

    const filtered = getFilteredList();

    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const pageData = filtered.slice(start, end);

    const template = document.getElementById("course-row-template");

    pageData.forEach((c, idx) => {
        const clone = template.content.cloneNode(true);

        // 번호
        clone.querySelector(".no").textContent =
            filtered.length - (start + idx);
        // 썸네일
        const img = clone.querySelector("img");
        img.src = `/uploads/${c.image_path}`;
        img.alt = c.title;

        // 카테고리
        clone.querySelector(".course_category").textContent = `[${c.top_category} / ${c.mid_category}]`;

        // 제목 링크
        const a = clone.querySelector(".course_link");
        a.href = `/admin/course/edit?id=${c.course_id}`;
        a.textContent = c.title;

        // 등록일
        clone.querySelector(".date").textContent = c.reg_date;

        // 삭제 버튼
        const btn = clone.querySelector(".admin_delete_btn");
        btn.dataset.courseId = c.course_id;

        tbody.appendChild(clone);
    });

    courseTotalCount.textContent = filtered.length;
    attachDeleteEvents();
}


// 3️⃣ 삭제 버튼 처리 (그대로)
function attachDeleteEvents() {
    document.querySelectorAll(".admin_delete_btn").forEach((btn) => {
        btn.addEventListener("click", async (e) => {
            const courseId = e.target.dataset.courseId;

            if (!confirm("정말 삭제하시겠습니까?")) return;

            try {
                await fetch(`/admin/api/course/${courseId}`, { method: "DELETE" });
                alert("삭제되었습니다.");

                // DB 재호출 대신 로컬 리스트에서 제거
                courseList = courseList.filter(c => c.course_id != courseId);

                applyFilter(); // 필터 후 다시 렌더

            } catch (err) {
                console.error(err);
                alert("삭제 중 오류 발생");
            }
        });
    });
}


// ⭐⭐⭐ 4️⃣ 카테고리 라디오 클릭 이벤트 추가
document.querySelectorAll("input[name='courseList_status']").forEach(radio => {
    radio.addEventListener("change", () => {
        categoryFilter = radio.value;  // "자가 응급 처치" / "구조자 응급 처치"

        currentPage = 1; // 첫 페이지로
        applyFilter();
    });
});


// 5️⃣ 행 개수 변경
rowSelect.addEventListener("change", function () {
    rowsPerPage = parseInt(this.value);
    currentPage = 1;
    applyFilter();
});


// 6️⃣ 페이지 변경
function changePage(page) {
    currentPage = page;
    applyFilter();
}


// 7️⃣ 시작
loadCourseList();
