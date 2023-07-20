package com.gnj.e_koperasi;

public class History {
    private String price;
    private String date;
    private String purchase;

    public History() {
        // Default constructor required for calls to DataSnapshot.getValue(History.class)
    }

    public History(String price, String date, String purchase) {
        this.price = price;
        this.date = date;
        this.purchase = purchase;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getPurchase() {
        return purchase;
    }
}
