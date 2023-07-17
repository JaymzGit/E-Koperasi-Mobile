package com.gnj.e_koperasi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class viewItem extends AppCompatActivity {
    ImageView image;
    TextView name,price,quantity;
    Button add,minus,cart;
    int itemNum = 1;
    String itemName,imageUrl;
    double itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        image = findViewById(R.id.itemImage);
        name = findViewById(R.id.tvName);
        price = findViewById(R.id.tvPrice);
        add = findViewById(R.id.btnAdd);
        minus = findViewById(R.id.btnMinus);
        quantity = findViewById(R.id.tvNum);
        cart = findViewById(R.id.btnCart);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemName = bundle.getString("itemName");
            itemPrice = bundle.getDouble("itemPrice");
            imageUrl = bundle.getString("itemImage");

            name.setText(itemName);
            price.setText("RM " + String.valueOf(itemPrice) + "0");
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark) // Replace with your placeholder image resource
                    .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark) // Replace with your error image resource
                    .into(image);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemNum +=1;
                if(itemNum<1){
                    itemNum = 1;
                }
                quantity.setText(String.valueOf(itemNum));

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemNum -=1;
                if(itemNum<1){
                    itemNum = 1;
                }
                quantity.setText(String.valueOf(itemNum));

            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double total = itemPrice * itemNum;
                Bundle orderBundle = new Bundle();
                orderBundle.putString("itemName", itemName);
                orderBundle.putDouble("itemPrice", itemPrice);
                orderBundle.putString("itemImage", imageUrl);
                orderBundle.putInt("itemQuantities", itemNum);
                orderBundle.putDouble("totalPrice", total);

                // Retrieve the existing orderList from MyApplication or create a new one if it doesn't exist
                ArrayList<Bundle> orderList = MyApplication.getOrderList();
                if (orderList == null) {
                    orderList = new ArrayList<>();
                    MyApplication.setOrderList(orderList);
                }

                orderList.add(orderBundle);

                // Clear the input fields

                quantity.setText("1");



                // Reset the itemNum to 1
                itemNum = 1;
            }
        });


    }
}
