package com.example.mycourseprojectapplication.Models;

public class Notification {
    private String notification_id;
    private String notification_title;
    private String notification_body;
    private long notification_timestamp;
    private String state;

    public Notification(String notification_id, String notification_title, String notification_body, long notification_timestamp, String state) {
        this.notification_id = notification_id;
        this.notification_title = notification_title;
        this.notification_body = notification_body;
        this.notification_timestamp = notification_timestamp;
        this.state = state;
    }

    public Notification() {
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }

    public String getNotification_body() {
        return notification_body;
    }

    public void setNotification_body(String notification_body) {
        this.notification_body = notification_body;
    }

    public long getNotification_timestamp() {
        return notification_timestamp;
    }

    public void setNotification_timestamp(long notification_timestamp) {
        this.notification_timestamp = notification_timestamp;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
