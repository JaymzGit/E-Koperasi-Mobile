package com.gnj.e_koperasi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    Context context;
    ArrayList<MainModal> itemlist;

    public MainAdapter(Context context, ArrayList<MainModal> itemlist) {
        this.context = context;
        this.itemlist = itemlist;
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false);
        return new MyViewHolder(v, itemlist);
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
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_image;
        TextView item_name, item_price;
        ArrayList<MainModal> itemlist;

        public MyViewHolder(@NonNull View itemView, ArrayList<MainModal> itemlist) {
            super(itemView);

            this.itemlist = itemlist;

            item_image = itemView.findViewById(R.id.itemImage);
            item_name = itemView.findViewById(R.id.name);
            item_price = itemView.findViewById(R.id.price);

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

                Intent i = new Intent(v.getContext(), viewItem.class);
                Bundle bundle = new Bundle();
                bundle.putString("itemName", itemName);
                bundle.putDouble("itemPrice", itemPrice);
                bundle.putString("itemImage", itemImage);
                i.putExtras(bundle);
                v.getContext().startActivity(i);
            }
        }

    }
    }

