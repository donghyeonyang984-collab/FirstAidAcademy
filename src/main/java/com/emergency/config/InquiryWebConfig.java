// src/main/java/com/emergency/config/InquiryWebConfig.java
package com.emergency.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class InquiryWebConfig implements WebMvcConfigurer {

    @Value("${file.upload.inquiry-dir:uploads/inquiry}")
    private String inquiryUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 실제 물리 경로 (절대경로로 변환)
        String uploadPath = Paths.get(inquiryUploadDir)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/") + "/";

        // /uploads/inquiry/** 요청을 실제 폴더로 매핑
        registry.addResourceHandler("/uploads/inquiry/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
