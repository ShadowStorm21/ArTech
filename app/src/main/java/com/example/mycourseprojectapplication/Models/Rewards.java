package com.example.mycourseprojectapplication.Models;

public class Rewards {
    private String reward_price;
    private int reward_points;
    private int reward_drawable;
    private int pointsLeft;
    private int userPoints;


    public Rewards(String reward_price, int reward_points, int reward_drawable, int pointsLeft, int userPoints) {
        this.reward_price = reward_price;
        this.reward_points = reward_points;
        this.reward_drawable = reward_drawable;
        this.pointsLeft = pointsLeft;
        this.userPoints = userPoints;

    }

    public String getReward_price() {
        return reward_price;
    }

    public int getReward_points() {
        return reward_points;
    }

    public int getReward_drawable() {
        return reward_drawable;
    }

    public int getPointsLeft() {
        return pointsLeft;
    }

    public int getUserPoints() {
        return userPoints;
    }


}
