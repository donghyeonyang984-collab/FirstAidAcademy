package com.emergency.enrollment.web;

import com.emergency.enrollment.domain.EnrollmentListItem;
import com.emergency.enrollment.service.EnrollmentService;
import com.emergency.enrollment.service.LecturePopupService;
import com.emergency.enrollment.service.LecturePopupService.LecturePopupResult;
import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * 나의강의실 - 수강 관련 3개 페이지 컨트롤러
 *
 * 1) 수강목록      : /myPage/courseList      or /myPage/courseList.html
 * 2) 미수료        : /myPage/nonCompletion  or /myPage/nonCompletion.html
 * 3) 수료          : /myPage/completion     or /myPage/completion.html
 */
@Controller
@RequiredArgsConstructor
public class MyEnrollmentController {

    private final EnrollmentService enrollmentService;
    private final LecturePopupService lecturePopupService;   // 🔹 팝업용 서비스
    // LoginController 에서 로그인 성공 시 사용하는 세션 키와 동일해야 함
    private static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    /**
     * 공통: 세션에서 로그인 유저 꺼내기
     */
    private LoginUser getLoginUser(HttpSession session) {
        Object obj = session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (obj instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    /**
     * 공통: layout.html 기본 세팅
     */
    private void setupMyPage(Model model, String title, String contentTemplate) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "MYCLASS");   // 나의강의실 메뉴 활성화
        model.addAttribute("showSidebar", true);       // 나의강의실 사이드바 사용
        model.addAttribute("contentTemplate", contentTemplate);

        model.addAttribute("pageCss", List.of(
//                "/css/courses_css/courses.css",
                "/css/myPage_css/courseList.css",
                "/css/myPage_css/coursePopUp.css",
                "/css/myPage_css/completion.css"


        ));
        // 필요 시 JS 추가
         model.addAttribute("pageJs",  List.of("/fragments_js/coursePopUp.js"));
    }

    /* --------------------------------------------------------------------
     * 1) 수강목록 (상태 전체)
     * ------------------------------------------------------------------ */
    @GetMapping({"/myPage/courseList", "/myPage/courseList.html"})
    public String courseList(@RequestParam(name = "mid", required = false) String midCategory,
                             @RequestParam(name = "keyword", required = false) String keyword, // [검색추가]
                             HttpSession session,
                             Model model) {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        List<EnrollmentListItem> enrollments =
                enrollmentService.getMyEnrollments(userId, null, midCategory);

        // [검색추가] 검색어가 있으면 제목에 keyword 포함된 것만 필터
        String trimmedKeyword = (keyword != null) ? keyword.trim() : null;
        if (trimmedKeyword != null && !trimmedKeyword.isEmpty()) {
            final String kw = trimmedKeyword;
            enrollments = enrollments.stream()
                    .filter(e -> e.getTitle() != null && e.getTitle().contains(kw))
                    .toList();
        }

        // 디버그 로그
        System.out.println("[MyEnrollmentController] /myPage/courseList userId="
                + userId + ", midCategory=" + midCategory
                + ", keyword=" + trimmedKeyword                              // [검색추가]
                + ", enrollments.size=" + (enrollments != null ? enrollments.size() : 0));

        // 수강목록 템플릿: templates/myPage/courseList.html
        setupMyPage(model, "수강 목록", "myPage/courseList");

        // 🔴 핵심: 템플릿에서 사용할 이름 두 개 다 넣어줌
        model.addAttribute("enrollments", enrollments);   // 새 이름
        model.addAttribute("courseList", enrollments);    // 예전 템플릿 호환용

        model.addAttribute("selectedMid", midCategory == null ? "" : midCategory);
        model.addAttribute("statusFilter", "ALL");
        model.addAttribute("keyword", trimmedKeyword == null ? "" : trimmedKeyword); // [검색추가]

        return "layout";
    }

    // MyEnrollmentController.java

