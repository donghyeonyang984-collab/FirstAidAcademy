package com.emergency.adminuser.web;

import com.emergency.adminuser.service.AdminUserService;
import com.emergency.adminuser.web.dto.AdminUserCourseDto;
import com.emergency.adminuser.web.dto.AdminUserGameDto;
import com.emergency.adminuser.web.dto.AdminUserListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/adminUser")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** 회원 관리 페이지 진입 */
    @GetMapping("/adminUser")
    public String adminUserPage() {
        return "adminUser/adminUser";
    }

    /** 회원 목록 JSON (role = 'User' 인 사람만) */
    @GetMapping("/api/users")
    @ResponseBody
    public List<AdminUserListDto> getUsers() {
        return adminUserService.getUserListForAdmin();
    }

    /** 특정 회원의 수강 정보 JSON */
    @GetMapping("/api/users/{userId}/courses")
    @ResponseBody
    public List<AdminUserCourseDto> getUserCourses(@PathVariable("userId") Long userId) {
        return adminUserService.getUserCourses(userId);
    }
    @GetMapping("/api/users/{userId}/game")
    @ResponseBody
    public AdminUserGameDto getUserGame(@PathVariable("userId") Long userId) {
        return adminUserService.getUserGame(userId);
    }
}
