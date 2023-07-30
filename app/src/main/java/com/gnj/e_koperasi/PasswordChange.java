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

import com.gnj.e_koperasi.MainActivity;
import com.gnj.e_koperasi.Setting;
import com.gnj.e_koperasi.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordChange extends AppCompatActivity {
    Button btnSend;
    EditText Id, newpass, cnpass;
    String id, NewPass, CnPass, frompage, startid, lastid, phone;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        Id = findViewById(R.id.etId);
        newpass = findViewById(R.id.EtPass);
        cnpass = findViewById(R.id.EtConfirmPass);
        btnSend = findViewById(R.id.btnSend);

        Bundle b = getIntent().getExtras();
        frompage=b.getString("frompage");

        Id.setText(id);
        if(frompage.equals("OTP")) {
            phone = b.getString("mobile");
            // Query by Phone Number
            Query query = userRef.orderByChild("phone").equalTo(phone);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        String customerID = firstChild.child("customer_id").getValue(String.class);

                        // Set the Id field
                        Id.setText(customerID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);

                }
            });
        }else {
            id=b.getString("id");
            Id.setText(id);
        }

        Id.setEnabled(false);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = (Id.getText().toString()).toUpperCase();
                NewPass = newpass.getText().toString();
                CnPass = cnpass.getText().toString();

                if (isValid()) {
                    if (!frompage.equals("Login")) {
                        // Query by Mobile Number
                        Query query = userRef.orderByChild("phone").equalTo(phone);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                    DatabaseReference userRef = firstChild.getRef();
                                    String bcryptHashString = BCrypt.withDefaults().hashToString(12, NewPass.toCharArray());

                                    userRef.child("password").setValue(bcryptHashString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(main);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Query by ID
                        Query query = userRef.orderByChild("customer_id").equalTo(id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                    DatabaseReference userRef = firstChild.getRef();
                                    String bcryptHashString = BCrypt.withDefaults().hashToString(12, NewPass.toCharArray());

                                    userRef.child("password").setValue(bcryptHashString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(main);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

        public boolean isValid() {
            if (NewPass.isEmpty() || NewPass.length() < 8) {
                newpass.setError("Password must be at least 8 characters long.");
                return false;
            } else if (!NewPass.equals(CnPass)) {
                newpass.setError("Passwords do not match");
                cnpass.setError("Passwords do not match");
                return false;
            } else {
                return true;
            }
        }
    @Override
    public void onBackPressed() {
        if(frompage.equals("OTP")){
        super.onBackPressed();
        }else {
            Intent back = new Intent(getApplicationContext(), Setting.class);
            Bundle info = new Bundle();
            info.putString("id",id);
            back.putExtras(info);
            startActivity(back);
            super.onBackPressed();
            }
    }
}
