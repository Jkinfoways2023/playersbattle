package com.GlaDius.war.model;

public class WinnerPojo {
    private String id, user_id, pubg_id;
    private int kills, position, win, prize;

    public WinnerPojo() {
    }

    public WinnerPojo(String id, String user_id, String pubg_id, int kills, int position, int win, int prize) {
        this.id = id;
        this.user_id = user_id;
        this.pubg_id = pubg_id;
        this.kills = kills;
        this.position = position;
        this.win = win;
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPubg_id() {
        return pubg_id;
    }

    public void setPubg_id(String pubg_id) {
        this.pubg_id = pubg_id;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }
}
