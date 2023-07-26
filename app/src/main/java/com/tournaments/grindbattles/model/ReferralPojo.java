package com.tournaments.grindbattles.model;

public class ReferralPojo {
    private String fname, lname, refer_date, refer_status;

    public ReferralPojo() {
    }

    public ReferralPojo(String fname, String lname, String refer_date, String refer_status) {
        this.fname = fname;
        this.lname = lname;
        this.refer_date = refer_date;
        this.refer_status = refer_status;
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

    public String getRefer_date() {
        return refer_date;
    }

    public void setRefer_date(String refer_date) {
        this.refer_date = refer_date;
    }

    public String getRefer_status() {
        return refer_status;
    }

    public void setRefer_status(String refer_status) {
        this.refer_status = refer_status;
    }
}
