package com.emergency.enrollment.repository.Popup;

/**
 * lecture_progress 테이블 접근용 Repository
 *
 *  - 수강신청 시 초기 진도 생성
 *  - 강의 시청 중/종료 시 구간/완료 여부 저장
 *  - 이어보기용 시청 위치 조회
 *  - 수강 진도율 계산(차시 수 기준)
 */
public interface LectureProgressRepository {

    /** 단일 차시에 대한 초기 진도 row INSERT */
    void insertInitial(Long enrollmentId, Long courseLectureId);

    /** 시청 구간 및 완료 여부 UPDATE (이어보기/진도 저장용) */
    void updateProgress(Long enrollmentId,
                        Long courseLectureId,
                        int watchSec,
                        boolean completed);

    /** 이어보기용 마지막 시청 구간(초) 조회. 없으면 null 반환 */
    Integer findWatchSec(Long enrollmentId,
                         Long courseLectureId);

    /** 해당 수강(enrollment)에 포함된 전체 차시 수 */
    int countByEnrollment(Long enrollmentId);

    /** 해당 수강(enrollment)에서 completed = 1 인 차시 수 */
    int countCompletedByEnrollment(Long enrollmentId);
}
