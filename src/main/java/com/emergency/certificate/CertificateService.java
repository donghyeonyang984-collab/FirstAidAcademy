package com.emergency.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateDto buildCertificate(Long enrollmentId, Long userId) {

        CertificateRow row = certificateRepository.findCertificateInfo(enrollmentId, userId);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        CertificateDto dto = new CertificateDto();

        dto.setUserName(row.getUserName());
        dto.setCourseTitle(row.getCourseTitle());

        // 수강 기간
        dto.setStartDate(row.getStartDate().format(fmt));  // enrolled_at
        dto.setEndDate(row.getEndDate().format(fmt));      // passed_at

        // 발급일 = passed_at 날짜
        dto.setIssuedDate(row.getEndDate().format(fmt));

        // 문서번호도 passed_at 기준
        dto.setCertificateNumber(row.getEndDate().getYear() + "-" +
                String.format("%04d", enrollmentId));

        dto.setLectureList(row.getLectureList());

        return dto;
    }
}


