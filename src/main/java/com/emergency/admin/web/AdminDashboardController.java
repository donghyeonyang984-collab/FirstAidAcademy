package com.emergency.admin.web;

import com.emergency.admin.domain.CourseCompletionStat;
import com.emergency.admin.domain.DashboardStats;
import com.emergency.admin.domain.MonthlyUserJoin;
import com.emergency.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/adminHome")   // ★ 브라우저 주소와 동일하게
    public String adminHome(Model model) {

        DashboardStats stats = adminDashboardService.getDashboardStats();

        // ★ 여기서 모델에 값 넣어주기
        model.addAttribute("userTotalCnt", stats.getUserTotalCnt());
        model.addAttribute("courseTotalCnt", stats.getCourseTotalCnt());
        model.addAttribute("inqUnrepliedCnt", stats.getInqUnrepliedCnt());
        model.addAttribute("noticeTotalCnt", stats.getNoticeTotalCnt());

        // ----- 회원 현황(신규 가입자) -----
        List<MonthlyUserJoin> userJoins = adminDashboardService.getLast6MonthsUserJoins();
        List<String> userJoinLabels = userJoins.stream()
                .map(MonthlyUserJoin::getLabel)
                .collect(Collectors.toList());
        List<Long> userJoinData = userJoins.stream()
                .map(MonthlyUserJoin::getCount)
                .collect(Collectors.toList());

        model.addAttribute("userJoinLabels", userJoinLabels);
        model.addAttribute("userJoinData", userJoinData);

        // ----- 강의별 수료율 -----
        List<CourseCompletionStat> completionStats =
                adminDashboardService.getCompletionByMidCategory();

        List<String> courseLabels = completionStats.stream()
                .map(CourseCompletionStat::getMidCategory)      // 출혈/기도막힘/심정지/화상
                .toList();

        List<Double> courseCompletionData = completionStats.stream()
                .map(CourseCompletionStat::getCompletionRate)   // 각 카테고리 수료율
                .toList();

        model.addAttribute("courseLabels", courseLabels);
        model.addAttribute("courseCompletionData", courseCompletionData);

        return "adminHome";     // templates/adminHome.html
    }
}