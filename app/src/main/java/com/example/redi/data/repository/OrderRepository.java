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
}
