package com.emergency.user.web.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long userId;
    private String username;
    private String name;
    private String role;
}