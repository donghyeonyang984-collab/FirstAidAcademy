package com.emergency.notice.repository;

import com.emergency.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("noticeRepository")  // 또는 "userNoticeRepository" 같은 이름도 OK
@RequiredArgsConstructor
public class NoticeRepository {

    private final JdbcTemplate jdbc;

    // 공통 RowMapper
    private final RowMapper<Notice> noticeRowMapper = (rs, rowNum) -> {
        Notice n = new Notice();
        n.setNoticeId(rs.getLong("notice_id"));
        n.setTitle(rs.getString("title"));
        n.setContentHtml(rs.getString("content_html"));
        n.setUserId(rs.getLong("user_id"));

        var ts = rs.getTimestamp("created_at");
        if (ts != null) {
            n.setCreatedAt(ts.toLocalDateTime());
        }

        // users.name AS writer_name
        n.setWriterName(rs.getString("writer_name"));
        return n;
    };

    /** 페이지 목록 조회 (제목 검색 + 페이징) */
    public List<Notice> findPage(int page, int size, String keyword) {

        StringBuilder sb = new StringBuilder("""
            SELECT n.notice_id,
                   n.title,
                   n.user_id,
                   n.content_html,
                   n.created_at,
                   u.name AS writer_name
              FROM notices n
              LEFT JOIN users u ON u.user_id = n.user_id
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" WHERE n.title LIKE ? ");
            params.add("%" + keyword + "%");
        }

        sb.append(" ORDER BY n.created_at DESC, n.notice_id DESC ");
        sb.append(" LIMIT ? OFFSET ? ");

        params.add(size);
        params.add((page - 1) * size);

        return jdbc.query(sb.toString(), params.toArray(), noticeRowMapper);
    }

    /** 검색 포함 전체 개수 */
    public int count(String keyword) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM notices");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" WHERE title LIKE ? ");
            params.add("%" + keyword + "%");
        }

        Integer result = jdbc.queryForObject(sb.toString(), params.toArray(), Integer.class);
        return (result != null) ? result : 0;
    }

    /** 단건 조회 */
    public Optional<Notice> findById(Long id) {
        String sql = """
            SELECT n.notice_id,
                   n.title,
                   n.user_id,
                   n.content_html,
                   n.created_at,
                   u.name AS writer_name
              FROM notices n
              LEFT JOIN users u ON u.user_id = n.user_id
             WHERE n.notice_id = ?
            """;
        List<Notice> list = jdbc.query(sql, new Object[]{id}, noticeRowMapper);
        return list.stream().findFirst();
    }

    /** 저장 */
    public Long save(Notice notice) {
        String sql = """
            INSERT INTO notices (title, user_id, content_html)
            VALUES (?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, notice.getTitle());
            if (notice.getUserId() != null) {
                ps.setLong(2, notice.getUserId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, notice.getContentHtml());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return (key != null) ? key.longValue() : null;
    }

    /** 수정 */
    public void update(Notice notice) {
        String sql = """
            UPDATE notices
               SET title = ?,
                   content_html = ?
             WHERE notice_id = ?
            """;
        jdbc.update(sql,
                notice.getTitle(),
                notice.getContentHtml(),
                notice.getNoticeId());
    }

    /** 삭제 */
    public void delete(Long id) {
        jdbc.update("DELETE FROM notices WHERE notice_id = ?", id);
    }

    /**
     * 이전글: 목록에서 "바로 위" 글
     * - 공지 목록이 notice_id DESC(번호 큰 게 최신, 위에 있음) 기준이라고 가정
     * - 현재 글보다 번호가 큰 것 중에서 가장 작은 ID
     *   예) 목록: 10, 9, 8, 7
     *       현재: 9 -> 이전글: 10
     */
    public Optional<Long> findPrevId(Long id) {
        String sql = "SELECT MIN(notice_id) FROM notices WHERE notice_id > ?";
        Long prevId = jdbc.queryForObject(sql, Long.class, id);
        return Optional.ofNullable(prevId);
    }

    /**
     * 다음글: 목록에서 "바로 아래" 글
     * - 현재 글보다 번호가 작은 것 중에서 가장 큰 ID
     *   예) 목록: 10, 9, 8, 7
     *       현재: 9 -> 다음글: 8
     */
    public Optional<Long> findNextId(Long id) {
        String sql = "SELECT MAX(notice_id) FROM notices WHERE notice_id < ?";
        Long nextId = jdbc.queryForObject(sql, Long.class, id);
        return Optional.ofNullable(nextId);
    }

}
