package com.emergency.enrollment.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * enrollments 테이블 매핑용 도메인 객체
 *
 * 테이블 구조 (요약)
 *  - enrollment_id   BIGINT PK AI
 *  - user_id        BIGINT
 *  - course_id      BIGINT
 *  - progress_percent DECIMAL(5,2) DEFAULT 0.00
 *  - status         ENUM('수강중','미수료','수료') DEFAULT '수강중'
 *  - enrolled_at    DATETIME DEFAULT CURRENT_TIMESTAMP
 *  - passed_at      DATETIME NULL
 */
@Data
public class Enrollment {

    private Long enrollmentId;
    private Long userId;
    private Long courseId;

    private BigDecimal progressPercent; // 진도율
    private String status;              // 수강 상태
    private LocalDateTime enrolledAt;   // 신청일시
    private LocalDateTime passedAt;     // 수료일시
}
