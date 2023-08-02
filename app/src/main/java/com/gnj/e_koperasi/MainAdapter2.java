package com.gnj.e_koperasi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainAdapter2 extends RecyclerView.Adapter<MainAdapter2.MyViewHolder> {
    Context context;
    ArrayList<MainModal2> cartlist;
    double totalCartPrice = 0.0;
    private TotalCartPriceListener totalCartPriceListener;
    private String orderSnapshotKey;
    private Cart cartActivity;

    public MainAdapter2(Context context, ArrayList<MainModal2> cartlist, String orderSnapshotKey) {
        this.context = context;
        this.cartlist = cartlist;
        this.orderSnapshotKey = orderSnapshotKey; // Set the orderSnapshotKey
        this.cartActivity = cartActivity; // Store the reference of the Cart activity
        calculateTotalCartPrice();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
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
        holder.item_quantity.setText(String.valueOf(mainModal.getQuantity()));
        DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Format for two decimal places

        // Calculate the total item price based on the item price and quantity
        double totalItemPrice = mainModal.getItem_price() * mainModal.getQuantity();
        holder.item_totalprice.setText(decimalFormat.format(totalItemPrice));
        holder.delete.setTag(position);

        // Fetch the item_quantity for each item from Firebase Realtime Database
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("item");
        Query query = itemRef.orderByChild("item_name").equalTo(mainModal.getItem_name());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // We expect only one item to match the item_name, so use a loop to get the item_quantity
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        int maxQuantity = itemSnapshot.child("item_quantity").getValue(Integer.class);
                        mainModal.setItem_quantity(maxQuantity);

                        // Disable the "Add" button if the item's quantity reaches the maximum
                        holder.btnAdd.setEnabled(mainModal.getQuantity() < mainModal.getItem_quantity());

                        // Also set the max quantity for the item
                        mainModal.setMaxQuantity(maxQuantity);

                        // Notify the adapter that the data has changed for this item
                        // This will update the button state accordingly
                        notifyItemChanged(position);
                        break; // Exit the loop after getting the first matching item
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the failure, e.g., show an error message
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name, item_price, item_quantity, item_totalprice;
        ImageButton delete;
        Button btnAdd, btnMinus;
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
            btnAdd = itemView.findViewById(R.id.btnAdd2);
            btnMinus = itemView.findViewById(R.id.btnMinus2);

            // Plus Button Click Listener
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MainModal2 item = cartlist.get(position);
                        int currentQuantity = item.getQuantity();
                        item.setQuantity(currentQuantity + 1);
                        notifyItemChanged(position);
                        calculateTotalCartPrice(); // Recalculate the total price after updating the quantity

                        // Update the corresponding order in the orderList in MyApplication
                        ArrayList<Bundle> orderList = MyApplication.getOrderList();
                        if (orderList != null && position < orderList.size()) {
                            Bundle order = orderList.get(position);
                            int updatedQuantity = currentQuantity + 1;
                            order.putInt("item_quantity", updatedQuantity); // Use the same key "item_quantity"
                        }

                        // Update the cartList on Firebase after changing the quantity
                        if (orderSnapshotKey != null) {
                            updateCartListOnFirebase();
                        }
                    }
                }
            });

            // Delete Button Click Listener
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cartlist.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartlist.size());
                        calculateTotalCartPrice(); // Recalculate the total price after removing an item

                        // Update the corresponding order in the orderList in MyApplication
                        ArrayList<Bundle> orderList = MyApplication.getOrderList();
                        if (orderList != null && position < orderList.size()) {
                            orderList.remove(position);
                        }

                        // Update the cartList on Firebase after removing the item
                        if (orderSnapshotKey != null) {
                            updateCartListOnFirebase();
                        }
                    }
                }
            });

            // Minus Button Click Listener
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MainModal2 item = cartlist.get(position);
                        int currentQuantity = item.getQuantity();
                        if (currentQuantity > 1) {
                            item.setQuantity(currentQuantity - 1);
                            notifyItemChanged(position);
                            calculateTotalCartPrice(); // Recalculate the total price after updating the quantity

                            // Update the corresponding order in the orderList in MyApplication
                            ArrayList<Bundle> orderList = MyApplication.getOrderList();
                            if (orderList != null && position < orderList.size()) {
                                Bundle order = orderList.get(position);
                                int updatedQuantity = currentQuantity - 1;
                                order.putInt("item_quantity", updatedQuantity); // Use the same key "item_quantity"
                            }

                            // Update the cartList on Firebase after changing the quantity
                            if (orderSnapshotKey != null) {
                                updateCartListOnFirebase();
                            }
                        } else {
                            // If the item quantity is 1, remove the entire item from the list
                            cartlist.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartlist.size());
                            calculateTotalCartPrice(); // Recalculate the total price after removing an item

                            // Remove the corresponding order from the orderList in MyApplication
                            ArrayList<Bundle> orderList = MyApplication.getOrderList();
                            if (orderList != null && position < orderList.size()) {
                                orderList.remove(position);
                            }

                            // Update the cartList on Firebase after removing the item
                            if (orderSnapshotKey != null) {
                                updateCartListOnFirebase();
                            }
                        }
                    }
                }
            });

            // Plus Button Click Listener
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MainModal2 item = cartlist.get(position);
                        int currentQuantity = item.getQuantity();
                        item.setQuantity(currentQuantity + 1);
                        notifyItemChanged(position);
                        calculateTotalCartPrice(); // Recalculate the total price after updating the quantity

                        // Update the corresponding order in the orderList in MyApplication
                        ArrayList<Bundle> orderList = MyApplication.getOrderList();
                        if (orderList != null && position < orderList.size()) {
                            Bundle order = orderList.get(position);
                            int updatedQuantity = currentQuantity + 1;
                            order.putInt("itemQuantities", updatedQuantity);
                        }

                        // Update the cartList on Firebase after changing the quantity
                        if (orderSnapshotKey != null) {
                            updateCartListOnFirebase();
                        }
                    }
                }
            });
        }

        private void updateCartListOnFirebase() {
            DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference("orders")
                    .child(orderSnapshotKey)
                    .child("cartList");
            if (cartlist.isEmpty()) {
                // If the cart is empty, set the value to null to remove the "cartList" node from Firebase
                cartListRef.setValue(null);
            } else {
                // If the cart is not empty, update the cartList on Firebase
                cartListRef.setValue(cartlist);
            }
        }
    }

    void calculateTotalCartPrice() {
        totalCartPrice = 0.0;
        for (MainModal2 item : cartlist) {
            double totalItemPrice = item.getItem_price() * item.getQuantity();
            totalCartPrice += totalItemPrice;
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