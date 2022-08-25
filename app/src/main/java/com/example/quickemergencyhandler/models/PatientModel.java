package com.example.quickemergencyhandler.models;

public class PatientModel {
    private String id;
    private String name;
    private String email;
    private String cnic;
    private String phoneNo;
    private String status;
    private String userType;
    private int isAdmin;
    private int isPatient;
    private int isDriver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PatientModel(String id, String name, String email, String cnic, String phoneNo, String status, String userType, int isAdmin, int isPatient, int isDriver) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cnic = cnic;
        this.phoneNo = phoneNo;
        this.status = status;
        this.userType = userType;
        this.isAdmin = isAdmin;
        this.isPatient = isPatient;
        this.isDriver = isDriver;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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
}
