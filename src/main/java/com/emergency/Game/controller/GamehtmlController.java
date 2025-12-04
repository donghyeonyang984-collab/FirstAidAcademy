package com.emergency.Game.controller;

import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GamehtmlController {

    // 로그인 세션 키 (다른 컨트롤러와 동일하게 사용)
    private static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    // game_data 조회용
    private final JdbcTemplate jdbcTemplate;

    /* ================== 공통 세팅 ================== */

    // 게임 페이지 공통 세팅 (NewsController의 setupNews 와 같은 역할)
    private void setupGame(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "GAME");   // 헤더에서 GAME 메뉴 활성화
        model.addAttribute("showSidebar", true);    // 게임 사이드바 사용 (sidebarG)

        // layout.html에서 th:replace="~{${contentTemplate} :: content}" 로 사용
        model.addAttribute("contentTemplate", contentTemplate);

        // 페이지 전용 CSS
        model.addAttribute("pageCss", List.of(
                "/css/game/game.css"
        ));
    }

    // 세션에서 로그인 유저 꺼내기
    private LoginUser getLoginUser(HttpSession session) {
        Object obj = session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (obj instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    /* ================== game_data 조회 ================== */

    // game_data 에서 현재 회원의 star_levels 배열 불러오기
    // 값이 없으면 [0,0,0,0] 반환
    private int[] loadStarLevels(int userId) {
        String sql = "SELECT star_levels FROM game_data WHERE user_id = ?";
        try {
            String json = jdbcTemplate.queryForObject(sql, String.class, userId);
            if (json == null || json.isBlank()) {
                return new int[4];
            }

            // 예: "[1,0,3,2]" → "1,0,3,2"
            String cleaned = json.replaceAll("[\\[\\]\\s]", "");
            if (cleaned.isEmpty()) {
                return new int[4];
            }

            String[] parts = cleaned.split(",");
            int[] result = new int[4];   // 1: CPR, 2: 기도막힘, 3: 출혈, 4: 화상
            for (int i = 0; i < result.length && i < parts.length; i++) {
                try {
                    result[i] = Integer.parseInt(parts[i]);
                } catch (NumberFormatException e) {
                    result[i] = 0;
                }
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            // game_data 가 없으면 기본값
            return new int[4];
        }
    }

    // ★ starLevels → 배지 보유 여부로 변환
    // starLevels[0] = CPR, [1] = 기도막힘, [2] = 출혈, [3] = 화상
    private Map<String, Boolean> buildBadgeData(int[] starLevels) {
        Map<String, Boolean> badgeData = new HashMap<>();

        int len = (starLevels != null) ? starLevels.length : 0;

        // 1번째 : CPR
        badgeData.put("cpr",      len > 0 && starLevels[0] > 0);
        // 2번째 : 기도막힘
        badgeData.put("airway",   len > 1 && starLevels[1] > 0);
        // 3번째 : 출혈
        badgeData.put("bleeding", len > 2 && starLevels[2] > 0);
        // 4번째 : 화상
        badgeData.put("burn",     len > 3 && starLevels[3] > 0);

        return badgeData;
    }

    /* ================== 페이지 맵핑 ================== */

    /**
     * /game 또는 /game/ 로 들어오면
     * 게임 소개 페이지로 리다이렉트
     */
    @GetMapping({"/game", "/game/"})
    public String redirectToAboutGame() {
        return "redirect:/game/aboutGame";
    }

    /**
     * 게임 소개 페이지
     * URL: /game/aboutGame, /game/aboutGame.html
     * template: templates/game/aboutGame.html
     */
    @GetMapping({"/game/aboutGame", "/game/aboutGame.html"})
    public String aboutGame(Model model) {
        // templates/game/aboutGame.html :: content
        setupGame(model, "응급 처치 시뮬레이션 게임", "game/aboutGame");
        return "layout";
    }

    /**
     * 배지 보관함 페이지
     * URL: /game/myGame, /game/myGame.html
     * template: templates/game/myGame.html
     *
     * - 로그인한 회원만 접근 가능
     * - "회원" → 실제 유저 이름으로 표시
     * - game_data 의 star_levels 기준으로 뱃지 잠금/해제
     *   (1: CPR, 2: 기도막힘, 3: 출혈, 4: 화상)
     */
    @GetMapping({"/game/myGame", "/game/myGame.html"})
    public String myGame(Model model, HttpSession session) {

        // 1) 로그인 체크
        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";   // 미로그인 시 로그인 페이지로
        }

        // 2) 기본 레이아웃/사이드바 세팅
        setupGame(model, "배지 보관함", "game/myGame");

        // 3) 사용자 이름 (myGame.html 의 ${userName})
        String userName = loginUser.getName();
        model.addAttribute("userName", userName);

        // 4) game_data 에서 이 회원의 star_levels 조회
        int userId = loginUser.getUserId().intValue();
        int[] starLevels = loadStarLevels(userId);

        // 5) 배지 획득 여부 맵 구성 (myGame.html 의 badgeData 사용)
        Map<String, Boolean> badgeData = buildBadgeData(starLevels);
        model.addAttribute("badgeData", badgeData);

        // 6) 아직 날짜 저장 테이블이 없으므로 빈 맵 전달
        Map<String, String> badgeDates = new HashMap<>();
        model.addAttribute("badgeDates", badgeDates);

        return "layout";
    }

    /**
     * Unity 게임 실행
     * URL: /game/runUnity
     * Unity 실행 후 나의 배지 페이지로 이동
     */
    @GetMapping("/game/runUnity")
    public String runUnity(HttpSession session, Model model) {

        LoginUser loginUser = getLoginUser(session);
        String userId;
        String name;

        if (loginUser != null) {
            userId = String.valueOf(loginUser.getUserId());
            name = loginUser.getName();
        } else {
            userId = "guest";
            name = "게스트";
        }

        System.out.println("Unity에 전달할 userId: " + userId);
        System.out.println("사용자 이름: " + name);

        // 모델에 name 저장 (필요 시 화면에서 사용)
        model.addAttribute("Name", name);

        // Unity 실행
        String unityExePath = "C:\\Games\\MyUnityGame\\EmergencyGame.exe";
        try {
            // userId와 userName을 실행 인자로 전달
            ProcessBuilder pb = new ProcessBuilder(unityExePath, userId, name);
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/game/myGame";
    }

}
