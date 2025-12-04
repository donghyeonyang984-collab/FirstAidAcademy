package com.emergency.enrollment.domain;

import lombok.Data;

/**
 * course_lectures 테이블 매핑용 도메인 객체
 *
 * 테이블 구조 (요약)
 *  - course_lecture_id BIGINT PK AI
 *  - course_id         BIGINT
 *  - lecture_no        TINYINT  (1강, 2강...)
 *  - title             VARCHAR(200)
 *  - video_url         VARCHAR(500)
 *  - information       TEXT
 *  - duration_sec      INT NULL
 */
@Data
public class CourseLecture {

    private Long courseLectureId;
    private Long courseId;
    private int lectureNo;
    private String title;
    private String videoUrl;
    private String information;
    private Integer durationSec;
}
