package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;

public class TimeLineModel implements Serializable {
    private String message;
    private String date;
    public OrderStatus order_status;
    public TimelineStatus status;
    private String location;
    private int priority;
    private String time;


    public TimeLineModel(String message, String date, OrderStatus order_status, TimelineStatus status, String location, int priority, String time) {
        this.message = message;
        this.date = date;
        this.order_status = order_status;
        this.status = status;
        this.location = location;
        this.priority = priority;
        this.time = time;
    }

    public TimeLineModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public OrderStatus getOrder_status() {
        return order_status;
    }

    public void setOrder_status(OrderStatus order_status) {
        this.order_status = order_status;
    }

    public TimelineStatus getStatus() {
        return status;
    }

    public void setStatus(TimelineStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}


