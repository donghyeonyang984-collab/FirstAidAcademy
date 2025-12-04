package com.emergency.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;




@Repository
@RequiredArgsConstructor
public class CertificateRepository {

    private final JdbcTemplate jdbc;

    public CertificateRow findCertificateInfo(Long enrollmentId, Long userId) {

        String sql = """
            SELECT 
                u.name,
                c.title AS course_title,
                e.enrolled_at,
                e.passed_at
            FROM enrollments e
            JOIN users u ON u.user_id = e.user_id
            JOIN courses c ON c.course_id = e.course_id
            WHERE e.enrollment_id = ?
              AND e.user_id = ?
        """;

        CertificateRow row = jdbc.queryForObject(sql,
                new Object[]{enrollmentId, userId},
                (rs, n) -> {
                    CertificateRow r = new CertificateRow();
                    r.setUserName(rs.getString("name"));
                    r.setCourseTitle(rs.getString("course_title"));


                    r.setStartDate(rs.getTimestamp("enrolled_at")
                            .toLocalDateTime()
                            .toLocalDate());   // 날짜만 추출

                    if (rs.getTimestamp("passed_at") != null) {
                        r.setEndDate(rs.getTimestamp("passed_at")
                                .toLocalDateTime()
                                .toLocalDate());
                    } else {
                        r.setEndDate(r.getStartDate());
                    }

                    return r;
                });

        String lectureSql = """
            SELECT title
            FROM course_lectures
            WHERE course_id = (
                SELECT course_id FROM enrollments WHERE enrollment_id = ?
            )
            ORDER BY lecture_no
        """;

        row.setLectureList(
                jdbc.queryForList(lectureSql, new Object[]{enrollmentId}, String.class)
        );

        return row;
    }
}
