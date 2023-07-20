package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
    Button update;
    EditText etName, etID, etPhone, etEmail, et60;
    String name, id, phone, email, customer_id, cus_name, cus_phone, cus_email;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        update = findViewById(R.id.btnUpdate);
        etName = findViewById(R.id.etName);
        etID = findViewById(R.id.EtId);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.EtEmail);
        et60 = findViewById(R.id.et60);
        et60.setEnabled(false);
        etID.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("user");

        Query query = usersRef.orderByChild("customer_id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        customer_id = childSnapshot.child("customer_id").getValue(String.class);
                        cus_name = childSnapshot.child("name").getValue(String.class);
                        cus_phone = childSnapshot.child("phone").getValue(String.class);
                        cus_email = childSnapshot.child("email").getValue(String.class);

                        etName.setText(cus_name);
                        etID.setText(id.toUpperCase());
                        etPhone.setText(cus_phone.substring(3));
                        etEmail.setText(cus_email);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                id = etID.getText().toString();
                email = etEmail.getText().toString();
                phone = etPhone.getText().toString();
                if (name.isEmpty() && id.isEmpty() && phone.isEmpty() && email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Fill All the required Fill", Toast.LENGTH_SHORT).show();
                } else {
                    phone = "+60" + phone;
                    Query query = usersRef.orderByChild("customer_id").equalTo(id);
                    query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.exists()) {
                                    DatabaseReference userRef = dataSnapshot.getChildren().iterator().next().getRef();
                                    userRef.child("name").setValue(name);
                                    userRef.child("phone").setValue(phone);
                                    userRef.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Your account was updated", Toast.LENGTH_SHORT).show();
                                                Intent Setting = new Intent(getApplicationContext(), Setting.class);
                                                Bundle b = new Bundle();
                                                b.putString("id", id);
                                                Setting.putExtras(b);
                                                startActivity(Setting);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to update account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
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
                        Intent Settings = new Intent(getApplicationContext(), Setting.class);
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
}