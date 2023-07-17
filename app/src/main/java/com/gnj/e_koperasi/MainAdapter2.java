package com.gnj.e_koperasi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainAdapter2 extends RecyclerView.Adapter<MainAdapter2.MyViewHolder> {
    Context context;
    ArrayList<MainModal2> cartlist;
    double totalCartPrice = 0.0;
    private TotalCartPriceListener totalCartPriceListener;

    public MainAdapter2(Context context, ArrayList<MainModal2> cartlist) {
        this.context = context;
        this.cartlist = cartlist;
        calculateTotalCartPrice();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cart, parent, false);
        return new MyViewHolder(v, cartlist);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MainModal2 mainModal = cartlist.get(position);
        Glide.with(holder.item_image.getContext())
                .load(mainModal.getItem_image())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark)
                .into(holder.item_image);
        holder.item_name.setText(mainModal.getItem_name());
        holder.item_price.setText(String.valueOf(mainModal.getItem_price()) + "0");
        holder.item_quantity.setText("x" + String.valueOf(mainModal.getQuantity()));
        DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Format for two decimal places
        holder.item_totalprice.setText(decimalFormat.format(mainModal.getTotalPrice()));
        holder.delete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return cartlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name, item_price, item_quantity, item_totalprice;
        ImageButton delete;
        ArrayList<MainModal2> cartlist;

        public MyViewHolder(@NonNull View itemView, ArrayList<MainModal2> cartlist) {
            super(itemView);
            this.cartlist = cartlist;
            item_image = itemView.findViewById(R.id.itemImage);
            item_name = itemView.findViewById(R.id.name);
            item_price = itemView.findViewById(R.id.price);
            item_quantity = itemView.findViewById(R.id.quantities);
            item_totalprice = itemView.findViewById(R.id.totalprice);
            delete = itemView.findViewById(R.id.btnDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cartlist.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartlist.size());
                        calculateTotalCartPrice(); // Recalculate the total price after removing an item

                        // Remove the corresponding order from the orderList in MyApplication
                        ArrayList<Bundle> orderList = MyApplication.getOrderList();
                        if (orderList != null && position < orderList.size()) {
                            orderList.remove(position);
                        }
                    }
                }
            });


        }
    }

    void calculateTotalCartPrice() {
        totalCartPrice = 0.0;
        for (MainModal2 item : cartlist) {
            totalCartPrice += item.getTotalPrice();
        }
        if (totalCartPriceListener != null) {
            totalCartPriceListener.onTotalCartPriceUpdated(totalCartPrice);
        }
    }


    public double getTotalCartPrice() {
        return totalCartPrice;
    }

    public interface TotalCartPriceListener {
        void onTotalCartPriceUpdated(double totalCartPrice);
    }

    public void setTotalCartPriceListener(TotalCartPriceListener listener) {
        this.totalCartPriceListener = listener;
    }
}
