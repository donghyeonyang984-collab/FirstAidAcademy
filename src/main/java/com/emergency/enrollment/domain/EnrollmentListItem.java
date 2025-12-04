package com.emergency.enrollment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 나의강의실 - 수강목록 화면에서
 * 카드 한 장(한 줄)에 표시할 데이터를 담는 DTO.
 *
 * - enrollments + courses 조인 결과를 담는다.
 * - 컨트롤러(MyEnrollmentController) -> 뷰(courseList.html) 사이에서 사용.
 */
@Data
@AllArgsConstructor
public class EnrollmentListItem {

    /** enrollments.enrollment_id (수강신청 PK) */
    private Long enrollmentId;

    /** courses.course_id (강의 PK) */
    private Long courseId;

    /** 강의 제목 (courses.title) */
    private String title;

    /** 상단 카테고리 (구조자 / 자가) */
    private String topCategory;

    /** 중간 카테고리 (출혈 / 기도막힘 / 심정지 / 화상) */
    private String midCategory;

    /** 강의 요약 설명 (courses.summary) */
    private String summary;

    /** 강의 썸네일 경로 (courses.image_path) */
    private String imagePath;

    /** 진도율 (enrollments.progress_percent) */
    private BigDecimal progressPercent;

    /** 수강 상태 (수강중 / 미수료 / 수료) */
    private String status;

    /** 수강신청 일시 (enrollments.enrolled_at) */
    private LocalDateTime enrolledAt;
}
