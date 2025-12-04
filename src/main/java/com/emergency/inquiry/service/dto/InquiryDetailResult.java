// src/main/java/com/emergency/inquiry/service/dto/InquiryDetailResult.java
package com.emergency.inquiry.service.dto;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;
import lombok.Data;

import java.util.List;

@Data
public class InquiryDetailResult {

    private Inquiry inquiry;
    private InquiryAnswer answer;
    private List<InquiryAttachment> attachments;

    private Long prevId; // 이전글 id (없으면 null)
    private Long nextId; // 다음글 id (없으면 null)
}
