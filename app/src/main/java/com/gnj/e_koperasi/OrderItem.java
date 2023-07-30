package com.gnj.e_koperasi;

public class OrderItem {
    private String item_name;
    private int item_quantity;
    private double item_price;

    public OrderItem() {
        // Default constructor required for Firebase
    }

    public OrderItem(String item_name, int item_quantity, double item_price) {
        this.item_name = item_name;
        this.item_quantity = item_quantity;
        this.item_price = item_price;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }
}
