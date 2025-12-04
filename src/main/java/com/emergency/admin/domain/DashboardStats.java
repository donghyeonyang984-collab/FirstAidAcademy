package com.emergency.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardStats {

    private final long userTotalCnt;       // 총 회원 수
    private final long courseTotalCnt;     // 등록된 강의 수
    private final long inqUnrepliedCnt;    // 미답변 문의사항 수
    private final long noticeTotalCnt;     // 총 공지사항 수
}
