package com.gnj.e_koperasi;

public class MainModal2 {
    String item_image,item_name;
    double item_price,totalPrice;
    int quantity;

    MainModal2(){

    }
    public MainModal2(String item_image, String item_name, double item_price, int quantity, double totalPrice) {
        this.item_image = item_image;
        this.item_name = item_name;
        this.item_price = item_price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
