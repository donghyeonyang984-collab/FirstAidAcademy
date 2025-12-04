// 사이드바 드롭다운
function initSidebarCommon() {
  // 사이드바 드롭다운
  document.querySelectorAll('.sidebar_toggle').forEach(btn => {
    btn.addEventListener('click', () => {
      btn.parentElement.classList.toggle('active');
    });
  });

  // 현재 페이지 URL 기준으로 열린 상태 유지
  const sidebarCurrentPage = window.location.pathname.split("/").pop();
  document.querySelectorAll('.aSidebar_sub_menu li a').forEach(link => {
    const linkHref = link.getAttribute('href');
    if (linkHref && sidebarCurrentPage === linkHref.split("/").pop()) {
      link.classList.add('active');
      link.closest('.aSidebar_sub').classList.add('active');
    }
  });
}

// ====================================================================== //

// 페이지 로드 시 자동으로 사이드바 초기화
document.addEventListener('DOMContentLoaded', function() {
    initSidebarCommon();
});



// 목록 페이징 처리
const rowsPerPage = 10;
let currentPage = 1;

const pagingDiv = document.getElementById("pagination");

// 회원 목록 페이지 버튼 렌더링
function renderPagination(totalItems) {
  pagingDiv.innerHTML = "";
  const pageCount = Math.ceil(totalItems / rowsPerPage);

  // 이전 버튼
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "〈";
  prevBtn.disabled = currentPage === 1;
  prevBtn.addEventListener("click", () => changePage(currentPage - 1));
  pagingDiv.appendChild(prevBtn);

  // 현재 페이지 / 전체 페이지 표시
  const pageInfo = document.createElement("span");
  pageInfo.textContent = `${currentPage} / ${pageCount}`;
  pagingDiv.appendChild(pageInfo);

  // 다음 버튼
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "〉";
  nextBtn.disabled = currentPage === pageCount;
  nextBtn.addEventListener("click", () => changePage(currentPage + 1));
  pagingDiv.appendChild(nextBtn);
}

// ====================================================================== //

// 모달 안 페이징 처리
const modalRowsPerPage = 10;
let modalCurrentPage = 1;

const modalPagingDiv = document.getElementById("modal_pagination");

// 회원 수강 목록 페이징 렌더링 함수
function renderModalPagination(totalItems) {
  modalPagingDiv.innerHTML = "";
  const pageCount = Math.ceil(totalItems / modalRowsPerPage);

  // 이전 버튼
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "〈";
  prevBtn.disabled = modalCurrentPage === 1;
  prevBtn.addEventListener("click", () => changeModalPage(modalCurrentPage - 1));
  modalPagingDiv.appendChild(prevBtn);

  // 페이지 정보
  const pageInfo = document.createElement("span");
  pageInfo.textContent = `${modalCurrentPage} / ${pageCount || 1}`;
  modalPagingDiv.appendChild(pageInfo);

  // 다음 버튼
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "〉";
  nextBtn.disabled = modalCurrentPage === pageCount || pageCount === 0;
  nextBtn.addEventListener("click", () => changeModalPage(modalCurrentPage + 1));
  modalPagingDiv.appendChild(nextBtn);
}

// ====================================================================== //

// 행 개수 드롭다운
const rowSelect = document.getElementById("admin_row_select");