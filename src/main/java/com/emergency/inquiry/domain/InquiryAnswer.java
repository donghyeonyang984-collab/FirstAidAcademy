// src/main/java/com/emergency/inquiry/domain/InquiryAnswer.java
package com.emergency.inquiry.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InquiryAnswer {

    private Long inquiryAnswerId;
    private Long inquiryId;

    private String adminName;
    private String answerContent;

    private LocalDateTime answeredAt;
}
