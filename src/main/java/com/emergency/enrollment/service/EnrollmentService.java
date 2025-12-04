package com.emergency.enrollment.service;

import com.emergency.enrollment.domain.*;
import com.emergency.enrollment.repository.Course.CourseLectureRepository;
import com.emergency.enrollment.repository.Enrollment.EnrollmentRepository;
import com.emergency.enrollment.repository.Popup.LectureProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 수강신청 + 수강목록 조회 + 강의 진도 관리 비즈니스 로직 서비스
 *
 * 기능
 *  1) enroll()                        : 수강신청 처리
 *  2) getMyEnrollments(...)           : 나의 수강목록(전체/미수료/수료) 조회
 *  3) updateLectureProgress(...)      : 개별 차시 진도/이어보기 저장   // [추가]
 *  4) getLastWatchSec(...)            : 이어보기용 마지막 시청 위치 조회 // [추가]
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseLectureRepository courseLectureRepository;
    private final LectureProgressRepository lectureProgressRepository;

    /**
     * 수강신청 처리
     *
     * @param userId   users.user_id
     * @param courseId courses.course_id
     * @return 생성된 enrollment_id
     * @throws AlreadyEnrolledException 이미 수강중인 경우
     */
    @Transactional
    public Long enroll(Long userId, Long courseId) {
        // 1) 중복 수강신청 체크
        if (enrollmentRepository.existsByUserAndCourse(userId, courseId)) {
            throw new AlreadyEnrolledException("이미 수강 중인 강의입니다.");
        }

        // 2) enrollments INSERT
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        Long enrollmentId = enrollmentRepository.insert(enrollment);

        // 3) 해당 강의에 속한 모든 차시(course_lectures)를 조회
        List<CourseLecture> lectures = courseLectureRepository.findByCourseId(courseId);

        // 4) 각 차시에 대해 lecture_progress 초기 레코드 생성
        for (CourseLecture lecture : lectures) {
            lectureProgressRepository.insertInitial(enrollmentId, lecture.getCourseLectureId());
        }

        return enrollmentId;
    }

    /**
     * 개별 강의 차시의 시청 구간/완료 여부 저장   // [추가]
     *
     * @param enrollmentId    enrollments.enrollment_id
     * @param courseLectureId course_lectures.course_lecture_id
     * @param watchSec        현재까지 시청한 위치(초)
     * @param completed       강의를 끝까지 시청했는지 여부
     */
    @Transactional // [추가]
    public void updateLectureProgress(Long enrollmentId,
                                      Long courseLectureId,
                                      int watchSec,
                                      boolean completed) {   // [추가]

        // 음수 값 방어                                 // [추가]
        if (watchSec < 0) {                             // [추가]
            watchSec = 0;                               // [추가]
        }                                               // [추가]

        lectureProgressRepository.updateProgress(       // [추가]
                enrollmentId,                           // [추가]
                courseLectureId,                        // [추가]
                watchSec,                               // [추가]
                completed                               // [추가]
        );                                              // [추가]
    }

    /**
     * 이어보기용 마지막 시청 구간(초) 조회            // [추가]
     *
     * @param enrollmentId    enrollments.enrollment_id
     * @param courseLectureId course_lectures.course_lecture_id
     * @return 마지막 시청 위치(초). 데이터 없으면 null
     */
    @Transactional(readOnly = true)                    // [추가]
    public Integer getLastWatchSec(Long enrollmentId,
                                   Long courseLectureId) { // [추가]

        return lectureProgressRepository.findWatchSec( // [추가]
                enrollmentId,                          // [추가]
                courseLectureId                        // [추가]
        );                                             // [추가]
    }

    /**
     * 나의 수강목록 조회
     *
     * @param userId       현재 로그인한 사용자 ID
     * @param statusFilter "" 또는 null  : 전체
     *                     "수강중"      : 수강중만
     *                     "미수료"      : 미수료만
     *                     "수료"        : 수료만
     * @param midCategory  "" 또는 null  : 전체
     *                     그 외(출혈/기도막힘/심정지/화상) : 해당 카테고리만
     */
    @Transactional(readOnly = true)
    public List<EnrollmentListItem> getMyEnrollments(Long userId,
                                                     String statusFilter,
                                                     String midCategory) {
        return enrollmentRepository.findMyEnrollments(userId, statusFilter, midCategory);
    }
    /*나의 학습활동 추가 부분 */
    public MyStudyStatusCount getMyStudyStatusCount(Long userId) {
        return enrollmentRepository.countMyStudyStatus(userId);
    }
    /** 나의 학습활동 - 강의 이어 보기 리스트 (최신 순 최대 3개) */
    public List<MyStudyCourseItem> getMyOngoingCoursesForMyStudy(Long userId) {
        int limit = 3; // 카드 몇 개 보여줄지
        return enrollmentRepository.findMyOngoingCourses(userId, limit);
    }

    public boolean isMyEnrollment(Long enrollmentId, Long userId) {
        Enrollment e = enrollmentRepository.findById(enrollmentId);
        return e != null && e.getUserId().equals(userId);
    }
}
