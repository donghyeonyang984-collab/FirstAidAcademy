document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("joinForm");
    const cancelBtn = document.querySelector(".cancel");

    const userid = document.getElementById("userid");
    const userHint = document.getElementById("idCheckMsg");
    const checkBtn = document.getElementById("checkDuplicateBtn");

    const pw = document.getElementById("password");
    const pwHint = pw.nextElementSibling;
    const pwCheck = document.getElementById("passwordCheck");
    const pwCheckHint = pwCheck.nextElementSibling;
    const birth = document.getElementById("birth");
    const birthHint = birth.nextElementSibling;
    const phone = document.getElementById("phone");
    const phoneHint = phone.nextElementSibling;
    const email = document.getElementById("email");
    const emailHint = email.nextElementSibling;

    /* 아이디 유효성 검사 */
    userid.addEventListener("input", () => {
        const pattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{4,}$/;
        if (userid.value.trim() === "") {
            userHint.textContent = "아이디를 입력해주세요.";
            userHint.style.color = "red";
        } else if (pattern.test(userid.value)) {
            userHint.textContent = "사용 가능한 형식입니다. 중복확인을 진행해주세요.";
            userHint.style.color = "orange";
        } else {
            userHint.textContent = "영문과 숫자를 포함한 4자리 이상이어야 합니다.";
            userHint.style.color = "red";
        }
    });

    /* ✅ 아이디 중복확인 버튼 */
    if (checkBtn) {
        checkBtn.addEventListener("click", () => {
            const username = userid.value.trim();
            if (username === "") {
                userHint.textContent = "아이디를 입력해주세요.";
                userHint.style.color = "red";
                return;
            }

            const pattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{4,}$/;
            if (!pattern.test(username)) {
                userHint.textContent = "아이디 형식이 올바르지 않습니다.";
                userHint.style.color = "red";
                return;
            }

            fetch(`/api/users/check-username?username=${encodeURIComponent(username)}`)
                .then(res => res.json())
                .then(data => {
                    if (data.exists) {
                        userHint.textContent = "이미 사용 중인 아이디입니다.";
                        userHint.style.color = "red";
                    } else {
                        userHint.textContent = "사용 가능한 아이디입니다!";
                        userHint.style.color = "green";
                    }
                })
                .catch(() => {
                    userHint.textContent = "중복확인 중 오류가 발생했습니다.";
                    userHint.style.color = "red";
                });
        });
    }

    /* 비밀번호 유효성 검사 */
    pw.addEventListener("input", () => {
        const pwPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
        if (pw.value.trim() === "") {
            pwHint.textContent = "비밀번호를 입력해주세요.";
            pwHint.style.color = "red";
        } else if (pwPattern.test(pw.value)) {
            pwHint.textContent = "사용 가능한 비밀번호입니다.";
            pwHint.style.color = "green";
        } else {
            pwHint.textContent = "영문, 숫자, 특수문자 포함 8자리 이상이어야 합니다.";
            pwHint.style.color = "red";
        }
    });

    /* 비밀번호 확인 */
    pwCheck.addEventListener("input", () => {
        if (pwCheck.value === "") {
            pwCheckHint.textContent = "비밀번호를 다시 입력해주세요.";
            pwCheckHint.style.color = "red";
        } else if (pwCheck.value === pw.value) {
            pwCheckHint.textContent = "비밀번호가 일치합니다.";
            pwCheckHint.style.color = "green";
        } else {
            pwCheckHint.textContent = "비밀번호가 일치하지 않습니다.";
            pwCheckHint.style.color = "red";
        }
    });

    /* 생년월일 숫자만 입력 */
    birth.addEventListener("input", () => {
        birth.value = birth.value.replace(/[^0-9]/g, "");
        if (birth.value === "") {
            birthHint.textContent = "생년월일을 입력해주세요.";
            birthHint.style.color = "red";
        } else if (/^\d{8}$/.test(birth.value)) {
            birthHint.textContent = "올바른 형식입니다.";
            birthHint.style.color = "green";
        } else {
            birthHint.textContent = "8자리 숫자로 입력해주세요. (예: 20051107)";
            birthHint.style.color = "red";
        }
    });

    /* 전화번호 입력 시 하이픈 자동 추가 */
    phone.addEventListener("input", () => {
        let value = phone.value.replace(/[^0-9]/g, "");

        if (value.length < 4) {
            phone.value = value;
        } else if (value.length < 8) {
            phone.value = value.replace(/(\d{3})(\d{1,4})/, "$1-$2");
        } else {
            phone.value = value.replace(/(\d{3})(\d{4})(\d{1,4})/, "$1-$2-$3");
        }

        if (value === "") {
            phoneHint.textContent = "전화번호를 입력해주세요.";
            phoneHint.style.color = "red";
        } else if (/^010-\d{4}-\d{4}$/.test(phone.value)) {
            phoneHint.textContent = "올바른 전화번호 형식입니다.";
            phoneHint.style.color = "green";
        } else {
            phoneHint.textContent = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)";
            phoneHint.style.color = "red";
        }
    });

    /* 이메일 형식 검사 */
    email.addEventListener("input", () => {
        const value = email.value.trim();
        const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (value === "") {
            emailHint.textContent = "이메일을 입력해주세요.";
            emailHint.style.color = "red";
        } else if (pattern.test(value)) {
            emailHint.textContent = "올바른 이메일 형식입니다.";
            emailHint.style.color = "green";
        } else {
            emailHint.textContent = "올바른 이메일 형식이 아닙니다.";
            emailHint.style.color = "red";
        }
    });

    /* 폼 제출 시 검사 후 서버 전송 */
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const requiredFields = form.querySelectorAll("[required]");
        let allFilled = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                allFilled = false;
                field.style.border = "2px solid red";
            } else {
                field.style.border = "";
            }
        });

        if (!allFilled) {
            alert("필수 항목을 모두 입력해주세요.");
            return;
        }

        if (pw.value !== pwCheck.value) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        form.submit();
    });

    /* 취소 버튼 */
    cancelBtn.addEventListener("click", function () {
        if (confirm("입력을 취소하고 이전 단계로 돌아가시겠습니까?")) {
            history.back();
        }
    });
});
