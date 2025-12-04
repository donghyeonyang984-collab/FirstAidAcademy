// src/main/java/com/emergency/inquiry/web/form/InquiryForm.java
package com.emergency.inquiry.web.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InquiryForm {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "문의 유형을 선택해주세요.")
    private String category;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    private String content;
}
