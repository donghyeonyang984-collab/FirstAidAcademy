package com.emergency.myPage.controllers;

import com.emergency.enrollment.domain.MyStudyCourseItem;
import com.emergency.enrollment.domain.MyStudyStatusCount;
import com.emergency.enrollment.service.EnrollmentService;
import com.emergency.user.domain.User;
import com.emergency.user.service.UserService;
import com.emergency.user.web.LoginUser;
import com.emergency.user.web.form.UserProfileForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MyPageController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    public MyPageController(EnrollmentService enrollmentService,
                            UserService userService) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
    }

    private void setupMyPage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "MYCLASS");
        model.addAttribute("showSidebar", true); // 나의 강의실 사이드바

        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of(
                "/css/join_css/join.css",
                "/css/myPage_css/myStudy.css",
                "/css/pages_css/news.css"
        ));
        model.addAttribute("pageJs", List.of(
                "/fragments_js/pageNationC.JS",
                "/fragments_js/infoModify.js",
                "/fragments_js/coursePopUp.js",
                "/fragments_js/courseList.js"
        ));
    }

    // ===================== 나의 학습활동 =====================

    @GetMapping({"/myPage/myStudy", "/myPage/myStudy.html"})
    public String myStudy(Model model,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        // ✅ 세션에서 LoginUser 꺼내기
        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");

        // 로그인 안 된 경우
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("loginMessage", "로그인이 필요한 서비스입니다.");
            return "redirect:/login";
        }

        // ✅ 로그인 유저의 id 꺼내기
        Long userId = loginUser.getUserId();

        // ✅ 상태별 수강 현황 조회
        MyStudyStatusCount counts = enrollmentService.getMyStudyStatusCount(userId);

        model.addAttribute("studyingCount", counts.getStudyingCount());
        model.addAttribute("completedCount", counts.getCompletedCount());
        model.addAttribute("notCompletedCount", counts.getNotCompletedCount());

        // ✅ 강의 이어 보기 리스트
        List<MyStudyCourseItem> ongoingCourses =
                enrollmentService.getMyOngoingCoursesForMyStudy(userId);
        model.addAttribute("ongoingCourses", ongoingCourses);

        // 마이페이지 공통 세팅
        setupMyPage(model, "나의 학습활동", "myPage/myStudy");
        return "layout";
    }



    // ===================== 회원정보 수정 =====================

    /**
     * 회원정보 수정 화면 (GET)
     * templates/myPage/infoModify.html 를 content로 사용
     */
    @GetMapping({"/myPage/infoModify", "/myPage/infoModify.html"})
    public String infoModify(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("loginMessage", "로그인이 필요한 서비스입니다.");
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        // 이미 redirect 후 플래시로 form을 넣어둔 경우 덮어쓰지 않음
        if (!model.containsAttribute("userProfileForm")) {

            Optional<User> optionalUser = userService.findById(userId);
            User user = optionalUser.orElseThrow(
                    () -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다.")
            );

            UserProfileForm form = new UserProfileForm();
            form.setUserId(user.getUserId());
            form.setUsername(user.getUsername());
            form.setName(user.getName());
            form.setGender(user.getGender());

            LocalDate birth = user.getBirthdate();
            form.setBirth(birth != null ? birth.toString() : "");

            form.setPhone(user.getPhone());
            form.setEmail(user.getEmail());
            form.setAddress(user.getAddress());
            // 새 비밀번호/확인은 비워둠

            model.addAttribute("userProfileForm", form);
        }

        setupMyPage(model, "회원정보 수정", "myPage/infoModify");
        return "layout";
    }

    /**
     * 회원정보 수정 처리 (POST)
     * infoModify.html 의 form action="/myPage/infoModify" method="post" 로 맞추기
     */
    @PostMapping("/myPage/infoModify")
    public String updateProfile(HttpSession session,
                                @ModelAttribute("userProfileForm") UserProfileForm form,
                                RedirectAttributes redirectAttributes) {

        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // JS에서 1차 검증을 다 했다고 가정하고, 여기서는 바로 서비스 호출
        userService.updateProfile(loginUser.getUserId(), form);

        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/myPage/infoModify";
    }
    /**
     * 현재 비밀번호 검증 (AJAX)
     * : 입력한 현재 비밀번호가 DB와 일치하는지 체크
     */
    @PostMapping("/myPage/checkPassword")
    @ResponseBody
    public Map<String, Object> checkPassword(HttpSession session,
                                             @RequestParam("currentPassword") String currentPassword) {

        Map<String, Object> result = new HashMap<>();

        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            result.put("valid", false);
            result.put("message", "로그인이 필요한 서비스입니다.");
            return result;
        }

        boolean ok = userService.checkCurrentPassword(loginUser.getUserId(), currentPassword);

        result.put("valid", ok);
        if (!ok) {
            result.put("message", "현재 비밀번호가 일치하지 않습니다.");
        }
        return result;
    }
}
