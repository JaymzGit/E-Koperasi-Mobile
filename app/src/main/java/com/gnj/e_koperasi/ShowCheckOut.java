package com.gnj.e_koperasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShowCheckOut extends AppCompatActivity {
    String id;
    Button cash,online;
    TextView tvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_check_out);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        cash = findViewById(R.id.btncash);
        online = findViewById(R.id.btnonline);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        ArrayList<MainModal2> cartlist = bundle.getParcelableArrayList("cartlist");
        double totalCartPrice = calculateTotalPrice(cartlist);
        tvTotalPrice.setText("Total price : RM " + String.format("%.2f", totalCartPrice));


        for (MainModal2 item : cartlist) {
            TableLayout tableLayout = findViewById(R.id.table);
            // Create a new TableRow
            TableRow row = new TableRow(this);


            // Create TextViews for each column and set their properties
            TextView itemNameTextView = createTextView(item.getItem_name());
            TextView itemPriceTextView = createTextView(String.valueOf(item.getItem_price()));
            TextView quantityTextView = createTextView(String.valueOf(item.getQuantity()));
            TextView totalPriceTextView = createTextView(String.valueOf(item.getItem_price() * item.getQuantity()));

            // Add the TextViews to the TableRow
            row.addView(itemNameTextView);
            //row.addView(itemPriceTextView);
            String quantity = quantityTextView.getText().toString();

            // Create a new TextView
            TextView Quantity = new TextView(this);
            Quantity.setText("    x" + quantity);
            Quantity.setTextColor(Color.WHITE);
            row.addView(Quantity);
           // row.addView(quantityTextView);
            String totalPrice = totalPriceTextView.getText().toString();

            // Create a new TextView
            TextView totalprice = new TextView(this);
            totalprice.setText("  RM " + totalPrice + "0");
            totalprice.setTextColor(Color.WHITE);
            row.addView(totalprice);



            // Add the TableRow to the TableLayout
            tableLayout.addView(row);
        }

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Receipt = new Intent(getApplicationContext(),receipt.class);
                Bundle receipt = new Bundle();
                receipt.putString("id",id);
                Receipt.putExtra("cartlist", cartlist);
                startActivity(Receipt);

            }
        });
    }

    // Helper method to create a TextView with common properties
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(16, 8, 16, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


        return textView;
    }
    private double calculateTotalPrice(ArrayList<MainModal2> cartlist) {
        double totalPrice = 0;
        for (MainModal2 item : cartlist) {
            totalPrice += (item.getItem_price() * item.getQuantity());
        }
        return totalPrice;
    }





    @Override
    public void onBackPressed() {
        Intent back = new Intent(getApplicationContext(), Cart.class);
        Bundle info = new Bundle();
        info.putString("id",id);
        back.putExtras(info);
        startActivity(back);
        super.onBackPressed();
    }


}