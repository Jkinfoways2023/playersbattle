package com.GlaDius.war.model;

public class RewardPojo {
    private String fname, lname, reward_date, reward_count, reward_points;

    public RewardPojo() {
    }

    public RewardPojo(String fname, String lname, String reward_date, String reward_count, String reward_points) {
        this.fname = fname;
        this.lname = lname;
        this.reward_date = reward_date;
        this.reward_count = reward_count;
        this.reward_points = reward_points;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getReward_date() {
        return reward_date;
    }

    public void setReward_date(String reward_date) {
        this.reward_date = reward_date;
    }

    public String getReward_count() {
        return reward_count;
    }

    public void setReward_count(String reward_count) {
        this.reward_count = reward_count;
    }

    public String getReward_points() {
        return reward_points;
    }

    public void setReward_points(String reward_points) {
        this.reward_points = reward_points;
    }
}
