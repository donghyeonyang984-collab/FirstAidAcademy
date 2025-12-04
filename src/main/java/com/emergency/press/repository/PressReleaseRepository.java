package com.emergency.press.repository;

import com.emergency.press.domain.PressRelease;

import java.util.List;
import java.util.Optional;

public interface PressReleaseRepository {

    int count(String keyword);

    List<PressRelease> findPage(int page, int size, String keyword);

    Optional<PressRelease> findById(Long id);

    Long save(PressRelease pressRelease);

    void update(PressRelease pressRelease);

    void delete(Long id);

    Optional<PressRelease> findPrev(Long currentId);

    Optional<PressRelease> findNext(Long currentId);
}
