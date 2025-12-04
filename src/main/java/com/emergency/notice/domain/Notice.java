package com.emergency.notice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Long noticeId;
    private String title;
    private Long userId;
    private String contentHtml;
    private LocalDateTime createdAt;
    private String writerName;
}
