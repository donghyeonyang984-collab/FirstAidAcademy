package com.emergency.notes.web;

import com.emergency.notes.service.UserNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 강의 메모장 API
 *  - GET  /api/notes       : 메모 조회
 *  - POST /api/notes/save  : 메모 저장/수정
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class UserNoteController {

    private final UserNoteService userNoteService;

    /**
     * 메모 조회
     * 예) /api/notes?userId=1&courseId=2&courseLectureId=3
     */
    @GetMapping
    public ResponseEntity<NoteResponse> getNote(@RequestParam Long userId,
                                                @RequestParam Long courseId,
                                                @RequestParam Long courseLectureId) {

        String content = userNoteService.getNoteContent(userId, courseId, courseLectureId);
        return ResponseEntity.ok(new NoteResponse(content));
    }

    /**
     * 메모 저장 (폼 전송 or AJAX)
     * form-data 나 query-string으로 보내면 됨
     */
    @PostMapping("/save")
    public ResponseEntity<Void> saveNote(@RequestParam Long userId,
                                         @RequestParam Long courseId,
                                         @RequestParam Long courseLectureId,
                                         @RequestParam String content) {

        userNoteService.saveNote(userId, courseId, courseLectureId, content);
        return ResponseEntity.ok().build();
    }

    // --- 응답 DTO ---
    public record NoteResponse(String content) {}
}
