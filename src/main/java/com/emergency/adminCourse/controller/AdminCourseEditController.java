package com.emergency.adminCourse.controller;

import com.emergency.adminCourse.service.AdminCourseEditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class AdminCourseEditController {

    private final AdminCourseEditService editService;

    // 수정 페이지 진입 (Thymeleaf 렌더링)
    @GetMapping("/admin/course/edit")
    public String editPage(@RequestParam Long id, Model model) {
        model.addAttribute("course", editService.getCourseDetail(id));
        model.addAttribute("lectures", editService.getLectures(id));



        return "adminCourse/adminCourseEdit"; // templates/adminCourseEdit.html
    }

    // 수정 처리 (FormData POST)
    @PostMapping("/admin/course/edit")
    @ResponseBody
    public String updateCourse(
            @RequestParam Long courseId,
            @RequestParam String courseTitle,
            @RequestParam String courseMain,
            @RequestParam String courseSub,
            @RequestParam String courseDesc,

            @RequestParam(value = "courseThumb", required = false) MultipartFile courseThumb,

            @RequestParam(value = "courseVideo1", required = false) MultipartFile video1,
            @RequestParam(value = "caption1", required = false) String caption1,
            @RequestParam(value = "durationSec1", required = false, defaultValue = "0") int durationSec1,

            @RequestParam(value = "courseVideo2", required = false) MultipartFile video2,
            @RequestParam(value = "caption2", required = false) String caption2,
            @RequestParam(value = "durationSec2", required = false, defaultValue = "0") int durationSec2,

            @RequestParam(value = "courseVideo3", required = false) MultipartFile video3,
            @RequestParam(value = "caption3", required = false) String caption3,
            @RequestParam(value = "durationSec3", required = false, defaultValue = "0") int durationSec3
    ) {
        try {
            editService.updateCourseAndLectures(
                    courseId,
                    courseTitle, courseMain, courseSub, courseDesc,
                    courseThumb,
                    video1, caption1, durationSec1,
                    video2, caption2, durationSec2,
                    video3, caption3, durationSec3
            );
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
// 삭제 보류

//    @DeleteMapping("/admin/api/course/{courseId}")
//    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
//        editService.deleteCourse(courseId);
//        return ResponseEntity.ok().body("deleted");
//    }
}
