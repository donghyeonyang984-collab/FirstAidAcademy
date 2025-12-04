package com.emergency.adminuser.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {

    // PK
    private Long userId;          // user_id

    // 기본 정보
    private String username;      // username
    private String passwordPlain; // password_plain
    private String name;          // name
    private LocalDate birthdate;  // birthdate
    private Gender gender;        // gender (ENUM)

    private String phone;         // phone
    private String email;         // email
    private String address;       // address

    private Role role;            // role (ENUM: Admin, User)

    // 감사 정보
    private LocalDateTime createdAt; // created_at
    private LocalDateTime updatedAt; // updated_at

    /** DB ENUM('M','F','N') 매핑 */
    public enum Gender {
        M, F, N
    }

    /** DB ENUM('Admin','User') 매핑 */
    public enum Role {
        Admin, User
    }
}
