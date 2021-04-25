package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;
import java.util.Map;

public class Tracking implements Serializable {
    private double userLatitude;
    private double userLongitude;
    private double raiderLatitude;
    private double raiderLongitude;
    private String tracking_key;
    private String product_id;
    private String order_id;
    private Map<String,TimeLineModel> timeLineModelMap;

    public Tracking(double userLatitude, double userLongitude, double raiderLatitude, double raiderLongitude, String tracking_key, String product_id, String order_id, Map<String, TimeLineModel> timeLineModelMap) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.raiderLatitude = raiderLatitude;
        this.raiderLongitude = raiderLongitude;
        this.tracking_key = tracking_key;
        this.product_id = product_id;
        this.order_id = order_id;
        this.timeLineModelMap = timeLineModelMap;
    }

    public String getProduct_id() {
        return product_id;
    }

    public Tracking() {
    }

    public String getTracking_key() {
        return tracking_key;
    }

    public void setTracking_key(String tracking_key) {
        this.tracking_key = tracking_key;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public double getRaiderLatitude() {
        return raiderLatitude;
    }

    public double getRaiderLongitude() {
        return raiderLongitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public void setRaiderLatitude(double raiderLatitude) {
        this.raiderLatitude = raiderLatitude;
    }

    public void setRaiderLongitude(double raiderLongitude) {
        this.raiderLongitude = raiderLongitude;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Map<String, TimeLineModel> getTimeLineModelMap() {
        return timeLineModelMap;
    }

    public void setTimeLineModelMap(Map<String, TimeLineModel> timeLineModelMap) {
        this.timeLineModelMap = timeLineModelMap;
    }

    @Override
    public String toString() {
        return "Tracking{" +
                "userLatitude=" + userLatitude +
                ", userLongitude=" + userLongitude +
                ", raiderLatitude=" + raiderLatitude +
                ", raiderLongitude=" + raiderLongitude +
                ", tracking_key='" + tracking_key + '\'' +
                ", product_id='" + product_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", timeLineModelMap=" + timeLineModelMap +
                '}';
    }
}
