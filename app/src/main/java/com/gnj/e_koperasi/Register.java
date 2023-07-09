package com.gnj.e_koperasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Register extends AppCompatActivity {
    Button Registr;
    TextView SignIn;
    EditText Name, Id, Phone, Password, ConfirmPass, Email, et60;
    String name, id, phone, password, cpass, email, docId;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Registr = findViewById(R.id.btnRegister);
        SignIn = findViewById(R.id.tvSignUp);
        Name = findViewById(R.id.etRegName);
        Email = findViewById(R.id.etRegEmail);
        Id = findViewById(R.id.etRegUserID);
        Phone = findViewById(R.id.etRegPhone);
        Password = findViewById(R.id.etRegPassword);
        ConfirmPass = findViewById(R.id.etRegCPassword);
        et60 = findViewById(R.id.et60);
        et60.setEnabled(false);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(sign);
            }
        });

        Registr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = Name.getText().toString();
                id = (Id.getText().toString()).toUpperCase();
                phone = Phone.getText().toString();
                password = Password.getText().toString();
                cpass = ConfirmPass.getText().toString();
                email = Email.getText().toString();

                if (isValid()) {
                    Query idQuery = userRef.orderByChild("customer_id").equalTo(id);
                    Query phoneQuery = userRef.orderByChild("phone").equalTo("+60" + phone);
                    Query emailQuery = userRef.orderByChild("email").equalTo(email);
                    idQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Id.setError("ID already exists");
                           } else {
                                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Email.setError("Email already exists");
                                        } else {
                                            phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Phone.setError("Phone number already exists");
                                                    } else {
                                                        String key = userRef.push().getKey();

                                                        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());

                                                        phone = "+60" + Phone.getText().toString();

                                                        User insertdata = new User(name, id, email, phone, bcryptHashString);
                                                        userRef.child(key).setValue(insertdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                                                    Intent sign = new Intent(getApplicationContext(), MainActivity.class);
                                                                    startActivity(sign);
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException(), Toast.LENGTH_SHORT).show();
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
    }

    public boolean isValid() {
        if (name.isEmpty() || !name.matches("[a-zA-Z /]+")) {
            Name.setError("Name can only consists of letters");
            return false;
        }else if(id.isEmpty()) {
            Id.setError("Enter your matric num or staff ID.");
            return false;
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter a valid email address.");
            return false;
        } else if (phone.isEmpty() || !phone.matches("\\d{9,10}")) {
            Phone.setError("Phone number must follow format: +60 xxxxxxxxx");
            return false;
        }else if(password.isEmpty() || password.length() < 8) {
            Password.setError("Password must be at least 8 characters long.");
            return false;
        }else if (!password.equals(cpass)) {
            Password.setError("Passwords do not match");
            ConfirmPass.setError("Passwords do not match");
            return false;
        }else {
            return true;
        }
    }
}