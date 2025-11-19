package com.example.redi.data.repository;

import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.firebase.FirebaseOrderDataSource;
import java.util.List;

public class OrderRepository {
    private final FirebaseOrderDataSource dataSource;

    public OrderRepository() {
        this.dataSource = new FirebaseOrderDataSource();
    }

    public void createOrder(Order order, DataSourceCallback<Void> callback) {
        dataSource.createOrder(order, callback);
    }

    public void updateOrder(Order order) {
        dataSource.updateOrder(order);
    }

    public void listenOrdersByUser(String userId, DataSourceCallback<List<Order>> callback) {
        dataSource.listenOrdersByUser(userId, callback);
    }

    public void removeListeners() {
        dataSource.removeListeners();
    }
    public void listenAllOrders(DataSourceCallback<List<Order>> callback) {
        dataSource.listenAllOrders(callback);
    }

    public void listenOrdersByStatus(String status, DataSourceCallback<List<Order>> callback) {
        dataSource.listenOrdersByStatus(status, callback);
    }

    public void getOrderById(String orderId, DataSourceCallback<Order> callback) {
        dataSource.getOrderById(orderId, callback);
    }

    public void updateOrderStatus(String orderId, String status) {
        dataSource.updateOrderStatus(orderId, status);
    }

    public void updatePaymentStatus(String orderId, String paymentStatus) {
        dataSource.updatePaymentStatus(orderId, paymentStatus);
    }

    public void cancelOrder(String orderId, String reason) {
        dataSource.cancelOrder(orderId, reason);
    }

    public void updateOrderStatusAndDateReceive(String orderId, String status, String dateReceive) {
        dataSource.updateOrderStatusAndDateReceive(orderId, status, dateReceive);
    }
    public void cancelOrderWithTimestamp(String orderId, String reason) {
        dataSource.cancelOrderWithTimestamp(orderId, reason);
    }


}
