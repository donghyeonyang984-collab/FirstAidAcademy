package com.emergency.pages.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NewsController {

    private void setupNews(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "NEWS");
        model.addAttribute("showSidebar", true); // 소식센터 사이드바

        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of("/css/pages_css/news.css"));
        model.addAttribute("pageJs", List.of("/fragments_js/pageNationC.JS"));
    }



    // FAQ
    @GetMapping({"/pages/faq", "/pages/faq.html"})
    public String faq(Model model) {
        // templates/pages/faq.html
        setupNews(model, "FAQ", "pages/faq");
        return "layout";
    }
}
