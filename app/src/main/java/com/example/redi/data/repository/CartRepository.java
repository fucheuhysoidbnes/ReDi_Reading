package com.example.redi.data.repository;

import com.example.redi.common.models.Cart;
import com.example.redi.common.models.CartItem;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.firebase.FirebaseCartDataSource;

public class CartRepository {
    private final FirebaseCartDataSource dataSource;

    public CartRepository() {
        this.dataSource = new FirebaseCartDataSource();
    }

    public void findCartByUser(String userId, DataSourceCallback<Cart> callback) {
        dataSource.findCartByUser(userId, callback);
    }

    public void createCart(String userId, DataSourceCallback<Cart> callback) {
        dataSource.createCart(userId, callback);
    }

    public void addOrUpdateItem(String cartId, CartItem item, DataSourceCallback<Void> callback) {
        dataSource.addOrUpdateItem(cartId, item, callback);
    }
}
