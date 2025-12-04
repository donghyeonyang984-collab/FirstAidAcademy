// 회원 현황(신규 가입자)
const ctxUser = document.getElementById("userJoinChart").getContext("2d");

const userJoinChart = new Chart(ctxUser, {
    type: "line",
    data: {
        // ★ 서버에서 내려준 값 사용, 없으면 기존 더미값
        labels:
            typeof userJoinLabels !== "undefined"
                ? userJoinLabels
                : ["1월", "2월", "3월", "4월", "5월", "6월"],
        datasets: [
            {
                label: "신규 가입자 수",
                data:
                    typeof userJoinData !== "undefined"
                        ? userJoinData
                        : [20, 25, 15, 30, 28, 40],
                borderColor: "#7ed3d9",
                backgroundColor: "rgba(105, 213, 221, 0.15)",
                borderWidth: 2,
                fill: true,
                tension: 0.3,
                borderDash: [5, 5], // 점선 스타일
                pointRadius: 4,
                pointBackgroundColor: "#7ed3d9",
                pointHoverRadius: 6,
            },
        ],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: false, // 범례 제거
            },
        },
        scales: {
            x: {
                grid: {
                    color: "#e6edf2",
                },
            },
            y: {
                grid: {
                    color: "#f1f1f1",
                },
                ticks: {
                    stepSize: 10,
                },
            },
        },
    },
});

// 강의별 수료율
const ctxCourse = document
    .getElementById("courseCompletionChart")
    .getContext("2d");

const courseCompletionChart = new Chart(ctxCourse, {
    type: "bar",
    data: {
        labels:
            typeof courseLabels !== "undefined"
                ? courseLabels
                : ["출혈", "기도막힘", "심정지", "화상"],
        datasets: [
            {
                label: "수료율 (%)",
                data:
                    typeof courseCompletionData !== "undefined"
                        ? courseCompletionData
                        : [90, 80, 70, 60],
                backgroundColor: "rgba(255, 195, 195, 0.8)",
                borderColor: "#ffb3b3",
                borderWidth: 1,
                barThickness: 40,
            },
        ],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: false,
            },
        },
        scales: {
            x: {
                grid: {
                    color: "#e6edf2",
                },
            },
            y: {
                grid: {
                    color: "#f1f1f1",
                },
                min: 0,
                max: 100,
                ticks: {
                    stepSize: 20,
                    callback: (value) => `${value}%`,
                },
            },
        },
    },
});