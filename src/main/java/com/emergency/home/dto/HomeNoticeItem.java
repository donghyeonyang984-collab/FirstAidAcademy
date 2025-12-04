package com.emergency.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeNoticeItem {
    private Long id;          // notices PK
    private String title;
    private String createdDate;
}
