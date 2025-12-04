package com.emergency.adminCourse.service;

import com.emergency.adminCourse.Dao.AdminCourseAddDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminCourseAddService {

    private final AdminCourseAddDao courseAddDao;

    private final String uploadPath = "C:/uploads/courses/";  // 원하는 경로로 수정

    public void addCourseAndLectures(
            String title, String main, String sub, String desc,
            MultipartFile thumb,
            MultipartFile video1, String cap1, int dur1,
            MultipartFile video2, String cap2, int dur2,
            MultipartFile video3, String cap3, int dur3
    ) throws Exception {

        // 1. 썸네일 저장
        String thumbName = saveFile(thumb);

        // 2. 강의 기본정보 Map 생성
        Map<String, Object> courseMap = Map.of(
                "title", title,
                "top_category", main,
                "mid_category", sub,
                "summary", desc,
                "image_path", thumbName

        );

        // 3. 강의 INSERT
        Long courseId = courseAddDao.insertCourse(courseMap);

        // 4. 영상 저장 (duration 적용)
        insertLecture(courseId, 1, video1, cap1, dur1);
        insertLecture(courseId, 2, video2, cap2, dur2);
        insertLecture(courseId, 3, video3, cap3, dur3);
    }


    private void insertLecture(Long courseId, int no, MultipartFile video, String caption, int duration) throws Exception {

        if (video == null || video.isEmpty()) return;

        String videoName = saveFile(video);

        Map<String, Object> map = Map.of(
                "course_id", courseId,
                "lecture_no", no,
                "title", "영상 " + no,
                "video_url", videoName,
                "information", caption,
                "duration_sec", duration   // ✅ 여기서 실제 duration 값이 들어감
        );

        courseAddDao.insertLecture(map);
    }


    // 파일 저장
    private String saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return null;

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadPath + filename);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);
        return filename;
    }
}
