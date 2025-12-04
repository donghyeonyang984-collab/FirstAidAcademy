package com.emergency.user.web;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 세션에 보관할 로그인 사용자 정보
 */
@Data
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private String username;
    private String name;
    private String role;
}
