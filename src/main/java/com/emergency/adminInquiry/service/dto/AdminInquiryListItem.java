// src/main/java/com/emergency/adminInquiry/service/dto/AdminInquiryListItem.java
package com.emergency.adminInquiry.service.dto;

import java.time.LocalDateTime;

public class AdminInquiryListItem {

    private Long inquiryId;        // 문의 ID
    private String userName;       // 회원 이름
    private String username;       // 회원 아이디
    private String title;          // 문의 제목
    private String status;         // 답변대기 / 답변완료
    private LocalDateTime createdAt; // 작성일

    // ----- getter / setter -----

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsername() {
        return username;
    }

    // 🔥 여기 때문에 setUsername 못찾았던 거
    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
