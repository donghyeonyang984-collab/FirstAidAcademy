package com.emergency.adminuser.repository;

import com.emergency.adminuser.web.dto.AdminUserCourseDto;
import com.emergency.adminuser.web.dto.AdminUserGameDto;
import com.emergency.adminuser.web.dto.AdminUserListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminUserRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * role 이 'User' 인 회원만 조회
     */
    public List<AdminUserListDto> findAllNormalUsers() {
        String sql = """
                SELECT
                    user_id,
                    username,
                    name,
                    email,
                    phone,
                    birthdate,
                    address
                FROM users
                WHERE role = 'User'
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs));
    }

    private AdminUserListDto mapUser(ResultSet rs) throws SQLException {
        AdminUserListDto dto = new AdminUserListDto();
        dto.setUserNo(rs.getLong("user_id"));          // PK
        dto.setUserId(rs.getString("username"));       // 로그인 아이디
        dto.setUserName(rs.getString("name"));
        dto.setUserEmail(rs.getString("email"));
        dto.setUserPhone(rs.getString("phone"));

        LocalDate birth = rs.getObject("birthdate", LocalDate.class);
        dto.setUserBirth(birth);

        dto.setUserAddr(rs.getString("address"));
        return dto;
    }

    /**
     * 특정 회원의 수강 정보 조회
     * 아래 쿼리는 예시이므로, 실제 enrollments / courses / certificates
     * 테이블 컬럼명에 맞게 필요하면 수정해서 사용해줘.
     */
    public List<AdminUserCourseDto> findCoursesByUserId(Long userId) {
        String sql = """
            SELECT
                e.enrollment_id        AS courseNo,       -- 수강신청 PK
                c.title                AS courseTitle,    -- 강의명
                DATE(e.enrolled_at)    AS courseStart,    -- 수강 시작일
                DATE(e.passed_at)      AS courseEnd,      -- 수료일(있으면)
                e.progress_percent     AS progress,       -- 진도율(0~100)
                s.max_score            AS score,          -- 시험 최고 점수
                e.status               AS status,         -- '수강중','미수료','수료'
                cert.certificate_id    AS certificateId   -- 이수증 존재 여부
            FROM enrollments e
                JOIN courses c
                    ON e.course_id = c.course_id
                -- 사용자 + 과정별 시험 최고 점수
                LEFT JOIN (
                    SELECT
                        ea.user_id,
                        ex.course_id,
                        MAX(ea.score) AS max_score
                    FROM exam_attempts ea
                        JOIN exams ex
                            ON ea.exam_id = ex.exam_id
                    GROUP BY ea.user_id, ex.course_id
                ) s
                    ON s.user_id = e.user_id
                   AND s.course_id = e.course_id
                -- 이수증(있으면 발급 완료)
                LEFT JOIN certificates cert
                    ON cert.enrollment_id = e.enrollment_id
            WHERE e.user_id = ?
            ORDER BY e.enrolled_at DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AdminUserCourseDto dto = new AdminUserCourseDto();

            dto.setCourseNo(rs.getLong("courseNo"));
            dto.setCourseTitle(rs.getString("courseTitle"));
            dto.setCourseStart(rs.getString("courseStart")); // yyyy-MM-dd
            dto.setCourseEnd(rs.getString("courseEnd"));     // yyyy-MM-dd or null

            // 진도율: DECIMAL -> "80%" 이런 문자열로
            BigDecimal progress = rs.getBigDecimal("progress");
            if (progress != null) {
                dto.setCourseProgress(progress.stripTrailingZeros().toPlainString() + "%");
            } else {
                dto.setCourseProgress(null);
            }

            // 점수: INT, null 처리
            int score = rs.getInt("score");
            if (rs.wasNull()) {
                dto.setCourseScore(null);
            } else {
                dto.setCourseScore(score);
            }

            // 상태 매핑: DB('수강중','미수료','수료') -> 화면('진행중','미수료','이수')
            String status = rs.getString("status");
            String courseStatus;
            if ("수료".equals(status)) {
                courseStatus = "이수";
            } else if ("미수료".equals(status)) {
                courseStatus = "미수료";
            } else {
                courseStatus = "수강중";   // 수강중 또는 null
            }
            dto.setCourseStatus(courseStatus);

            // 이수증 여부: certificates row 존재 여부로 판단
            Long certId = rs.getLong("certificateId");
            if (rs.wasNull()) {
                dto.setCourseCert("미발급");
            } else {
                dto.setCourseCert("발급 완료");
            }

            return dto;
        }, userId);
    }
    public AdminUserGameDto findGameDataByUserId(Long userId) {
        String sql = """
                SELECT user_id, star_levels
                FROM game_data
                WHERE user_id = ?
                ORDER BY game_id DESC
                LIMIT 1
                """;

        var result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            AdminUserGameDto dto = new AdminUserGameDto();
            dto.setUserId(rs.getLong("user_id"));
            dto.setStarLevels(rs.getString("star_levels")); // "[4,0,0,0]" 형식
            return dto;
        }, userId);

        return result.isEmpty() ? null : result.get(0);
    }

}