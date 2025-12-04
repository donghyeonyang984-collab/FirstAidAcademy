package com.emergency.user.repository;

import com.emergency.user.domain.TermsAgreement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository( "TermsAgreementRepository")
@RequiredArgsConstructor
public class TermsAgreementRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(TermsAgreement agreement) {
        String sql = "insert into terms_agreements (user_id, terms_version) values (?, ?)";
        jdbcTemplate.update(sql, agreement.getUserId(), agreement.getTermsVersion());
    }
}
