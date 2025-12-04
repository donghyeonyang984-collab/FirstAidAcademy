package com.emergency.Game.dto;

import java.util.Arrays;

public class GameDataDto {

    private int gameId;
    private int userId;
    private int[] starLevels;
    private int starLevelsSavedCount;

    // getter / setter
    public int getGameId() {
        return gameId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int[] getStarLevels() {
        return starLevels;
    }
    public void setStarLevels(int[] starLevels) {
        this.starLevels = starLevels;
    }

    public int getStarLevelsSavedCount() {
        return starLevelsSavedCount;
    }
    public void setStarLevelsSavedCount(int starLevelsSavedCount) {
        this.starLevelsSavedCount = starLevelsSavedCount;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameId=" + gameId +
                ", userId=" + userId +
                ", starLevels=" + Arrays.toString(starLevels) +
                ", starLevelsSavedCount=" + starLevelsSavedCount +
                '}';
    }
}
