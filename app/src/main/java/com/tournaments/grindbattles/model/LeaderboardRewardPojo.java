package com.tournaments.grindbattles.model;

public class LeaderboardRewardPojo {
    private String  fname, lname, reward_points;

    public LeaderboardRewardPojo() {
    }

    public LeaderboardRewardPojo(String fname, String lname, String reward_points) {
        this.fname = fname;
        this.lname = lname;
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

    public String getReward_points() {
        return reward_points;
    }

    public void setReward_points(String reward_points) {
        this.reward_points = reward_points;
    }
}

