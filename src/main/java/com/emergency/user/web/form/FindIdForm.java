package com.emergency.user.web.form;

import lombok.Data;

/**
 * 아이디 찾기용 폼
 */
@Data
public class FindIdForm {

    /** 이름 */
    private String name;

    /** 생년월일 (yyyyMMdd 문자열로 받기) */
    private String birth;
}
