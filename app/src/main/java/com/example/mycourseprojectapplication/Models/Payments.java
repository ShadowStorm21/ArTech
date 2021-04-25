package com.example.mycourseprojectapplication.Models;

public class Payments {
    private String payment_id;
    private String order_id;
    private long dateOfPurchase;



    public Payments(String payment_id, String order_id, long dateOfPurchase) {
        this.payment_id = payment_id;
        this.order_id = order_id;
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public long getDateOfPurchase() {
        return dateOfPurchase;
    }
}
