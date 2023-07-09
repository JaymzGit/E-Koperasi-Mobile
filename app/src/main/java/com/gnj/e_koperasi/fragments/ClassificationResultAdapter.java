package com.gnj.e_koperasi.fragments;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gnj.e_koperasi.databinding.ItemClassificationResultBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Adapter for displaying the list of classifications for the image */
public class ClassificationResultAdapter extends RecyclerView.Adapter<ClassificationResultAdapter.ViewHolder> {
    private static final String NO_VALUE = "--";
    private List<Category> categories = new ArrayList<>();
    private int adapterSize = 0;

    @SuppressLint("NotifyDataSetChanged")
    public void updateResults(List<Category> categories) {
        List<Category> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories, new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                return category1.getIndex() - category2.getIndex();
            }
        });
        this.categories = new ArrayList<>(Collections.nCopies(adapterSize, null));
        int min = Math.min(sortedCategories.size(), adapterSize);
        for (int i = 0; i < min; i++) {
            this.categories.set(i, sortedCategories.get(i));
        }
        notifyDataSetChanged();
    }

    public void updateAdapterSize(int size) {
        adapterSize = size;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClassificationResultBinding binding = ItemClassificationResultBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /** Data structure for items in list */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLabel;
        private final TextView tvScore;

        private FirebaseDatabase database = FirebaseDatabase.getInstance();
        private DatabaseReference itemsRef = database.getReference("item");

        public ViewHolder(@NonNull ItemClassificationResultBinding binding) {
            super(binding.getRoot());
            tvLabel = binding.tvLabel;
            tvScore = binding.tvScore;
        }

        public void bind(Category category) {
            if (category != null) {
                Query query = itemsRef.orderByChild("item_name").equalTo(category.getLabel());
                query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String item_name = childSnapshot.child("item_name").getValue(String.class);
                                    String item_price = childSnapshot.child("item_price").getValue(String.class);
                                    if (item_name != null && item_name.equals(category.getLabel())) {
                                        tvLabel.setText(item_name + " (" + item_price + ")");
                                        tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                                        Log.d("Item",item_name);
                                        break;
                                    }
                                }
                            } else {
                                tvLabel.setText(category.getLabel());
                                tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                            }
                        }
                    }
                });
            } else {
                tvLabel.setText(NO_VALUE);
                tvScore.setText(NO_VALUE);
            }
        }
    }
}