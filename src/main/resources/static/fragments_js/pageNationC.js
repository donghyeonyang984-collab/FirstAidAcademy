//강의 카드 페이지 네이션
  
  
  const itemsPerPage = 6; // 한 페이지당 6개
  const items = document.querySelectorAll('.course-card,.data-card,.completion-card'); 
  const totalPages = Math.ceil(items.length / itemsPerPage);
  let currentPage = 1;

  const pageNumbers = document.querySelector('.page-numbers');
  const prevBtn = document.querySelector('.prev');
  const nextBtn = document.querySelector('.next');

  function renderPagination() {
    pageNumbers.innerHTML = '';

    const createPage = (num, text = num) => {
      const li = document.createElement('li');
      li.textContent = text;
      if (num === currentPage) li.classList.add('active');
      if (!isNaN(num)) {
        li.addEventListener('click', () => showPage(num));
      }
      pageNumbers.appendChild(li);
    };

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) createPage(i);
    } else {
      if (currentPage > 3) createPage(1);
      if (currentPage > 4) createPage(null, '...');
      const start = Math.max(1, currentPage - 2);
      const end = Math.min(totalPages, currentPage + 2);
      for (let i = start; i <= end; i++) createPage(i);
      if (currentPage < totalPages - 3) createPage(null, '...');
      if (currentPage < totalPages - 2) createPage(totalPages);
    }

    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;
  }

  function showPage(page) {
    currentPage = page;
    items.forEach((item, i) => {
      item.style.display =
        i >= (page - 1) * itemsPerPage && i < page * itemsPerPage
          ? 'block'
          : 'none';
    });
    renderPagination();
  }

  prevBtn.addEventListener('click', () => {
    if (currentPage > 1) showPage(currentPage - 1);
  });

  nextBtn.addEventListener('click', () => {
    if (currentPage < totalPages) showPage(currentPage + 1);
  });

  showPage(1);
