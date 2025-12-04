package com.emergency.enrollment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyStudyStatusCount {

    private final long studyingCount;      // 수강중
    private final long completedCount;     // 수료
    private final long notCompletedCount;  // 미수료
}