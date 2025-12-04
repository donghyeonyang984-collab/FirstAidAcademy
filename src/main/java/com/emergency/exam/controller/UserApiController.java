//package com.emergency.exam.controller;
//
//import com.emergency.user.service.UserService;
//import com.emergency.user.web.LoginUser;
//import com.emergency.user.web.dto.UserDto;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class UserApiController {
//
//    private final UserService userService;
//
//    @GetMapping("/api/user/me")
//    public UserDto getLoginUser(HttpSession session) {
//
//        // 🔥 LoginUser 객체로 받아야 함
//        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
//
//        if (loginUser == null) return null;
//
//        // LoginUser 안에서 userId 꺼내기
//        Long userId = loginUser.getUserId();
//
//        return userService.findUserDtoById(userId);
//    }
//
//}
