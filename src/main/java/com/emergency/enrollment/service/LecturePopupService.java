package com.emergency.enrollment.service;

import com.emergency.enrollment.domain.LecturePopupLecture;
import com.emergency.enrollment.repository.Popup.LecturePopupDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturePopupService {

    private final LecturePopupDao lecturePopupDao;

    public LecturePopupResult loadPopup(Long enrollmentId, Long courseId) {
        List<LecturePopupLecture> lectures =
                lecturePopupDao.findLectures(enrollmentId, courseId);

        if (lectures.isEmpty()) {
            return new LecturePopupResult(null, List.of());
        }

        // 일단 lecture_no 가장 작은 강의를 현재 강의로 사용
        LecturePopupLecture current = lectures.get(0);

        return new LecturePopupResult(current, lectures);
    }

    public record LecturePopupResult(
            LecturePopupLecture currentLecture,
            List<LecturePopupLecture> lectureList
    ) {}
}
