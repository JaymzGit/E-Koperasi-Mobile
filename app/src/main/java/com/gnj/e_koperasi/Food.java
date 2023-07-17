package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Food extends AppCompatActivity {
    RecyclerView dispFood;
    DatabaseReference database;
    MainAdapter myAdapter;
    ArrayList<MainModal> itemlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        database = FirebaseDatabase.getInstance().getReference("item2");
        dispFood = findViewById(R.id.dispFood);
        dispFood.setLayoutManager(new LinearLayoutManager(this));
        dispFood.setHasFixedSize(true);

        itemlist = new ArrayList<>();
        myAdapter = new MainAdapter(this,itemlist);
        dispFood.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MainModal mainModal = dataSnapshot.getValue(MainModal.class);
                    itemlist.add(mainModal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}