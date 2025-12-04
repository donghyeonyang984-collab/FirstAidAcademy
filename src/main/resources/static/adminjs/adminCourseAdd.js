// 영상 길이 계산 함수
async function getVideoDuration(file) {
    return new Promise((resolve) => {
        const video = document.createElement("video");
        video.preload = "metadata";

        video.onloadedmetadata = function () {
            resolve(Math.floor(video.duration)); // 초 단위 반환
        };

        video.src = URL.createObjectURL(file);
    });
}

document.getElementById("admin_course_add_form")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const form = document.getElementById("admin_course_add_form");
        const formData = new FormData(form);

        // ✔ 영상 길이 계산 후 formData에 추가
        async function appendDuration(id, keyName) {
            const file = document.getElementById(id).files[0];
            if (file) {
                const duration = await getVideoDuration(file);
                formData.append(keyName, duration); // durationSec1,2,3
            }
        }

        await appendDuration("courseVideo1", "durationSec1");
        await appendDuration("courseVideo2", "durationSec2");
        await appendDuration("courseVideo3", "durationSec3");

        // ✔ 필수 아닌 항목 정리
        ["courseVideo2", "courseVideo3"].forEach((id) => {
            const file = document.getElementById(id).files[0];
            if (!file) formData.delete(id);
        });

        ["addCaption2", "addCaption3"].forEach((id) => {
            const text = document.getElementById(id).value.trim();
            if (!text) formData.delete(id);
        });

        try {
            const response = await fetch("/admin/course/add", {
                method: "POST",
                body: formData
            });

            const result = await response.text();

            if (result === "success") {
                alert("강의가 등록되었습니다.");
                window.location.href = "/adminCourse/adminCourseList";
            } else {
                alert("등록 실패: " + result);
            }

        } catch (err) {
            alert("서버 오류");
            console.error(err);
        }
    });
