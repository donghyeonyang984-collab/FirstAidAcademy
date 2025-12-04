// 강의 재생 팝업 전체 제어 스크립트
(function () {
    let currentVideo;
    let currentLectureButton;
    let currentEnrollmentId;
    let currentCourseId;
    let currentLectureId;
    let maxWatchedSec = 0;   // 앞으로 넘길 수 있는 최대 시청 위치
    let lastSavedSec = 0;
    let isSavingProgress = false;
    let memoSaveTimer = null;
    let videoBasePath = null;

    /**
     * courseList에서 팝업 HTML 넣은 다음 호출하는 진입 함수
     *   -> openLecturePopup(enrollmentId, courseId) 안에서 호출
     */
    window.initLecturePopup = function (enrollmentId, courseId) {
        currentEnrollmentId = enrollmentId;
        currentCourseId = courseId;

        const popup = document.getElementById("lecturePopup");
        if (!popup) return;

        currentVideo = popup.querySelector(".video-wrapper video");
        const lectureButtons = popup.querySelectorAll(".lecture-item");
        const memoTextarea = popup.querySelector("#memo textarea");

        // 비디오 기본 경로 (/videos/...)
        if (currentVideo && currentVideo.src) {
            const src = currentVideo.src;
            const lastSlash = src.lastIndexOf("/");
            videoBasePath = src.substring(0, lastSlash + 1);
        }

        /* -------- 탭 버튼 -------- */
        popup.querySelectorAll(".tab-btn").forEach(function (btn) {
            btn.addEventListener("click", function () {
                const targetId = btn.dataset.tab;
                popup.querySelectorAll(".tab-btn").forEach(function (b) {
                    b.classList.toggle("active", b === btn);
                });
                popup.querySelectorAll(".tab-content").forEach(function (panel) {
                    panel.classList.toggle("active", panel.id === targetId);
                });
            });
        });

        /* -------- 닫기 / 수강완료 버튼 -------- */
        const closeBtn = document.getElementById("closePopup");
        if (closeBtn) {
            closeBtn.addEventListener("click", function () {
                saveLectureProgress({keepalive: true});
                saveCurrentMemo({keepalive: true});
                closeLecturePopup();
            });
        }

        const doneBtn = document.getElementById("btnLectureDone");
        if (doneBtn) {
            doneBtn.addEventListener("click", function () {
                // 현재 위치 기준으로 완료 처리
                saveLectureProgress({completed: true});
                alert("수강 완료로 저장되었습니다.");
            });
        }

        /* -------- 목차 초기 설정 및 클릭 핸들러 -------- */
        let activeIndex = 0;

        lectureButtons.forEach(function (btn, idx) {
            if (btn.closest("li").classList.contains("active")) {
                activeIndex = idx;
                currentLectureButton = btn;
            }

            btn.addEventListener("click", function () {
                // 잠긴 차시는 클릭 불가
                if (btn.disabled || btn.closest("li").classList.contains("locked")) {
                    return;
                }
                switchLecture(btn);
            });
        });

        // 서버가 active 표시를 안 줬을 경우 대비
        if (!currentLectureButton && lectureButtons.length > 0) {
            currentLectureButton = lectureButtons[0];
            lectureButtons[0].closest("li").classList.add("active");
        }

        // 완료 여부 기준으로 잠금 상태 설정
        recomputeLectureLocks();

        // 이어보기 대상 강의 세팅
        if (currentLectureButton) {
            loadLectureMeta(currentLectureButton);
            attachVideoEvents();
            loadWatchPosition(); // 이어보기 위치로 이동
            initMemoAutoSave(memoTextarea);
            loadMemo(); // 메모 불러오기
        }
    };

    /** 완료된 차시까지는 열어주고, 그 다음 차시 하나만 열어주기 */
    function recomputeLectureLocks() {
        const popup = document.getElementById("lecturePopup");
        if (!popup) return;
        const buttons = popup.querySelectorAll(".lecture-item");
        let maxAllowIndex = 0; // 0 ~ maxAllowIndex 까지 선택 허용

        buttons.forEach(function (btn, idx) {
            const completed = btn.dataset.completed === "true";
            if (completed) {
                maxAllowIndex = idx + 1;
            }
        });

        buttons.forEach(function (btn, idx) {
            const li = btn.closest("li");
            const locked = idx > maxAllowIndex;
            btn.disabled = locked;
            if (li) {
                li.classList.toggle("locked", locked);
            }
        });
    }

    /** 다른 차시로 이동 (현재 진도 저장 후 로드) */
    function switchLecture(btn) {
        if (!btn) return;

        // 이전 강의 진도 먼저 저장
        saveLectureProgress();

        currentLectureButton = btn;
        const popup = document.getElementById("lecturePopup");

        popup.querySelectorAll(".lecture-list li").forEach(function (li) {
            li.classList.remove("active");
        });
        const li = btn.closest("li");
        if (li) {
            li.classList.add("active");
        }

        loadLectureMeta(btn);
        attachVideoEvents();
        loadWatchPosition();
        loadMemo();
    }

    /** 좌측 제목/카테고리/내용/영상 src 교체 */
    function loadLectureMeta(btn) {
        const popup = document.getElementById("lecturePopup");
        if (!popup || !btn) return;

        currentLectureId = Number(btn.dataset.courseLectureId);

        const titleElem = popup.querySelector(".lecture-title");
        const topBadge = popup.querySelector('[data-role="topCategory"]');
        const midBadge = popup.querySelector('[data-role="midCategory"]');
        const summaryElem = popup.querySelector(".lecture-text");

        if (titleElem) {
            titleElem.textContent = btn.dataset.title || btn.innerText.trim();
        }
        if (topBadge) {
            topBadge.textContent = btn.dataset.topCategory || topBadge.textContent;
        }
        if (midBadge) {
            midBadge.textContent = btn.dataset.midCategory || midBadge.textContent;
        }
        if (summaryElem) {
            summaryElem.textContent = btn.dataset.summary || summaryElem.textContent;
        }

        if (currentVideo && btn.dataset.videoUrl) {
            if (!videoBasePath && currentVideo.src) {
                const src = currentVideo.src;
                const lastSlash = src.lastIndexOf("/");
                videoBasePath = src.substring(0, lastSlash + 1);
            }
            currentVideo.src = (videoBasePath || "/videos/") + btn.dataset.videoUrl;
        }

        maxWatchedSec = 0;
        lastSavedSec = 0;
    }

    /** 시청 중 이벤트 등록 (앞으로 스킵 막기 + 자동 저장 + 자동 다음 강의) */
    function attachVideoEvents() {
        if (!currentVideo) return;

        // 이전에 붙어있던 핸들러 제거 (강의 전환 시 중복 방지)
        if (currentVideo._onTimeUpdate) {
            currentVideo.removeEventListener("timeupdate", currentVideo._onTimeUpdate);
        }
        if (currentVideo._onSeeking) {
            currentVideo.removeEventListener("seeking", currentVideo._onSeeking);
        }
        if (currentVideo._onEnded) {
            currentVideo.removeEventListener("ended", currentVideo._onEnded);
        }

        maxWatchedSec = 0;
        lastSavedSec = 0;
        let seekingFromScript = false;

        // 실제 시청한 최대 지점 기록 + 5초마다 자동 저장
        const onTimeUpdate = function () {
            const t = currentVideo.currentTime;

            // 사용자가 드래그(seeking=true) 중일 때는 maxWatchedSec 갱신하지 않음
            if (!currentVideo.seeking && t > maxWatchedSec) {
                maxWatchedSec = t;
            }

            const nowSec = Math.floor(t);
            if (nowSec - lastSavedSec >= 5) {
                lastSavedSec = nowSec;
                saveLectureProgress();
            }
        };

        // 앞으로 넘기기 시도 막기
        const onSeeking = function () {
            if (seekingFromScript) return;

            // 아직 본 적 없는 구간으로 이동하려 하면 마지막 시청 지점으로 되돌림
            if (currentVideo.currentTime > maxWatchedSec + 0.1) {
                seekingFromScript = true;
                currentVideo.currentTime = maxWatchedSec;
                seekingFromScript = false;
            }
        };

        // 영상 끝나면 완료 처리 + 다음 강의 자동 재생
        const onEnded = function () {
            saveLectureProgress({ completed: true });
            openNextLectureIfExists();
        };

        currentVideo.addEventListener("timeupdate", onTimeUpdate);
        currentVideo.addEventListener("seeking", onSeeking);
        currentVideo.addEventListener("ended", onEnded);

        // 나중에 제거할 수 있도록 참조 저장
        currentVideo._onTimeUpdate = onTimeUpdate;
        currentVideo._onSeeking = onSeeking;
        currentVideo._onEnded = onEnded;
    }


    /** 다음 차시가 있으면 자동으로 열기, 없으면 안내 */
    function openNextLectureIfExists() {
        const popup = document.getElementById("lecturePopup");
        if (!popup || !currentLectureButton) return;

        const buttons = Array.from(popup.querySelectorAll(".lecture-item"));
        const idx = buttons.indexOf(currentLectureButton);
        if (idx === -1) return;

        // 현재 차시 완료 표시
        currentLectureButton.dataset.completed = "true";
        recomputeLectureLocks();

        const nextBtn = buttons[idx + 1];
        if (nextBtn) {
            switchLecture(nextBtn);
        } else {
            alert("모든 강의를 수강했습니다.\n미수료 강의 목록에서 시험을 응시해 주세요.");
        }
    }

    /** 이어보기 위치 불러오기 (GET /api/lecture-progress/{enrollmentId}/{lectureId}) */
    function loadWatchPosition() {
        if (!currentVideo || !currentEnrollmentId || !currentLectureId) return;

        fetch("/api/lecture-progress/" + currentEnrollmentId + "/" + currentLectureId)
            .then(function (res) {
                if (!res.ok) return null;
                return res.json();
            })
            .then(function (data) {
                if (!data || typeof data.watchSec !== "number") return;
                const sec = data.watchSec;
                if (sec > 0) {
                    currentVideo.currentTime = sec;
                    maxWatchedSec = sec;
                    lastSavedSec = sec;
                }
            })
            .catch(function (err) {
                console.error("load watchSec error", err);
            });
    }

    /** 진도 저장 (POST /api/lecture-progress) */
    function saveLectureProgress(options) {
        options = options || {};
        if (!currentVideo || !currentEnrollmentId || !currentLectureId) return;
        if (isSavingProgress) return;

        const watchSec = options.watchSec != null
            ? options.watchSec
            : Math.floor(currentVideo.currentTime);

        const completed = !!options.completed;

        isSavingProgress = true;

        fetch("/api/lecture-progress", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                enrollmentId: currentEnrollmentId,
                lectureId: currentLectureId,
                watchSec: watchSec,
                completed: completed
            }),
            keepalive: !!options.keepalive
        })
            .catch(function (err) {
                console.error("save progress error", err);
            })
            .finally(function () {
                isSavingProgress = false;
            });
    }

    /* ---------------- 메모: 자동 저장 + 복원 ---------------- */

    function initMemoAutoSave(textarea) {
        if (!textarea) return;

        textarea.addEventListener("input", function () {
            if (memoSaveTimer) {
                clearTimeout(memoSaveTimer);
            }
            memoSaveTimer = setTimeout(function () {
                saveCurrentMemo();
            }, 800); // 타이핑 멈춘 뒤 0.8초 후 자동 저장
        });
    }

    /** 메모 불러오기 (GET /api/lecture-notes?lectureId=) */
    function loadMemo() {
        const textarea = document.querySelector("#memo textarea");
        if (!textarea || !currentLectureId) return;

        textarea.value = "";

        fetch("/api/lecture-notes?lectureId=" + currentLectureId)
            .then(function (res) {
                if (!res.ok) return null;
                return res.json();
            })
            .then(function (data) {
                if (data && typeof data.content === "string") {
                    textarea.value = data.content;
                }
            })
            .catch(function (err) {
                console.error("load memo error", err);
            });
    }

    /** 메모 저장 (POST /api/lecture-notes) */
    function saveCurrentMemo(options) {
        options = options || {};
        const textarea = document.querySelector("#memo textarea");
        if (!textarea || !currentLectureId) return;

        const content = textarea.value;

        fetch("/api/lecture-notes", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                lectureId: currentLectureId,
                content: content
            }),
            keepalive: !!options.keepalive
        }).catch(function (err) {
            console.error("save memo error", err);
        });
    }

    /** 팝업 DOM 제거 */
    function closeLecturePopup() {
        const popup = document.getElementById("lecturePopup");
        if (popup && popup.parentElement) {
            popup.parentElement.innerHTML = "";
        }
    }

    // 페이지 전체가 닫힐 때도 마지막 진도/메모 저장 시도
    window.addEventListener("beforeunload", function () {
        saveLectureProgress({keepalive: true});
        saveCurrentMemo({keepalive: true});
    });
})();