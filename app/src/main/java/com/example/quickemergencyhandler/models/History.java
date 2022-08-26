package com.example.quickemergencyhandler.models;

public class History {
    private String date;
    private String time;
    private String charges;
    private String userName;
    private float ratingValue;
    private String comment;

    public History() {

    }

    public History(String date, String time, String charges, String userName, float ratingValue, String comment) {
        this.date = date;
        this.time = time;
        this.charges = charges;
        this.userName = userName;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
