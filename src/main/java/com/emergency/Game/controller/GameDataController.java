package com.emergency.Game.controller;

import com.emergency.Game.dto.GameDataDto;
import com.emergency.Game.service.GameDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameDataController {

    private final GameDataService gameDataService;

    public GameDataController(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    // userId로 게임 데이터 가져오기
    @GetMapping("/data/{userId}")
    public GameDataDto getGameData(@PathVariable int userId) {
        System.out.println("userId = " + userId);
        return gameDataService.getGameData(userId);
    }
}
