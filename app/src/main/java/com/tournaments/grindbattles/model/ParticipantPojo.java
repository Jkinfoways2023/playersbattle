package com.tournaments.grindbattles.model;

public class ParticipantPojo {
    String id, user_id, pubg_id,pos_string;
    int  kills, position,win, prize, slot;
    private String slot_position;
    private String match_id;

    public String getSlot_position() {
        return slot_position;
    }

    public void setSlot_position(String slot_position) {
        this.slot_position = slot_position;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public ParticipantPojo() {
    }

    public ParticipantPojo(String id, String user_id, String pubg_id, String pos_string, int kills, int position, int win, int prize, int slot) {
        this.id = id;
        this.user_id = user_id;
        this.pubg_id = pubg_id;
        this.pos_string = pos_string;
        this.kills = kills;
        this.position = position;
        this.win = win;
        this.prize = prize;
        this.slot = slot;

    }

    public String getPos_string() {
        return pos_string;
    }

    public void setPos_string(String pos_string) {
        this.pos_string = pos_string;
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

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
