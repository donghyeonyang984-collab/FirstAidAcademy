package com.emergency.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * terms_agreements 테이블 매핑
 */
@Data
public class TermsAgreement {

    private Long termsAgreementId;
    private Long userId;
    private LocalDateTime agreedAt;
    private String termsVersion;
}
