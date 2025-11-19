package com.example.redi.data.firebase;

import androidx.annotation.NonNull;
import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;
import java.util.*;

public class FirebaseOrderDataSource {

    private final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");
    private ValueEventListener userOrdersListener;
    private ValueEventListener allOrdersListener;
    private ValueEventListener statusOrdersListener;
    private ValueEventListener singleOrderListener;

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

    public void listenOrdersByStatus(String status, DataSourceCallback<List<Order>> callback) {
        if (statusOrdersListener != null) orderRef.removeEventListener(statusOrdersListener);
        Query q = orderRef.orderByChild("status").equalTo(status);
        statusOrdersListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    if (order != null) list.add(order);
                }
                callback.onSuccess(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };
        q.addValueEventListener(statusOrdersListener);
    }




    public void cancelOrder(String orderId, String reason) {
        if (orderId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", com.example.redi.common.utils.Constants.STATUS_CANCELLED);
        updates.put("cancelReason", reason);
        orderRef.child(orderId).updateChildren(updates);
    }


    // listen all orders
    public void listenAllOrders(DataSourceCallback<List<Order>> callback) {
        if (allOrdersListener != null) orderRef.removeEventListener(allOrdersListener);
        allOrdersListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order o = ds.getValue(Order.class);
                    if (o != null) list.add(o);
                }
                callback.onSuccess(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };
        orderRef.addValueEventListener(allOrdersListener);
    }

    // get single order
    public void getOrderById(String orderId, DataSourceCallback<Order> callback) {
        if (orderId == null) { callback.onError("orderId null"); return; }
        orderRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order o = snapshot.getValue(Order.class);
                callback.onSuccess(o);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onError(error.getMessage()); }
        });
    }

    // update status only
    public void updateOrderStatus(String orderId, String status) {
        if (orderId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        orderRef.child(orderId).updateChildren(updates);
    }

    // update status and dateReceive
    public void updateOrderStatusAndDateReceive(String orderId, String status, String dateReceive) {
        if (orderId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("dateReceive", dateReceive);
        orderRef.child(orderId).updateChildren(updates);
    }

    // update payment.status
    public void updatePaymentStatus(String orderId, String paymentStatus) {
        if (orderId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("payment/status", paymentStatus);
        orderRef.child(orderId).updateChildren(updates);
    }

    // cancel order with reason and cancelAt timestamp
    public void cancelOrderWithTimestamp(String orderId, String reason) {
        if (orderId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", com.example.redi.common.utils.Constants.STATUS_CANCELLED);
        updates.put("cancelReason", reason);
        updates.put("cancelAt", System.currentTimeMillis());
        orderRef.child(orderId).updateChildren(updates);
    }

    public void removeListeners() {
        if (userOrdersListener != null) orderRef.removeEventListener(userOrdersListener);
        if (allOrdersListener != null) orderRef.removeEventListener(allOrdersListener);
        if (statusOrdersListener != null) orderRef.removeEventListener(statusOrdersListener);
    }


}
