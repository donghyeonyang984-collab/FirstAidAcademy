package com.emergency.user.web.form;

import lombok.Data;

/**
 * 회원가입 2단계 - 회원 정보 입력 폼
 * info.html 의 th:field 들과 1:1 매핑
 */
@Data
public class JoinForm {

    // 아이디 (users.username)
    private String userid;

    // 비밀번호 (users.password_plain) - 데모용으로 평문 사용
    private String password;

    private String name;

    // "남자" / "여자" 그대로 들어옴 (서비스에서 'M' / 'F' 로 변환)
    private String gender;

    // "yyyyMMdd" 형식 문자열 (예: 20051107)
    private String birth;

    private String phone;
    private String email;
    private String address;
}
