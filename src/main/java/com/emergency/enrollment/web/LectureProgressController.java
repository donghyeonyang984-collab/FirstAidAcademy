package com.emergency.enrollment.web;

import com.emergency.enrollment.service.LectureProgressService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 강의 시청 구간(이어보기) 저장/조회용 REST 컨트롤러
 *
 *  - POST /api/lecture-progress
 *      시청 구간 저장 (진도율 갱신까지 포함)
 *
 *      요청 JSON 예)
 *      {
 *          "enrollmentId": 1,
 *          "lectureId": 10,
 *          "watchSec": 95,
 *          "completed": false
 *      }
 *
 *  - GET  /api/lecture-progress/{enrollmentId}/{lectureId}
 *      이어보기용 마지막 시청 위치(초) 조회
 *
 *      응답 JSON 예)
 *      {
 *          "watchSec": 95
 *      }
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture-progress")
public class LectureProgressController {

    private final LectureProgressService lectureProgressService;

    /** 시청 구간 저장 + 진도율 갱신 */
    @PostMapping
    public void save(@RequestBody SaveRequest request) {

        lectureProgressService.saveProgress(
                request.getEnrollmentId(),
                request.getLectureId(),
                request.getWatchSec(),
                request.isCompleted()
        );
        // 프런트에서 바디를 안 쓰고 있어서 반환값은 필요 없음 (200 OK 만)
    }

    /** 이어보기용 마지막 시청 위치 조회 */
    @GetMapping("/{enrollmentId}/{lectureId}")
    public ProgressResponse getWatchSec(@PathVariable Long enrollmentId,
                                        @PathVariable Long lectureId) {

        int sec = lectureProgressService.getWatchSec(enrollmentId, lectureId);
        return new ProgressResponse(sec);
    }

    /** 저장 요청 DTO */
    @Data
    public static class SaveRequest {
        private Long enrollmentId;
        private Long lectureId;
        private int watchSec;
        private boolean completed;
    }

    /** 조회 응답 DTO */
    @Data
    public static class ProgressResponse {
        private final int watchSec;
    }
}
