package com.GlaDius.war.model;

public class MatchResultPojo {
    int playerKills;
    String playerName;
    int playerWinning;
    int position;

    public MatchResultPojo() {
    }

    public MatchResultPojo(int playerKills, String playerName, int playerWinning, int position) {
        this.playerKills = playerKills;
        this.playerName = playerName;
        this.playerWinning = playerWinning;
        this.position = position;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerWinning() {
        return playerWinning;
    }

    public void setPlayerWinning(int playerWinning) {
        this.playerWinning = playerWinning;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



}
