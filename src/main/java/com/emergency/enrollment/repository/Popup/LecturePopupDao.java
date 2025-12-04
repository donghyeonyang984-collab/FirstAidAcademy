package com.emergency.enrollment.repository.Popup;

import com.emergency.enrollment.domain.LecturePopupLecture;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("PopupRepository")
@RequiredArgsConstructor
public class LecturePopupDao {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<LecturePopupLecture> lectureRowMapper() {
        return (rs, rowNum) -> {
            LecturePopupLecture lec = new LecturePopupLecture();
            lec.setCourseLectureId(rs.getLong("course_lecture_id"));
            lec.setCourseId(rs.getLong("course_id"));
            lec.setLectureNo(rs.getInt("lecture_no"));
            lec.setTitle(rs.getString("title"));
            lec.setVideoUrl(rs.getString("video_url"));

            // information 제거
            // lec.setInformation(...) X

            lec.setDurationSec(
                    rs.getObject("duration_sec") != null
                            ? rs.getInt("duration_sec")
                            : null
            );
            lec.setLastWatchSec(rs.getInt("last_watch_sec"));

            // 과정(코스) 정보
            lec.setCourseTitle(rs.getString("course_title"));
            lec.setTopCategory(rs.getString("top_category"));
            lec.setMidCategory(rs.getString("mid_category"));

            // ✨ courses.summary 컬럼을 summary 필드로 매핑
            lec.setSummary(rs.getString("summary"));

            return lec;
        };
    }

    public List<LecturePopupLecture> findLectures(Long enrollmentId, Long courseId) {
        String sql = """
                SELECT
                    cl.course_lecture_id,
                    cl.course_id,
                    cl.lecture_no,
                    cl.title,
                    cl.video_url,
                    cl.duration_sec,
                    COALESCE(lp.watch_sec, 0) AS last_watch_sec,
                    c.title        AS course_title,
                    c.top_category AS top_category,
                    c.mid_category AS mid_category,
                    c.summary      AS summary   -- ✨ 여기!
                FROM course_lectures cl
                JOIN courses c
                  ON c.course_id = cl.course_id
                LEFT JOIN lecture_progress lp
                  ON lp.course_lecture_id = cl.course_lecture_id
                 AND lp.enrollment_id     = ?
                WHERE cl.course_id = ?
                ORDER BY cl.lecture_no ASC
                """;

        return jdbcTemplate.query(sql, lectureRowMapper(), enrollmentId, courseId);
    }
}
