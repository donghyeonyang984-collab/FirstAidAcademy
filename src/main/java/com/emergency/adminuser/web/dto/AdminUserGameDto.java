package com.emergency.adminuser.web.dto;

import lombok.Data;

@Data
public class AdminUserGameDto {

    private Long userId;

    /**
     * game_data.star_levels JSON 값
     * 예) "[4,0,0,0]"
     */
    private String starLevels;
}
