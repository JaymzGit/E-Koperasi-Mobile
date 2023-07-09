package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gnj.e_koperasi.fragments.GuestFragment;
import com.gnj.e_koperasi.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Setting extends AppCompatActivity {
    String name = "", id, cus_name, customer_id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Bundle b = getIntent().getExtras();
        id = (b.getString("id")).toUpperCase();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id.equals("GUEST")) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, GuestFragment.class, null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, UserFragment.class, null).commit();
        }

        Query query = userRef.orderByChild("customer_id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        customer_id = snapshot.child("customer_id").getValue(String.class).toUpperCase();
                        cus_name = snapshot.child("name").getValue(String.class).toUpperCase();
                        name = cus_name;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Bundle b = new Bundle();
                        b.putString("id", id);
                        MainPage.putExtras(b);
                        startActivity(MainPage);
                        return true;
                    case R.id.scan:
                        Intent CamScan = new Intent(getApplicationContext(), ScanItems.class);
                        Bundle c = new Bundle();
                        c.putString("id", id);
                        CamScan.putExtras(c);
                        startActivity(CamScan);
                        return true;
                    case R.id.cart:
                        Intent cart = new Intent(getApplicationContext(), Cart.class);
                        Bundle d = new Bundle();
                        d.putString("id", id);
                        cart.putExtras(d);
                        startActivity(cart);
                        return true;
                    case R.id.setting:
                        Intent Setting = new Intent(getApplicationContext(), Setting.class);
                        Bundle e = new Bundle();
                        e.putString("id", id);
                        Setting.putExtras(e);
                        startActivity(Setting);
                        return true;
                }
                return false;
            }
        });
    }
}
