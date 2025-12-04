package com.emergency.adminDocs.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Material {

    private Long materialId;          // material_id
    private String title;             // title
    private TopCategory topCategory;  // top_category (예: RESCUER/SELF -> DB: 구조자/자가)
    private MidCategory midCategory;  // mid_category (예: BLEEDING 등 -> DB: 출혈 ...)
    private String contentHtml;       // content_html
    private Long userId;              // user_id
    private LocalDateTime createdAt;  // created_at

    // ====== PDF BLOB 저장 필드 ======
    private String pdfFilename;       // pdf_filename (원본 파일명)
    private String pdfContentType;    // pdf_content_type (보통 application/pdf)
    private byte[] pdfData;           // pdf_data (실제 PDF 바이너리)

    // 작성자 이름 (JOIN 용)
    private String writerName;
}