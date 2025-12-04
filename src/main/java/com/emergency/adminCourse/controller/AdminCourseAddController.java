package com.emergency.adminCourse.controller;


import com.emergency.adminCourse.service.AdminCourseAddService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;




@Controller
@RequiredArgsConstructor
public class AdminCourseAddController {

    private final AdminCourseAddService courseAddService;

    @PostMapping("/admin/course/add")
    @ResponseBody
    public String addCourse(
            @RequestParam String courseTitle,
            @RequestParam String courseMain,
            @RequestParam String courseSub,
            @RequestParam String courseDesc,

            @RequestParam("courseThumb") MultipartFile courseThumb,

            @RequestParam("courseVideo1") MultipartFile video1,
            @RequestParam("addCaption1") String caption1,
            @RequestParam("durationSec1") int durationSec1,

            @RequestParam(value = "courseVideo2", required = false) MultipartFile video2,
            @RequestParam(value = "addCaption2", required = false) String caption2,
            @RequestParam(value = "durationSec2", required = false, defaultValue = "0") int durationSec2,

            @RequestParam(value = "courseVideo3", required = false) MultipartFile video3,
            @RequestParam(value = "addCaption3", required = false) String caption3,
            @RequestParam(value = "durationSec3", required = false, defaultValue = "0") int durationSec3
    ) {
        try {
            courseAddService.addCourseAndLectures(
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



}
