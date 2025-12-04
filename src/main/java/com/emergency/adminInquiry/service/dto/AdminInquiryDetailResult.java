// src/main/java/com/emergency/adminInquiry/service/dto/AdminInquiryDetailResult.java
package com.emergency.adminInquiry.service.dto;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;

import java.util.List;

public class AdminInquiryDetailResult {

    private Inquiry inquiry;                       // 문의 본문
    private InquiryAnswer answer;                  // 관리자 답변
    private List<InquiryAttachment> attachments;   // 첨부 파일 목록

    private String userName;   // 회원 이름
    private String username;   // 회원 아이디

    // ----- getter / setter -----

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public InquiryAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(InquiryAnswer answer) {
        this.answer = answer;
    }

    public List<InquiryAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<InquiryAttachment> attachments) {
        this.attachments = attachments;
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

    // 🔥 여기 때문에 AdminInquiryService 에서 setUsername 못 찾았던 거
    public void setUsername(String username) {
        this.username = username;
    }
}
