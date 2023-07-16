package com.gnj.e_koperasi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gnj.e_koperasi.databinding.ItemClassificationResultBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClassificationResultAdapter
        extends RecyclerView.Adapter<ClassificationResultAdapter.ViewHolder> {
    private static final String NO_VALUE = "--";
    private List<Category> categories = new ArrayList<>();
    private int adapterSize = 0;

    private HashMap<String, String> labelMap = new HashMap<>();

    public ClassificationResultAdapter(Context context) {
        labelMap = readLabelsFromFile(context, "labels.txt");
    }
    public HashMap<String, String> readLabelsFromFile(Context context, String filePath) {
        HashMap<String, String> labelMap = new HashMap<>();
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ", 2); // Split only on the first space
                if (parts.length == 2) {
                    String number = parts[0].trim();
                    String itemName = parts[1].trim();
                    labelMap.put(number, itemName);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Log the contents of labelMap after populating it
        for (String key : labelMap.keySet()) {
            Log.d("ClassificationResult", "Label: " + key + ", Text: " + labelMap.get(key));
        }

        return labelMap;
    }
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

    // Method to update the labelMap in the adapter
    public void updateLabelMap(HashMap<String, String> labelMap) {
        this.labelMap.clear();
        this.labelMap.putAll(labelMap);

        // Notify the adapter that the data has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Update the layout file name to match your layout
        ItemClassificationResultBinding binding = ItemClassificationResultBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ClassificationResult", "Position: " + position);
        Log.d("ClassificationResult", "Category: " + categories.get(position));
        Log.d("ClassificationResult", "LabelMap: " + labelMap);
        holder.bind(categories.get(position), labelMap);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /** Data structure for items in list */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLabel;
        private final TextView tvScore;

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("item");
        public ViewHolder(@NonNull ItemClassificationResultBinding binding) {
            super(binding.getRoot());
            tvLabel = binding.tvLabel;
            tvScore = binding.tvScore;
        }

        public void bind(Category category, HashMap<String, String> labelMap) {
            if (category != null) {
                String categoryLabel = category.getLabel();
                Log.d("ClassificationResult", "Category Label: " + categoryLabel);
                String itemText = labelMap.get(categoryLabel);
                Log.d("ClassificationResult", "Category Text: " + itemText);


                Query query = itemsRef.orderByChild("item_name").equalTo(itemText);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();
                            String item_name = childSnapshot.child("item_name").getValue(String.class);
                            String item_price = childSnapshot.child("item_price").getValue(String.class);
                            if (item_name != null && item_name.equals(itemText)) {
                                tvLabel.setText(item_name + " (" + item_price + ")");
                                tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                            }
                        } else {
                            // If the item is not found, display the label from the labelMap as "Unknown" or any other desired text
                            tvLabel.setText(itemText);
                            tvScore.setText(String.format(Locale.US, "%.2f%%", category.getScore() * 100));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });
            } else {
                tvLabel.setText(NO_VALUE);
                tvScore.setText(NO_VALUE);
            }
        }
    }
}