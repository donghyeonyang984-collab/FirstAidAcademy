package com.emergency.adminuser.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * adminUser.js 에서 쓰는 필드 이름에 맞춘 DTO
 */
@Data
public class AdminUserListDto {

    /** PK (users.user_id) */
    private Long userNo;

    /** 로그인 아이디 (users.username) */
    private String userId;

    /** 사용자 이름 (users.name) */
    private String userName;

    private String userEmail;

    private String userPhone;

    /** 생년월일 (users.birthdate) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate userBirth;

    private String userAddr;
}
