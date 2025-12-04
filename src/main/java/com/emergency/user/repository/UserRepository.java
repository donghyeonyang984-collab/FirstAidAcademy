package com.emergency.user.repository;

import com.emergency.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository("userRepository")
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordPlain(rs.getString("password_plain"));
        u.setName(rs.getString("name"));
        u.setBirthdate(rs.getObject("birthdate", LocalDate.class));
        u.setGender(rs.getString("gender"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setAddress(rs.getString("address"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        u.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return u;
    };

    /** 아이디로 조회(username) */
    public Optional<User> findByUsername(String username) {
        List<User> result = jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ?",
                userRowMapper,
                username
        );
        return result.stream().findFirst();
    }

    /** user_id로 조회 (마이페이지용) */
    public Optional<User> findById(Long userId) {
        List<User> result = jdbcTemplate.query(
                "SELECT * FROM users WHERE user_id = ?",
                userRowMapper,
                userId
        );
        return result.stream().findFirst();
    }

    /** 아이디 + 비밀번호(평문)으로 로그인 */
    public Optional<User> findByUsernameAndPasswordPlain(String username, String passwordPlain) {
        List<User> result = jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? AND password_plain = ?",
                userRowMapper,
                username,
                passwordPlain
        );
        return result.stream().findFirst();
    }

    /** 아이디 중복 여부 확인 */
    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username
        );
        return count != null && count > 0;
    }

    /** 신규 회원 등록 */
    public User save(User user) {
        String sql = "INSERT INTO users " +
                "(username, password_plain, name, birthdate, gender, phone, email, address, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordPlain());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthdate());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getEmail());
            ps.setString(8, user.getAddress());
            ps.setString(9, user.getRole());
            return ps;
        }, keyHolder);

        user.setUserId(keyHolder.getKey().longValue());
        return user;
    }

    /** 이름 + 생년월일로 회원 찾기 (아이디 찾기용) */
    public Optional<User> findByNameAndBirth(String name, LocalDate birth) {
        List<User> result = jdbcTemplate.query(
                "SELECT * FROM users WHERE name = ? AND birthdate = ?",
                userRowMapper,
                name, birth
        );
        return result.stream().findFirst();
    }

    /** 아이디 + 이름으로 회원 찾기 (비밀번호 재설정용) */
    public Optional<User> findByUsernameAndName(String username, String name) {
        List<User> result = jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? AND name = ?",
                userRowMapper,
                username, name
        );
        return result.stream().findFirst();
    }

    /** 비밀번호 변경 */
    public void updatePasswordPlain(Long userId, String newPassword) {
        jdbcTemplate.update(
                "UPDATE users SET password_plain = ?, updated_at = NOW() WHERE user_id = ?",
                newPassword, userId
        );
    }

    /** 회원 정보(연락처 / 이메일 / 주소 / 성별) 수정 */
    public void updateProfileInfo(Long userId,
                                  String phone,
                                  String email,
                                  String address,
                                  String gender) {
        jdbcTemplate.update(
                "UPDATE users SET phone = ?, email = ?, address = ?, gender = ?, updated_at = NOW() " +
                        "WHERE user_id = ?",
                phone, email, address, gender, userId
        );
    }
}
