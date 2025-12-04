package com.emergency.Game.controller;

import com.emergency.Game.dto.GameDataDto;
import com.emergency.Game.service.GameDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameDataService gameDataService;

    public GameController(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveGameData(@RequestBody GameDataDto gameData) {
        try {
            // 전달받은 데이터 디버깅용 출력
            System.out.println("받은 게임 데이터:");
            System.out.println("userId: " + gameData.getUserId());
            System.out.println("gameId: " + gameData.getGameId());
            System.out.println("starLevels: " + Arrays.toString(gameData.getStarLevels()));
            System.out.println("starLevelsSavedCount: " + gameData.getStarLevelsSavedCount());

            gameDataService.saveGameData(gameData);
            return ResponseEntity.ok("게임 데이터 저장 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Object> GameDownload() {
        try {
            // 다운로드할 ZIP 파일 경로
            String filePath = "C:\\Games\\MyUnityGame.zip";
            java.io.File file = new java.io.File(filePath);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없습니다: " + filePath);
            }

            // 파일 읽기
            java.nio.file.Path path = file.toPath();
            byte[] fileBytes = java.nio.file.Files.readAllBytes(path);

            // 다운로드 응답 설정
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .header("Content-Type", "application/zip")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 다운로드 실패: " + e.getMessage());
        }
    }


}
