package com.GlaDius.war.model;

public class LeaderboardReferPojo {
    private String  fname, lname, refer_points;

    public LeaderboardReferPojo() {
    }

    public LeaderboardReferPojo(String fname, String lname, String refer_points) {
        this.fname = fname;
        this.lname = lname;
        this.refer_points = refer_points;
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

    public String getRefer_points() {
        return refer_points;
    }

    public void setRefer_points(String refer_points) {
        this.refer_points = refer_points;
    }
}
