package com.emergency.adminDocs.repository;

import com.emergency.adminDocs.domain.Material;
import com.emergency.adminDocs.domain.MidCategory;
import com.emergency.adminDocs.domain.TopCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("adminMaterialRepository")
@RequiredArgsConstructor
public class MaterialRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Material> materialRowMapper = (rs, rowNum) -> {
        Material m = new Material();
        m.setMaterialId(rs.getLong("material_id"));
        m.setTitle(rs.getString("title"));

        // ★ DB 값(한글 ENUM) → Enum 으로 변환
        String topDb = rs.getString("top_category");
        String midDb = rs.getString("mid_category");
        m.setTopCategory(TopCategory.fromDbValue(topDb));
        m.setMidCategory(MidCategory.fromDbValue(midDb));

        m.setContentHtml(rs.getString("content_html"));
        m.setUserId(rs.getLong("user_id"));

        var ts = rs.getTimestamp("created_at");
        if (ts != null) {
            m.setCreatedAt(ts.toLocalDateTime());
        }

        // BLOB 관련 필드
        m.setPdfFilename(rs.getString("pdf_filename"));
        m.setPdfContentType(rs.getString("pdf_content_type"));
        m.setPdfData(rs.getBytes("pdf_data"));

        // ✅ JOIN 해서 가져온 작성자 이름
        //   (SELECT ... u.name AS writer_name 에서 매핑)
        m.setWriterName(rs.getString("writer_name"));

