package com.tournaments.grindbattles.model;

public class BetPojo {

    private String id, user_id, bet_id, bet, pubg_id, name, win;

    public BetPojo() {
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

    public String getBet_id() {
        return bet_id;
    }

    public void setBet_id(String bet_id) {
        this.bet_id = bet_id;
    }

    public String getBet() {
        return bet;
    }

    public void setBet(String bet) {
        this.bet = bet;
    }

    public String getPubg_id() {
        return pubg_id;
    }

    public void setPubg_id(String pubg_id) {
        this.pubg_id = pubg_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }
}
