const slides = document.getElementById("bannerSlide");
const dots = document.querySelectorAll(".dot");
const banners = document.querySelectorAll(".banner");
const fixedTexts = document.querySelectorAll(".fixed-text");

const totalSlides = banners.length;
let currentIndex = 0;
let isScrolling = false;
let autoplayTimer = null;

function showSlide(index) {
    slides.style.transform = `translateX(-${index * 100}%)`;
    dots.forEach(dot => dot.classList.remove("active"));
    banners.forEach(b => b.classList.remove("active"));
    fixedTexts.forEach(text => text.classList.remove("active"));

    dots[index].classList.add("active");
    banners[index].classList.add("active");
    fixedTexts[index].classList.add("active");
    currentIndex = index;
}
// 도트 클릭 이동
dots.forEach((dot, index) => {
    dot.addEventListener("click", () => {
        showSlide(index);
        restartAutoplay();
    });
    // 접근성: 키보드 탭으로 포커스 시 엔터/스페이스로 이동
    dot.addEventListener("keydown", (e) => {
        if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            showSlide(index);
            restartAutoplay();
        }
    });
});

// 자동재생(5초 간격)
function startAutoplay(){
    stopAutoplay();
    autoplayTimer = setInterval(() => {
        currentIndex = (currentIndex + 1) % totalSlides;
        showSlide(currentIndex);
    }, 5000);
}
function stopAutoplay(){
    if (autoplayTimer) clearInterval(autoplayTimer);
    autoplayTimer = null;
}
function restartAutoplay(){ startAutoplay(); }

// 초기화
showSlide(0);
startAutoplay();