        return m;
    };

    /** 전체 목록 */
    public List<Material> findAll() {
        String sql = """
                SELECT m.material_id,
                       m.title,
                       m.top_category,
                       m.mid_category,
                       m.content_html,
                       m.user_id,
                       m.created_at,
                       m.pdf_filename,
                       m.pdf_content_type,
                       m.pdf_data,
                       u.name AS writer_name
                  FROM materials m
                  LEFT JOIN users u ON u.user_id = m.user_id
                 ORDER BY m.material_id DESC
                """;
        return jdbcTemplate.query(sql, materialRowMapper);
    }

    /** 단건 조회 */
    public Optional<Material> findById(Long id) {
        String sql = """
                SELECT m.material_id,
                       m.title,
                       m.top_category,
                       m.mid_category,
                       m.content_html,
                       m.user_id,
                       m.created_at,
                       m.pdf_filename,
                       m.pdf_content_type,
                       m.pdf_data,
                       u.name AS writer_name
                  FROM materials m
                  LEFT JOIN users u ON u.user_id = m.user_id
                 WHERE m.material_id = ?
                """;
        List<Material> list = jdbcTemplate.query(sql, materialRowMapper, id);
        return list.stream().findFirst();
    }

    /** 전체 개수 (카테고리 + 제목검색) */
    public int count(String topCategory, String keyword) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM materials m");
        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (topCategory != null && !topCategory.isBlank()) {
            sb.append(" WHERE m.top_category = ?");
            params.add(topCategory);
            whereAdded = true;
        }

        if (keyword != null && !keyword.isBlank()) {
            sb.append(whereAdded ? " AND " : " WHERE");
            sb.append(" m.title LIKE ?");
            params.add("%" + keyword + "%");
        }

        Integer cnt = jdbcTemplate.queryForObject(
                sb.toString(),
                Integer.class,
                params.toArray()
        );
        return (cnt != null) ? cnt : 0;
    }

    /** 목록 조회 (검색 + 카테고리 + 페이징) */
    public List<Material> search(String topCategory,
                                 String keyword,
                                 int size,
                                 int offset) {

        StringBuilder sb = new StringBuilder("""
                SELECT m.material_id,
                       m.title,
                       m.top_category,
                       m.mid_category,
                       m.content_html,
                       m.user_id,
                       m.created_at,
                       m.pdf_filename,
                       m.pdf_content_type,
                       m.pdf_data,
                       u.name AS writer_name
                  FROM materials m
                  LEFT JOIN users u ON u.user_id = m.user_id
                """);

        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (topCategory != null && !topCategory.isBlank()) {
            sb.append(" WHERE m.top_category = ?");
            params.add(topCategory);
            whereAdded = true;
        }

        if (keyword != null && !keyword.isBlank()) {
            sb.append(whereAdded ? " AND " : " WHERE");
            sb.append(" m.title LIKE ?");
            params.add("%" + keyword + "%");
        }

        sb.append(" ORDER BY m.material_id DESC ");
        sb.append(" LIMIT ? OFFSET ? ");

        params.add(size);
        params.add(offset);

        return jdbcTemplate.query(sb.toString(), materialRowMapper, params.toArray());
    }

    // 소카테고리 조회(홈페이지 등에서 사용)
    public List<Material> findByMidCategory(MidCategory midCategory) {
        String sql = """
                SELECT m.material_id,
                       m.title,
                       m.top_category,
                       m.mid_category,
                       m.content_html,
                       m.user_id,
                       m.created_at,
                       m.pdf_filename,
                       m.pdf_content_type,
                       m.pdf_data,
                       u.name AS writer_name
                  FROM materials m
                  LEFT JOIN users u ON u.user_id = m.user_id
                 WHERE m.mid_category = ?
                 ORDER BY m.material_id DESC
                """;

        // 여기서 getDbValue()가 바로 "출혈", "기도막힘", "심정지", "화상" 같은 한글 값
        String dbValue = midCategory.getDbValue();

        return jdbcTemplate.query(sql, materialRowMapper, dbValue);
    }

    /**
     * String 버전 (이미 서비스에서 String 값으로 부르고 있으므로 그대로 유지)
     *  - "출혈" 같은 DB값이 들어오면 Enum으로 변환해서 재사용
     */
    public List<Material> findByMidCategory(String midCategoryDbValue) {
        MidCategory midCategory = MidCategory.fromDbValue(midCategoryDbValue);
        return findByMidCategory(midCategory);
    }

    /** 신규 저장 (INSERT) - PDF 를 BLOB 으로 DB에 직접 저장 */
    public Long save(Material material) {
        String sql = """
        INSERT INTO materials
            (title, top_category, mid_category, content_html, user_id,
             pdf_filename, pdf_content_type, pdf_data)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, material.getTitle());
            ps.setString(2, material.getTopCategory().getDbValue());
            ps.setString(3, material.getMidCategory().getDbValue());
            ps.setString(4, material.getContentHtml());
            ps.setLong(5, material.getUserId());
            ps.setString(6, material.getPdfFilename());
            ps.setString(7, material.getPdfContentType());
            ps.setBytes(8, material.getPdfData());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.longValue() : null;
    }

    /** 수정 (파일 포함) */
    public void update(Material material) {
        String sql = """
                UPDATE materials
                   SET title = ?,
                       top_category = ?,
                       mid_category = ?,
                       content_html = ?,
                       pdf_filename = ?,
                       pdf_content_type = ?,
                       pdf_data = ?
                 WHERE material_id = ?
                """;
        jdbcTemplate.update(sql,
                material.getTitle(),
                material.getTopCategory().getDbValue(),
                material.getMidCategory().getDbValue(),
                material.getContentHtml(),
                material.getPdfFilename(),
                material.getPdfContentType(),
                material.getPdfData(),
                material.getMaterialId());
    }

    /** 수정 (파일 변경 없음) */
    public void updateWithoutFile(Material material) {
        String sql = """
        UPDATE materials
           SET title = ?,
               top_category = ?,
               mid_category = ?,
               content_html = ?
         WHERE material_id = ?
        """;
        jdbcTemplate.update(sql,
                material.getTitle(),
                material.getTopCategory().getDbValue(),
                material.getMidCategory().getDbValue(),
                material.getContentHtml(),
                material.getMaterialId());
    }

    /** 삭제 */
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM materials WHERE material_id = ?", id);
    }
}
