package com.gnj.e_koperasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ShowCheckOut extends AppCompatActivity {
    String id,itemName,itemPrice,quantity,totalPrice;
    TextView ItemName,ItemPrice,Quantity;

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
        ItemName=findViewById(R.id.ItemName);
        ItemPrice=findViewById(R.id.ItemPrice);
        Quantity=findViewById(R.id.Quantity);

        ItemName.setText(itemName);
        ItemPrice.setText(itemPrice);
        Quantity.setText(quantity);
    }
}