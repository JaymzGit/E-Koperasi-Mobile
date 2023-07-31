package com.gnj.e_koperasi;

import java.util.ArrayList;

public class Order {
    private String customer_id;
    private ArrayList<OrderItem> cartList;
    private String date;
    private String time;
    private double total_price;
    private String payment_method;
    private String status;

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String customer_id, ArrayList<OrderItem> cartList, String date, String time, double total_price, String payment_method, String status) {
        this.customer_id = customer_id;
        this.cartList = cartList;
        this.date = date;
        this.time = time;
        this.total_price = total_price;
        this.payment_method = payment_method;
        this.status = status;
    }

    // Add getters and setters for customer_id, date, total_price, and payment_method

    public ArrayList<OrderItem> getCartList() {
        return cartList;
    }

    public void setCartList(ArrayList<OrderItem> cartList) {
        this.cartList = cartList;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
