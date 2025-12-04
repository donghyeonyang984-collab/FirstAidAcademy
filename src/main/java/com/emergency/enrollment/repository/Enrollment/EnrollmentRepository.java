package com.emergency.enrollment.repository.Enrollment;

import com.emergency.enrollment.domain.Enrollment;
import com.emergency.enrollment.domain.EnrollmentListItem;
import com.emergency.enrollment.domain.MyStudyCourseItem;
import com.emergency.enrollment.domain.MyStudyStatusCount;

import java.util.List;

/**
 * enrollments 테이블 접근용 Repository
 *
 * 역할
 *  1) 수강신청 중복체크/INSERT (수강신청 기능)
 *  2) 나의 수강목록 조회 (수강목록/미수료/수료 페이지)
 */
public interface EnrollmentRepository {

    /** 같은 사용자가 같은 강의를 이미 수강 중인지 여부 반환 */
    boolean existsByUserAndCourse(Long userId, Long courseId);

    /**
     * 새 수강신청 INSERT.
     *  - progress_percent, status, enrolled_at 은 DB 기본값 사용.
     *  - 생성된 PK(enrollment_id)를 반환.
     */
    Long insert(Enrollment enrollment);

    /**
     * 나의 수강목록 조회용 메서드
     *
     * @param userId       users.user_id
     * @param statusFilter null 또는 "" 이면 상태 전체,
     *                     "수강중" / "미수료" / "수료" 중 하나면 해당 상태만
     * @param midCategory  출혈/기도막힘/심정지/화상 필터 (null 또는 "" 이면 전체)
     */
    List<EnrollmentListItem> findMyEnrollments(Long userId,
                                               String statusFilter,
                                               String midCategory);
    // === [추가] 진도율 계산/조회용 ===

    /** enrollment_id 기준 단일 수강신청 조회 */
    Enrollment findById(Long enrollmentId);

    /** 진도율(progress_percent)만 업데이트 */
    void updateProgressPercent(Long enrollmentId, java.math.BigDecimal progressPercent);

    /*나의 학습활동 추가부분*/
    MyStudyStatusCount countMyStudyStatus(Long userId);

    /** 나의 학습활동 - 강의 이어보기 리스트 (수강중 강의) */
    List<MyStudyCourseItem> findMyOngoingCourses(Long userId, int limit);
}
