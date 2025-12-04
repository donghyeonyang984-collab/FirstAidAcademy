package com.emergency.enrollment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MyStudyCourseItem {

    private final Long enrollmentId;
    private final Long courseId;

    private final String title;
    private final String topCategory;   // '구조자', '자가'
    private final String midCategory;   // '심정지' 등
    private final String summary;
    private final String imagePath;

    private final BigDecimal progressPercent; // 진도율
}
