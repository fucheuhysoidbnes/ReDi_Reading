package com.example.redi.data.firebase;

import androidx.annotation.NonNull;
import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;
import java.util.*;

public class FirebaseOrderDataSource {

    private final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");
    private ValueEventListener userOrdersListener;

    public void createOrder(Order order, DataSourceCallback<Void> callback) {
        String id = orderRef.push().getKey();
        if (id == null) {
            callback.onError("Không thể tạo ID đơn hàng");
            return;
        }
        order.setOrderId(id);
        orderRef.child(id).setValue(order)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateOrder(Order order) {
        if (order == null || order.getOrderId() == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", order.getStatus());
        updates.put("cancelReason", order.getCancelReason());
        updates.put("review", order.getReview());
        updates.put("payment", order.getPayment());
        orderRef.child(order.getOrderId()).updateChildren(updates);
    }

    public void listenOrdersByUser(String userId, DataSourceCallback<List<Order>> callback) {
        if (userOrdersListener != null) orderRef.removeEventListener(userOrdersListener);

        Query query = orderRef.orderByChild("userId").equalTo(userId);
        userOrdersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    if (order != null) list.add(order);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };
        query.addValueEventListener(userOrdersListener);
    }

    public void removeListeners() {
        if (userOrdersListener != null)
            orderRef.removeEventListener(userOrdersListener);
    }
}
