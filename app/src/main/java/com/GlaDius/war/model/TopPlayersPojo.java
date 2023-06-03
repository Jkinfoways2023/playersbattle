package com.GlaDius.war.model;

public class TopPlayersPojo {
    String pubg_id;
    int prize;
    int position;

    public TopPlayersPojo() {
    }

    public TopPlayersPojo(String pubg_id, int prize, int position) {
        this.pubg_id = pubg_id;
        this.prize = prize;
        this.position = position;
    }

    public String getPubg_id() {
        return pubg_id;
    }

    public void setPubg_id(String pubg_id) {
        this.pubg_id = pubg_id;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
