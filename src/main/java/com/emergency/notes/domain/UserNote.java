package com.emergency.notes.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNote {

    private Long userNoteId;
    private Long userId;
    private Long courseId;
    private Long courseLectureId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
