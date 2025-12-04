package com.emergency.adminCourse.service;

import com.emergency.adminCourse.Dao.AdminCourseListDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminCourseListService {

    private final AdminCourseListDao courseListDao;

    public List<Map<String, Object>> getCourseList() {
        return courseListDao.findAllCourses();
    }
}