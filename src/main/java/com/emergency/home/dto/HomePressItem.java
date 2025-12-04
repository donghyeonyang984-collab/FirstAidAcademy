package com.emergency.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomePressItem {
    private Long id;          // press_releases PK
    private String title;     // 제목
    private String createdDate; // yyyy-MM-dd 형식 문자열
}
