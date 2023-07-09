package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserProfile extends AppCompatActivity {
    Button update;
    EditText etName, etID, etPhone, etEmail, et60;
    String name,id,phone,email,customer_id,cus_name,cus_phone,cus_email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        Query query = db.collection("user").whereEqualTo("customer_id", id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    customer_id = document.get("customer_id").toString().toUpperCase();
                                    cus_name = document.get("name").toString();
                                    cus_phone = document.get("phone").toString();
                                    cus_email = document.get("email").toString();

                                    etName.setText(cus_name);
                                    etID.setText(id.toUpperCase());
                                    etPhone.setText(cus_phone.substring(3));
                                    etEmail.setText(cus_email);
                                }
                            }
                        }
                    });
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                id = etID.getText().toString();
                email = etEmail.getText().toString();
                phone = etPhone.getText().toString();
                if(name.isEmpty() && id.isEmpty()&&phone.isEmpty()&&email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Fill All the required Fill",Toast.LENGTH_SHORT).show();
                }
                else {
                    phone="+60"+phone;
                    Query query = db.collection("user").whereEqualTo("customer_id", id);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    DocumentReference userRef = querySnapshot.getDocuments().get(0).getReference();
                                    userRef.update("name", name,"phone",phone,"email",email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Your account was updated", Toast.LENGTH_SHORT).show();
                                                Intent Setting = new Intent(getApplicationContext(),Setting.class);
                                                Bundle b=new Bundle();
                                                b.putString("id",id);
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
    }
}