package com.example.mycourseprojectapplication.Models;

import java.io.Serializable;

public class Requests implements Serializable {
    private String request_id;
    private String description;
    private String helpCategory;
    private String detailedHelp;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private boolean isSolved;
    private long requestDate;

    public Requests(String request_id, String description, String helpCategory, String detailedHelp, String customerName, String customerEmail, String customerPhoneNumber, boolean isSolved, long requestDate) {
        this.request_id = request_id;
        this.description = description;
        this.helpCategory = helpCategory;
        this.detailedHelp = detailedHelp;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.isSolved = isSolved;
        this.requestDate = requestDate;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public Requests() {
    }

    public long getRequestDate() {
        return requestDate;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getDescription() {
        return description;
    }

    public String getHelpCategory() {
        return helpCategory;
    }

    public String getDetailedHelp() {
        return detailedHelp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
}
