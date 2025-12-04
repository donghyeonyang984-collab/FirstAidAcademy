package com.emergency.adminCourse.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("adminCourseListDao")
@RequiredArgsConstructor
public class AdminCourseListDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAllCourses() {
        String sql = """
            SELECT 
                course_id,
                title,
                top_category,
                mid_category,
                image_path,
                DATE_FORMAT(created_at, '%Y-%m-%d') AS reg_date
            FROM courses
            ORDER BY course_id DESC
        """;

        return jdbcTemplate.queryForList(sql, Map.of());
    }
}
