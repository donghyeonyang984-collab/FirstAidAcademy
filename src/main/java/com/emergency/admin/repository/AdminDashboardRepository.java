package com.emergency.admin.repository;

import com.emergency.admin.domain.CourseCompletionStat;
import com.emergency.admin.domain.MonthlyUserJoin;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    /** users 테이블 총 회원 수 */
    public long countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /** courses 테이블 등록된 강의 수 */
    public long countCourses() {
        String sql = "SELECT COUNT(*) FROM courses";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /** inquiries 테이블 status = '대기' 인 미답변 문의 수 */
    public long countPendingInquiries() {
        String sql = "SELECT COUNT(*) FROM inquiries WHERE status = '대기'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /** notices 테이블 총 공지사항 수 */
    public long countNotices() {
        String sql = "SELECT COUNT(*) FROM notices";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    /** 최근 6개월 신규 가입자 수 (users.created_at 기준) */
    public List<MonthlyUserJoin> findLast6MonthsUserJoins() {
        String sql = """
        SELECT
            DATE_FORMAT(created_at, '%Y-%m') AS ym,      -- 그룹핑용 키
            DATE_FORMAT(created_at, '%c월')   AS label,   -- 차트 라벨
            COUNT(*) AS cnt
        FROM users
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 5 MONTH)
        GROUP BY ym, label
        ORDER BY ym
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MonthlyUserJoin(
                        rs.getString("label"),  // "1월", "2월" ...
                        rs.getLong("cnt")
                )
        );
    }

    /** 강의별 수료율 (enrollments.status = 'COMPLETED' 기준) */
    public List<CourseCompletionStat> findCompletionByMidCategory() {
        String sql = """
            SELECT
                c.mid_category AS midCategory,
                SUM(CASE WHEN e.status = '수료' THEN 1 ELSE 0 END) AS completed_cnt,
                SUM(CASE WHEN e.status IN ('수료', '미수료') THEN 1 ELSE 0 END) AS total_cnt
            FROM courses c
            LEFT JOIN enrollments e
                ON c.course_id = e.course_id
            GROUP BY c.mid_category
            ORDER BY c.mid_category
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long completed = rs.getLong("completed_cnt");
            long total = rs.getLong("total_cnt");
            double rate = (total == 0) ? 0.0 : (completed * 100.0 / total);

            return new CourseCompletionStat(
                    rs.getString("midCategory"),
                    rate
            );
        });
    }
}
