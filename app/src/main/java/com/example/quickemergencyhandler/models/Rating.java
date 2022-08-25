package com.example.quickemergencyhandler.models;

public class Rating {
    private String bookingID;
    private String userID;
    private String driverID;
    private String comment;
    private float rating;

    public Rating(String bookingID, String userID, String driverID, String comment, float rating) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.driverID = driverID;
        this.comment = comment;
        this.rating = rating;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
