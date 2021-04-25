package com.example.mycourseprojectapplication.Models;



import java.io.Serializable;
import java.util.HashMap;

 public class Products implements Serializable {

    private String product_id;
    private String productName;
    private String productDescription;
    private String productBrand;
    private double productPrice;
    private HashMap<String,String> productImages;
    private long timeCreated;
    private String category;
    private int quantity;

     public Products() {
     }

     public Products(String product_id, String productName, String productDescription, String productBrand, double productPrice, HashMap<String, String> productImages, long timeCreated, String category, int quantity) {
         this.product_id = product_id;
         this.productName = productName;
         this.productDescription = productDescription;
         this.productBrand = productBrand;
         this.productPrice = productPrice;
         this.productImages = productImages;
         this.timeCreated = timeCreated;
         this.category = category;
         this.quantity = quantity;

     }



     public int getQuantity() {
         return quantity;
     }

     public String getProduct_id() {
         return product_id;
     }

     public String getProductName() {
         return productName;
     }

     public String getProductDescription() {
         return productDescription;
     }

     public String getProductBrand() {
         return productBrand;
     }

     public double getProductPrice() {
         return productPrice;
     }

     public HashMap<String, String> getProductImages() {
         return productImages;
     }

     public long getTimeCreated() {
         return timeCreated;
     }

     public String getCategory() {
         return category;
     }

     @Override
     public String toString() {
         return "Products{" +
                 "product_id='" + product_id + '\'' +
                 ", productName='" + productName + '\'' +
                 ", productDescription='" + productDescription + '\'' +
                 ", productBrand='" + productBrand + '\'' +
                 ", productPrice=" + productPrice +
                 ", productImages=" + productImages +
                 ", timeCreated=" + timeCreated +
                 ", category='" + category + '\'' +
                 ", quantity=" + quantity +
                 '}';
     }
 }
