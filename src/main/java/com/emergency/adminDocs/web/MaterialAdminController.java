package com.emergency.adminDocs.web;

import com.emergency.adminDocs.domain.Material;
import com.emergency.adminDocs.domain.MidCategory;
import com.emergency.adminDocs.domain.TopCategory;
import com.emergency.adminDocs.service.MaterialService;
import com.emergency.user.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/adminEdu")
@RequiredArgsConstructor
public class MaterialAdminController {

    private final MaterialService materialService;

    // ★ 목록 + 검색 + 페이징
    @GetMapping("/adminEdu")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "category", required = false) String category,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       Model model) {

        if (size <= 0) size = 10;

        // 총 건수
        int totalCount = materialService.count(category, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages == 0) {
            page = 1;
        } else {
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;
        }

        // 한 페이지 목록
        List<Material> materials = materialService.search(category, keyword, page, size);

        // 페이지 번호 (현재 기준 양옆 2개씩)
        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        // 🔹 여기부터 추가: 번호 시작값 (최신 글이 가장 큰 번호)
        int startNo = totalCount - (page - 1) * size;
        if (startNo < 1) {
            startNo = 1;
        }

        model.addAttribute("materials", materials);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("size", size);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 🔹 추가
        model.addAttribute("startNo", startNo);

        return "adminEdu/adminEdu";
    }

    /** 등록 폼 */
    @GetMapping("/adminEduAdd")
    public String showAddForm(Model model) {
        model.addAttribute("topCategories", TopCategory.values());
        model.addAttribute("midCategories", MidCategory.values());
        return "adminEdu/adminEduAdd"; // adminEduAdd.html
    }

    /** 등록 처리 */
    @PostMapping("/adminEduAdd")
    public String add(@RequestParam("title") String title,
                      @RequestParam("content") String content,
                      @RequestParam("topCategory") TopCategory topCategory,
                      @RequestParam("midCategory") MidCategory midCategory,
                      @RequestParam("pdfFile") MultipartFile pdfFile,
                      HttpSession session) throws Exception {

        Long userId = extractUserId(session);

        materialService.create(title, content, topCategory, midCategory, userId, pdfFile);

        return "redirect:/adminEdu/adminEdu";
    }

    /** 수정 폼 */
    @GetMapping("/adminEduEdit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Material material = materialService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("교육자료를 찾을 수 없습니다. id=" + id));

        model.addAttribute("material", material);      // ★ 이 이름으로 씀
        model.addAttribute("topCategories", TopCategory.values());
        model.addAttribute("midCategories", MidCategory.values());
        return "adminEdu/adminEduEdit";
    }

    /** 수정 처리 */
    @PostMapping("/adminEduEdit/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam("topCategory") TopCategory topCategory,
                         @RequestParam("midCategory") MidCategory midCategory,
                         @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                         HttpSession session) throws Exception {

        Long userId = extractUserId(session);

        materialService.update(id, title, content, topCategory, midCategory, userId, pdfFile);

        return "redirect:/adminEdu/adminEdu";
    }

    /** 삭제 */
    @PostMapping("/adminEduDelete/{id}")
    public String delete(@PathVariable("id") Long id) {
        materialService.delete(id);
        return "redirect:/adminEdu/adminEdu";
    }

    /**
     * PDF 다운로드 (관리자 화면에서 사용)
     *  - DB BLOB 에서 바로 읽어서 전송
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") Long id) throws Exception {
        Material material = materialService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("교육자료를 찾을 수 없습니다. id=" + id));

        byte[] data = material.getPdfData();
        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String filename = material.getPdfFilename();
        if (filename == null || filename.isBlank()) {
            filename = material.getTitle() + ".pdf";
        }

        String encodedName = encodeFileName(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    // -------------------- 내부 유틸 -------------------- //

    private Long extractUserId(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser instanceof User user) {
            return user.getUserId();
        }
        // 아직 로그인 연동 안 되어 있으면 임시로 1번 사용자
        return 1L;
    }

    private String encodeFileName(String fileName) throws UnsupportedEncodingException {
        // 공백을 %20 으로
        return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
    }
}
