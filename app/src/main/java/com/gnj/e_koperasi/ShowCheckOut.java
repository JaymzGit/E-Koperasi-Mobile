package com.gnj.e_koperasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ShowCheckOut extends AppCompatActivity {
    String id,itemName,itemPrice,quantity,totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_check_out);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        itemName=bundle.getString("itemName");
        itemPrice=bundle.getString("itemPrice");
        quantity=bundle.getString("quantity");
        totalPrice=bundle.getString("totalPrice");
    }
}