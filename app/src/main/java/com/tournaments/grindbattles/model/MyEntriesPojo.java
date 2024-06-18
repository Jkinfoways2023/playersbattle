package com.tournaments.grindbattles.model;

public class MyEntriesPojo {

    private String id;
    private String user_id;
    private String match_id;
    private String pubg_id;
    private String slot;
    private String slot_position;
    private String is_canceled;

    public MyEntriesPojo() {
    }

    public String getSlot_position() {
        return slot_position;
    }

    public void setSlot_position(String slot_position) {
        this.slot_position = slot_position;
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

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getPubg_id() {
        return pubg_id;
    }

    public void setPubg_id(String pubg_id) {
        this.pubg_id = pubg_id;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getIs_canceled() {
        return is_canceled;
    }

    public void setIs_canceled(String is_canceled) {
        this.is_canceled = is_canceled;
    }
}
