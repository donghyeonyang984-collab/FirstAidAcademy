document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('profileForm');
    const currentPw = document.getElementById('password');
    const newPw = document.getElementById('newPassword');
    const pwCheck = document.getElementById('passwordCheck');
    const phone = document.getElementById('phone');
    const email = document.getElementById('email');
    const resetBtn = document.querySelector('.check-btn');
    const pwSection = document.getElementById('passwordChangeSection');

    function setError(input, message) {
        const field = input.closest('.field');
        const hint = field.querySelector('.hint');
        field.classList.add('error');
        if (hint) hint.textContent = message;
    }

    function clearError(input, defaultMsg) {
        const field = input.closest('.field');
        const hint = field.querySelector('.hint');
        field.classList.remove('error');
        if (hint && defaultMsg !== undefined) {
            hint.textContent = defaultMsg;
        }
    }

    // ✅ 비밀번호 재설정 버튼 동작 (DB 검증 후에만 칸 열기)
    if (resetBtn) {
        resetBtn.addEventListener('click', function () {
            const curVal = currentPw.value.trim();
            if (curVal === '') {
                setError(currentPw, '현재 비밀번호를 입력해주세요.');
                currentPw.focus();
                return;
            }

            // 서버에 현재 비밀번호 검증 요청
            fetch('/myPage/checkPassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                },
                body: 'currentPassword=' + encodeURIComponent(curVal)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.valid) {
                        // ✅ 비밀번호 일치할 때만 신규/확인 칸 보여주기
                        clearError(currentPw, '비밀번호 확인이 완료되었습니다.');
                        pwSection.style.display = 'block';
                        resetBtn.disabled = true;
                        currentPw.readOnly = true;
                        if (newPw) {
                            newPw.focus();
                        }
                    } else {
                        // ❌ 일치하지 않으면 에러 표시
                        setError(currentPw, data.message || '현재 비밀번호가 일치하지 않습니다.');
                        alert(data.message || '현재 비밀번호가 일치하지 않습니다.');
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert('비밀번호 확인 중 오류가 발생했습니다.');
                });
        });
    }

    // ✅ 비밀번호 확인 실시간 체크
    if (pwCheck) {
        pwCheck.addEventListener('input', function () {
            const pwVal = newPw.value.trim();
            const pwCheckVal = pwCheck.value.trim();

            if (pwCheckVal.length === 0) {
                clearError(pwCheck, '비밀번호 동일 조건 확인');
                return;
            }

            if (pwVal !== pwCheckVal) {
                setError(pwCheck, '비밀번호가 일치하지 않습니다.');
            } else {
                clearError(pwCheck, '비밀번호가 일치합니다.');
            }
        });
    }

    // ✅ 전화번호 하이픈 자동 적용 함수
    function formatPhone(value) {
        const digits = value.replace(/\D/g, '');
        if (digits.length === 0) return '';

        let result = digits;

        // 02(서울) 전화번호
        if (digits.startsWith('02')) {
            if (digits.length <= 2) {
                result = digits;
            } else if (digits.length <= 5) {
                // 02-123
                result = digits.replace(/(\d{2})(\d{1,3})/, '$1-$2');
            } else {
                // 02-123-4567, 02-1234-5678
                result = digits.replace(/(\d{2})(\d{3,4})(\d{1,4})/, '$1-$2-$3');
            }
        } else {
            // 휴대폰/일반 전화 (010, 031 등)
            if (digits.length <= 3) {
                result = digits;
            } else if (digits.length <= 7) {
                // 010-1234
                result = digits.replace(/(\d{3})(\d{1,4})/, '$1-$2');
            } else {
                // 010-1234-5678, 031-123-4567 등
                result = digits.replace(/(\d{3})(\d{3,4})(\d{1,4})/, '$1-$2-$3');
            }
        }
        return result;
    }

    // ✅ 입력하면서 하이픈 자동 적용
    if (phone) {
        phone.addEventListener('input', function () {
            const prevLength = phone.value.length;
            const prevCursor = phone.selectionStart || 0;

            const formatted = formatPhone(phone.value);
            phone.value = formatted;

            const newLength = formatted.length;
            const diff = newLength - prevLength;
            const newCursor = prevCursor + diff;

            // 커서 위치 대략 유지
            phone.setSelectionRange(newCursor, newCursor);
        });
    }

    // ✅ 폼 제출 시 최종 검증
    form.addEventListener('submit', function (e) {
        let valid = true;

        document.querySelectorAll('.field.error').forEach(f => f.classList.remove('error'));

        const pwVal = newPw.value.trim();
        const pwCheckVal = pwCheck.value.trim();

        // 비밀번호 변경을 요청한 경우에만 검증
        if (pwVal.length > 0 || pwCheckVal.length > 0) {
            const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

            if (!pwRegex.test(pwVal)) {
                valid = false;
                setError(newPw, '영문, 숫자, 특수문자를 포함해 8자리 이상 입력해주세요.');
            } else {
                clearError(newPw, '비밀번호 조건 확인');
            }

            if (pwVal !== pwCheckVal) {
                valid = false;
                setError(pwCheck, '비밀번호가 일치하지 않습니다.');
            } else {
                clearError(pwCheck, '비밀번호 동일 조건 확인');
            }
        }

        // ✅ 전화번호 최종 검증
        const phoneVal = phone.value.trim();

        if (phoneVal === '') {
            valid = false;
            setError(phone, '전화번호를 입력해주세요.');
        } else {
            const formatted = formatPhone(phoneVal);

            if (!/^\d{2,3}-\d{3,4}-\d{4}$/.test(formatted)) {
                valid = false;
                setError(phone, '전화번호 형식을 확인해주세요. 예: 010-1234-5678');
            } else {
                phone.value = formatted; // 최종 형식 맞춰서 세팅
                clearError(phone, '전화번호 조건 확인');
            }
        }

        // 이메일 검증
        const emailVal = email.value.trim();
        if (emailVal === '') {
            valid = false;
            setError(email, '이메일을 입력해주세요.');
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailVal)) {
            valid = false;
            setError(email, '이메일 형식을 확인해주세요.');
        } else {
            clearError(email, '이메일 조건 확인');
        }

        if (!valid) {
            e.preventDefault();
            alert('입력값을 다시 확인해주세요.');
        }
    });
});
