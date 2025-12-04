package com.emergency.notice.service;

import com.emergency.notice.domain.Notice;
import com.emergency.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> findPage(int page, int size, String keyword) {
        return noticeRepository.findPage(page, size, keyword);
    }

    public int count(String keyword) {
        return noticeRepository.count(keyword);
    }

    public Optional<Notice> findById(Long id) {
        return noticeRepository.findById(id);
    }

    public Long create(String title, String contentHtml, Long userId) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContentHtml(contentHtml);
        notice.setUserId(userId);
        return noticeRepository.save(notice);
    }

    public void update(Long id, String title, String contentHtml) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));

        notice.setTitle(title);
        notice.setContentHtml(contentHtml);

        noticeRepository.update(notice);
    }

    public void delete(Long id) {
        noticeRepository.delete(id);
    }
    public Optional<Long> findPrevId(Long id) {
        return noticeRepository.findPrevId(id);
    }

    public Optional<Long> findNextId(Long id) {
        return noticeRepository.findNextId(id);
    }

}
