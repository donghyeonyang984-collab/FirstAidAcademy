package com.emergency.enrollment.repository.Course;

import com.emergency.enrollment.domain.CourseLecture;

import java.util.List;

/**
 * course_lectures 테이블 조회용 Repository
 *
 * 사용 목적
 *  - 수강신청 시, 해당 강의(course_id)에 어떤 차시들이 있는지 조회하기 위해 사용
 */
public interface CourseLectureRepository {

    /** 특정 강의(course_id)에 속한 모든 차시 목록을 lecture_no 순서로 조회 */
    List<CourseLecture> findByCourseId(Long courseId);
}
