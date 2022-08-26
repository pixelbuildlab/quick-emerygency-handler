package com.example.quickemergencyhandler.models;

public class AdminModel {
    private String email;
    private String password;
    private int baseFare;
    private int perKmFare;

    public AdminModel() {
        email = "";
        password = "";
        baseFare = 0;
        perKmFare = 0;
    }

    public AdminModel(String email, String password, int baseFare, int perKmFare) {
        this.email = email;
        this.password = password;
        this.baseFare = baseFare;
        this.perKmFare = perKmFare;
    }

    public int getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(int baseFare) {
        this.baseFare = baseFare;
    }

    public int getPerKmFare() {
        return perKmFare;
    }

    public void setPerKmFare(int perKmFare) {
        this.perKmFare = perKmFare;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
