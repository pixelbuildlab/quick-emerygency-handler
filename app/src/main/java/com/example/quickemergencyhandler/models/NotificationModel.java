package com.example.quickemergencyhandler.models;

public class NotificationModel {
    private String notificationID;
    private String from;
    private String to;
    private String title;
    private String subTitle;
    private int isRead;
    private String date;
    private String time;

    public NotificationModel(String notificationID, String from, String to, String title, String subTitle, int isRead, String date, String time) {
        this.notificationID = notificationID;
        this.from = from;
        this.to = to;
        this.title = title;
        this.subTitle = subTitle;
        this.isRead = isRead;
        this.date = date;
        this.time = time;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
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
