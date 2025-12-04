package com.emergency.home.repository;

import com.emergency.home.dto.HomeNoticeItem;
import com.emergency.home.dto.HomePressItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HomeRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<HomePressItem> findLatestPress(int limit) {
        String sql = """
            SELECT press_release_id AS press_id,   -- ✅ 실제 컬럼명에 alias만 줌
                   title,
                   DATE(created_at) AS created_date
            FROM press_releases
            ORDER BY created_at DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new HomePressItem(
                        rs.getLong("press_id"),          // 위에서 AS press_id 로 별칭 줬으니까 그대로 사용
                        rs.getString("title"),
                        rs.getString("created_date")
                ), limit);
    }

    public List<HomeNoticeItem> findLatestNotices(int limit) {
        String sql = """
                SELECT notice_id, title, DATE(created_at) AS created_date
                FROM notices
                ORDER BY created_at DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new HomeNoticeItem(
                        rs.getLong("notice_id"),
                        rs.getString("title"),
                        rs.getString("created_date")
                ), limit);
    }
}
