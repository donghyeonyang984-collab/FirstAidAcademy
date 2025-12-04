package com.emergency.enrollment.domain;

import lombok.Data;

@Data
public class LecturePopupLecture {

    private Long courseLectureId;
    private Long courseId;
    private Integer lectureNo;
    private String title;
    private String videoUrl;
    private Integer durationSec;
    private Integer lastWatchSec;

    // course 정보
    private String courseTitle;
    private String topCategory;
    private String midCategory;

    // 🔹 강의 내용에 표시할 courses.summary
    private String summary;
}