    /* --------------------------------------------------------------------
     * 2) 미수료 강의 목록
     * ------------------------------------------------------------------ */
    @GetMapping({"/myPage/nonCompletion", "/myPage/nonCompletion.html"})
    public String nonCompletionList(@RequestParam(name = "mid", required = false) String midCategory,
                                    HttpSession session,
                                    Model model) {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        // 1) 상태 필터 없이 전체 수강 목록 가져오기
        List<EnrollmentListItem> allEnrollments =
                enrollmentService.getMyEnrollments(userId, null, midCategory);

        // 2) "미수료" 상태이거나,
        //    진도율이 100%인데 아직 "수료" 상태가 아닌 것만 필터링
        List<EnrollmentListItem> enrollments = allEnrollments.stream()
                .filter(e -> {
                    // 상태 기준
                    boolean isNonCompletionStatus = "미수료".equals(e.getStatus());

                    // 진도율 기준 (필드 타입/이름에 맞게 getProgress() 부분만 수정)
                    Number progress = e.getProgressPercent();  // 예: getProgressPercent() 로 변경 가능
                    boolean isFullProgress =
                            (progress != null && progress.doubleValue() >= 100.0);

                    boolean isCompletedStatus = "수료".equals(e.getStatus());

                    // ① 원래부터 미수료이거나
                    // ② 진도율이 100%인데 수료는 아닌 경우 → 미수료 목록에 포함
                    return isNonCompletionStatus || (isFullProgress && !isCompletedStatus);
                })
                .toList();

        System.out.println("[MyEnrollmentController] /myPage/nonCompletion userId="
                + userId + ", midCategory=" + midCategory
                + ", enrollments.size=" + (enrollments != null ? enrollments.size() : 0));

        setupMyPage(model, "미수료 강의", "myPage/nonCompletion");

        // 상태/탭만 다르고 나머지 구조는 동일
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("courseList", enrollments);    // 호환용

        model.addAttribute("selectedMid", midCategory == null ? "" : midCategory);
        model.addAttribute("statusFilter", "INCOMPLETE");

        return "layout";
    }

    /* --------------------------------------------------------------------
     * 3) 수료 강의 목록
     * ------------------------------------------------------------------ */

    @GetMapping({"/myPage/completion", "/myPage/completion.html"})
    public String completionList(
            @RequestParam(name = "mid", required = false) String midCategory,
            HttpSession session,
            Model model) {


        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        List<EnrollmentListItem> enrollments =
                enrollmentService.getMyEnrollments(userId, "수료", midCategory);

        setupMyPage(model, "수료 강의", "myPage/completion");

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("courseList", enrollments);
        model.addAttribute("selectedMid", midCategory == null ? "" : midCategory);
        model.addAttribute("statusFilter", "COMPLETED");

        return "layout";
    }
    /* --------------------------------------------------------------------
     * 4) 강의 재생 팝업 (AJAX로 불러오는 모달 fragment)
     * ------------------------------------------------------------------ */
//    /** 강의 재생 팝업 */
    @GetMapping("/myPage/lecturePopup")
    public String lecturePopup(@RequestParam Long enrollmentId,
                               @RequestParam Long courseId,
                               HttpSession session,
                               Model model) {

        LoginUser loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/login";
        }

        Long userId = loginUser.getUserId();

        // 🔥 핵심: 이 enrollmentId 가 로그인한 userId 의 것인지 검증
        boolean isMine = enrollmentService.isMyEnrollment(enrollmentId, userId);
        if (!isMine) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        // 정상적으로 본인 강의라면 팝업 정보 로드
        LecturePopupResult result = lecturePopupService.loadPopup(enrollmentId, courseId);

        model.addAttribute("enrollmentId", enrollmentId);
        model.addAttribute("lecture", result.currentLecture());
        model.addAttribute("lectureList", result.lectureList());

        return "myPage/coursePopUp :: lecturePopup";
    }
}

