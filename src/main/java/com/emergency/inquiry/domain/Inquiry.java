// src/main/java/com/emergency/inquiry/domain/Inquiry.java
package com.emergency.inquiry.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Inquiry {

    private Long inquiryId;
    private Long userId;

    // 문의 유형: 강의 / 시스템 / 이수증 / 회원정보
    private String category;

    private String title;
    private String content;

    // '대기', '답변완료'
    private String status;

    private LocalDateTime createdAt;
}
