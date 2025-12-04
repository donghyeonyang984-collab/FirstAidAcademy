package com.emergency.notice.web;

import com.emergency.notice.domain.Notice;
import com.emergency.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
// ✅ /notice 와 /pages/notice.html 둘 다 이 컨트롤러로 오게 처리
@RequestMapping({"/notice", "/pages/notice.html"})
public class NoticeController {

    private final NoticeService noticeService;

    /** 공통: 레이아웃 세팅 */
    private void setupNoticePage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "NEWS");  // 소식센터 메뉴 활성화
        model.addAttribute("showSidebar", true);   // 사이드바 사용 여부
        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of(
                "/css/fragments_css/common.css",
                "/css/fragments_css/components.css",
                "/css/notice_css/notice.css",
                "/css/pages_css/news.css"
        ));
        model.addAttribute("pageJs", List.of());
    }

    /** 공지 목록 + 검색 + 페이징
     *  - GET /notice
     *  - GET /notice?page=1&size=10&q=검색어
     *  - GET /pages/notice.html (헤더에서 이 경로로 들어와도 처리됨)
     */
    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "8") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        int totalCount = noticeService.count(keyword);
        if (page < 1) page = 1;

        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<Notice> notices = noticeService.findPage(page, size, keyword);

        // layout + content fragment
        setupNoticePage(model, "공지사항", "pages/notice");

        model.addAttribute("notices", notices);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("q", keyword);

        // templates/layout.html 사용
        return "layout";
    }

    /** 공지 상세
     *  - GET /notice/{id}
     *  - GET /pages/notice.html/{id}
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "q", required = false) String keyword,
                         Model model) {

        Notice notice = noticeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 이전글 / 다음글 ID 조회
        Long prevId = noticeService.findPrevId(id).orElse(null);
        Long nextId = noticeService.findNextId(id).orElse(null);

        setupNoticePage(model, "공지사항", "pages/noticeDetail");

        model.addAttribute("notice", notice);
        model.addAttribute("page", page);
        model.addAttribute("q", keyword);
        model.addAttribute("prevId", prevId);
        model.addAttribute("nextId", nextId);

        return "layout";
    }
}
