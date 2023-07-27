package com.gnj.e_koperasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ViewItem extends AppCompatActivity {
    String id;
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
        price = findViewById(R.id.price);
        add = findViewById(R.id.btnAdd);
        minus = findViewById(R.id.btnMinus);
        quantity = findViewById(R.id.tvNum);
        cart = findViewById(R.id.btnCart);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            id = bundle.getString("id");
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

                // Check if the item already exists in the orderList
                boolean itemExists = false;
                for (Bundle bundle : orderList) {
                    String existingItemName = bundle.getString("itemName");
                    if (existingItemName != null && existingItemName.equals(itemName)) {
                        // Item already exists in the cart, update its quantity and total price
                        int existingQuantity = bundle.getInt("itemQuantities");
                        double existingTotalPrice = bundle.getDouble("totalPrice");
                        existingQuantity += itemNum;
                        existingTotalPrice += total;
                        bundle.putInt("itemQuantities", existingQuantity);
                        bundle.putDouble("totalPrice", existingTotalPrice);
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    // Item doesn't exist in the cart, add it as a new item
                    orderList.add(orderBundle);
                }

                // Clear the input fields
                quantity.setText("1");
                // Reset the itemNum to 1
                itemNum = 1;
                finish();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.catelog);
        item.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.catelog:
                        return true;
                    case R.id.announcements:
                        Intent Announcements = new Intent(getApplicationContext(), Announcements.class);
                        Bundle announce = new Bundle();
                        announce.putString("id",id);
                        Announcements.putExtras(announce);
                        startActivity(Announcements);
                        return true;
                    case R.id.scan:
                        Intent ScanItems = new Intent(getApplicationContext(), ScanItems.class);
                        Bundle scan = new Bundle();
                        scan.putString("id",id);
                        ScanItems.putExtras(scan);
                        startActivity(ScanItems);
                        return true;
                    case R.id.cart:
                        Intent Cart = new Intent(getApplicationContext(), com.gnj.e_koperasi.Cart.class);
                        Bundle cart = new Bundle();
                        cart.putString("id",id);
                        Cart.putExtras(cart);
                        startActivity(Cart);
                        return true;
                    case R.id.setting:
                        Intent Settings = new Intent(getApplicationContext(), Setting.class);
                        Bundle setting = new Bundle();
                        setting.putString("id",id);
                        Settings.putExtras(setting);
                        startActivity(Settings);
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
