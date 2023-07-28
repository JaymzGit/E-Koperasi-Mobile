package com.gnj.e_koperasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowCheckOut extends AppCompatActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_check_out);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        // Retrieve the cartlist ArrayList from the intent
        ArrayList<MainModal2> cartlist = bundle.getParcelableArrayList("cartlist");

        TableLayout tableLayout = findViewById(R.id.table);
        // Create a new TableRow
        TableRow row = new TableRow(this);

        // Loop through the cartlist and create rows dynamically

        for (MainModal2 item : cartlist) {


            // Create TextViews for each column and set their properties
            TextView itemNameTextView = createTextView(item.getItem_name());
            TextView itemPriceTextView = createTextView(String.valueOf(item.getItem_price()));
            TextView quantityTextView = createTextView(String.valueOf(item.getQuantity()));
            TextView totalPriceTextView = createTextView(String.valueOf(item.getItem_price() * item.getQuantity()));

            // Add the TextViews to the TableRow
            row.addView(itemNameTextView);
            row.addView(itemPriceTextView);
            row.addView(quantityTextView);
            row.addView(totalPriceTextView);

            // Add the TableRow to the TableLayout
            tableLayout.addView(row);
        }
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