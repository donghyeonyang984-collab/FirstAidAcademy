// 공통 include + 초기화
document.addEventListener("DOMContentLoaded", async () => {
    const includes = document.querySelectorAll("[data-include]");

    // include 파일 로드
    await Promise.all(
        [...includes].map(async (el) => {
            const url = el.getAttribute("data-include");
            try {
                const res = await fetch(url);
                if (!res.ok) throw new Error(`${url} 로드 실패`);
                el.innerHTML = await res.text();
            } catch (e) {
                el.innerHTML = `<div style="color:red;">${url} 불러오기 실패</div>`;
                console.error(e);
            }
        })
    );

    // 사이드바 / 헤더 / 탭 / 페이지네이션 초기화
    if (document.querySelector(".sidebar")) {
        initSidebar();
    }
    if (document.querySelector(".navbar") && typeof initHeaderMenu === "function") {
        initHeaderMenu();
    }
    if (typeof initTabs === "function") {
        initTabs();
    }
    if (typeof initPagination === "function") {
        initPagination();
    }
});

// ==========================
// 사이드바
// ==========================
function initSidebar() {
    const sidebarItems = document.querySelectorAll(".sidebar-item");
    const submenuLinks = document.querySelectorAll(".submenu a");
    const currentPath = location.pathname;

    // 경로를 비교하기 쉽게 정규화하는 함수
    // 예)
    //   "/courses"                -> "courses"
    //   "/courses/"               -> "courses"
    //   "/courses/courses.html"   -> "courses"
    //   "/courses/course1.html"   -> "course1"
    const normalizePath = (path) => {
        if (!path) return "";

        // 쿼리 / 해시 제거
        let clean = path.split(/[?#]/)[0];

        // 세그먼트 분리 후 마지막만 사용
        const segments = clean.split("/").filter(Boolean);
        if (!segments.length) return "";
        let last = segments[segments.length - 1]; // ex) "courses.html"

        // .html 확장자 제거
        return last.replace(/\.html$/i, ""); // ex) "courses"
    };

    const currentKey = normalizePath(currentPath);

    // 부모 메뉴 클릭 → 페이지 이동
    sidebarItems.forEach((item) => {
        const href =
            item.getAttribute("data-href") ||
            item.querySelector("a")?.getAttribute("href");
        if (!href) return;

        // 클릭 시 이동
        item.addEventListener("click", () => {
            location.href = href;
        });

        const hrefKey = normalizePath(href);

        // URL 기반 활성화 (확장자/쿼리 무시)
        if (hrefKey && hrefKey === currentKey) {
            item.classList.add("active");

            // 해당 부모 메뉴 서브 메뉴 펼치기
            const next = item.nextElementSibling;
            if (next && next.classList.contains("submenu")) {
                next.classList.add("show");
            }
        }
    });

    // 서브 메뉴 클릭 → 페이지 이동 + 부모 메뉴 active 유지
    submenuLinks.forEach((link) => {
        const href = link.getAttribute("href");
        if (!href) return;

        link.addEventListener("click", (e) => {
            e.stopPropagation();
            location.href = href;
        });

        const hrefKey = normalizePath(href);

        // URL 기반 활성화 (확장자/쿼리 무시)
        if (hrefKey && hrefKey === currentKey) {
            link.classList.add("current");

            // 부모 메뉴 active 및 서브 메뉴 펼치기
            const parentItem = link.closest(".submenu")?.previousElementSibling;
            parentItem?.classList.add("active");
            link.closest(".submenu")?.classList.add("show");
        }
    });
}

// ==========================
// 탭
// ==========================
function initTabs() {
    const tabs = document.querySelectorAll(".tab");
    const contents = document.querySelectorAll(".tab-content");
    if (!tabs.length) return;

    tabs.forEach((tab, idx) => {
        tab.addEventListener("click", () => {
            tabs.forEach((t) => t.classList.remove("active"));
            contents.forEach((c) => c.classList.remove("active"));
            tab.classList.add("active");
            contents[idx].classList.add("active");
        });
    });
}

// ==========================
// 약관 동의
// ==========================
document.addEventListener("DOMContentLoaded", function () {
    const allCheck = document.getElementById("allCheck");
    // 약관 영역이 없는 페이지에서는 아무 것도 하지 않음
    if (!allCheck) return;

    const checkboxes = document.querySelectorAll(
        ".agree-check input[type='checkbox']"
    );
    const nextBtn = document.querySelector(".next");

    // 전체 동의 클릭 시 전체 체크박스 상태 변경
    allCheck.addEventListener("change", function () {
        checkboxes.forEach((chk) => (chk.checked = this.checked));
    });

    // 개별 체크 변경 시 전체동의 상태 갱신
    checkboxes.forEach((chk) => {
        chk.addEventListener("change", () => {
            const allChecked = Array.from(checkboxes).every((c) => c.checked);
            allCheck.checked = allChecked;
        });
    });

    // 다음 버튼 클릭 시 필수항목 확인
    if (nextBtn) {
        nextBtn.addEventListener("click", function () {
            const requiredChecks = document.querySelectorAll(
                ".agree-check input.required"
            );
            const allRequiredChecked = Array.from(requiredChecks).every(
                (chk) => chk.checked
            );

            if (!allRequiredChecked) {
                alert("필수 약관에 모두 동의해야 합니다.");
                return;
            }

            alert("모든 필수 약관에 동의하셨습니다. 다음 단계로 이동합니다.");
            window.location.href = "info.html";
        });
    }
});
