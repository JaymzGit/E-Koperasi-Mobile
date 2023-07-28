package com.gnj.e_koperasi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Cart extends AppCompatActivity implements MainAdapter2.TotalCartPriceListener {
    RecyclerView dispCart;
    MainAdapter2 myAdapter;
    ArrayList<MainModal2> cartlist;
    TextView totalCartPrice;
    String id;
    Button button2;

    @SuppressLint("SetTextI18n")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dispCart = findViewById(R.id.cartList);
        button2=findViewById(R.id.button2);
        button2.setVisibility(View.GONE);
        dispCart.setLayoutManager(new LinearLayoutManager(this));
        dispCart.setHasFixedSize(true);
        totalCartPrice = findViewById(R.id.totalPrice);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        // Retrieve the existing orderList from MyApplication
        ArrayList<Bundle> orderList = MyApplication.getOrderList();

        // Create a new ArrayList if orderList doesn't exist or is empty
        if (orderList == null || orderList.isEmpty()) {
            cartlist = new ArrayList<>();
        } else {
            // Convert the orderList into MainModal2 items and add them to the cartlist
            cartlist = new ArrayList<>();
            for (Bundle orderBundle : orderList) {
                String itemName = orderBundle.getString("itemName");
                double itemPrice = orderBundle.getDouble("itemPrice");
                String imageUrl = orderBundle.getString("itemImage");
                int quantity = orderBundle.getInt("itemQuantities");
                double totalPrice = orderBundle.getDouble("totalPrice");

                MainModal2 item = new MainModal2(imageUrl, itemName, itemPrice, quantity);
                cartlist.add(item);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent Checkout = new Intent(getApplicationContext(),ShowCheckOut.class);
                        Bundle checkout = new Bundle();
                        checkout.putString("id",id);
                        checkout.putString("itemName",itemName);
                        checkout.putString("itemPrice", String.valueOf(itemPrice));
                        checkout.putString("quantity", String.valueOf(quantity));
                        checkout.putString("totalPrice", String.valueOf(totalPrice));
                        Checkout.putExtras(checkout);
                        Checkout.putExtra("cartlist", cartlist);
                        startActivity(Checkout);
                    }
                });
            }
        }

        myAdapter = new MainAdapter2(this, cartlist);
        myAdapter.setTotalCartPriceListener(this);
        dispCart.setAdapter(myAdapter);

        // Notify the adapter that the data set has changed
        myAdapter.notifyDataSetChanged();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.cart);
        item.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.catelog:
                        Intent MainPage = new Intent(getApplicationContext(), Catalog.class);
                        Bundle main = new Bundle();
                        main.putString("id",id);
                        MainPage.putExtras(main);
                        startActivity(MainPage);
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
                        Intent Settings = new Intent(getApplicationContext(),Setting.class);
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
    public void onTotalCartPriceUpdated(double totalCartPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.totalCartPrice.setText("Total price : RM " + decimalFormat.format(totalCartPrice));
        if (totalCartPrice == 0) {
            button2.setVisibility(View.GONE);
        } else {
            button2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.cart);
        item.setChecked(true);
        super.onResume();

        // Update the total cart price
        myAdapter.calculateTotalCartPrice();
        double totalCartPrice = myAdapter.getTotalCartPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.totalCartPrice.setText("Total price : RM " + decimalFormat.format(totalCartPrice));
        if (totalCartPrice == 0) {
            button2.setVisibility(View.GONE);
        } else {
            button2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
