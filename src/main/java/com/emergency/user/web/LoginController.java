package com.emergency.user.web;

import com.emergency.user.domain.User;
import com.emergency.user.service.UserService;
import com.emergency.user.web.form.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    // 약관 버전
    private static final String CURRENT_TERMS_VERSION = "v1.0";

    // 로그인 후 이동할 기본 URL
    private static final String ADMIN_HOME_URL = "/adminHome";
    private static final String USER_HOME_URL  = "/home";

    /** 공통: 레이아웃 기본 세팅 */
    private void setupAuthPage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", null);
        model.addAttribute("showSidebar", false);
        model.addAttribute("contentTemplate", contentTemplate);
        model.addAttribute("pageCss", List.of(
                "/css/login_css/login.css",
                "/css/join_css/join.css"
        ));
        model.addAttribute("pageJs", "/fragments_js/info.js");
    }

    /* ==============================
       ✅ 아이디 중복확인 (AJAX용 API)
       ============================== */
    @GetMapping("/api/users/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Map.of("exists", exists);
    }

    /* ==============================
       1) 회원가입 - 약관 동의
       ============================== */
    @GetMapping("/join/agreement")
    public String showAgreement(Model model) {
        if (!model.containsAttribute("termsForm")) {
            model.addAttribute("termsForm", new TermsForm());
        }
        setupAuthPage(model, "회원가입 - 약관 동의", "join/agreement");
        return "layout";
    }

    @PostMapping("/join/agreement")
    public String submitAgreement(@ModelAttribute("termsForm") TermsForm form,
                                  Model model,
                                  HttpSession session) {

        if (!form.isAgreeService() || !form.isAgreePrivacy() || !form.isAgreeLocation()) {
            model.addAttribute("error", "필수 약관에 모두 동의해야 다음 단계로 진행할 수 있습니다.");
            setupAuthPage(model, "회원가입 - 약관 동의", "join/agreement");
            return "layout";
        }

        session.setAttribute("termsAgreed", true);
        session.setAttribute("termsVersion", CURRENT_TERMS_VERSION);

        return "redirect:/join/info";
    }

    /* ==============================
       2) 회원가입 - 정보 입력
       ============================== */
    @GetMapping("/join/info")
    public String showJoinInfo(Model model, HttpSession session) {
        Boolean agreed = (Boolean) session.getAttribute("termsAgreed");
        if (agreed == null || !agreed) {
            return "redirect:/join/agreement";
        }

        if (!model.containsAttribute("joinForm")) {
            model.addAttribute("joinForm", new JoinForm());
        }

        setupAuthPage(model, "회원가입 - 정보 입력", "join/info");
        return "layout";
    }

    @PostMapping("/join/info")
    public String submitJoinInfo(@ModelAttribute("joinForm") JoinForm form,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        if (userService.existsByUsername(form.getUserid())) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            setupAuthPage(model, "회원가입 - 정보 입력", "join/info");
            return "layout";
        }

        String termsVersion = (String) session.getAttribute("termsVersion");
        if (termsVersion == null) termsVersion = CURRENT_TERMS_VERSION;

        User saved;
        try {
            saved = userService.registerUser(form, termsVersion);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            setupAuthPage(model, "회원가입 - 정보 입력", "join/info");
            return "layout";
        }

        session.removeAttribute("termsAgreed");
        session.removeAttribute("termsVersion");

        redirectAttributes.addFlashAttribute("name", saved.getName());
        redirectAttributes.addFlashAttribute("username", saved.getUsername());

        return "redirect:/join/completed";
    }

    /* ==============================
       3) 회원가입 완료
       ============================== */
    @GetMapping("/join/completed")
    public String joinCompleted(Model model) {
        String name = (String) model.asMap().get("name");
        String username = (String) model.asMap().get("username");
        if (name == null || name.isBlank()) name = "회원";
        if (username == null) username = "";

        model.addAttribute("name", name);
        model.addAttribute("username", username);
        setupAuthPage(model, "회원가입 완료", "join/joinCompleted");
        return "layout";
    }

    /* ==============================
   4) 로그인 화면
   ============================== */
    @GetMapping("/login")
    public String showLogin(
            @RequestParam(value = "redirectURL", defaultValue = USER_HOME_URL) String redirectURL,  // ✅ "/" → USER_HOME_URL 로 변경
            Model model) {

        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        model.addAttribute("redirectURL", redirectURL);
        setupAuthPage(model, "로그인", "login/login");
        return "layout";
    }

    @GetMapping("/login/login")
    public String showLoginAlias(
            @RequestParam(value = "redirectURL", defaultValue = USER_HOME_URL) String redirectURL,  // ✅ 동일하게 변경
            Model model) {
        return showLogin(redirectURL, model);
    }

    /* ==============================
       5) 로그인 처리
       ============================== */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute("loginForm") LoginForm form,
                          @RequestParam(value = "redirectURL", defaultValue = USER_HOME_URL) String redirectURL,  // ✅ "/" → USER_HOME_URL
                          Model model,
                          HttpSession session) {

        Optional<User> userOpt = userService.login(form.getUsername(), form.getPassword());
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "아이디 또는 비밀번호를 다시 확인해주세요.");
            model.addAttribute("redirectURL", redirectURL);
            setupAuthPage(model, "로그인", "login/login");
            return "layout";
        }

        User user = userOpt.get();
        LoginUser loginUser = new LoginUser(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getRole()
        );
        session.setAttribute("LOGIN_USER", loginUser);

        String role = user.getRole();
        String targetUrl = "Admin".equalsIgnoreCase(role)
                ? ADMIN_HOME_URL
                : (redirectURL != null && !redirectURL.isBlank() ? redirectURL : USER_HOME_URL);

        return "redirect:" + targetUrl;   // ✅ 일반 유저는 기본적으로 /home 으로 이동
    }


    /* ==============================
       6) 로그아웃
       ============================== */
    @GetMapping("/login/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();   // ✅ 로그인 정보 날리기
        }

        // ✅ 로그아웃 후 메인 페이지로 이동
        return "redirect:/home";
    }

    /* ==============================
       7) 아이디 찾기 (/login/findId)
       ============================== */
    @GetMapping("/login/findId")
    public String showFindIdPage(Model model) {
        model.addAttribute("findIdForm", new FindIdForm());
        setupAuthPage(model, "아이디 찾기", "login/findID");
        return "layout";
    }

    @PostMapping("/login/findid")
    public String processFindId(@ModelAttribute("findIdForm") FindIdForm form, Model model) {
        Optional<User> userOpt = userService.findUserByNameAndBirth(form.getName(), form.getBirth());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("foundUsername", user.getUsername());
            model.addAttribute("createdDate", user.getCreatedAt() != null
                    ? user.getCreatedAt().toLocalDate().toString() : "확인 불가");
            setupAuthPage(model, "아이디 확인", "login/IDCheck");
        } else {
            model.addAttribute("error", "입력하신 정보와 일치하는 회원이 없습니다.");
            setupAuthPage(model, "아이디 찾기", "login/findID");
        }
        return "layout";
    }

    /* ==============================
       8) 비밀번호 재설정
       ============================== */
    @GetMapping("/login/resetPW")
    public String showResetPwPage(Model model) {
        model.addAttribute("resetPwForm", new ResetPwForm());
        setupAuthPage(model, "비밀번호 재설정", "login/resetPW");
        return "layout";
    }

    @PostMapping("/login/resetPW")
    public String processResetPw(@ModelAttribute("resetPwForm") ResetPwForm form, Model model) {
        boolean success = userService.resetPassword(
                form.getUsername(),
                form.getName(),
                form.getNewPassword(),
                form.getConfirmPassword()
        );

        if (success) {
            model.addAttribute("username", form.getUsername());
            model.addAttribute("newPassword", form.getNewPassword());
            setupAuthPage(model, "비밀번호 확인", "login/PWCheck");
        } else {
            model.addAttribute("error", "입력하신 정보가 일치하지 않거나 비밀번호 확인이 다릅니다.");
            setupAuthPage(model, "비밀번호 재설정", "login/resetPW");
        }
        return "layout";
    }
}
