package com.emergency.notes.repository;

import com.emergency.notes.domain.UserNote;

import java.util.Optional;

public interface UserNoteRepository {

    /**
     * 유저 + 강의 + 강의차시 기준으로 메모 한 건 조회
     */
    Optional<UserNote> findByUserAndLecture(Long userId,
                                            Long courseId,
                                            Long courseLectureId);

    /**
     * 있으면 UPDATE, 없으면 INSERT (한 강의당 메모 1개라고 가정)
     */
    void upsert(Long userId,
                Long courseId,
                Long courseLectureId,
                String content);
}
