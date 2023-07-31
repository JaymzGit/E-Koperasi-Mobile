package com.gnj.e_koperasi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<MainModal> itemlist;
    ArrayList<MainModal> originalItemList; // Reference to the original list
    String id;

    public MainAdapter(Context context, ArrayList<MainModal> itemlist, String id) {
        this.context = context;
        this.itemlist = itemlist;
        this.originalItemList = new ArrayList<>(itemlist); // Make a copy of the original list
        this.id = id;
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false);
        return new MyViewHolder(v, itemlist, id);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MainModal mainModal = itemlist.get(position);
        Glide.with(holder.item_image.getContext())
                .load(mainModal.getItem_image())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.item_image);
        holder.item_name.setText(mainModal.getItem_name());
        holder.item_price.setText(String.valueOf(mainModal.getItem_price()) + "0");
        holder.item_quantity.setText("Qty: " + String.valueOf(mainModal.getItem_quantity()));
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_image;
        TextView item_name, item_price, item_quantity;
        ArrayList<MainModal> itemlist;
        String id;

        public MyViewHolder(@NonNull View itemView, ArrayList<MainModal> itemlist, String id) {
            super(itemView);

            this.itemlist = itemlist;
            this.id = id;

            item_image = itemView.findViewById(R.id.itemImage);
            item_name = itemView.findViewById(R.id.name);
            item_price = itemView.findViewById(R.id.price);
            item_quantity = itemView.findViewById(R.id.quantity);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MainModal mainModal = itemlist.get(position);
                String itemName = mainModal.getItem_name();
                double itemPrice = mainModal.getItem_price();
                String itemImage = mainModal.getItem_image();
                String itemQuantity = String.valueOf(mainModal.getItem_quantity());

                Intent i = new Intent(v.getContext(), ViewItem.class);
                Bundle bundle = new Bundle();
                bundle.putString("itemName", itemName);
                bundle.putDouble("itemPrice", itemPrice);
                bundle.putString("itemImage", itemImage);
                bundle.putString("itemQuantity", itemQuantity);
                bundle.putString("id", id); // Use the id from the class member variable
                i.putExtras(bundle);
                v.getContext().startActivity(i);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MainModal> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalItemList); // Return the original list if the search query is empty
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MainModal item : originalItemList) {
                    if (item.getItem_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemlist.clear();
            itemlist.addAll((ArrayList<MainModal>) results.values);
            notifyDataSetChanged();
        }
    };
}