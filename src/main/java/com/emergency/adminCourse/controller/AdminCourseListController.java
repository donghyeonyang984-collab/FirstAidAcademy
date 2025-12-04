package com.emergency.adminCourse.controller;

import com.emergency.adminCourse.service.AdminCourseListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminCourseListController {

    private final AdminCourseListService courseListService;

    @GetMapping("/admin/api/courses")
    public List<Map<String, Object>> getCourses() {
        return courseListService.getCourseList();
    }

}