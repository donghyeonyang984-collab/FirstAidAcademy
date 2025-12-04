package com.emergency;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminWebController {

    @GetMapping("/admin")  // 루트 경로
    public String home() {
        return "exam";  // templates/index.html 로 연결
    }

    @GetMapping("adminCourse/adminCourseList")
    public String adminCourseList() {
        return "adminCourse/adminCourseList";
    }

    @GetMapping("adminCourse/adminCourseAdd")
    public String adminCourseAdd() {
        return "adminCourse/adminCourseAdd";
    }


}
