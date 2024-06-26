package com.gnj.e_koperasi;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice;
        private TextView tvDate;
        private TextView tvPurchaseTime;
        private TextView tvPurchase;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.price);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPurchase = itemView.findViewById(R.id.name);
            tvPurchaseTime = itemView.findViewById(R.id.tvPurchaseTime);
        }

        public void bind(History history) {
            String purchaseText = history.getPurchase();
            if (purchaseText.length() > 30) {
                purchaseText = purchaseText.substring(0, 31) + "...";
            }
            tvPurchase.setText(purchaseText);
            tvPrice.setText("-RM " + history.getPrice() + "0");
            tvDate.setText(history.getDate());
            tvPurchaseTime.setText(history.getTime());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click here
                    // Open ViewHistoryItem activity and pass the selected history item's details
                    Intent intent = new Intent(v.getContext(), ViewHistoryItem.class);
                    intent.putExtra("price", history.getPrice());
                    intent.putExtra("date", history.getDate());
                    intent.putExtra("time", history.getTime());
                    intent.putExtra("purchase", history.getPurchase());

                    // Pass the order's snapshot data as an extra
                    intent.putExtra("orderSnapshotKey", history.getOrderSnapshotKey());

                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
