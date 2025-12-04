package com.emergency.notice.web;

import com.emergency.notice.domain.Notice;
import com.emergency.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/adminNotice")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 + 검색 + 페이징
     *  GET /adminNotice/adminNotice?page=1&size=10&q=검색어
     */
    @GetMapping("/adminNotice")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "q", required = false) String keyword,
                       Model model) {

        // 방어 코드
        if (size <= 0) {
            size = 10;
        }

        // 전체 개수
        int totalCount = noticeService.count(keyword);

        // 전체 페이지 수 (데이터 0개여도 1페이지로 처리해서 sequence 에러 방지)
        int totalPages = (totalCount == 0)
                ? 1
                : (int) Math.ceil((double) totalCount / size);

        // page 범위 보정
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // 현재 페이지 데이터 조회
        List<Notice> notices = noticeService.findPage(page, size, keyword);

        // 뷰에서 사용할 값 세팅
        model.addAttribute("notices", notices);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("q", keyword);

        // templates/adminNotice/adminNotice.html
        return "adminNotice/adminNotice";
    }

    /** 등록 폼 */
    @GetMapping("/adminNoticeAdd")
    public String showAddForm() {
        // templates/adminNotice/adminNoticeAdd.html
        return "adminNotice/adminNoticeAdd";
    }

    /** 등록 처리 */
    @PostMapping("/adminNoticeAdd")
    public String add(@RequestParam("title") String title,
                      @RequestParam("contentHtml") String contentHtml) {

        // TODO: 로그인 관리자 ID로 교체
        Long userId = 1L;
        noticeService.create(title, contentHtml, userId);

        return "redirect:/adminNotice/adminNotice";
    }

    /** 수정 폼 */
    @GetMapping("/adminNoticeEdit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Notice notice = noticeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음: " + id));

        model.addAttribute("notice", notice);
        // templates/adminNotice/adminNoticeEdit.html
        return "adminNotice/adminNoticeEdit";
    }

    /** 수정 처리 */
    @PostMapping("/adminNoticeEdit/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("title") String title,
                         @RequestParam("contentHtml") String contentHtml) {

        noticeService.update(id, title, contentHtml);
        return "redirect:/adminNotice/adminNotice";
    }

    /** 삭제 */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        noticeService.delete(id);
        return "redirect:/adminNotice/adminNotice";
    }
}
