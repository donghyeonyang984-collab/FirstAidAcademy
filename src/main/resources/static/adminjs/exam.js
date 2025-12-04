document.addEventListener("DOMContentLoaded", async () => {

    const params = new URLSearchParams(location.search);
    const courseId = params.get("courseId");

    if (!courseId) {
        alert("courseId가 없습니다.");
        return;
    }

    // 🔹 로그인 사용자 정보 가져오기
    const userRes = await fetch(`/api/user/me`);
    const user = await userRes.json();

    // 🔹 응시일
    const today = new Date();
    document.getElementById("exam_date").innerText =
        `${today.getFullYear()}-${today.getMonth() + 1}-${today.getDate()}`;

    // 🔹 응시자 이름 표시
    document.getElementById("exam_user_name").innerText = user.name;

    // 🔹 시험 문제 가져오기 (userId는 세션에서 자동)
    const res = await fetch(`/exam/start?courseId=${courseId}`);
    const examData = await res.json();

    window.attemptId = examData.attemptId;

    renderQuestions(examData.questions);
});

// 문제 렌더링
function renderQuestions(questions) {
    const examMain = document.querySelector(".exam_main");
    examMain.innerHTML = "";

    questions.forEach(q => {
        const card = document.createElement("div");
        card.classList.add("question_card");

        // ★ questionNo + questionId 둘 다 저장
        card.dataset.qno = q.questionNo;
        card.dataset.questionId = q.questionId;

        card.innerHTML = `
            <div class="question_header">
                <span class="q_number">Q${q.questionNo}</span>
                <p class="q_text">${q.questionText}</p>
            </div>

            <div class="options_group">
                ${q.choices.map(choice => `
                    <label class="option_item">
                        <input type="radio"
                               name="q${q.questionId}"
                               value="${choice.choiceId}">
                        <span class="option_label">
                            ${String.fromCharCode(64 + choice.choiceNo)}
                        </span>
                        <span class="option_text">${choice.choiceText}</span>
                    </label>
                `).join("")}
            </div>
        `;

        examMain.appendChild(card);
    });
}

// 제출 버튼 클릭시
document.querySelector(".submit_btn").addEventListener("click", async () => {

    const cards = document.querySelectorAll(".question_card");
    let allAnswered = true;
    const answers = [];

    cards.forEach(card => {
        const questionId = Number(card.dataset.questionId);
        const selected = card.querySelector("input[type='radio']:checked");

        if (!selected) {
            allAnswered = false;
        } else {
            answers.push({
                questionId: questionId,
                choiceId: Number(selected.value)
            });
        }
    });

    if (!allAnswered) {
        alert("풀지 않은 문제가 있습니다. 모든 문항에 답해주세요.");
        return;
    }

    const payload = {
        attemptId: window.attemptId,
        answers: answers
    };

    const res = await fetch("/exam/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const result = await res.json();
    console.log("채점결과:", result);

    renderResult(result);

});


// 채점 후 화면 표시
function renderResult(result) {

    if (!result.results) {
        alert(`점수: ${result.score}점`);
        return;
    }

    result.results.forEach(r => {

        const card = document.querySelector(`.question_card[data-question-id="${r.questionId}"]`);

        const options = card.querySelectorAll("label.option_item");
        options.forEach(opt => opt.classList.remove("correct", "wrong", "user-select"));

        if (r.userChoiceId) {
            const userOpt = card.querySelector(`input[value="${r.userChoiceId}"]`).parentNode;
            userOpt.classList.add("user-select");
        }

        const correctOpt = card.querySelector(`input[value="${r.correctChoiceId}"]`).parentNode;
        correctOpt.classList.add("correct");

        if (!r.isCorrect && r.userChoiceId) {
            const wrongOpt = card.querySelector(`input[value="${r.userChoiceId}"]`).parentNode;
            wrongOpt.classList.add("wrong");
        }
    });

    alert(`채점 완료! 점수: ${result.score}점`);
    // location.href = "myPage/myStudy.html";
}
let remainSeconds = 30 * 60; // 30분

function updateTimer() {
    let min = Math.floor(remainSeconds / 60);
    let sec = remainSeconds % 60;

    document.getElementById("examTimer").innerText =
        `${min}분 ${sec.toString().padStart(2, '0')}초`;

    if (remainSeconds <= 0) {
        alert("시간 종료! 자동 제출됩니다.");
        document.querySelector(".submit_btn").click();
        return;
    }

    remainSeconds--;
}

setInterval(updateTimer, 1000);
updateTimer();