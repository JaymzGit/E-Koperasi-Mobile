package com.gnj.e_koperasi;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class MyApplication extends Application {
    private static Intent cartIntent;
    private static Bundle cartBundle;
    private static ArrayList<MainModal2> cartList;

    public static ArrayList<MainModal2> getCartList() {
        return cartList;
    }

    public static void setCartList(ArrayList<MainModal2> list) {
        cartList = list;
    }

    public static void setCartIntent(Intent intent) {
        cartIntent = intent;
    }

    public static Intent getCartIntent() {
        return cartIntent;
    }

    public static void setCartBundle(Bundle bundle) {
        cartBundle = bundle;
    }

    public static Bundle getCartBundle() {
        return cartBundle;
    }

    private static ArrayList<Bundle> orderList;
    // Other existing code in the class

    public static ArrayList<Bundle> getOrderList() {
        return orderList;
    }

    public static void setOrderList(ArrayList<Bundle> orderList) {
        MyApplication.orderList = orderList;
    }
}
