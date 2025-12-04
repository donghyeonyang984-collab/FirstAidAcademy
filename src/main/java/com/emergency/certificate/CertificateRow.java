package com.emergency.certificate;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CertificateRow {
    private String userName;
    private String courseTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> lectureList; // 이수 강의명 리스트
}
