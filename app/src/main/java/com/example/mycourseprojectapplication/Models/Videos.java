package com.example.mycourseprojectapplication.Models;

public class Videos {
    private int uri;
    private String details;

    public Videos(int uri, String details) {
        this.uri = uri;
        this.details = details;
    }

    public int getUri() {
        return uri;
    }

    public String getDetails() {
        return details;
    }
}
