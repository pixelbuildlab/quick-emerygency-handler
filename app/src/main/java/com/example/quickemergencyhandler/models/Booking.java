package com.example.quickemergencyhandler.models;

public class Booking {
    private String bookingID;
    private String userID;
    private String driverID;
    private double userLat;
    private double userLng;
    private String status;
    private String date;
    private String time;
    private double driverLat;
    private double driverLng;
    private int cost;

    public Booking(String bookingID, String userID, String driverID, double userLat, double userLng, String status, String date, String time, double driverLat, double driverLng, int cost) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.driverID = driverID;
        this.userLat = userLat;
        this.userLng = userLng;
        this.status = status;
        this.date = date;
        this.time = time;
        this.driverLat = driverLat;
        this.driverLng = driverLng;
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(double driverLat) {
        this.driverLat = driverLat;
    }

    public double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(double driverLng) {
        this.driverLng = driverLng;
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

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    public void setUserLng(double userLng) {
        this.userLng = userLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
