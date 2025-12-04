package com.emergency.press.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PressRelease {

    private Long pressReleaseId;   // PK
    private String title;          // 제목
    private Long userId;           // 등록자 (users.user_id)
    private LocalDateTime createdAt; // 등록일
    private String contentHtml;    // 내용
    private String linkUrl;        // 외부 링크(URL)
    private String writerName;
    public String getWriterName() {
        return "관리자";
    }
}
