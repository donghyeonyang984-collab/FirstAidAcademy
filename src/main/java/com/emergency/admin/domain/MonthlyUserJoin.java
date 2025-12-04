package com.emergency.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyUserJoin {
    private final String label;  // "1월" 이런 식
    private final long count;    // 가입자 수
}
