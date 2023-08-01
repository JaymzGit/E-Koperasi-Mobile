package com.gnj.e_koperasi;

public class History {
    private double price;
    private String date;
    private String time;
    private String purchase;
    private String orderSnapshotKey;
    public History() {
        // Default constructor required for calls to DataSnapshot.getValue(History.class)
    }

    public History(double price, String date, String time, String purchase) {
        this.price = price;
        this.date = date;
        this.time = time;
        this.purchase = purchase;
    }

    public double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPurchase() {
        return purchase;
    }

    public String getOrderSnapshotKey() {
        return orderSnapshotKey;
    }

    public void setOrderSnapshotKey(String orderSnapshotKey) {
        this.orderSnapshotKey = orderSnapshotKey;
    }
}
