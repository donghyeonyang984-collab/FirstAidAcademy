package com.emergency.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseCompletionStat {

    // 차트 라벨로 사용할 mid_category (출혈 / 기도막힘 / 심정지 / 화상)
    private final String midCategory;

    // 0 ~ 100 사이 수료율 (%)
    private final double completionRate;
}
