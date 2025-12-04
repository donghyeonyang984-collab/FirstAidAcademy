package com.emergency.adminCourse.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("adminCourseEditDao")
@RequiredArgsConstructor
public class AdminCourseEditDao {

    private final NamedParameterJdbcTemplate jdbc;

    public Map<String, Object> findCourseById(Long id) {
        String sql = "SELECT course_id, title, top_category, mid_category, summary, image_path, DATE_FORMAT(created_at, '%Y-%m-%d') AS reg_date FROM courses WHERE course_id = :id";
        return jdbc.queryForMap(sql, Map.of("id", id));
    }

    public List<Map<String, Object>> findLectures(Long courseId) {
        String sql = "SELECT course_id, lecture_no, title, video_url, information, duration_sec FROM course_lectures WHERE course_id = :cid ORDER BY lecture_no";
        return jdbc.queryForList(sql, Map.of("cid", courseId));
    }

    public Map<String, Object> findLecture(Long cid, int no) {
        String sql = "SELECT * FROM course_lectures WHERE course_id = :cid AND lecture_no = :no";
        return jdbc.queryForMap(sql, Map.of("cid", cid, "no", no));
    }

    public void updateCourse(Map<String, Object> course) {
        String sql = """
                UPDATE courses
                SET title = :title,
                    top_category = :top_category,
                    mid_category = :mid_category,
                    summary = :summary,
                    image_path = :image_path,
                    updated_at = NOW()
                WHERE course_id = :course_id
                """;
        jdbc.update(sql, new MapSqlParameterSource(course));
    }

    public void updateLecture(Map<String, Object> lec) {
        String sql = """
                UPDATE course_lectures
                SET title = :title,
                    video_url = :video_url,
                    information = :information,
                    duration_sec = :duration_sec
                WHERE course_id = :course_id AND lecture_no = :lecture_no
                """;
        jdbc.update(sql, new MapSqlParameterSource(lec));
    }
    // 삭제 보류
//    public int deleteCourse(long courseId) {
//
//        // 1) 해당 강의의 차시 삭제
//        String sql1 = "DELETE FROM course_lectures WHERE course_id = :cid";
//        jdbc.update(sql1, Map.of("cid", courseId));
//
//        // 2) 강의 삭제
//        String sql2 = "DELETE FROM courses WHERE course_id = :cid";
//        return jdbc.update(sql2, Map.of("cid", courseId));
//    }

}
