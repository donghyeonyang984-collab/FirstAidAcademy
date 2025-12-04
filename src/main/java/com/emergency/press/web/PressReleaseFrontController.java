package com.emergency.press.web;

import com.emergency.press.domain.PressRelease;
import com.emergency.press.service.PressReleaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({"/news", "/pages/news.html"})   // ★ /news, /news/{id} 전부 여기서 처리
@RequiredArgsConstructor
public class PressReleaseFrontController {

    private final PressReleaseService pressReleaseService;

    /**
     * 보도자료 목록 + 검색 + 페이징
     * GET /news?page=1&size=10&q=검색어
     */
    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "8") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        int totalCount = pressReleaseService.count(keyword);

        if (page < 1) page = 1;

        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<PressRelease> list = totalCount > 0
                ? pressReleaseService.findPage(page, size, keyword)
                : List.of();

        // 뷰에서 사용할 값들
        model.addAttribute("pressReleases", list);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        // layout 공통 세팅
        model.addAttribute("pageTitle", "보도자료");
        model.addAttribute("activeMenu", "NEWS");
        model.addAttribute("showSidebar", true);
        model.addAttribute("contentTemplate", "pages/news");  // templates/pages/news.html 프래그먼트

        model.addAttribute("pageCss", List.of(
                "/css/pages_css/news.css"
        ));
        model.addAttribute("pageJs", List.of());  // JS 없으면 빈 리스트

        return "layout";
    }

    /**
     * 보도자료 상세
     * GET /news/{id}
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         Model model) {

        PressRelease press = pressReleaseService.findById(id);
        if (press == null) {
            // 글이 없으면 목록으로
            return "redirect:/news";
        }

        PressRelease prev = pressReleaseService.findPrev(id);
        PressRelease next = pressReleaseService.findNext(id);

        model.addAttribute("press", press);
        model.addAttribute("prevPress", prev);
        model.addAttribute("nextPress", next);

        // layout 공통 세팅
        model.addAttribute("pageTitle", "보도자료 상세");
        model.addAttribute("activeMenu", "NEWS");
        model.addAttribute("showSidebar", true);
        model.addAttribute("contentTemplate", "pages/newsDetail"); // templates/pages/newsDetail.html

        model.addAttribute("pageCss", List.of(
                "/css/pages_css/news.css"
        ));
        model.addAttribute("pageJs", List.of());

        return "layout";
    }
}
