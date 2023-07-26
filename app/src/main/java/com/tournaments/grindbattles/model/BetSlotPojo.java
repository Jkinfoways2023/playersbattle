package com.tournaments.grindbattles.model;

public class BetSlotPojo {

    private String id, user_id, pubg_id, slot;
    private int maches_played, total_kills, amount_won, bet_status, bet_count, size;

    public BetSlotPojo() {
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

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public int getMaches_played() {
        return maches_played;
    }

    public void setMaches_played(int maches_played) {
        this.maches_played = maches_played;
    }

    public int getTotal_kills() {
        return total_kills;
    }

    public void setTotal_kills(int total_kills) {
        this.total_kills = total_kills;
    }

    public int getAmount_won() {
        return amount_won;
    }

    public void setAmount_won(int amount_won) {
        this.amount_won = amount_won;
    }

    public int getBet_status() {
        return bet_status;
    }

    public void setBet_status(int bet_status) {
        this.bet_status = bet_status;
    }

    public int getBet_count() {
        return bet_count;
    }

    public void setBet_count(int bet_count) {
        this.bet_count = bet_count;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
