// src/main/java/com/emergency/enrollment/repository/Popup/JdbcLectureProgressRepository.java
package com.emergency.enrollment.repository.Popup;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * lecture_progress 테이블에 대한 JdbcTemplate 구현체
 */
@Repository("ProgressRepository")
@RequiredArgsConstructor
public class JdbcLectureProgressRepository implements LectureProgressRepository {

    private final JdbcTemplate jdbc;

    @Override
    public void insertInitial(Long enrollmentId, Long courseLectureId) {
        String sql = """
                INSERT INTO lecture_progress
                    (enrollment_id, course_lecture_id, watch_sec, completed)
                VALUES (?, ?, 0, 0)
                """;
        jdbc.update(sql, enrollmentId, courseLectureId);
    }

    @Override
    public void updateProgress(Long enrollmentId,
                               Long courseLectureId,
                               int watchSec,
                               boolean completed) {

        int completedValue = completed ? 1 : 0;

        // 시청 위치는 기존 값보다 뒤로만 갱신
        // completed 는 한 번 1이면 다시 0으로 떨어지지 않도록 CASE 처리
        String sql = """
                UPDATE lecture_progress
                   SET watch_sec = GREATEST(IFNULL(watch_sec, 0), ?),
                       completed = CASE
                                       WHEN completed = 1 THEN 1
                                       ELSE ?
                                   END
                 WHERE enrollment_id     = ?
                   AND course_lecture_id = ?
                """;

        int updated = jdbc.update(sql, watchSec, completedValue, enrollmentId, courseLectureId);

        // row 가 없으면 처음 생성
        if (updated == 0) {
            String insertSql = """
                    INSERT INTO lecture_progress
                        (enrollment_id, course_lecture_id, watch_sec, completed)
                    VALUES (?, ?, ?, ?)
                    """;
            jdbc.update(insertSql, enrollmentId, courseLectureId, watchSec, completedValue);
        }
    }

    @Override
    public Integer findWatchSec(Long enrollmentId, Long courseLectureId) {
        String sql = """
                SELECT watch_sec
                  FROM lecture_progress
                 WHERE enrollment_id     = ?
                   AND course_lecture_id = ?
                """;

        return jdbc.query(sql,
                        (rs, rowNum) -> rs.getInt("watch_sec"),
                        enrollmentId, courseLectureId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public int countByEnrollment(Long enrollmentId) {
        String sql = """
                SELECT COUNT(*) AS cnt
                  FROM lecture_progress
                 WHERE enrollment_id = ?
                """;
        Integer cnt = jdbc.queryForObject(sql, Integer.class, enrollmentId);
        return (cnt != null) ? cnt : 0;
    }

    @Override
    public int countCompletedByEnrollment(Long enrollmentId) {
        String sql = """
                SELECT COUNT(*) AS cnt
                  FROM lecture_progress
                 WHERE enrollment_id = ?
                   AND completed = 1
                """;
        Integer cnt = jdbc.queryForObject(sql, Integer.class, enrollmentId);
        return (cnt != null) ? cnt : 0;
    }
}
