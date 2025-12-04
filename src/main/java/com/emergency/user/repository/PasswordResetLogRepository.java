package com.emergency.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * password_reset_logs 테이블용 리포지토리
 *
 *  password_reset_logs (
 *      password_reset_log_id BIGINT PK AI,
 *      user_id               BIGINT NOT NULL,
 *      requested_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
 *      reset_success         TINYINT(1),
 *      note                  VARCHAR(200) NULL
 *  )
 */
@Repository( "passwordResetLogRepository")
@RequiredArgsConstructor
public class PasswordResetLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public void logReset(Long userId, boolean success, String note) {
        String sql = "INSERT INTO password_reset_logs " +
                "(user_id, reset_success, note) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, success ? 1 : 0, note);
    }
}
