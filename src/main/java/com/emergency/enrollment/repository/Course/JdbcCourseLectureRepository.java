package com.emergency.enrollment.repository.Course;

import com.emergency.enrollment.domain.CourseLecture;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * course_lectures 테이블에 대한 JdbcTemplate 구현체
 */
@Repository("CourseLectureRepository")
@RequiredArgsConstructor
public class JdbcCourseLectureRepository implements CourseLectureRepository {

    private final JdbcTemplate jdbc;

    @Override
    public List<CourseLecture> findByCourseId(Long courseId) {
        String sql = """
                SELECT course_lecture_id,
                       course_id,
                       lecture_no,
                       title,
                       video_url,
                       information,
                       duration_sec
                  FROM course_lectures
                 WHERE course_id = ?
                 ORDER BY lecture_no ASC
                """;

        return jdbc.query(sql, (rs, rowNum) -> {
            CourseLecture lecture = new CourseLecture();
            lecture.setCourseLectureId(rs.getLong("course_lecture_id"));
            lecture.setCourseId(rs.getLong("course_id"));
            lecture.setLectureNo(rs.getInt("lecture_no"));
            lecture.setTitle(rs.getString("title"));
            lecture.setVideoUrl(rs.getString("video_url"));
            lecture.setInformation(rs.getString("information"));
            lecture.setDurationSec((Integer) rs.getObject("duration_sec"));
            return lecture;
        }, courseId);
    }
}
