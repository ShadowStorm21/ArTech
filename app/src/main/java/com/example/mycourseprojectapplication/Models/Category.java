package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;

public class Category implements Serializable {
    private String title;
    private int res_img;

    public Category(String title, int res_img) {
        this.title = title;
        this.res_img = res_img;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRes_img() {
        return res_img;
    }

    public void setRes_img(int res_img) {
        this.res_img = res_img;
    }
}
