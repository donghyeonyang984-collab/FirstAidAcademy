package com.emergency.certificate;



import lombok.Data;

import java.util.List;

@Data
public class CertificateDto {
    private String userName;        // 성명
    private String courseTitle;     // 과정명
    private String startDate;       // 수강 시작
    private String endDate;         // 수료일
    private String issuedDate;      // 발급일
    private String certificateNumber; // 문서번호
    private List<String> lectureList;

}
