package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Orders implements Serializable {
    private String order_id;
    private String customer_id;
    private String payment_option;
    private ArrayList<Cart> purchasedProducts;
    private String order_status;
    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;
    private long order_date;
    private double total;
    private HashMap<String,String> tracking_key;

    public Orders(String order_id, String customer_id, String payment_option, ArrayList<Cart> purchasedProducts, String order_status, String firstName, String lastName, String city, String phoneNumber, long order_date, double total, HashMap<String, String> tracking_key) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.payment_option = payment_option;
        this.purchasedProducts = purchasedProducts;
        this.order_status = order_status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.order_date = order_date;
        this.total = total;
        this.tracking_key = tracking_key;
    }

    public Orders() {
    }

    public HashMap<String, String> getTracking_key() {
        return tracking_key;
    }

    public long getOrder_date() {
        return order_date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getPayment_option() {
        return payment_option;
    }

    public ArrayList<Cart> getPurchasedProducts() {
        return purchasedProducts;
    }

    public String getOrder_status() {
        return order_status;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCity() {
        return city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "order_id='" + order_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", payment_option='" + payment_option + '\'' +
                ", purchasedProducts=" + purchasedProducts +
                ", order_status='" + order_status + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", order_date=" + order_date +
                ", total=" + total +
                ", tracking_key=" + tracking_key +
                '}';
    }
}
