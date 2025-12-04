// src/main/java/com/emergency/inquiry/repository/JdbcInquiryRepository.java
package com.emergency.inquiry.repository;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcInquiryRepository implements InquiryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Inquiry> inquiryRowMapper = (rs, rowNum) -> {
        Inquiry i = new Inquiry();
        i.setInquiryId(rs.getLong("inquiry_id"));
        i.setUserId(rs.getLong("user_id"));
        try {
            i.setCategory(rs.getString("category"));
        } catch (SQLException e) {
            // category 컬럼 없으면 무시 (보완 전 호환용)
        }
        i.setTitle(rs.getString("title"));
        i.setContent(rs.getString("content"));
        i.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            i.setCreatedAt(ts.toLocalDateTime());
        }
        return i;
    };

    private final RowMapper<InquiryAnswer> answerRowMapper = (rs, rowNum) -> {
        InquiryAnswer a = new InquiryAnswer();
        a.setInquiryAnswerId(rs.getLong("inquiry_answer_id"));
        a.setInquiryId(rs.getLong("inquiry_id"));
        a.setAdminName(rs.getString("admin_name"));
        a.setAnswerContent(rs.getString("answer_content"));
        Timestamp ts = rs.getTimestamp("answered_at");
        if (ts != null) {
            a.setAnsweredAt(ts.toLocalDateTime());
        }
        return a;
    };

    private final RowMapper<InquiryAttachment> attachmentRowMapper = (rs, rowNum) -> {
        InquiryAttachment att = new InquiryAttachment();
        att.setInquiryAttachmentId(rs.getLong("inquiry_attachment_id"));
        att.setInquiryId(rs.getLong("inquiry_id"));
        att.setFilePath(rs.getString("file_path"));
        return att;
    };

    @Override
    public Long saveInquiry(Inquiry inquiry) {
        String sql = "INSERT INTO inquiries (user_id, category, title, content, status, created_at) " +
                "VALUES (?, ?, ?, ?, '대기', NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, inquiry.getUserId());
            ps.setString(2, inquiry.getCategory());
            ps.setString(3, inquiry.getTitle());
            ps.setString(4, inquiry.getContent());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long id = (key != null ? key.longValue() : 0L);
        inquiry.setInquiryId(id);
        inquiry.setStatus("대기");
        inquiry.setCreatedAt(LocalDateTime.now());
        return id;
    }

    @Override
    public void saveAttachments(Long inquiryId, List<InquiryAttachment> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO inquiry_attachments (inquiry_id, file_path) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, attachments, attachments.size(),
                (ps, att) -> {
                    ps.setLong(1, inquiryId);
                    ps.setString(2, att.getFilePath());
                });
    }

    @Override
    public List<Inquiry> findUserInquiries(Long userId, String category, String keyword,
                                           int offset, int limit) {

        StringBuilder sql = new StringBuilder("SELECT * FROM inquiries WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }

        sql.append(" ORDER BY inquiry_id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), inquiryRowMapper, params.toArray());
    }

    @Override
    public int countUserInquiries(Long userId, String category, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM inquiries WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return (count != null) ? count : 0;
    }

    @Override
    public Optional<Inquiry> findInquiry(Long inquiryId, Long userId) {
        String sql = "SELECT * FROM inquiries WHERE inquiry_id = ? AND user_id = ?";
        List<Inquiry> list = jdbcTemplate.query(sql, inquiryRowMapper, inquiryId, userId);
        return list.stream().findFirst();
    }

    @Override
    public Optional<InquiryAnswer> findAnswerByInquiryId(Long inquiryId) {
        String sql = "SELECT * FROM inquiry_answers WHERE inquiry_id = ?";
        List<InquiryAnswer> list = jdbcTemplate.query(sql, answerRowMapper, inquiryId);
        return list.stream().findFirst();
    }

    @Override
    public List<InquiryAttachment> findAttachmentsByInquiryId(Long inquiryId) {
        String sql = "SELECT * FROM inquiry_attachments WHERE inquiry_id = ?";
        return jdbcTemplate.query(sql, attachmentRowMapper, inquiryId);
    }

    @Override
    public Optional<Long> findPrevId(Long userId, Long currentId) {
        String sql = "SELECT MAX(inquiry_id) FROM inquiries " +
                "WHERE user_id = ? AND inquiry_id < ?";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, userId, currentId);
        return Optional.ofNullable(id);
    }

    @Override
    public Optional<Long> findNextId(Long userId, Long currentId) {
        String sql = "SELECT MIN(inquiry_id) FROM inquiries " +
                "WHERE user_id = ? AND inquiry_id > ?";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, userId, currentId);
        return Optional.ofNullable(id);
    }
}
