package com.emergency.Game.entity;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameData {
    private int gameId;
    private int userId;
    private int[] starLevels;
    private int starLevelsSavedCount;

}
