package com.emergency.enrollment.service;

import com.emergency.enrollment.repository.Popup.LectureProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 강의 진도(lecture_progress) + 수강(enrollments.progress) 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class LectureProgressService {

    private final LectureProgressRepository lectureProgressRepository;
    private final JdbcTemplate jdbcTemplate;  // enrollments.progress / status 업데이트용

    /**
     * 시청 구간 저장 + 차시 완료 여부 + 전체 진도율 계산
     */
    public void saveProgress(Long enrollmentId,
                             Long lectureId,
                             int watchSec,
                             boolean completed) {

        // 1) 개별 차시 진도 저장
        lectureProgressRepository.updateProgress(enrollmentId, lectureId, watchSec, completed);

        // 2) 이 enrollment의 전체 진도율 계산 (차시 개수 기준)
        int totalCount     = lectureProgressRepository.countByEnrollment(enrollmentId);
        int completedCount = lectureProgressRepository.countCompletedByEnrollment(enrollmentId);

        int progressPercent = 0;
        if (totalCount > 0) {
            progressPercent = (int) Math.round(completedCount * 100.0 / totalCount);
        }

        // 0 ~ 100 범위로 보정
        if (progressPercent < 0) {
            progressPercent = 0;
        } else if (progressPercent > 100) {
            progressPercent = 100;
        }

        // 3) enrollments.progress_percent + status 컬럼에 반영
        if (progressPercent >= 100) {
            // 🔹 진도율이 100% 이상일 때
            //  - progress_percent = 100 저장
            //  - 현재 status 가 '수강중' 이면 '미수료' 로 변경
            //  - 이미 '수료' 인 경우에는 그대로 유지
            String sql = """
                    UPDATE enrollments
                       SET progress_percent = ?,
                           status = CASE
                                       WHEN status = '수강중' THEN '미수료'
                                       ELSE status
                                    END
                     WHERE enrollment_id = ?
                    """;
            jdbcTemplate.update(sql, progressPercent, enrollmentId);
        } else {
            // 🔹 100% 미만일 때는 진도율만 갱신 (상태는 그대로)
            String sql = """
                    UPDATE enrollments
                       SET progress_percent = ?
                     WHERE enrollment_id = ?
                    """;
            jdbcTemplate.update(sql, progressPercent, enrollmentId);
        }
    }

    /**
     * 이어보기용 마지막 시청 위치(초) 반환
     */
    public int getWatchSec(Long enrollmentId, Long lectureId) {
        Integer sec = lectureProgressRepository.findWatchSec(enrollmentId, lectureId);
        return (sec != null) ? sec : 0;
    }
}
