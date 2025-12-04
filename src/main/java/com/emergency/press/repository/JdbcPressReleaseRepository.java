package com.emergency.press.repository;

import com.emergency.press.domain.PressRelease;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("pressRepository")
@RequiredArgsConstructor
public class JdbcPressReleaseRepository implements PressReleaseRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 공통 RowMapper
     */
    private RowMapper<PressRelease> pressRowMapper() {
        return (rs, rowNum) -> {
            PressRelease p = new PressRelease();
            p.setPressReleaseId(rs.getLong("press_release_id"));
            p.setTitle(rs.getString("title"));
            p.setUserId(rs.getLong("user_id"));
            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                p.setCreatedAt(ts.toLocalDateTime());
            }
            p.setContentHtml(rs.getString("content_html"));
            p.setLinkUrl(rs.getString("link_url"));
            return p;
        };
    }

    /**
     * 전체 개수(검색 포함)
     */
    @Override
    public int count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM press_releases ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("WHERE title LIKE ? ");
            params.add("%" + keyword + "%");
        }

        Integer count = jdbcTemplate.queryForObject(
                sql.toString(),
                params.toArray(),
                Integer.class
        );
        return (count != null) ? count : 0;
    }

    /**
     * 목록 + 검색 + 페이징 (offset, size 사용)
     */
    @Override
    public List<PressRelease> findPage(int offset, int size, String keyword) {

        // 🔒 여기서 한 번 더 방어: 음수 offset/size 절대 안 나가게
        if (offset < 0) {
            offset = 0;
        }
        if (size < 1) {
            size = 10;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT press_release_id, title, user_id, created_at, content_html, link_url " +
                        "FROM press_releases "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("WHERE title LIKE ? ");
            params.add("%" + keyword + "%");
        }

        sql.append("ORDER BY press_release_id DESC ");
        sql.append("LIMIT ? OFFSET ?");

        // LIMIT ?, OFFSET ? 순서대로 바인딩
        params.add(size);   // LIMIT size
        params.add(offset); // OFFSET offset

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                pressRowMapper()
        );
    }

    /**
     * 단건 조회
     */
    @Override
    public Optional<PressRelease> findById(Long id) {
        String sql = "SELECT press_release_id, title, user_id, created_at, content_html, link_url " +
                "FROM press_releases " +
                "WHERE press_release_id = ?";

        List<PressRelease> result = jdbcTemplate.query(
                sql,
                pressRowMapper(),
                id
        );

        return result.stream().findFirst();
    }

    /**
     * 저장(INSERT)
     */
    @Override
    public Long save(PressRelease press) {
        String sql = "INSERT INTO press_releases " +
                "(title, user_id, created_at, content_html, link_url) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, press.getTitle());
            ps.setLong(2, press.getUserId());
            LocalDateTime createdAt = press.getCreatedAt();
            ps.setTimestamp(3, createdAt != null ? Timestamp.valueOf(createdAt) : null);
            ps.setString(4, press.getContentHtml());
            ps.setString(5, press.getLinkUrl());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            Long generatedId = key.longValue();
            press.setPressReleaseId(generatedId);
            return generatedId;
        }
        return null;
    }

    /**
     * 수정(UPDATE)
     */
    @Override
    public void update(PressRelease press) {
        String sql = "UPDATE press_releases " +
                "SET title = ?, user_id = ?, content_html = ?, link_url = ? " +
                "WHERE press_release_id = ?";

        jdbcTemplate.update(
                sql,
                press.getTitle(),
                press.getUserId(),
                press.getContentHtml(),
                press.getLinkUrl(),
                press.getPressReleaseId()
        );
    }

    /**
     * 삭제(DELETE)
     */
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM press_releases WHERE press_release_id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * 현재 글보다 '새로운 글(아이디 큰 것)' 하나 – 보통 '이전글'로 사용
     */
    public Optional<PressRelease> findPrev(Long currentId) {
        String sql =
                "SELECT pr.press_release_id, pr.title, pr.user_id, " +
                        "       pr.created_at, pr.content_html, pr.link_url, " +
                        "       u.name AS writer_name " +
                        "  FROM press_releases pr " +
                        "  LEFT JOIN users u ON pr.user_id = u.user_id " +
                        " WHERE pr.press_release_id > ? " +
                        " ORDER BY pr.press_release_id ASC " +
                        " LIMIT 1";

        List<PressRelease> result =
                jdbcTemplate.query(sql, pressRowMapper(), currentId);

        return result.stream().findFirst();
    }

    /**
     * 현재 글보다 '오래된 글(아이디 작은 것)' 하나 – 보통 '다음글'로 사용
     */
    public Optional<PressRelease> findNext(Long currentId) {
        String sql =
                "SELECT pr.press_release_id, pr.title, pr.user_id, " +
                        "       pr.created_at, pr.content_html, pr.link_url, " +
                        "       u.name AS writer_name " +
                        "  FROM press_releases pr " +
                        "  LEFT JOIN users u ON pr.user_id = u.user_id " +
                        " WHERE pr.press_release_id < ? " +
                        " ORDER BY pr.press_release_id DESC " +
                        " LIMIT 1";

        List<PressRelease> result =
                jdbcTemplate.query(sql, pressRowMapper(), currentId);

        return result.stream().findFirst();
    }
}
