// src/main/java/com/emergency/course/controllers/CourseController.java
package com.emergency.course.controllers;

import com.emergency.course.domain.CourseListItem;
import com.emergency.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /** 공통 레이아웃 세팅 */
    private void setupCoursePage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "COURSE");   // 헤더 메뉴 활성화
        model.addAttribute("showSidebar", true);      // 교육과정 사이드바 사용
        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of(
                "/css/courses_css/courses.css",
                "/css/courses_css/about.css"
        ));
        model.addAttribute("pageJs", List.of("/fragments_js/pageNationC.js",
                "/fragments_js/courseSidebar.js"));
    }

    /** 전체 강의 목록 (/courses) */
    @GetMapping({"/courses", "/courses/courses", "/courses/courses.html"})
    public String courseList(@RequestParam(name = "q", required = false) String keyword,
                             @RequestParam(name = "sort", required = false) String sort,
                             @RequestParam(name = "top", required = false) String topCategory,
                             @RequestParam(name = "mid", required = false) String midCategory,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             Model model) {

        if (keyword == null) keyword = "";
        if (sort == null) sort = "";
        if (topCategory == null) topCategory = "";
        if (midCategory == null) midCategory = "";

        List<CourseListItem> courses =
                courseService.getCoursePage(keyword, sort, topCategory, midCategory, page);
        int totalPages = courseService.getTotalPages(keyword, topCategory, midCategory);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", courseService.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedTop", topCategory);
        model.addAttribute("selectedMid", midCategory);

        // ✅ mid 값에 따라 제목 동적으로 변경
        // mid = "출혈"  → "출혈 강의 목록"
        // mid = "기도막힘" → "기도막힘 강의 목록" ...
        String pageTitle = "강의목록";
        if (!midCategory.isEmpty()) {
            pageTitle =  " 강의 목록-"+ midCategory;
        }

        // templates/courses/courses.html 의 content 프래그먼트 사용
        setupCoursePage(model, pageTitle, "courses/courses");
        return "layout";
    }

    // ===== 이하 4개는 사이드바 미드 카테고리별 페이지 =====

    /** 출혈 */
    @GetMapping({"/courses/course1", "/courses/course1.html", "/courses/출혈"})
    public String courseBleeding(@RequestParam(name = "q", required = false) String keyword,
                                 @RequestParam(name = "sort", required = false) String sort,
                                 @RequestParam(name = "top", required = false) String topCategory,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 Model model) {

        if (keyword == null) keyword = "";
        if (sort == null) sort = "";
        if (topCategory == null) topCategory = "";
        String midCategory = "출혈";

        List<CourseListItem> courses =
                courseService.getCoursePage(keyword, sort, topCategory, midCategory, page);
        int totalPages = courseService.getTotalPages(keyword, topCategory, midCategory);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", courseService.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedTop", topCategory);
        model.addAttribute("selectedMid", midCategory);

        // 출혈 전용 페이지 제목
        setupCoursePage(model, "강의 목록-출혈 ", "courses/course1");
        return "layout";
    }

    /** 기도막힘 */
    @GetMapping({"/courses/course2", "/courses/course2.html", "/courses/기도막힘"})
    public String courseAirway(@RequestParam(name = "q", required = false) String keyword,
                               @RequestParam(name = "sort", required = false) String sort,
                               @RequestParam(name = "top", required = false) String topCategory,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               Model model) {

        if (keyword == null) keyword = "";
        if (sort == null) sort = "";
        if (topCategory == null) topCategory = "";
        String midCategory = "기도막힘";

        List<CourseListItem> courses =
                courseService.getCoursePage(keyword, sort, topCategory, midCategory, page);
        int totalPages = courseService.getTotalPages(keyword, topCategory, midCategory);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", courseService.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedTop", topCategory);
        model.addAttribute("selectedMid", midCategory);

        setupCoursePage(model, "강의 목록-기도막힘 ", "courses/course2");
        return "layout";
    }

    /** 심정지 */
    @GetMapping({"/courses/course3", "/courses/course3.html", "/courses/심정지"})
    public String courseArrest(@RequestParam(name = "q", required = false) String keyword,
                               @RequestParam(name = "sort", required = false) String sort,
                               @RequestParam(name = "top", required = false) String topCategory,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               Model model) {

        if (keyword == null) keyword = "";
        if (sort == null) sort = "";
        if (topCategory == null) topCategory = "";
        String midCategory = "심정지";

        List<CourseListItem> courses =
                courseService.getCoursePage(keyword, sort, topCategory, midCategory, page);
        int totalPages = courseService.getTotalPages(keyword, topCategory, midCategory);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", courseService.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedTop", topCategory);
        model.addAttribute("selectedMid", midCategory);

        setupCoursePage(model, "강의 목록-심정지 ", "courses/course3");
        return "layout";
    }

    /** 화상 */
    @GetMapping({"/courses/course4", "/courses/course4.html", "/courses/화상"})
    public String courseBurn(@RequestParam(name = "q", required = false) String keyword,
                             @RequestParam(name = "sort", required = false) String sort,
                             @RequestParam(name = "top", required = false) String topCategory,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             Model model) {

        if (keyword == null) keyword = "";
        if (sort == null) sort = "";
        if (topCategory == null) topCategory = "";
        String midCategory = "화상";

        List<CourseListItem> courses =
                courseService.getCoursePage(keyword, sort, topCategory, midCategory, page);
        int totalPages = courseService.getTotalPages(keyword, topCategory, midCategory);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", courseService.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedTop", topCategory);
        model.addAttribute("selectedMid", midCategory);

        setupCoursePage(model, "강의 목록-화상 ", "courses/course4");
        return "layout";
    }

    /** 소개 */
    @GetMapping({"/courses/about", "/courses/about.html"})
    public String about(Model model) {
        setupCoursePage(model, "응급처치 소개", "courses/about");
        return "layout";
    }
}
