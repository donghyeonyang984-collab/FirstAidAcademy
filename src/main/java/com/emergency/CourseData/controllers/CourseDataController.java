package com.emergency.CourseData.controllers;

import com.emergency.adminDocs.domain.Material;
import com.emergency.adminDocs.repository.MaterialRepository;
import com.emergency.adminDocs.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseDataController {

    private final MaterialService materialService;

    /**
     * 공통: 교육자료 페이지 기본 세팅
     */
    private void setupDataPage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "COURSE"); // 같은 메뉴 그룹
        model.addAttribute("showSidebar", true);
        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of(
                "/css/courses_css/courseData.css"

        ));
//        model.addAttribute("pageJs", List.of("/fragments_js/pageNationC.JS"));
    }

    /**
     * 공통: 상단 필터(topCategory) + 검색(keyword) 적용
     */
    private List<Material> applyFilter(List<Material> materials,
                                       String topCategory,
                                       String keyword) {

        boolean hasTop = topCategory != null && !topCategory.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        return materials.stream()
                // 상단 카테고리 필터 (구조자 / 자가)
                .filter(m -> {
                    if (!hasTop) return true;              // 선택 안 했으면 통과
                    if (m.getTopCategory() == null) return false;

                    // DB에 들어 있는 한글 값 (구조자 / 자가)
                    String dbValue = m.getTopCategory().getDbValue();
                    return topCategory.equals(dbValue);
                })
                // 제목 검색
                .filter(m -> {
                    if (!hasKeyword) return true;
                    String title = m.getTitle();
                    return title != null && title.contains(keyword);
                })
                .toList();
    }

    // =========================
    //  교육자료 - 전체 (mid_category 전체)
    // =========================
    @GetMapping({"/courses/data", "/courses/data.html"})
    public String dataAll(@RequestParam(name = "topCategory", required = false) String topCategory,
                          @RequestParam(name = "keyword", required = false) String keyword,
                          Model model) {

        setupDataPage(model, "교육 자료", "courses/data");

        // 전체 자료
        List<Material> materials = materialService.findAll();
        materials = applyFilter(materials, topCategory, keyword);

        model.addAttribute("materials", materials);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentMidCategoryLabel", "전체");
        model.addAttribute("currentMidCategoryCode", "ALL");

        return "layout";
    }

    // =========================
    //  교육자료 - 출혈 (MID_1)
    // =========================
    @GetMapping({"/courses/data1", "/courses/data1.html"})
    public String dataBleeding(@RequestParam(name = "topCategory", required = false) String topCategory,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               Model model) {

        setupDataPage(model, "교육 자료 - 출혈", "courses/data1");

        // 기본 리스트: mid_category = 출혈
        List<Material> materials = materialService.findByMidCategory("출혈");
        materials = applyFilter(materials, topCategory, keyword);

        model.addAttribute("materials", materials);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentMidCategoryLabel", "출혈");
        model.addAttribute("currentMidCategoryCode", "출혈");

        return "layout";
    }

    // =========================
    //  교육자료 - 기도막힘 (MID_2)
    // =========================
    @GetMapping({"/courses/data2", "/courses/data2.html"})
    public String dataAirway(@RequestParam(name = "topCategory", required = false) String topCategory,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             Model model) {

        setupDataPage(model, "교육 자료 - 기도막힘", "courses/data2");

        List<Material> materials = materialService.findByMidCategory("기도막힘");
        materials = applyFilter(materials, topCategory, keyword);

        model.addAttribute("materials", materials);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentMidCategoryLabel", "기도막힘");
        model.addAttribute("currentMidCategoryCode", "기도막힘");

        return "layout";
    }

    // =========================
    //  교육자료 - 심정지 (MID_3)
    // =========================
    @GetMapping({"/courses/data3", "/courses/data3.html"})
    public String dataCardiac(@RequestParam(name = "topCategory", required = false) String topCategory,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              Model model) {

        setupDataPage(model, "교육 자료 - 심정지", "courses/data3");

        List<Material> materials = materialService.findByMidCategory("심정지");
        materials = applyFilter(materials, topCategory, keyword);

        model.addAttribute("materials", materials);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentMidCategoryLabel", "심정지");
        model.addAttribute("currentMidCategoryCode", "심정지");

        return "layout";
    }

    // =========================
    //  교육자료 - 화상 (MID_4)
    // =========================
    @GetMapping({"/courses/data4", "/courses/data4.html"})
    public String dataBurn(@RequestParam(name = "topCategory", required = false) String topCategory,
                           @RequestParam(name = "keyword", required = false) String keyword,
                           Model model) {

        setupDataPage(model, "교육 자료 - 화상", "courses/data4");

        List<Material> materials = materialService.findByMidCategory("화상");
        materials = applyFilter(materials, topCategory, keyword);

        model.addAttribute("materials", materials);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentMidCategoryLabel", "화상");
        model.addAttribute("currentMidCategoryCode", "화상");

        return "layout";
    }
}
