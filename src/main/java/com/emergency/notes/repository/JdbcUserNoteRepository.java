package com.emergency.notes.repository;

import com.emergency.notes.domain.UserNote;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository( "userNoteRepository")
@RequiredArgsConstructor
public class JdbcUserNoteRepository implements com.emergency.notes.repository.UserNoteRepository {

    private final JdbcTemplate jdbc;

    private RowMapper<UserNote> userNoteRowMapper() {
        return new RowMapper<>() {
            @Override
            public UserNote mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserNote note = new UserNote();
                note.setUserNoteId(rs.getLong("user_note_id"));
                note.setUserId(rs.getLong("user_id"));
                note.setCourseId(rs.getLong("course_id"));
                note.setCourseLectureId(rs.getLong("course_lecture_id"));
                note.setContent(rs.getString("content"));

                // created_at / updated_at 은 NULL일 수도 있으니 체크
                if (rs.getTimestamp("created_at") != null) {
                    note.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_at") != null) {
                    note.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }

                return note;
            }
        };
    }

    @Override
    public Optional<UserNote> findByUserAndLecture(Long userId,
                                                   Long courseId,
                                                   Long courseLectureId) {

        String sql = """
                SELECT user_note_id, user_id, course_id, course_lecture_id,
                       content, created_at, updated_at
                  FROM user_notes
                 WHERE user_id = ?
                   AND course_id = ?
                   AND course_lecture_id = ?
                """;

        List<UserNote> list = jdbc.query(sql,
                userNoteRowMapper(),
                userId, courseId, courseLectureId);

        return list.stream().findFirst();
    }

    @Override
    public void upsert(Long userId,
                       Long courseId,
                       Long courseLectureId,
                       String content) {

        // 1) 먼저 UPDATE 시도 (이미 메모가 있을 때)
        String updateSql = """
                UPDATE user_notes
                   SET content = ?
                 WHERE user_id = ?
                   AND course_id = ?
                   AND course_lecture_id = ?
                """;

        int updated = jdbc.update(updateSql,
                content, userId, courseId, courseLectureId);

        // 2) 없으면 INSERT (최초 작성)
        if (updated == 0) {
            String insertSql = """
                    INSERT INTO user_notes
                        (user_id, course_id, course_lecture_id, content)
                    VALUES (?, ?, ?, ?)
                    """;

            jdbc.update(insertSql,
                    userId, courseId, courseLectureId, content);
        }
    }
}
