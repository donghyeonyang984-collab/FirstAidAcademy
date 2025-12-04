package com.emergency.adminCourse.Dao;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("adminCourseAddDao")
@RequiredArgsConstructor
public class AdminCourseAddDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // 1️⃣ 강의 기본정보 저장
    public Long insertCourse(Map<String, Object> course) {
        String sql = """
            INSERT INTO courses (title, top_category, mid_category, summary, image_path, created_at, updated_at)
            VALUES (:title, :top_category, :mid_category, :summary, :image_path, NOW(), NOW())
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(course), keyHolder, new String[]{"course_id"});
        
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("강의 등록에 실패했습니다. Generated key를 가져올 수 없습니다.");
        }
        return key.longValue();
    }

    // 2️⃣ 영상 정보 저장
    public void insertLecture(Map<String, Object> lecture) {
        String sql = """
            INSERT INTO course_lectures (course_id, lecture_no, title, video_url, information, duration_sec)
            VALUES (:course_id, :lecture_no, :title, :video_url, :information, :duration_sec)
        """;
        jdbcTemplate.update(sql, lecture);
    }
}
