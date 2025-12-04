// src/main/java/com/emergency/inquiry/domain/InquiryAttachment.java
package com.emergency.inquiry.domain;

import lombok.Data;

@Data
public class InquiryAttachment {

    private Long inquiryAttachmentId;
    private Long inquiryId;
    private String filePath;
}
