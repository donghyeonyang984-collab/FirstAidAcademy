package com.emergency.user.web.form;

import lombok.Data;

/**
 * 비밀번호 재설정 폼
 */
@Data
public class ResetPwForm {

    /** 아이디 */
    private String username;

    /** 이름 (본인 확인용) */
    private String name;

    /** 새 비밀번호 */
    private String newPassword;

    /** 새 비밀번호 확인 */
    private String confirmPassword;
}
