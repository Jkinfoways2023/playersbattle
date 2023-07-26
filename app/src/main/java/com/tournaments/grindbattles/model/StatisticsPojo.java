package com.tournaments.grindbattles.model;

public class StatisticsPojo {
    private String id, title, time;
    private int entry_fee, prize;

    public StatisticsPojo() {
    }

    public StatisticsPojo(String id, String title, String time, int entry_fee, int prize) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.entry_fee = entry_fee;
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getEntry_fee() {
        return entry_fee;
    }

    public void setEntry_fee(int entry_fee) {
        this.entry_fee = entry_fee;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }
}
