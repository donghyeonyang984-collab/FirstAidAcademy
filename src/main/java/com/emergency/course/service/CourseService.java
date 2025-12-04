// src/main/java/com/emergency/course/service/CourseService.java
package com.emergency.course.service;

import com.emergency.course.domain.CourseListItem;
import com.emergency.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // 한 페이지당 강의 수
    private static final int DEFAULT_PAGE_SIZE = 9;

    public List<CourseListItem> getCoursePage(String keyword,
                                              String sort,
                                              String topCategory,
                                              String midCategory,
                                              int page) {
        if (page < 1) page = 1;
        return courseRepository.findPage(
                keyword,
                sort,
                topCategory,
                midCategory,
                page,
                DEFAULT_PAGE_SIZE
        );
    }

    public int getTotalPages(String keyword,
                             String topCategory,
                             String midCategory) {
        long totalCount = courseRepository.count(keyword, topCategory, midCategory);
        if (totalCount == 0) return 0;
        return (int) Math.ceil(totalCount / (double) DEFAULT_PAGE_SIZE);
    }

    public int getPageSize() {
        return DEFAULT_PAGE_SIZE;
    }
}
