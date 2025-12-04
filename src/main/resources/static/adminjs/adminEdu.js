

// 삭제 버튼 이벤트
function attachDeleteEvents() {
  document.querySelectorAll(".admin_delete_btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const eduId = parseInt(e.target.dataset.eduId);
      const confirmDelete = confirm("정말로 해당 교육 자료를 삭제하시겠습니까?");
      if (confirmDelete) {
        const index = eduList.findIndex(edu => edu.eduId === eduId);
        if (index !== -1) {
          eduList.splice(index, 1);
          alert("교육 자료가 삭제되었습니다.");
          renderEduTable(currentPage);
          renderPagination(eduList.length);
        }
      }
    });
  });
}

// 행 개수 변경
rowSelect.addEventListener("change", function () {
  rowsPerPage = parseInt(this.value);
  currentPage = 1;
  renderEduTable(currentPage);
  renderPagination(eduList.length);
});

// 강의 목록 페이징 처리
function changePage(page) {
  currentPage = page;
  renderEduTable(currentPage);
  renderPagination(eduList.length);
}

// 초기 실행
renderEduTable(currentPage);
renderPagination(eduList.length);