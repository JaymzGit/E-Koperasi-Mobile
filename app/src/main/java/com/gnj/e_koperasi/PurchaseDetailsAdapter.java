package com.gnj.e_koperasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PurchaseDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PURCHASE_DETAIL = 1;
    private static final int VIEW_TYPE_TOTAL = 2;

    private List<PurchaseDetail> purchaseDetailsList;

    public PurchaseDetailsAdapter(List<PurchaseDetail> purchaseDetailsList) {
        this.purchaseDetailsList = purchaseDetailsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PURCHASE_DETAIL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_detail, parent, false);
            return new PurchaseDetailsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_total, parent, false);
            return new TotalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PurchaseDetailsViewHolder) {
            PurchaseDetailsViewHolder purchaseDetailsViewHolder = (PurchaseDetailsViewHolder) holder;
            PurchaseDetail purchaseDetail = purchaseDetailsList.get(position);
            purchaseDetailsViewHolder.bind(purchaseDetail);
        } else if (holder instanceof TotalViewHolder) {
            TotalViewHolder totalViewHolder = (TotalViewHolder) holder;
            double totalPrice = calculateTotalPrice();
            totalViewHolder.tvTotalPrice.setText(String.format(Locale.getDefault(), "RM %.2f", totalPrice));
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 to the item count to accommodate the "Total:" TextView
        return purchaseDetailsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type for the "Total:" TextView
        if (position == purchaseDetailsList.size()) {
            return VIEW_TYPE_TOTAL;
        } else {
            return VIEW_TYPE_PURCHASE_DETAIL;
        }
    }

    // ViewHolder for regular purchase details
    static class PurchaseDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvItemPrice;
        private TextView tvItemQuantity;

        public PurchaseDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
        }

        public void bind(PurchaseDetail purchaseDetail) {
            String itemName = purchaseDetail.getItemName();
            int itemQuantity = purchaseDetail.getItemQuantity();
            double itemPrice = purchaseDetail.getItemPrice();

            // Set the values to the TextViews
            tvItemName.setText(itemName);
            tvItemPrice.setText(String.format(Locale.getDefault(), "RM %.2f", itemPrice));
            tvItemQuantity.setText(String.valueOf(itemQuantity));
        }
    }

    // ViewHolder for "Total:" TextView
    static class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotalPrice;

        public TotalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }

    // Calculate the total price based on purchase details
    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (PurchaseDetail purchaseDetail : purchaseDetailsList) {
            totalPrice += (purchaseDetail.getItemPrice() * purchaseDetail.getItemQuantity());
        }
        return totalPrice;
    }
}
