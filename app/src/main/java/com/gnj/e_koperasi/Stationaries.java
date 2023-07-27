package com.gnj.e_koperasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Stationaries extends AppCompatActivity {
    String id;
    RecyclerView dispStationaries;
    DatabaseReference database;
    MainAdapter myAdapter;
    ArrayList<MainModal> itemlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationaries);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        database = FirebaseDatabase.getInstance().getReference("item");
        dispStationaries = findViewById(R.id.dispStationaries);
        dispStationaries.setLayoutManager(new LinearLayoutManager(this));

        itemlist = new ArrayList<>();
        myAdapter = new MainAdapter(this, itemlist, id);
        dispStationaries.setAdapter(myAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MainModal mainModal = dataSnapshot.getValue(MainModal.class);
                    if (mainModal != null && "other".equals(mainModal.getItem_category())) {
                        itemlist.add(mainModal);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
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
                        Intent MainPage = new Intent(getApplicationContext(), Catalog.class);
                        Bundle main = new Bundle();
                        main.putString("id",id);
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
                        Intent Cart = new Intent(getApplicationContext(), com.gnj.e_koperasi.Cart.class);
                        Bundle cart = new Bundle();
                        cart.putString("id", id);
                        Cart.putExtras(cart);
                        startActivity(Cart);
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
    }
    @Override
    public void onBackPressed() {
        Intent back = new Intent(getApplicationContext(), Catalog.class);
        Bundle info = new Bundle();
        info.putString("id",id);
        back.putExtras(info);
        startActivity(back);
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
