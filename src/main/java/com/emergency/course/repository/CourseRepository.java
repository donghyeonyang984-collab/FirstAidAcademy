// src/main/java/com/emergency/course/repository/CourseRepository.java
package com.emergency.course.repository;

import com.emergency.course.domain.CourseListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("CoursRepository")
@RequiredArgsConstructor
public class CourseRepository {

    private final JdbcTemplate jdbc;

    /**
     * 강의 목록 페이지
     *
     * @param keyword     검색어 (제목/요약)
     * @param sort        latest / oldest / null
     * @param topCategory 구조자 / 자가 / null
     * @param midCategory 출혈 / 기도막힘 / 심정지 / 화상 / null
     */
    public List<CourseListItem> findPage(String keyword,
                                         String sort,
                                         String topCategory,
                                         String midCategory,
                                         int page,
                                         int size) {

        StringBuilder sb = new StringBuilder("""
            SELECT course_id,
                   title,
                   top_category,
                   mid_category,
                   summary,
                   image_path
            FROM courses
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        // 검색어
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (title LIKE ? OR summary LIKE ?) ");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
        }

        // 탑 카테고리 필터(구조자/자가)
        if (topCategory != null && !topCategory.isBlank()) {
            sb.append(" AND top_category = ? ");
            params.add(topCategory);
        }

        // 미드 카테고리 필터(출혈/기도막힘/심정지/화상)
        if (midCategory != null && !midCategory.isBlank()) {
            sb.append(" AND mid_category = ? ");
            params.add(midCategory);
        }

        // 정렬
        if ("oldest".equalsIgnoreCase(sort)) {
            sb.append(" ORDER BY created_at ASC, course_id ASC ");
        } else {
            // 기본: 최신순
            sb.append(" ORDER BY created_at DESC, course_id DESC ");
        }

        // 페이징
        sb.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add((page - 1) * size);

        return jdbc.query(sb.toString(), params.toArray(), (rs, i) ->
                new CourseListItem(
                        rs.getLong("course_id"),
                        rs.getString("title"),
                        rs.getString("summary"),
                        rs.getString("image_path"),
                        rs.getString("top_category"),
                        rs.getString("mid_category")
                )
        );
    }

    /** 페이징용 전체 개수 (필터 적용 버전) */
    public long count(String keyword,
                      String topCategory,
                      String midCategory) {

        StringBuilder sb = new StringBuilder("""
            SELECT COUNT(*)
            FROM courses
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (title LIKE ? OR summary LIKE ?) ");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
        }

        if (topCategory != null && !topCategory.isBlank()) {
            sb.append(" AND top_category = ? ");
            params.add(topCategory);
        }

        if (midCategory != null && !midCategory.isBlank()) {
            sb.append(" AND mid_category = ? ");
            params.add(midCategory);
        }

        return jdbc.queryForObject(sb.toString(), params.toArray(), Long.class);
    }
}
