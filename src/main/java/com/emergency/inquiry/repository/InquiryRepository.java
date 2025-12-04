// src/main/java/com/emergency/inquiry/repository/InquiryRepository.java
package com.emergency.inquiry.repository;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository {

    Long saveInquiry(Inquiry inquiry);

    void saveAttachments(Long inquiryId, List<InquiryAttachment> attachments);

    List<Inquiry> findUserInquiries(Long userId, String category, String keyword,
                                    int offset, int limit);

    int countUserInquiries(Long userId, String category, String keyword);

    Optional<Inquiry> findInquiry(Long inquiryId, Long userId);

    Optional<InquiryAnswer> findAnswerByInquiryId(Long inquiryId);

    List<InquiryAttachment> findAttachmentsByInquiryId(Long inquiryId);

    Optional<Long> findPrevId(Long userId, Long currentId);

    Optional<Long> findNextId(Long userId, Long currentId);
}
