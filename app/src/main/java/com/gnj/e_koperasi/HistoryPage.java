package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryPage extends AppCompatActivity {
    String id;
    boolean appearOnce = true;
    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        recyclerViewHistory = findViewById(R.id.RecyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);

        FirebaseDatabase.getInstance().getReference("orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        historyList.clear();
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String customerId = orderSnapshot.child("customer_id").getValue(String.class);
                            if (customerId != null && customerId.equals(id)) {
                                // This order belongs to the current customer, add it to the historyList
                                double price = orderSnapshot.child("total_price").getValue(double.class);
                                String date = orderSnapshot.child("date").getValue(String.class);
                                String time = orderSnapshot.child("time").getValue(String.class);
                                String payment_method = orderSnapshot.child("payment_method").getValue(String.class);

                                if ("Online Payment".equals(payment_method)) {
                                    price = price + 1; //Add admin fee
                                }

                                StringBuilder purchase = new StringBuilder();
                                DataSnapshot cartListSnapshot = orderSnapshot.child("cartList");
                                int itemCount = 0;
                                HashMap<String, Integer> itemQuantities = new HashMap<>();

                                for (DataSnapshot cartItemSnapshot : cartListSnapshot.getChildren()) {
                                    String itemName = cartItemSnapshot.child("item_name").getValue(String.class);
                                    int itemQuantity = cartItemSnapshot.child("item_quantity").getValue(Integer.class);

                                    // Keep track of unique item names and their quantities
                                    if (itemName != null) {
                                        if (itemQuantities.containsKey(itemName)) {
                                            itemQuantities.put(itemName, itemQuantities.get(itemName) + itemQuantity);
                                        } else {
                                            itemQuantities.put(itemName, itemQuantity);
                                        }
                                    }
                                }

                                // Count the number of items with quantities greater than 1
                                int multipleItemsCount = 0;
                                for (int itemQuantity : itemQuantities.values()) {
                                    if (itemQuantity > 1) {
                                        multipleItemsCount++;
                                    }
                                }

                                // Append the item names and quantities to the purchase string
                                boolean isFirstItem = true;
                                for (Map.Entry<String, Integer> entry : itemQuantities.entrySet()) {
                                    String itemName = entry.getKey();
                                    int itemQuantity = entry.getValue();

                                    if (multipleItemsCount > 1) {
                                        isFirstItem = false;
                                    }

                                    if (itemCount > 0) {
                                        purchase.append(", ");
                                    }

                                    // Only append the quantity if the item appears more than once in the entire cart list
                                    if (itemQuantity > 1 && isFirstItem) {
                                        purchase.append(itemName + " x" + itemQuantity);
                                    } else {
                                        purchase.append(itemName);
                                    }

                                    itemCount++;
                                }

                                History history = new History(price, date, time, purchase.toString());
                                history.setOrderSnapshotKey(orderSnapshot.getKey());
                                historyList.add(history);
                            }
                        }
                        // Sort the historyList based on the date (latest date on top)
                        Collections.sort(historyList, new Comparator<History>() {
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                            @Override
                            public int compare(History h1, History h2) {
                                try {
                                    Date date1 = dateTimeFormat.parse(h1.getDate() + " " + h1.getTime());
                                    Date date2 = dateTimeFormat.parse(h2.getDate() + " " + h2.getTime());
                                    return date2.compareTo(date1); // For descending order (latest date and time on top)
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        });
                        historyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(HistoryPage.this, "Failed to load history: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Intent Cart = new Intent(getApplicationContext(), com.gnj.e_koperasi.Cart.class);
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
        Intent back = new Intent(getApplicationContext(), Setting.class);
        Bundle info = new Bundle();
        info.putString("id",id);
        back.putExtras(info);
        startActivity(back);
        super.onBackPressed();
    }

    public void onResume() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.setting);
        item.setChecked(true);
        super.onResume();
    }
}