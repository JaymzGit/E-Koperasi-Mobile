package com.gnj.e_koperasi;

public class MainModal {

    String item_category,item_image,item_name;
    double item_price;
    int item_id,item_quantity;

    public MainModal(String item_category, String item_image, String item_name, double item_price, int item_id, int item_quantity) {
        this.item_category = item_category;
        this.item_image = item_image;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_id = item_id;
        this.item_quantity = item_quantity;
    }



    MainModal(){

    }

    public String getItem_category() {
        return item_category;
    }

    public void setItem_category(String item_category) {
        this.item_category = item_category;
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

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }
}
