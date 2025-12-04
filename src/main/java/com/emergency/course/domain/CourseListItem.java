// src/main/java/com/emergency/course/domain/CourseListItem.java
package com.emergency.course.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseListItem {

    private Long courseId;
    private String title;
    private String summary;
    private String imagePath;

    private String topCategory;  // '구조자' / '자가'
    private String midCategory;  // '출혈','기도막힘','심정지','화상'
}
