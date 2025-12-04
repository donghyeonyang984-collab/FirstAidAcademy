package com.emergency.admin.service;

import com.emergency.admin.domain.CourseCompletionStat;
import com.emergency.admin.domain.DashboardStats;
import com.emergency.admin.domain.MonthlyUserJoin;
import com.emergency.admin.repository.AdminDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AdminDashboardRepository adminDashboardRepository;

    public DashboardStats getDashboardStats() {
        long userTotalCnt = adminDashboardRepository.countUsers();
        long courseTotalCnt = adminDashboardRepository.countCourses();
        long inqUnrepliedCnt = adminDashboardRepository.countPendingInquiries();
        long noticeTotalCnt = adminDashboardRepository.countNotices();

        return new DashboardStats(
                userTotalCnt,
                courseTotalCnt,
                inqUnrepliedCnt,
                noticeTotalCnt
        );
    }
    public List<MonthlyUserJoin> getLast6MonthsUserJoins() {
        return adminDashboardRepository.findLast6MonthsUserJoins();
    }

    public List<CourseCompletionStat> getCompletionByMidCategory() {
        return adminDashboardRepository.findCompletionByMidCategory();
    }

}