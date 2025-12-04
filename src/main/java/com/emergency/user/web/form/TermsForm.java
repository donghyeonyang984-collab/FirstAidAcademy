package com.emergency.user.web.form;

import lombok.Data;

/**
 * 회원가입 1단계 - 약관 동의 폼
 */
@Data
public class TermsForm {
    private boolean agreeService;   // 서비스 이용 약관(필수)
    private boolean agreePrivacy;   // 개인정보 수집/이용(필수)
    private boolean agreeLocation;  // 위치기반 서비스(필수)
    private boolean agreeMarketing; // 마케팅 동의(선택)
}
