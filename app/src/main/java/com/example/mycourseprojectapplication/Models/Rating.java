package com.example.mycourseprojectapplication.Models;

public class Rating {
    private String user_id;
    private long date;
    private String comment;
    private float ratting;
    private String username;
    private String product_id;


    public Rating(String user_id, long date, String comment, float ratting, String username, String product_id) {
        this.user_id = user_id;
        this.date = date;
        this.comment = comment;
        this.ratting = ratting;
        this.username = username;
        this.product_id = product_id;
    }

    public Rating() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_id() {
        return user_id;
    }

    public long getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public float getRatting() {
        return ratting;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "user_id='" + user_id + '\'' +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", ratting=" + ratting +
                ", username='" + username + '\'' +
                ", product_id='" + product_id + '\'' +
                '}';
    }
}
