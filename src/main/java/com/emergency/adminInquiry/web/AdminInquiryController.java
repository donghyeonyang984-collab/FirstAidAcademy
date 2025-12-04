// src/main/java/com/emergency/adminInquiry/web/AdminInquiryController.java
package com.emergency.adminInquiry.web;

import com.emergency.adminInquiry.service.AdminInquiryService;
import com.emergency.adminInquiry.service.dto.AdminInquiryDetailResult;
import com.emergency.adminInquiry.service.dto.AdminInquiryListItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
// ✔ /AdminInquiry, /adminInquiry 둘 다 허용
@RequestMapping({"/AdminInquiry", "/adminInquiry"})
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    // 한 페이지당 문의 수
    private static final int PAGE_SIZE = 10;
    // 페이징 블럭 사이즈 (페이지 번호 몇 개씩 보여줄지)
    private static final int PAGE_BLOCK_SIZE = 5;

    /**
     * 관리자 문의 목록
     */
    @GetMapping({"", "/", "/AdminInquiry", "/adminInquiry"})
    public String list(@RequestParam(name = "inquiry_status", required = false) String status,
                       @RequestParam(name = "inquiry_keyword", required = false) String keyword,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       Model model) {

        // 🔹 상태값 정리: '전체' 또는 빈값이면 필터 안 걸리도록 null 처리
        if (status != null) {
            status = status.trim();
            if (status.isBlank() || "전체".equals(status)) {
                status = null;
            }
        }

        // 🔹 검색어도 공백만 있으면 null 처리
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isBlank()) {
                keyword = null;
            }
        }

        if (page < 1) {
            page = 1;
        }

        int totalCount = adminInquiryService.countInquiries(status, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<AdminInquiryListItem> inquiries =
                adminInquiryService.getInquiries(status, keyword, page, PAGE_SIZE);

        // 🔹 페이징 블럭(startPage, endPage) 계산
        int startPage = ((page - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;
        int endPage = startPage + PAGE_BLOCK_SIZE - 1;
        if (endPage > totalPages) {
            endPage = totalPages;
        }

        // 목록 + 검색/필터 값
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("status", status);      // ⬅️ 템플릿에서 status 로 쓰고 있음
        model.addAttribute("keyword", keyword);

        // 페이징 정보
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 템플릿에서 size, pageSize 둘 다 쓰고 있어서 그대로 유지
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("size", PAGE_SIZE);

        return "adminInquiry/adminInquiry";
    }

    /**
     * 문의 상세 + 답변 폼
     */
    @GetMapping({"/AdminInquiryReply/{inquiryId}", "/adminInquiryReply/{inquiryId}"})
    public String detail(@PathVariable Long inquiryId,
                         Model model) {

        AdminInquiryDetailResult detail = adminInquiryService.getDetail(inquiryId);
        model.addAttribute("detail", detail);

        return "adminInquiry/adminInquiryReply";
    }

    /**
     * 답변 저장
     */
    @PostMapping({"/AdminInquiryReply/{inquiryId}", "/adminInquiryReply/{inquiryId}"})
    public String submitReply(@PathVariable Long inquiryId,
                              @RequestParam("replyContent") String replyContent,
                              HttpSession session) {

        // TODO: 실제 관리자 이름 세션에서 꺼내기
        String adminName = "관리자";

        adminInquiryService.saveAnswer(inquiryId, adminName, replyContent);

        // 저장 후 다시 상세로 이동
        return "redirect:/AdminInquiry/AdminInquiryReply/" + inquiryId;
    }
}
