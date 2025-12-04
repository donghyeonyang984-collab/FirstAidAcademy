//package com.emergency.exam.controller;
//
//import com.emergency.exam.service.ExamService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class ExamSubmitController {
//
//    private final ExamService examService;
//
//    @PostMapping("/exam/submit")
//    public Map<String, Object> submitExam(@RequestBody Map<String, Object> body) {
//        return examService.submitExam(body);
//    }
//}
