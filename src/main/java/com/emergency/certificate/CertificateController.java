package com.emergency.certificate;

import ch.qos.logback.core.model.Model;
import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/certificate/view")
    public String showCertificate(@RequestParam Long enrollmentId,
                                  HttpSession session,
                                  org.springframework.ui.Model model) {

        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        Long userId = loginUser.getUserId();

        CertificateDto dto = certificateService.buildCertificate(enrollmentId, userId);
        model.addAttribute("cert", dto);

        return "/certificate";
    }
}