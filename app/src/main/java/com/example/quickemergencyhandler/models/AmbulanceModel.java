package com.example.quickemergencyhandler.models;

import java.util.ArrayList;

public class AmbulanceModel {
    private String number;
    private String copyNumber;
    private String model;
    private ArrayList<Integer> features;
    private String driverID;

    public AmbulanceModel(String number, String copyNumber, String model, ArrayList<Integer> features, String driverID) {
        this.number = number;
        this.copyNumber = copyNumber;
        this.model = model;
        this.features = features;
        this.driverID = driverID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(String copyNumber) {
        this.copyNumber = copyNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ArrayList<Integer> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Integer> features) {
        this.features = features;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
