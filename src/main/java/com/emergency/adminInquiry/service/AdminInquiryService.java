// src/main/java/com/emergency/adminInquiry/service/AdminInquiryService.java
package com.emergency.adminInquiry.service;

import com.emergency.adminInquiry.repository.AdminInquiryRepository;  // ✅ 관리자 전용 레포
import com.emergency.adminInquiry.service.dto.AdminInquiryDetailResult;
import com.emergency.adminInquiry.service.dto.AdminInquiryListItem;
import com.emergency.inquiry.domain.Inquiry;           // ← 이건 도메인(엔티티)라 공용으로 써도 됨
import com.emergency.inquiry.domain.InquiryAnswer;    // ← 마찬가지
import com.emergency.inquiry.domain.InquiryAttachment;
import com.emergency.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminInquiryService {

    private final AdminInquiryRepository adminInquiryRepository;

    @Transactional(readOnly = true)
    public int countInquiries(String status, String keyword) {
        return adminInquiryRepository.countInquiries(status, keyword);
    }

    @Transactional(readOnly = true)
    public List<AdminInquiryListItem> getInquiries(String status,
                                                   String keyword,
                                                   int page,
                                                   int size) {
        int offset = (page - 1) * size;
        return adminInquiryRepository.findInquiries(status, keyword, offset, size);
    }

    @Transactional(readOnly = true)
    public AdminInquiryDetailResult getDetail(Long inquiryId) {

        Inquiry inquiry = adminInquiryRepository.findInquiryById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. inquiryId=" + inquiryId));

        List<InquiryAttachment> attachments = adminInquiryRepository.findAttachmentsByInquiryId(inquiryId);
        InquiryAnswer answer = adminInquiryRepository.findAnswerByInquiryId(inquiryId).orElse(null);
        User user = adminInquiryRepository.findUserByInquiryId(inquiryId).orElse(null);

        AdminInquiryDetailResult result = new AdminInquiryDetailResult();
        result.setInquiry(inquiry);
        result.setAttachments(attachments);
        result.setAnswer(answer);

        if (user != null) {
            result.setUserName(user.getName());
            result.setUsername(user.getUsername());
        }

        return result;
    }

    public void saveAnswer(Long inquiryId, String adminName, String answerContent) {
        adminInquiryRepository.saveOrUpdateAnswer(inquiryId, adminName, answerContent);
    }
}
