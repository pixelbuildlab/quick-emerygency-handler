package com.example.quickemergencyhandler.models;

import java.util.ArrayList;

public class DriverModel {
    private String id;
    private String name;
    private String email;
    private String cnic;
    private String phoneNo;
    private String imageUrl;
    private String status;
    private String vehicleNumber;
    private String vehicleCopyNumber;
    private String vehicleModel;
    private ArrayList<Integer> vehicleFeatures;
    private String userType;
    private double lat;
    private double lng;
    private boolean available;

    public DriverModel(String id, String name, String email, String cnic, String phoneNo, String imageUrl, String status, String vehicleNumber, String vehicleCopyNumber, String vehicleModel, ArrayList<Integer> vehicleFeatures, String userType, double lat, double lng, boolean available) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cnic = cnic;
        this.phoneNo = phoneNo;
        this.imageUrl = imageUrl;
        this.status = status;
        this.vehicleNumber = vehicleNumber;
        this.vehicleCopyNumber = vehicleCopyNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleFeatures = vehicleFeatures;
        this.userType = userType;
        this.lat = lat;
        this.lng = lng;
        this.available = available;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleCopyNumber() {
        return vehicleCopyNumber;
    }

    public void setVehicleCopyNumber(String vehicleCopyNumber) {
        this.vehicleCopyNumber = vehicleCopyNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public ArrayList<Integer> getVehicleFeatures() {
        return vehicleFeatures;
    }

    public void setVehicleFeatures(ArrayList<Integer> vehicleFeatures) {
        this.vehicleFeatures = vehicleFeatures;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
