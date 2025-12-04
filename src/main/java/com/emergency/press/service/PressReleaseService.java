package com.emergency.press.service;

import com.emergency.press.domain.PressRelease;
import com.emergency.press.repository.PressReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 조회 전용
public class PressReleaseService {

    private final PressReleaseRepository pressReleaseRepository;

    /** 관리자 전용 페이지이므로 user_id 기본값 1로 고정 */
    private static final Long ADMIN_USER_ID = 1L;

    /** 전체 개수(검색 포함) */
    public int count(String keyword) {
        return pressReleaseRepository.count(keyword);
    }

    /** 목록 + 검색 + 페이징 */
    public List<PressRelease> findPage(int page, int size, String keyword) {

        // page, size 방어 로직
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;   // 최소 10개씩 보기
        }

        int offset = (page - 1) * size;  // 0, 10, 20, ...

        return pressReleaseRepository.findPage(offset, size, keyword);
    }

    /** 단건 조회 (Optional) */
    public PressRelease findById(Long id) {
        return pressReleaseRepository.findById(id).orElse(null);
    }

    /** 등록 */
    @Transactional // 쓰기 작업이라 readOnly=false
    public Long create(PressRelease press) {

        // user_id 가 null이면 관리자(1)로 세팅
        if (press.getUserId() == null) {
            press.setUserId(ADMIN_USER_ID);
        }

        // created_at 이 비어 있으면 지금 시간으로 세팅
        if (press.getCreatedAt() == null) {
            press.setCreatedAt(LocalDateTime.now());
        }

        return pressReleaseRepository.save(press);
    }

    /** 수정 */
    @Transactional
    public void update(PressRelease press) {

        // 혹시라도 userId 가 비어 있으면 1로 보정
        if (press.getUserId() == null) {
            press.setUserId(ADMIN_USER_ID);
        }

        pressReleaseRepository.update(press);
    }

    /** 삭제 */
    @Transactional
    public void delete(Long id) {
        pressReleaseRepository.delete(id);
    }

    public PressRelease findPrev(Long currentId) {
        return pressReleaseRepository.findPrev(currentId).orElse(null);
    }

    public PressRelease findNext(Long currentId) {
        return pressReleaseRepository.findNext(currentId).orElse(null);
    }
}
