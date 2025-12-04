//package com.emergency.exam.controller;
//
//import com.emergency.exam.service.ExamService;
//import com.emergency.user.web.LoginUser;
//import com.emergency.user.web.dto.UserDto;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class ExamStartController {
//
//    private final ExamService examService;
//
//    // 시험 시작 API
//    @GetMapping("/exam/start")
//    public ResponseEntity<?> startExam(
//            @RequestParam Long courseId,
//            HttpSession session
//    ) {
//        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
//
//        if (loginUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of(
//                            "success", false,
//                            "message", "로그인이 필요합니다."
//                    ));
//        }
//
//        Long userId = loginUser.getUserId();
//
//        return ResponseEntity.ok(examService.startExam(courseId, userId));
//    }
//
//}
