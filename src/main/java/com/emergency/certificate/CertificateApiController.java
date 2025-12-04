package com.emergency.certificate;

import com.emergency.user.web.LoginUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CertificateApiController {

    private final CertificateService certificateService;

    @GetMapping("/api/certificate/{enrollmentId}")
    public CertificateDto getCertificate(@PathVariable Long enrollmentId,
                                         HttpSession session) {

        LoginUser loginUser = (LoginUser) session.getAttribute("LOGIN_USER");
        Long userId = loginUser.getUserId();

        return certificateService.buildCertificate(enrollmentId, userId);
    }
}
