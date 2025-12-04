package com.emergency.adminuser.service;

import com.emergency.adminuser.repository.AdminUserRepository;
import com.emergency.adminuser.web.dto.AdminUserCourseDto;
import com.emergency.adminuser.web.dto.AdminUserGameDto;
import com.emergency.adminuser.web.dto.AdminUserListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    public List<AdminUserListDto> getUserListForAdmin() {
        return adminUserRepository.findAllNormalUsers();
    }

    public List<AdminUserCourseDto> getUserCourses(Long userId) {
        return adminUserRepository.findCoursesByUserId(userId);
    }
    public AdminUserGameDto getUserGame(Long userId) {
        AdminUserGameDto dto = adminUserRepository.findGameDataByUserId(userId);
        if (dto == null) {
            dto = new AdminUserGameDto();
            dto.setUserId(userId);
            dto.setStarLevels("[]");   // 게임 데이터가 없으면 0개 클리어
        }
        return dto;
    }
}
