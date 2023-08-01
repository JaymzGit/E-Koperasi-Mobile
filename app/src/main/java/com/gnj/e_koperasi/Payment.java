package com.gnj.e_koperasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Payment extends AppCompatActivity {
    String id, latestOrderId;
    WebView webView;
    boolean isPaymentDone = false;
    boolean isBackPressed = false;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
            latestOrderId = bundle.getString("latestOrderId");
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("orders");

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Do nothing here
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains("check.php") && !url.contains("payment.php") && url.contains("bill")) {
                    // Load the URL and extract the payment status from the HTML content
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                if (url.contains("bill")) {
                    // Extract the payment status from the WebView content
                    view.evaluateJavascript("(function() { " +
                            "   var h5Elements = document.getElementsByTagName('h5'); " +
                            "   for (var i = 0; i < h5Elements.length; i++) { " +
                            "       if (h5Elements[i].innerText.includes('Status :')) { " +
                            "           var nextElement = h5Elements[i].nextElementSibling; " +
                            "           if (nextElement.tagName === 'STRONG') { " +
                            "               return nextElement.innerText.trim(); " +
                            "           } " +
                            "       } " +
                            "   } " +
                            "   return 'Payment Unsuccessful'; " +
                            "})()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String paymentStatus = value.replaceAll("\"", "").trim();
                            Log.d("PaymentStatus", paymentStatus);
                                if (paymentStatus.equalsIgnoreCase("Payment Approved")) {
                                //if(paymentStatus.equalsIgnoreCase("Payment Unsuccessful")) { //used for testing
                                // Handle the case when the payment is successful
                                isPaymentDone = true;
                                databaseRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Your existing code to update the order status to "Completed" here
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle the error if needed
                                    }
                                });

                                updateOrderStatusToCompleted(latestOrderId);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isBackPressed) {
                                            // Perform redirection only if the back button was not pressed by the user
                                            Intent mainIntent = new Intent(Payment.this, ViewHistoryItem.class);
                                            Bundle info = new Bundle();
                                            info.putString("orderSnapshotKey", latestOrderId);
                                            mainIntent.putExtras(info);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    }
                                }, 10000);
                            } else {
                                if (latestOrderId != null && !latestOrderId.isEmpty()) {
                                    databaseRef.child(latestOrderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Order deleted successfully, show a toast or perform any other action
                                            } else {
                                                // Failed to delete order, handle the error if needed
                                            }
                                        }
                                    });
                                }

                                // Perform redirection after a delay, regardless of payment status
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isBackPressed) {
                                            // Perform redirection only if the back button was not pressed by the user
                                            Intent mainIntent = new Intent(Payment.this, Cart.class);
                                            Bundle info = new Bundle();
                                            info.putString("id", id);
                                            mainIntent.putExtras(info);
                                            startActivity(mainIntent);
                                            Toast.makeText(getApplicationContext(), "Payment Unsuccessful", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                }, 10000);
                            }
                        }
                    });
                }
            }
        });

        String websiteUrl = "http://ekoop.000webhostapp.com/check.php?id=" + id;
        webView.loadUrl(websiteUrl);
    }

    // Method to update the order status to "Completed" and update item quantities
    private void updateOrderStatusToCompleted(String orderId) {
        String status = "Completed";
        databaseRef.child(orderId).child("status").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Order status updated successfully, now update item quantities
                            databaseRef.child(orderId).child("cartList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                                        String item_name = cartItemSnapshot.child("item_name").getValue(String.class);
                                        int item_quantity = cartItemSnapshot.child("item_quantity").getValue(Integer.class);
                                        updateItemQuantityInCollection(item_name, item_quantity);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle the error if needed
                                }
                            });
                        } else {
                            // Failed to update order status, handle the error if needed
                        }
                    }
                });
    }

    // Method to update item quantity in the item collection
    private void updateItemQuantityInCollection(String itemName, int itemQuantity) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("item");
        itemRef.orderByChild("item_name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    // Assuming the item_quantity is stored as an integer in the item collection
                    int currentQuantity = itemSnapshot.child("item_quantity").getValue(Integer.class);
                    int updatedQuantity = currentQuantity - itemQuantity;
                    itemSnapshot.getRef().child("item_quantity").setValue(updatedQuantity)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Item quantity updated successfully, show a toast or perform any other action
                                    } else {
                                        // Failed to update item quantity, handle the error if needed
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isPaymentDone) {
            // If payment is done, handle the back button as needed, e.g., go back to the previous activity
            Intent intent = new Intent(Payment.this, ViewHistoryItem.class);
            Bundle info = new Bundle();
            info.putString("orderSnapshotKey", latestOrderId);
            intent.putExtras(info);
            startActivity(intent);
            super.onBackPressed();
        } else {
            if (latestOrderId != null && !latestOrderId.isEmpty()) {
                databaseRef.child(latestOrderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //If successful
                        } else {
                            // Failed to delete order, handle the error if needed
                        }
                    }
                });
            }

            isBackPressed = true;

            Intent mainIntent = new Intent(Payment.this, Cart.class);
            Bundle info = new Bundle();
            info.putString("id", id);
            mainIntent.putExtras(info);
            startActivity(mainIntent);
            Toast.makeText(getApplicationContext(), "Payment Unsuccessful", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}