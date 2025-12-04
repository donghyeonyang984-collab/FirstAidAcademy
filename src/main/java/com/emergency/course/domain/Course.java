// src/main/java/com/emergency/course/domain/Course.java
package com.emergency.course.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Course {

    private Long courseId;
    private String title;        // 강의제목
    private String topCategory;  // '구조자' / '자가'
    private String midCategory;  // '출혈','기도막힘','심정지','화상'
    private String summary;      // 강의간단소개
    private String imagePath;    // 이미지 경로 (NULL 가능)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
