package com.gnj.e_koperasi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Cart extends AppCompatActivity implements MainAdapter2.TotalCartPriceListener {
    RecyclerView dispCart;
    MainAdapter2 myAdapter;
    ArrayList<MainModal2> cartlist;
    TextView totalCartPriceText;
    String id, latestOrderId;
    double totalCartPrice;
    Button button2;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = database.getReference("orders");

    Gson gson = new Gson();
    String cartListJson = gson.toJson(cartlist);
    String currentDate = getCurrentDate();
    String currentTime = getCurrentTime();

    ArrayList<OrderItem> orderItems;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dispCart = findViewById(R.id.cartList);
        button2 = findViewById(R.id.button2);
        button2.setVisibility(View.GONE);
        dispCart.setLayoutManager(new LinearLayoutManager(this));
        dispCart.setHasFixedSize(true);
        totalCartPriceText = findViewById(R.id.totalPrice);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        // Retrieve the existing orderList from MyApplication
        ArrayList<Bundle> orderList = MyApplication.getOrderList();

        // Create a new ArrayList if orderList doesn't exist or is empty
        if (orderList == null || orderList.isEmpty()) {
            cartlist = new ArrayList<>();
            orderItems = new ArrayList<>();
        } else {
            // Convert the orderList into MainModal2 items and add them to the cartlist
            cartlist = new ArrayList<>();
            orderItems = new ArrayList<>();
            for (Bundle orderBundle : orderList) {
                String itemName = orderBundle.getString("itemName");
                double itemPrice = orderBundle.getDouble("itemPrice");
                String imageUrl = orderBundle.getString("itemImage");
                int quantity = orderBundle.getInt("itemQuantities");
                double totalPrice = orderBundle.getDouble("totalPrice");

                MainModal2 item = new MainModal2(imageUrl, itemName, itemPrice, quantity);
                cartlist.add(item);

                OrderItem orderItem = new OrderItem(itemName, quantity, itemPrice);
                orderItems.add(orderItem);
            }
        }

        orderItems = new ArrayList<>();
        for (MainModal2 item : cartlist) {
            OrderItem orderItem = new OrderItem(item.getItem_name(), item.getQuantity(), item.getItem_price());
            orderItems.add(orderItem);
        }

        myAdapter = new MainAdapter2(this, cartlist, null); // Set orderSnapshotKey to null initially
        myAdapter.setTotalCartPriceListener(this);
        dispCart.setAdapter(myAdapter);

        // Calculate and set the total cart price
        myAdapter.calculateTotalCartPrice();

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
                        main.putString("id", id);
                        MainPage.putExtras(main);
                        startActivity(MainPage);
                        return true;
                    case R.id.announcements:
                        Intent Announcements = new Intent(getApplicationContext(), Announcements.class);
                        Bundle announce = new Bundle();
                        announce.putString("id", id);
                        Announcements.putExtras(announce);
                        startActivity(Announcements);
                        return true;
                    case R.id.scan:
                        Intent ScanItems = new Intent(getApplicationContext(), ScanItems.class);
                        Bundle scan = new Bundle();
                        scan.putString("id", id);
                        ScanItems.putExtras(scan);
                        startActivity(ScanItems);
                        return true;
                    case R.id.cart:
                        return true;
                    case R.id.setting:
                        Intent Settings = new Intent(getApplicationContext(), Setting.class);
                        Bundle setting = new Bundle();
                        setting.putString("id", id);
                        Settings.putExtras(setting);
                        startActivity(Settings);
                        return true;
                }
                return false;
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderItems = new ArrayList<>();
                for (MainModal2 item : cartlist) {
                    OrderItem orderItem = new OrderItem(item.getItem_name(), item.getQuantity(), item.getItem_price());
                    orderItems.add(orderItem);
                }
                if (id.equals("GUEST")) {
                    String key = databaseRef.push().getKey();
                    latestOrderId = key;
                    String status = "Pending";
                    String payment_method = "Cash/Other";
                    Order insertdata = new Order(id, orderItems, currentDate, currentTime, totalCartPrice, payment_method, status);
                    databaseRef.child(key).setValue(insertdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //toast
                            } else {
                                //
                            }
                        }
                    });

                    cartlist.clear(); // Clear the cart list
                    orderItems.clear();
                    myAdapter.notifyDataSetChanged();

                    Intent mainIntent = new Intent(getApplicationContext(), ViewHistoryItem.class);
                    Bundle info = new Bundle();
                    info.putString("orderSnapshotKey", latestOrderId);
                    info.putBoolean("clear", true);
                    info.putString("frompage", "Cash");
                    mainIntent.putExtras(info);
                    startActivity(mainIntent);
                    finish();
                } else {
                    showPaymentDialog();
                }
            }
        });
    }

    @Override
    public void onTotalCartPriceUpdated(double totalCartPrice) {
        this.totalCartPrice = totalCartPrice; // Update the class-level totalCartPrice variable
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.totalCartPriceText.setText("Total price : RM " + decimalFormat.format(totalCartPrice));
        if (totalCartPrice == 0) {
            button2.setVisibility(View.GONE);
        } else {
            button2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.cart);
        item.setChecked(true);

        // Check if the 'clear' flag is set to true
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("clear", false)) {
            int size = cartlist.size();
            cartlist.clear();
            myAdapter.notifyDataSetChanged();

            ArrayList<Bundle> orderList = MyApplication.getOrderList();
            if (orderList != null) {
                orderList.clear();
            }

            // Clear the 'clear' flag so that it doesn't clear the cart again in the future
            extras.putBoolean("clear", false);
            getIntent().putExtras(extras);
        }

        // Update the total cart price
        myAdapter.calculateTotalCartPrice();
        totalCartPrice = myAdapter.getTotalCartPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.totalCartPriceText.setText("Total price : RM " + decimalFormat.format(totalCartPrice));
        if (totalCartPrice == 0) {
            button2.setVisibility(View.GONE);
        } else {
            button2.setVisibility(View.VISIBLE);
        }

        // Notify the adapter that the data set has changed
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showPaymentDialog() {
        // Create a new AlertDialog.Builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Cart.this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_payment_dialog, null);
        alertDialogBuilder.setView(customLayout);

        // Find the buttons in the custom layout
        Button buttonPayAtCounter = customLayout.findViewById(R.id.buttonPayAtCounter);
        Button buttonOnlinePayment = customLayout.findViewById(R.id.buttonOnlinePayment);

        // Create the AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Set click listeners for the buttons
        buttonPayAtCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = databaseRef.push().getKey();
                latestOrderId = key;
                String status = "Pending";
                String payment_method = "Cash/Other";
                Order insertdata = new Order(id, orderItems, currentDate, currentTime, totalCartPrice, payment_method, status);
                databaseRef.child(key).setValue(insertdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Database Update", "Successfully saved new order for cash payment.");
                        }
                    }
                });

                cartlist.clear(); // Clear the cart list
                orderItems.clear();
                myAdapter.notifyDataSetChanged();

                Intent mainIntent = new Intent(getApplicationContext(), ViewHistoryItem.class);
                Bundle info = new Bundle();
                info.putString("orderSnapshotKey", latestOrderId);
                info.putBoolean("clear", true);
                info.putString("frompage", "Cash");
                mainIntent.putExtras(info);
                startActivity(mainIntent);
                finish();
                alertDialog.dismiss(); // Dismiss the dialog
            }
        });

        buttonOnlinePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = databaseRef.push().getKey();
                String status = "Pending";
                latestOrderId = key;
                String payment_method = "Online Payment";
                Order insertdata = new Order(id, orderItems, currentDate, currentTime, totalCartPrice, payment_method, status);
                databaseRef.child(key).setValue(insertdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Database Update", "Successfully saved new order for online payment.");
                        }
                    }
                });

                cartlist.clear(); // Clear the cart list
                orderItems.clear();
                myAdapter.notifyDataSetChanged();

                Intent payment = new Intent(getApplicationContext(), Payment.class);
                Bundle data = new Bundle();
                data.putString("id", id);
                data.putString("latestOrderId", latestOrderId);
                payment.putExtras(data);
                startActivity(payment);
                alertDialog.dismiss(); // Dismiss the dialog
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = Calendar.getInstance().getTime();
        return dateFormat.format(currentDate);
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        return timeFormat.format(currentTime);
    }
}