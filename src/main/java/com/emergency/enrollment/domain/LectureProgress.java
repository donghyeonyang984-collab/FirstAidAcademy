package com.emergency.enrollment.domain;

import lombok.Data;

/**
 * lecture_progress 테이블 매핑용 도메인 객체
 *
 * 테이블 구조 (요약)
 *  - lecture_progress_id BIGINT PK AI
 *  - enrollment_id       BIGINT
 *  - course_lecture_id   BIGINT
 *  - watch_sec           INT DEFAULT 0
 *  - completed           TINYINT(1) DEFAULT 0
 */
@Data
public class LectureProgress {

    private Long lectureProgressId;
    private Long enrollmentId;
    private Long courseLectureId;
    private Integer watchSec;
    private boolean completed;
}
