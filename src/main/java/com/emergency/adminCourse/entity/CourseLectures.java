package com.emergency.adminCourse.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourseLectures {
    private Long courseLectureId; // course_lecture_id (PK)
    private Long courseId;        // FK → courses.course_id
    private int lectureNo;        // 1~3
    private String title;         // 영상 제목
    private String videoUrl;      // 영상 URL
    private String information;   // 자막 / 설명
    private Integer durationSec;  // 영상 길이(초
}
