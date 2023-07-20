package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    TextView tvSignUp, tvGuest, tvForgotPassword;
    EditText userID, password;
    String id, pass;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SharedPrefManager
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);

        // Check if the user has already logged in
        if (sharedPrefManager.hasLoggedIn()) {
            // User has already logged in, redirect to ScanItems activity
            Intent intent = new Intent(this, ScanItems.class);
            startActivity(intent);
            finish(); // Optional, it closes the MainActivity so pressing back won't go back to it
        } else {

            setContentView(R.layout.activity_main);
            btnLogin = findViewById(R.id.btnLogin);
            tvSignUp = findViewById(R.id.tvSignUp);
            userID = findViewById(R.id.etUserID);
            password = findViewById(R.id.etPassword);
            tvForgotPassword = findViewById(R.id.tvForgotPassword);
            tvGuest = findViewById(R.id.tvGuest);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            userRef = database.getReference("user");

            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent reg = new Intent(getApplicationContext(), Register.class);
                    startActivity(reg);
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id = (userID.getText().toString()).toUpperCase().trim();
                    pass = password.getText().toString();

                    if (isValid()) {
                        Query query = userRef.orderByChild("customer_id").equalTo(id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);

                                        if (user != null) {
                                            String bcryptHashString = user.getPassword();
                                            BCrypt.Result result = BCrypt.verifyer().verify(pass.toCharArray(), bcryptHashString);

                                            if (result.verified) {
                                                Toast.makeText(getApplicationContext(), "Welcome, " + user.getName() + "!", Toast.LENGTH_SHORT).show();

                                                // Set the hasLoggedIn flag to true
                                                SharedPrefManager sharedPrefManager = new SharedPrefManager(MainActivity.this);
                                                sharedPrefManager.setLoggedIn(true);
                                                sharedPrefManager.saveUserId(id);

                                                Intent main = new Intent(getApplicationContext(), ScanItems.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("id", id);
                                                main.putExtras(bundle);
                                                startActivity(main);
                                                return;
                                            }
                                        }
                                    }
                                    password.setError("Incorrect username or password.");
                                } else {
                                    userID.setError("Account not registered. Please register an account.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            tvGuest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent guest = new Intent(getApplicationContext(), ScanItems.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", "GUEST");
                    guest.putExtras(bundle);
                    startActivity(guest);
                }
            });

            tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent forgot = new Intent(getApplicationContext(), ForgotPassword.class);
                    startActivity(forgot);
                }
            });
        }
    }

    public boolean isValid() {
        if (id.isEmpty() && pass.isEmpty()) {
            userID.setError("Enter your matric number or staff ID.");
            password.setError("Enter your password.");
            return false;
        } else if (id.isEmpty()) {
            userID.setError("Enter your matric number or staff ID.");
            return false;
        } else if (pass.isEmpty()) {
            password.setError("Enter your password.");
            return false;
        } else if (pass.length() < 8) {
            password.setError("Password must be at least 8 characters long.");
            return false;
        } else {
            return true;
        }
    }
}