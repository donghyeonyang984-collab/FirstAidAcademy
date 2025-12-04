// src/main/java/com/emergency/adminInquiry/repository/AdminInquiryRepository.java
package com.emergency.adminInquiry.repository;

import com.emergency.adminInquiry.service.dto.AdminInquiryListItem;
import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;
import com.emergency.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminInquiryRepository {

    private final JdbcTemplate jdbcTemplate;

    /* ===================== RowMapper ===================== */

    private RowMapper<Inquiry> inquiryRowMapper() {
        return (rs, rowNum) -> {
            Inquiry i = new Inquiry();
            i.setInquiryId(rs.getLong("inquiry_id"));
            i.setUserId(rs.getLong("user_id"));
            i.setCategory(rs.getString("category"));
            i.setTitle(rs.getString("title"));
            i.setContent(rs.getString("content"));
            i.setStatus(rs.getString("status"));

            Timestamp c = rs.getTimestamp("created_at");
            if (c != null) {
                i.setCreatedAt(c.toLocalDateTime());
            }
            // updated_at 컬럼이 없으므로 여기서는 세팅 X
            return i;
        };
    }

    private RowMapper<InquiryAttachment> attachmentRowMapper() {
        return (rs, rowNum) -> {
            InquiryAttachment a = new InquiryAttachment();
            a.setInquiryId(rs.getLong("inquiry_id"));
            a.setFilePath(rs.getString("file_path"));
            // inquiry_attachment_id, created_at 등은 도메인에 없다고 가정
            return a;
        };
    }

    private RowMapper<InquiryAnswer> answerRowMapper() {
        return (rs, rowNum) -> {
            InquiryAnswer a = new InquiryAnswer();
            a.setInquiryId(rs.getLong("inquiry_id"));
            a.setAdminName(rs.getString("admin_name"));
            a.setAnswerContent(rs.getString("answer_content"));
            // answered_at 컬럼은 필요하면 도메인에 추가해서 매핑
            return a;
        };
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User u = new User();
            u.setUserId(rs.getLong("user_id"));
            u.setUsername(rs.getString("username"));
            u.setName(rs.getString("name"));

            Timestamp c = rs.getTimestamp("created_at");
            if (c != null) u.setCreatedAt(c.toLocalDateTime());
            Timestamp up = rs.getTimestamp("updated_at");
            if (up != null) u.setUpdatedAt(up.toLocalDateTime());
            return u;
        };
    }

    /* ===================== 목록/카운트 ===================== */

    public int countInquiries(String status, String keyword) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM inquiries WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sb.append(" AND status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }

        return jdbcTemplate.queryForObject(sb.toString(), Integer.class, params.toArray());
    }

    public List<AdminInquiryListItem> findInquiries(String status,
                                                    String keyword,
                                                    int offset,
                                                    int size) {

        StringBuilder sb = new StringBuilder(
                "SELECT i.inquiry_id, i.title, i.status, i.created_at, " +
                        "u.name, u.username " +
                        "FROM inquiries i " +
                        "JOIN users u ON i.user_id = u.user_id " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sb.append(" AND i.status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND i.title LIKE ?");
            params.add("%" + keyword + "%");
        }

        sb.append(" ORDER BY i.inquiry_id DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        return jdbcTemplate.query(sb.toString(), (rs, rowNum) -> {
            AdminInquiryListItem item = new AdminInquiryListItem();
            item.setInquiryId(rs.getLong("inquiry_id"));
            item.setTitle(rs.getString("title"));
            item.setStatus(rs.getString("status"));
            Timestamp c = rs.getTimestamp("created_at");
            if (c != null) item.setCreatedAt(c.toLocalDateTime());
            item.setUserName(rs.getString("name"));
            item.setUsername(rs.getString("username"));
            return item;
        }, params.toArray());
    }

    /* ===================== 단건/첨부/답변/회원 ===================== */

    public Optional<Inquiry> findInquiryById(Long inquiryId) {
        String sql = "SELECT * FROM inquiries WHERE inquiry_id = ?";
        List<Inquiry> list = jdbcTemplate.query(sql, inquiryRowMapper(), inquiryId);
        return list.stream().findFirst();
    }

    public List<InquiryAttachment> findAttachmentsByInquiryId(Long inquiryId) {
        // ✅ 실제 컬럼명 inquiry_attachment_id 사용
        String sql = "SELECT * FROM inquiry_attachments " +
                "WHERE inquiry_id = ? " +
                "ORDER BY inquiry_attachment_id";
        return jdbcTemplate.query(sql, attachmentRowMapper(), inquiryId);
    }

    public Optional<InquiryAnswer> findAnswerByInquiryId(Long inquiryId) {
        String sql = "SELECT * FROM inquiry_answers WHERE inquiry_id = ?";
        List<InquiryAnswer> list = jdbcTemplate.query(sql, answerRowMapper(), inquiryId);
        return list.stream().findFirst();
    }

    public Optional<User> findUserByInquiryId(Long inquiryId) {
        String sql = "SELECT u.* FROM inquiries i " +
                "JOIN users u ON i.user_id = u.user_id " +
                "WHERE i.inquiry_id = ?";
        List<User> list = jdbcTemplate.query(sql, userRowMapper(), inquiryId);
        return list.stream().findFirst();
    }

    /* ===================== 답변 저장/수정 + 상태 변경 ===================== */

    public void saveOrUpdateAnswer(Long inquiryId, String adminName, String answerContent) {

        // 현재 해당 문의의 답변 존재 여부 확인
        String countSql = "SELECT COUNT(*) FROM inquiry_answers WHERE inquiry_id = ?";
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, inquiryId);
        if (count == null) count = 0;

        if (count > 0) {
            // ✅ 이미 답변이 있으면 UPDATE
            String updateSql =
                    "UPDATE inquiry_answers " +
                            "SET admin_name = ?, answer_content = ?, answered_at = ? " +
                            "WHERE inquiry_id = ?";
            jdbcTemplate.update(
                    updateSql,
                    adminName,
                    answerContent,
                    Timestamp.valueOf(LocalDateTime.now()),
                    inquiryId
            );
        } else {
            // ✅ 없으면 INSERT
            String insertSql =
                    "INSERT INTO inquiry_answers (inquiry_id, admin_name, answer_content, answered_at) " +
                            "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(
                    insertSql,
                    inquiryId,
                    adminName,
                    answerContent,
                    Timestamp.valueOf(LocalDateTime.now())
            );
        }

        // ✅ 문의 상태 '답변완료' 로 변경 (inquiries 테이블에는 updated_at 없음)
        String statusSql = "UPDATE inquiries SET status = '답변완료' WHERE inquiry_id = ?";
        jdbcTemplate.update(statusSql, inquiryId);
    }
}
