package com.emergency.press.web;

import com.emergency.press.domain.PressRelease;
import com.emergency.press.service.PressReleaseService;
import com.emergency.user.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/adminPress")
@RequiredArgsConstructor
public class AdminPressController {

    private final PressReleaseService pressReleaseService;

    /** 보도자료 목록 + 검색 + 페이징 */
    // /adminPress
    // /adminPress/           (슬래시 붙여도)
    // /adminPress/adminPressRelease  세 URL 모두 이 메서드로 오게 함
    @GetMapping({"", "/", "/adminPressRelease"})
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "press_keyword", required = false) String keyword,
                       Model model) {

        int totalCount = pressReleaseService.count(keyword);

        // 페이지 방어 로직 (0 이하 요청 방지)
        if (page < 1) page = 1;

        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<PressRelease> pressList = pressReleaseService.findPage(page, size, keyword);

        model.addAttribute("pressList", totalCount == 0 ? List.of() : pressList);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("keyword", keyword);

        // 템플릿: src/main/resources/templates/adminPress/adminPressRelease.html
        return "adminPress/adminPressRelease";
    }

    /** 등록 폼 */
    // /adminPress/add
    // /adminPress/adminPressAdd  둘 다 이 메서드로
    @GetMapping({"/add", "/adminPressAdd"})
    public String showAddForm() {
        // 템플릿: templates/adminPress/adminPressAdd.html
        return "adminPress/adminPressAdd";
    }

    /** 등록 처리 */
    // POST /adminPress
    @PostMapping
    public String create(@RequestParam("pressTitle") String pressTitle,
                         @RequestParam("pressContent") String pressContent,
                         @RequestParam("addPressLink") String linkUrl,
                         HttpSession session) {

        PressRelease press = new PressRelease();
        press.setTitle(pressTitle);
        press.setContentHtml(pressContent);
        press.setLinkUrl(linkUrl);

        // 로그인 한 관리자 정보가 있으면 user_id에 세팅
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj instanceof User user) {
            press.setUserId(user.getUserId());
        }

        pressReleaseService.create(press);
        // 목록으로 리다이렉트
        return "redirect:/adminPress";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        // ★ Optional 이 아니라 엔티티 그대로 받는다
        PressRelease press = pressReleaseService.findById(id);
        if (press == null) {
            throw new IllegalArgumentException("존재하지 않는 보도자료입니다. id=" + id);
            // 또는 return "redirect:/adminPress"; 로 바꿔도 됨
        }

        model.addAttribute("press", press);
        return "adminPress/adminPressEdit";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("editPressTitle") String title,
                         @RequestParam("editPressContent") String content,
                         @RequestParam(name = "editPressLink", required = false) String linkUrl) {

        // ★ 마찬가지로 Optional 아님
        PressRelease press = pressReleaseService.findById(id);
        if (press == null) {
            throw new IllegalArgumentException("존재하지 않는 보도자료입니다. id=" + id);
        }

        press.setTitle(title);
        press.setContentHtml(content);

        if (linkUrl != null && !linkUrl.isBlank()) {
            press.setLinkUrl(linkUrl);
        }

        press.setPressReleaseId(id);
        pressReleaseService.update(press);

        return "redirect:/adminPress";
    }

    /** 삭제 처리 */
    // POST /adminPress/{id}/delete
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        pressReleaseService.delete(id);
        return "redirect:/adminPress";
    }
}
