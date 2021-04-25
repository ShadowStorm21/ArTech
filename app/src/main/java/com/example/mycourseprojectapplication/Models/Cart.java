package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;
import java.util.HashMap;

public class Cart implements Serializable {

    private Products product;
    private String colorSelected;
    private String configurationSelected;
    private double totalPrice;
    private int quantity;


    public Cart(Products product, String colorSelected, String configurationSelected, double totalPrice, int quantity) {
        this.product = product;
        this.colorSelected = colorSelected;
        this.configurationSelected = configurationSelected;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }


    public Cart() {
    }


    public Products getProduct() {
        return product;
    }

    public String getColorSelected() {
        return colorSelected;
    }

    public String getConfigurationSelected() {
        return configurationSelected;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
