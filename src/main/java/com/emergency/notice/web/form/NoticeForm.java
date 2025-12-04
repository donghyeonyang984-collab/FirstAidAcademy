package com.emergency.notice.web.form;

import lombok.Data;

@Data
public class NoticeForm {
    private Long noticeId;      // 수정 시 사용
    private String title;
    private String contentHtml;
}
