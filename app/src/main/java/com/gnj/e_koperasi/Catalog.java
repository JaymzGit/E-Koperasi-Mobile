package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

    public class Catalog extends AppCompatActivity {
    String id;
    ImageButton btnFood, btnDrinks, btnStationaries, btnMisc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        btnFood = findViewById(R.id.btnFood);
        btnDrinks = findViewById(R.id.btnDrinks);
        btnStationaries = findViewById(R.id.btnStationaries);
        btnMisc = findViewById(R.id.btnMisc);

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Food.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Drinks.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnStationaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Stationaries.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Misc.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                i.putExtras(bundle);
                startActivity(i);
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
    public void onBackPressed() {
        super.onBackPressed();
    }
        public void onResume() {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem item = menu.findItem(R.id.catelog);
            item.setChecked(true);
            super.onResume();
        }
}