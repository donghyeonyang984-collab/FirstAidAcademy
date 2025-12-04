package com.emergency.home.service;

import com.emergency.home.dto.HomeNoticeItem;
import com.emergency.home.dto.HomePressItem;
import com.emergency.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    public List<HomePressItem> getLatestPress() {
        return homeRepository.findLatestPress(5);    // 최신 5개
    }

    public List<HomeNoticeItem> getLatestNotices() {
        return homeRepository.findLatestNotices(5);  // 최신 5개
    }
}
