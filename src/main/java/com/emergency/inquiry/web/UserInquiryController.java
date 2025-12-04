package com.emergency.inquiry.web;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.service.InquiryService;
import com.emergency.inquiry.service.dto.InquiryDetailResult;
import com.emergency.inquiry.web.form.InquiryForm;
import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class UserInquiryController {

    private final InquiryService inquiryService;

    private static final int PAGE_SIZE = 6;

    /** ✅ 마이페이지 공통 레이아웃 세팅 (MyPageController 와 동일 패턴) */
    private void setupMyPage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "MYCLASS");
        model.addAttribute("showSidebar", true); // 나의 강의실 사이드바

        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of("/css/pages_css/news.css",
                "/css/myPage_css/questionDetail.css"));
        model.addAttribute("pageJs", List.of());
    }

    /** 로그인 사용자 가져오기 (없으면 null) */
    private LoginUser getLoginUser(HttpSession session) {
        return (LoginUser) session.getAttribute("LOGIN_USER");
    }

    /** ✅ 문의 목록
     *  - /myPage/questions
     *  - /myPage/question.html (예전 링크 호환)
     */
    @GetMapping({"/questions", "/question.html"})
    public String list(HttpSession session,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";   // 로그인 안 되어 있으면 로그인 페이지로 이동
        }

        Long userId = loginUser.getUserId();

        int totalCount = inquiryService.countUserInquiries(userId, category, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Inquiry> inquiries =
                inquiryService.getUserInquiries(userId, category, keyword, page, PAGE_SIZE);

        model.addAttribute("inquiries", inquiries);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);

        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("totalCount", totalCount);

        // 🔹 레이아웃 + fragment 방식
        setupMyPage(model, "문의 사항", "myPage/question");
        return "layout";
    }

    /** ✅ 문의 작성 폼
     *  - /myPage/questions/new
     */
    @GetMapping("/questions/new")
    public String showForm(HttpSession session, Model model) {
        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("form", new InquiryForm());
        model.addAttribute("writerId", loginUser.getUsername());

        setupMyPage(model, "문의 등록", "myPage/uploadQuestion");
        return "layout";
    }

    /** ✅ 문의 등록 */
    @PostMapping("/questions/new")
    public String submit(HttpSession session,
                         @Valid @ModelAttribute("form") InquiryForm form,
                         BindingResult bindingResult,
                         @RequestParam(value = "files", required = false) List<MultipartFile> files,
                         Model model) throws IOException {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("writerId", loginUser.getUsername());
            setupMyPage(model, "문의 등록", "myPage/uploadQuestion");
            return "layout";
        }

        if (files == null) {
            files = Collections.emptyList();
        }

        Long inquiryId =
                inquiryService.createInquiry(loginUser.getUserId(), form, files);

        // 등록 후 상세 페이지로 이동
        return "redirect:/myPage/questions/" + inquiryId;
    }

    /** ✅ 문의 상세 */
    @GetMapping("/questions/{inquiryId}")
    public String detail(@PathVariable Long inquiryId,
                         HttpSession session,
                         Model model) {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        InquiryDetailResult detail =
                inquiryService.getUserInquiryDetail(loginUser.getUserId(), inquiryId);
        model.addAttribute("detail", detail);

        setupMyPage(model, "문의 상세", "myPage/questionDetail");
        return "layout";
    }
}
