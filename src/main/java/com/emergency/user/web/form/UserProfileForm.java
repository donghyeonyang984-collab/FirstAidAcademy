package com.emergency.user.web.form;

import lombok.Data;

/**
 * 마이페이지 > 회원정보 수정 폼 DTO
 * - 화면 폼 데이터 전달용
 * - 서버 단 검증은 최소화하고, 기본 검증은 JS에서 처리하는 구조
 */
@Data
public class UserProfileForm {

    /** DB PK (users.user_id) – 숨겨진 값으로만 사용 가능 */
    private Long userId;

    /** 로그인 아이디 (users.username) – 읽기 전용 표시 */
    private String username;

    /** 이름 – 읽기 전용 표시 */
    private String name;

    /** 성별 (예: "남자", "여자") */
    private String gender;

    /** 생년월일 – 화면에서는 문자열로 처리 (예: 1995-01-01) */
    private String birth;

    /** 전화번호 */
    private String phone;

    /** 이메일 */
    private String email;

    /** 주소 */
    private String address;

    /** 신규 비밀번호 (입력한 경우에만 변경) */
    private String newPassword;

    /** 비밀번호 확인 (newPassword와 일치 여부는 JS에서 1차 체크) */
    private String passwordCheck;
}
