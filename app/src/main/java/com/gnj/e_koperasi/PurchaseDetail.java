package com.gnj.e_koperasi;

public class PurchaseDetail {
    private String itemName;
    private int itemQuantity;
    private double itemPrice;

    public PurchaseDetail(String itemName, int itemQuantity, double itemPrice) {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }
}

