package com.emergency.enrollment.web;

import com.emergency.enrollment.service.AlreadyEnrolledException;
import com.emergency.enrollment.service.EnrollmentService;
import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 수강신청 전용 컨트롤러
 *
 * 강의 목록 등에서 "수강신청" 버튼 클릭 시
 *   POST /courses/{courseId}/enroll
 * 로 요청이 들어온다고 가정하고 매핑을 맞춘다.
 */
@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * 수강신청 처리
     *
     * 요청 URL 예:
     *   POST /courses/4/enroll
     */
    @PostMapping("/courses/{courseId}/enroll")
    public String enrollCourse(@PathVariable Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // 로그인 사용자 정보 가져오기
        //  - 세션에 LoginUser 가 "loginUser" 라는 키로 저장되어 있다고 가정
        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인 후 수강신청이 가능합니다.");
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        try {
            // 중복 수강신청이면 AlreadyEnrolledException 발생
            enrollmentService.enroll(userId, courseId);
            redirectAttributes.addFlashAttribute("message", "수강신청이 완료되었습니다.");
        } catch (AlreadyEnrolledException e) {
            redirectAttributes.addFlashAttribute("message", "이미 수강 중인 강의입니다.");
        }

        // ★ 수강목록(전체) 페이지로 이동
        return "redirect:/myPage/courseList.html";
    }
}
