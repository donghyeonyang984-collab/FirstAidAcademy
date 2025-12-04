package com.emergency.user.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * users 테이블 매핑
 */
@Data
public class User {

    private Long userId;          // user_id PK
    private String username;      // username
    private String passwordPlain; // password_plain
    private String name;          // name

    private LocalDate birthdate;  // birthdate (DATE)
    private String gender;        // 'M','F','N'

    private String phone;         // phone
    private String email;         // email
    private String address;       // address

    private String role;          // 'Admin','User'

    private LocalDateTime createdAt; // created_at
    private LocalDateTime updatedAt; // updated_at
}
