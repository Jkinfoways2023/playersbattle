package com.GlaDius.war.model;

public class TransactionPojo {
    private String id,payment_id,user_id,request_name,req_from,req_amount,coins_used,getway_name,remark,status,type,date;

    public TransactionPojo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRequest_name() {
        return request_name;
    }

    public void setRequest_name(String request_name) {
        this.request_name = request_name;
    }

    public String getReq_from() {
        return req_from;
    }

    public void setReq_from(String req_from) {
        this.req_from = req_from;
    }

    public String getReq_amount() {
        return req_amount;
    }

    public void setReq_amount(String req_amount) {
        this.req_amount = req_amount;
    }

    public String getCoins_used() {
        return coins_used;
    }

    public void setCoins_used(String coins_used) {
        this.coins_used = coins_used;
    }

    public String getGetway_name() {
        return getway_name;
    }

    public void setGetway_name(String getway_name) {
        this.getway_name = getway_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
