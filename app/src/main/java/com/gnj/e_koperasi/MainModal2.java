package com.gnj.e_koperasi;

import android.os.Parcel;
import android.os.Parcelable;

public class MainModal2 implements Parcelable{
    String item_image,item_name;
    double item_price,totalPrice;
    int quantity;

    MainModal2(){

    }
    public MainModal2(String item_image, String item_name, double item_price, int quantity) {
        this.item_image = item_image;
        this.item_name = item_name;
        this.item_price = item_price;
        this.quantity = quantity;
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

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }



    protected MainModal2(Parcel in) {
        item_image = in.readString();
        item_name = in.readString();
        item_price = in.readDouble();
        totalPrice = in.readDouble(); // Added read for totalPrice
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item_image);
        dest.writeString(item_name);
        dest.writeDouble(item_price);
        dest.writeDouble(totalPrice); // Added write for totalPrice
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainModal2> CREATOR = new Parcelable.Creator<MainModal2>() {
        @Override
        public MainModal2 createFromParcel(Parcel in) {
            return new MainModal2(in);
        }

        @Override
        public MainModal2[] newArray(int size) {
            return new MainModal2[size];
        }
    };

}
