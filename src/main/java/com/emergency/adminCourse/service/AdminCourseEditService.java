package com.emergency.adminCourse.service;

import com.emergency.adminCourse.Dao.AdminCourseEditDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminCourseEditService {

    private final AdminCourseEditDao editDao;

    // 업로드 루트 (환경에 맞게 변경)
    private final String uploadPath = "C:/uploads/courses/";

    public Map<String, Object> getCourseDetail(Long courseId) {
        return editDao.findCourseById(courseId);
    }

    public List<Map<String, Object>> getLectures(Long courseId) {
        return editDao.findLectures(courseId);
    }

    public void updateCourseAndLectures(
            Long courseId,
            String title, String main, String sub, String desc,
            MultipartFile thumb,

            MultipartFile v1, String cap1, int dur1,
            MultipartFile v2, String cap2, int dur2,
            MultipartFile v3, String cap3, int dur3
    ) throws Exception {

        // 1. 기존 course 정보
        Map<String, Object> course = editDao.findCourseById(courseId);

        // 2. 썸네일 처리: 새로 올리면 저장, 아니면 기존 유지
        String thumbName;
        if (thumb != null && !thumb.isEmpty()) {
            thumbName = saveFile(thumb);
        } else {
            thumbName = (String) course.get("image_path");
        }

        // 3. courses 업데이트
        Map<String, Object> courseUpdate = new HashMap<>();
        courseUpdate.put("course_id", courseId);
        courseUpdate.put("title", title);
        courseUpdate.put("top_category", main);
        courseUpdate.put("mid_category", sub);
        courseUpdate.put("summary", desc);
        courseUpdate.put("image_path", thumbName);
        editDao.updateCourse(courseUpdate);

        // 4. lectures 업데이트 (각 영상: 새로 올리면 교체, 아니면 기존 유지 but update caption/duration)
        // lecture 1
        updateLecture(courseId, 1, v1, cap1, dur1);
        updateLecture(courseId, 2, v2, cap2, dur2);
        updateLecture(courseId, 3, v3, cap3, dur3);
    }

    private void updateLecture(Long courseId, int no, MultipartFile video, String caption, int duration) throws Exception {
        Map<String, Object> old = null;
        try { old = editDao.findLecture(courseId, no); } catch (Exception e) { /* 없을 수도 있음 */ }

        String videoName;
        if (video != null && !video.isEmpty()) {
            videoName = saveFile(video);
        } else {
            videoName = old == null ? null : (String) old.get("video_url");
        }

        Map<String, Object> lecUpdate = new HashMap<>();
        lecUpdate.put("course_id", courseId);
        lecUpdate.put("lecture_no", no);
        // title 유지(기존이 없다면 기본값)
        lecUpdate.put("title", old != null ? old.get("title") : "영상 " + no);
        lecUpdate.put("video_url", videoName);
        lecUpdate.put("information", caption != null ? caption : (old != null ? old.get("information") : null));
        lecUpdate.put("duration_sec", duration > 0 ? duration : (old != null ? old.get("duration_sec") : 0));

        editDao.updateLecture(lecUpdate);
    }

    private String saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return null;
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadPath + filename);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        return filename;
    }
// 삭제 보류

//    public void deleteCourse(Long courseId) {
//        editDao.deleteCourse(courseId);
//    }
}