package com.emergency.adminDocs.service;

import com.emergency.adminDocs.domain.Material;
import com.emergency.adminDocs.domain.MidCategory;
import com.emergency.adminDocs.domain.TopCategory;
import com.emergency.adminDocs.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    public int count(String topCategory, String keyword) {
        return materialRepository.count(topCategory, keyword);
    }

    public List<Material> search(String topCategory,
                                 String keyword,
                                 int page,
                                 int size) {
        if (page < 1) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;
        return materialRepository.search(topCategory, keyword, size, offset);
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    /**
     * 등록 - PDF 파일을 DB(BLOB) 에 직접 저장
     */
    public Long create(String title,
                       String contentHtml,
                       TopCategory topCategory,
                       MidCategory midCategory,
                       Long userId,
                       MultipartFile pdfFile) throws IOException {

        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("첨부파일(PDF)은 필수입니다.");
        }

        Material material = new Material();
        material.setTitle(title);

        // ★ enum 그대로 세팅 (String 아님)
        material.setTopCategory(topCategory);
        material.setMidCategory(midCategory);

        material.setContentHtml(contentHtml);
        material.setUserId(userId);

        // ---- PDF BLOB 세팅 ----
        material.setPdfFilename(pdfFile.getOriginalFilename());
        material.setPdfContentType(pdfFile.getContentType());
        material.setPdfData(pdfFile.getBytes());

        return materialRepository.save(material);
    }

    /**
     * 수정
     */
    public void update(Long id,
                       String title,
                       String contentHtml,
                       TopCategory topCategory,
                       MidCategory midCategory,
                       Long userId,
                       MultipartFile pdfFile) throws IOException {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("교육자료를 찾을 수 없습니다. id=" + id));

        material.setTitle(title);
        material.setContentHtml(contentHtml);
        material.setUserId(userId);

        // ★ enum 그대로 세팅
        if (topCategory != null) {
            material.setTopCategory(topCategory);
        }
        if (midCategory != null) {
            material.setMidCategory(midCategory);
        }

        if (pdfFile != null && !pdfFile.isEmpty()) {
            // 새 파일로 교체 (기존 BLOB 덮어씀)
            material.setPdfFilename(pdfFile.getOriginalFilename());
            material.setPdfContentType(pdfFile.getContentType());
            material.setPdfData(pdfFile.getBytes());

            materialRepository.update(material);
        } else {
            // 파일 변경 X (제목/내용/카테고리/작성자만 수정)
            materialRepository.updateWithoutFile(material);
        }
    }

    /**
     * 삭제 (DB 레코드만 삭제 – BLOB 도 함께 사라짐)
     */
    public void delete(Long id) {
        materialRepository.delete(id);
    }

    public List<Material> findByMidCategory(String midCategory) {
        return materialRepository.findByMidCategory(midCategory);
    }
}
