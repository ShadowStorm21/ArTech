package com.example.mycourseprojectapplication.Models;

public class Users {
    private String user_id;
    private String username;
    private String email;
    private boolean isAdmin;
    private int userRewardPoints;
    private String userPhotoUrl;
    private String notification_token;

    public Users(String user_id, String username, String email, boolean isAdmin, int userRewardPoints, String userPhotoUrl, String notification_token) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.userRewardPoints = userRewardPoints;
        this.userPhotoUrl = userPhotoUrl;
        this.notification_token = notification_token;
    }

    public Users() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getUserRewardPoints() {
        return userRewardPoints;
    }

    public void setUserRewardPoints(int userRewardPoints) {
        this.userRewardPoints = userRewardPoints;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getNotification_token() {
        return notification_token;
    }

    public void setNotification_token(String notification_token) {
        this.notification_token = notification_token;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", userRewardPoints=" + userRewardPoints +
                ", userPhotoUrl='" + userPhotoUrl + '\'' +
                ", notification_token='" + notification_token + '\'' +
                '}';
    }
}