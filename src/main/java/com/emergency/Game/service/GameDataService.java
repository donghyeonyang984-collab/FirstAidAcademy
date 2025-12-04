package com.emergency.Game.service;

import com.emergency.Game.Repository.GameDataRepository;
import com.emergency.Game.dto.GameDataDto;
import com.emergency.Game.entity.GameData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GameDataService {

    private final JdbcTemplate jdbcTemplate;

    public GameDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveGameData(GameDataDto gameData) {
        String sql = "INSERT INTO game_data (user_id, star_levels, star_levels_saved_count) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE star_levels = VALUES(star_levels), " +
                "star_levels_saved_count = VALUES(star_levels_saved_count)";

        // starLevels 배열을 문자열로 변환해서 저장
        String starLevelsStr = Arrays.toString(gameData.getStarLevels());

        jdbcTemplate.update(sql,
                gameData.getUserId(),
                starLevelsStr,
                gameData.getStarLevelsSavedCount());
    }

    public GameDataDto getGameData(int userId) {
        String sql = "SELECT star_levels, star_levels_saved_count FROM game_data WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
                String starLevelsStr = rs.getString("star_levels"); // "[1,2,3]"
                int[] starLevels = Arrays.stream(starLevelsStr.replaceAll("\\[|\\]|\\s", "").split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                int savedCount = rs.getInt("star_levels_saved_count");

                GameDataDto dto = new GameDataDto();
                dto.setUserId(userId);
                dto.setStarLevels(starLevels);
                dto.setStarLevelsSavedCount(savedCount);

                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            // 조회 결과 없으면 기본값 반환
            GameDataDto dto = new GameDataDto();
            dto.setUserId(userId);
            dto.setStarLevels(new int[4]); // 빈 배열
            dto.setStarLevelsSavedCount(0);
            return dto;
        }
    }

}



