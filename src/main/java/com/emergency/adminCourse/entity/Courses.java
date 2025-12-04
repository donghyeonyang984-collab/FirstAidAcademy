package com.emergency.adminCourse.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Courses {
    private Long courseId;       // course_id (PK)
    private String title;        // 강의 제목
    private String topCategory;  // 상위 카테고리 (ENUM)
    private String midCategory;  // 하위 카테고리 (ENUM)
    private String summary;      // 요약 설명
    private String imagePath;    // 썸네일 경로
    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일

    private List<CourseLectures> lectures; // 연결된 영상 3개
}
