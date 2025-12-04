package com.emergency.adminuser.web.dto;

import lombok.Data;

/**
 * 회원 수강 정보 모달에서 사용하는 DTO
 */
@Data
public class AdminUserCourseDto {

    private Long courseNo;        // enrollment_id 또는 순번
    private String courseTitle;   // 과정명
    private String courseStart;   // 학습 시작일 (yyyy-MM-dd)
    private String courseEnd;     // 학습 종료일 (yyyy-MM-dd)
    private String courseProgress; // "80%" 같은 문자열
    private Integer courseScore;  // 점수
    private String courseStatus;  // "이수" / "진행중"
    private String courseCert;    // "발급 완료" / "미발급" 등
}
