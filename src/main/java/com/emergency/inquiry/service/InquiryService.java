// src/main/java/com/emergency/inquiry/service/InquiryService.java
package com.emergency.inquiry.service;

import com.emergency.inquiry.domain.Inquiry;
import com.emergency.inquiry.domain.InquiryAnswer;
import com.emergency.inquiry.domain.InquiryAttachment;
import com.emergency.inquiry.repository.InquiryRepository;
import com.emergency.inquiry.service.dto.InquiryDetailResult;
import com.emergency.inquiry.web.form.InquiryForm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    // ✅ 파일 저장 위치 (프로퍼티에서 오버라이드 가능)
    @Value("${file.upload.inquiry-dir:uploads/inquiry}")
    private String inquiryUploadDir;

    @PostConstruct
    public void initUploadDir() {
        try {
            Path basePath = Paths.get(inquiryUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new IllegalStateException("문의 첨부파일 저장 경로를 생성할 수 없습니다: " + inquiryUploadDir, e);
        }
    }

    public Long createInquiry(Long userId, InquiryForm form, List<MultipartFile> files) throws IOException {
        Inquiry inquiry = new Inquiry();
        inquiry.setUserId(userId);
        inquiry.setCategory(form.getCategory());
        inquiry.setTitle(form.getTitle());
        inquiry.setContent(form.getContent());

        Long inquiryId = inquiryRepository.saveInquiry(inquiry);

        List<InquiryAttachment> attachments = storeFiles(inquiryId, files);
        inquiryRepository.saveAttachments(inquiryId, attachments);

        return inquiryId;
    }

    /** ✅ 파일 저장 시, 항상 디렉터리부터 만들어 놓고 저장 */
    private List<InquiryAttachment> storeFiles(Long inquiryId, List<MultipartFile> files) throws IOException {
        List<InquiryAttachment> result = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return result;
        }

        // uploads/inquiry 절대경로
        Path uploadPath = Paths.get(inquiryUploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);  // 폴더가 없으면 무조건 생성

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String storedName = System.currentTimeMillis() + "_" + originalFilename;

            Path target = uploadPath.resolve(storedName);
            Files.createDirectories(target.getParent()); // 혹시 몰라서 한 번 더 보장

            // 실제 파일 저장
            file.transferTo(target.toFile());

            InquiryAttachment att = new InquiryAttachment();
            att.setInquiryId(inquiryId);
            // 화면에서 다운로드할 때 쓸 URL (나중에 ResourceHandler 로 매핑 예정)
            att.setFilePath("/uploads/inquiry/" + storedName);

            result.add(att);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Inquiry> getUserInquiries(Long userId, String category, String keyword,
                                          int page, int size) {

        int offset = (page - 1) * size;
        return inquiryRepository.findUserInquiries(userId, category, keyword, offset, size);
    }

    @Transactional(readOnly = true)
    public int countUserInquiries(Long userId, String category, String keyword) {
        return inquiryRepository.countUserInquiries(userId, category, keyword);
    }

    @Transactional(readOnly = true)
    public InquiryDetailResult getUserInquiryDetail(Long userId, Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findInquiry(inquiryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("조회할 수 없는 문의입니다."));

        InquiryDetailResult result = new InquiryDetailResult();
        result.setInquiry(inquiry);

        InquiryAnswer answer = inquiryRepository.findAnswerByInquiryId(inquiryId).orElse(null);
        result.setAnswer(answer);

        List<InquiryAttachment> attachments = inquiryRepository.findAttachmentsByInquiryId(inquiryId);
        result.setAttachments(attachments);

        result.setPrevId(inquiryRepository.findPrevId(userId, inquiryId).orElse(null));
        result.setNextId(inquiryRepository.findNextId(userId, inquiryId).orElse(null));

        return result;
    }
}
