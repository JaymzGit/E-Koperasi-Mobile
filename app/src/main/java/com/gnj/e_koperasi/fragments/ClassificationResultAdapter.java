package com.gnj.e_koperasi.fragments;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gnj.e_koperasi.databinding.ItemClassificationResultBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Adapter for displaying the list of classifications for the image */
public class ClassificationResultAdapter
        extends RecyclerView.Adapter<ClassificationResultAdapter.ViewHolder> {
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        public ViewHolder(@NonNull ItemClassificationResultBinding binding) {
            super(binding.getRoot());
            tvLabel = binding.tvLabel;
            tvScore = binding.tvScore;
        }

        public void bind(Category category) {
            if (category != null) {
                Query query = db.collection("item").whereEqualTo("item_name", category.getLabel());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    String item_name, item_price;
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                                            item_name = document.get("item_name").toString();
                                            item_price = document.get("item_price").toString();
                                            if (item_name.equals(category.getLabel())) {
                                                tvLabel.setText(item_name + " (" + item_price + ")");
                                                tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                                            }
                                        }
                                    } else {
                                        tvLabel.setText(category.getLabel());
                                        tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                                    }
                                }
                            });
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
