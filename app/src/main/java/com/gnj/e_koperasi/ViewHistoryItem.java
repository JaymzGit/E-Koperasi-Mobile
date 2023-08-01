package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewHistoryItem extends AppCompatActivity {

    private TextView tvPrice;
    private TextView tvDate;
    private TextView tvPurchaseTime;
    private TextView tvPaymentMethodValue;
    private TextView tvReferenceNo3;
    private TextView tvAdminFeePrice;
    private TextView tvStatus;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_item);

        // Get the order snapshot key from the Intent
        String orderSnapshotKey = getIntent().getStringExtra("orderSnapshotKey");

        // Initialize the TextViews
        tvPrice = findViewById(R.id.price);
        tvDate = findViewById(R.id.tvDate);
        tvPurchaseTime = findViewById(R.id.tvPurchaseTime);
        tvPaymentMethodValue = findViewById(R.id.tvPaymentMethodValue);
        tvReferenceNo3 = findViewById(R.id.tvReferenceNo3);
        tvAdminFeePrice = findViewById(R.id.tvadminFeePrice);
        tvStatus = findViewById(R.id.tvStatus);

        // Initialize the RecyclerView and its adapter for displaying purchase details
        RecyclerView purchaseDetailsRecyclerView = findViewById(R.id.purchaseDetailsRecyclerView);
        purchaseDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<PurchaseDetail> purchaseDetailsList = new ArrayList<>();
        PurchaseDetailsAdapter purchaseDetailsAdapter = new PurchaseDetailsAdapter(purchaseDetailsList);
        purchaseDetailsRecyclerView.setAdapter(purchaseDetailsAdapter);

        // Use the order snapshot key to fetch the order details from Firebase
        FirebaseDatabase.getInstance().getReference("orders")
                .child(orderSnapshotKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Retrieve the order details from dataSnapshot and display them in the activity
                        double price = dataSnapshot.child("total_price").getValue(Double.class);
                        String date = dataSnapshot.child("date").getValue(String.class);
                        String time = dataSnapshot.child("time").getValue(String.class);
                        String payment_method = dataSnapshot.child("payment_method").getValue(String.class);
                        String status = dataSnapshot.child("status").getValue(String.class);
                        String reference_no = orderSnapshotKey;
                        id = dataSnapshot.child("customer_id").getValue(String.class);

                        // Set the values to the TextViews
                        tvDate.setText(date);
                        tvPurchaseTime.setText(time);
                        tvPaymentMethodValue.setText(payment_method);
                        if ("Online Payment".equals(payment_method)) {
                            price = price + 1; //Add admin fee
                            tvAdminFeePrice.setText("RM 1.00");
                        }else {
                            tvAdminFeePrice.setText("RM 0.00");
                        }
                        tvPrice.setText("-RM " + price + "0");
                        tvReferenceNo3.setText(reference_no);
                        tvStatus.setText(status);

                        // Get the cartList snapshot
                        DataSnapshot cartListSnapshot = dataSnapshot.child("cartList");

                        // Clear the previous purchase details
                        purchaseDetailsList.clear();

                        // Loop through the cart items and add them to the purchase details list
                        for (DataSnapshot cartItemSnapshot : cartListSnapshot.getChildren()) {
                            String itemName = cartItemSnapshot.child("item_name").getValue(String.class);
                            Double itemPrice = cartItemSnapshot.child("item_price").getValue(Double.class);
                            Integer itemQuantity = cartItemSnapshot.child("item_quantity").getValue(Integer.class);

                            if (itemName != null && itemPrice != null && itemQuantity != null) {
                                PurchaseDetail purchaseDetail = new PurchaseDetail(itemName, itemQuantity, itemPrice);
                                purchaseDetailsList.add(purchaseDetail);
                            }
                        }

                        // Notify the adapter that data has changed
                        purchaseDetailsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewHistoryItem.this, "Failed to fetch order details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.setting);
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
                        Intent Cart = new Intent(getApplicationContext(), Cart.class);
                        Bundle cart = new Bundle();
                        cart.putString("id",id);
                        Cart.putExtras(cart);
                        startActivity(Cart);
                        return true;
                    case R.id.setting:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = getIntent().getExtras();
        String frompage = bundle.getString("frompage");
        if ("Cash".equals(frompage)) { // Use .equals() for string comparison
            // If payment is done, handle the back button as needed, e.g., go back to the previous activity
            Intent intent = new Intent(ViewHistoryItem.this, Cart.class);
            Bundle info = new Bundle();
            info.putString("id", id);
            info.putBoolean("clear", true);
            intent.putExtras(info);
            startActivity(intent);
        } else if ("Online".equals(frompage)) { // Use .equals() for string comparison
            // If payment is done, handle the back button as needed, e.g., go back to the previous activity
            Intent intent = new Intent(ViewHistoryItem.this, HistoryPage.class);
            Bundle info = new Bundle();
            info.putString("id", id);
            intent.putExtras(info);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}