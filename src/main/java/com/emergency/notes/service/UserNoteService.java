package com.emergency.notes.service;

import com.emergency.notes.domain.UserNote;
import com.emergency.notes.repository.UserNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserNoteService {

    private final UserNoteRepository userNoteRepository;

    @Transactional(readOnly = true)
    public String getNoteContent(Long userId,
                                 Long courseId,
                                 Long courseLectureId) {

        Optional<UserNote> noteOpt =
                userNoteRepository.findByUserAndLecture(userId, courseId, courseLectureId);

        return noteOpt.map(UserNote::getContent).orElse("");
    }

    @Transactional
    public void saveNote(Long userId,
                         Long courseId,
                         Long courseLectureId,
                         String content) {

        userNoteRepository.upsert(userId, courseId, courseLectureId, content);
    }
}
