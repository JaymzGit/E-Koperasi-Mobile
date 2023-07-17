package com.gnj.e_koperasi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class cart extends AppCompatActivity implements MainAdapter2.TotalCartPriceListener {
    RecyclerView dispCart;
    MainAdapter2 myAdapter;
    ArrayList<MainModal2> cartlist;
    TextView totalCartPrice;

    @SuppressLint("SetTextI18n")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dispCart = findViewById(R.id.cartList);
        dispCart.setLayoutManager(new LinearLayoutManager(this));
        dispCart.setHasFixedSize(true);
        totalCartPrice = findViewById(R.id.totalPrice);

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

                MainModal2 item = new MainModal2(imageUrl, itemName, itemPrice, quantity, totalPrice);
                cartlist.add(item);
            }
        }

        myAdapter = new MainAdapter2(this, cartlist);
        myAdapter.setTotalCartPriceListener(this);
        dispCart.setAdapter(myAdapter);

        // Notify the adapter that the data set has changed
        myAdapter.notifyDataSetChanged();
    }


    @Override
    public void onTotalCartPriceUpdated(double totalCartPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.totalCartPrice.setText(decimalFormat.format(totalCartPrice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the total cart price
        myAdapter.calculateTotalCartPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        totalCartPrice.setText(decimalFormat.format(myAdapter.getTotalCartPrice()));
    }
}
