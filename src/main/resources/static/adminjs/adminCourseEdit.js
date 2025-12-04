// adminCourseEdit.js
// 페이지에 표시된 form 요소 참조
const editForm = document.getElementById("admin_course_edit_form");

// 썸네일 미리보기 (if present)
const thumbInput = document.getElementById("editCourseThumb");
const thumbPreview = document.getElementById("editThumbPreview");
if (thumbInput && thumbPreview) {
    thumbInput.addEventListener("change", (e) => {
        const file = e.target.files[0];
        if (!file) {
            thumbPreview.src = "";
            return;
        }
        const reader = new FileReader();
        reader.onload = (ev) => {
            thumbPreview.src = ev.target.result;
            thumbPreview.style.display = "block";
        };
        reader.readAsDataURL(file);
    });
}

// 비디오 duration 구하는 헬퍼
function getVideoDurationFromFile(file) {
    return new Promise((resolve) => {
        if (!file) return resolve(0);
        const video = document.createElement("video");
        video.preload = "metadata";
        video.onloadedmetadata = function () {
            URL.revokeObjectURL(video.src);
            resolve(Math.floor(video.duration));
        };
        video.src = URL.createObjectURL(file);
    });
}

// submit 처리: duration 계산하고 FormData 전송
editForm && editForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    try {
        const formData = new FormData(editForm);

        // 파일 inputs
        const f1 = document.querySelector("input[name='courseVideo1']").files[0];
        const f2 = document.querySelector("input[name='courseVideo2']").files[0];
        const f3 = document.querySelector("input[name='courseVideo3']").files[0];

        // duration 계산 (존재할 때만)
        const d1 = f1 ? await getVideoDurationFromFile(f1) : formData.get("durationSec1") || 0;
        const d2 = f2 ? await getVideoDurationFromFile(f2) : formData.get("durationSec2") || 0;
        const d3 = f3 ? await getVideoDurationFromFile(f3) : formData.get("durationSec3") || 0;

        formData.set("durationSec1", d1);
        formData.set("durationSec2", d2);
        formData.set("durationSec3", d3);

        // courseId는 hidden input 으로 폼에 포함되어 있어야 함
        const resp = await fetch("/admin/course/edit", {
            method: "POST",
            body: formData
        });

        const text = await resp.text();
        if (text === "success") {
            alert("수정이 완료되었습니다.");
            location.href = "/adminCourse/adminCourseList";
        } else {
            alert("수정 실패: " + text);
        }
    } catch (err) {
        console.error(err);
        alert("서버 오류 발생");
    }
});
