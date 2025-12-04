package com.emergency.home.controllers; // ✅ 프로젝트 패키지에 맞게 변경

import com.emergency.home.dto.HomeNoticeItem;
import com.emergency.home.dto.HomePressItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.emergency.home.service.HomeService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    /**
     * 첫 진입: 인트로 화면
     *  - http://localhost:8080/  → intro.html 렌더링
     */
    @GetMapping("/")
    public String intro() {
        // src/main/resources/templates/intro.html
        return "intro";
    }

    /**
     * 실제 메인 홈 화면
     *  - http://localhost:8080/home  → layout + home.html 프래그먼트
     */
    @GetMapping("/home")
    public String home(Model model) {

        // layout.html에서 쓰는 공통 속성들 :contentReference[oaicite:0]{index=0}
        model.addAttribute("pageTitle", "First Aid Academy - Home");
        model.addAttribute("activeMenu", "HOME");   // 헤더에서 HOME 메뉴 활성화용
        model.addAttribute("showSidebar", false);   // 홈은 사이드바 없음

        // home.html 안의 th:fragment="content" 를 layout에 끼워 넣기 :contentReference[oaicite:1]{index=1}
        model.addAttribute("contentTemplate", "home"); // templates/home.html

        // 필요하면 홈 전용 CSS/JS도 나중에 추가 가능
         model.addAttribute("pageCss", List.of("/css/home.css" ));
//         model.addAttribute("pageJs",  List.of("/fragmets_js/home.js"));
        // 🔹 여기부터 추가: 최신 보도자료 / 공지사항 5개
        List<HomePressItem> latestPress = homeService.getLatestPress();
        List<HomeNoticeItem> latestNotices = homeService.getLatestNotices();

        model.addAttribute("latestPress", latestPress);
        model.addAttribute("latestNotices", latestNotices);


        return "layout";  // templates/layout.html 사용 :contentReference[oaicite:2]{index=2}
    }
}
