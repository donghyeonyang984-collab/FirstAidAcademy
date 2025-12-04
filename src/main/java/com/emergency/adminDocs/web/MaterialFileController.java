package com.emergency.adminDocs.web;

import com.emergency.adminDocs.domain.Material;
import com.emergency.adminDocs.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialFileController {

    private final MaterialService materialService;

    /** PDF 다운로드 (사용자용) */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Material material = materialService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        return buildPdfResponse(material, false);
    }

    /** PDF 미리보기 (브라우저 내장 뷰어에서 열기) */
    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> preview(@PathVariable Long id) {
        Material material = materialService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        return buildPdfResponse(material, true);
    }

    private ResponseEntity<byte[]> buildPdfResponse(Material material, boolean inline) {
        byte[] data = material.getPdfData();
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        String filename = material.getPdfFilename();
        if (filename == null || filename.isBlank()) {
            filename = material.getTitle() + ".pdf";
        }

        String encodedFileName = UriUtils.encode(filename, StandardCharsets.UTF_8);
        String dispositionType = inline ? "inline" : "attachment";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        dispositionType + "; filename=\"" + encodedFileName + "\"")
                .body(data);
    }
}
