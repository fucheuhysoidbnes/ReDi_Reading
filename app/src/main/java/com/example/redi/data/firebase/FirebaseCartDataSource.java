package com.example.redi.data.firebase;

import androidx.annotation.NonNull;

import com.example.redi.common.models.Cart;
import com.example.redi.common.models.CartItem;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;

public class FirebaseCartDataSource {

    private final DatabaseReference cartsRef;

    public FirebaseCartDataSource() {
        cartsRef = FirebaseDatabase.getInstance().getReference("carts");
    }

    public void findCartByUser(String userId, DataSourceCallback<Cart> callback) {
        cartsRef.orderByChild("userId").equalTo(userId).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Cart c = child.getValue(Cart.class);
                                if (c != null) c.setCartId(child.getKey());
                                callback.onSuccess(c);
                                return;
                            }
                        }
                        callback.onSuccess(null);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void createCart(String userId, DataSourceCallback<Cart> callback) {
        DatabaseReference newRef = cartsRef.push();
        Cart cart = new Cart();
        cart.setCartId(newRef.getKey());
        cart.setUserId(userId);
        cart.setCreatedAt(System.currentTimeMillis());
        newRef.setValue(cart).addOnCompleteListener(task -> {
            if (task.isSuccessful()) callback.onSuccess(cart);
            else callback.onError("Tạo giỏ hàng thất bại");
        });
    }

    public void addOrUpdateItem(String cartId, CartItem item, DataSourceCallback<Void> callback) {
        DatabaseReference itemRef = cartsRef.child(cartId).child("booklist").child(item.getBook_id());
        itemRef.runTransaction(new Transaction.Handler() {
            @Override public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                CartItem existing = currentData.getValue(CartItem.class);
                if (existing == null) currentData.setValue(item);
                else {
                    existing.setQty(existing.getQty() + item.getQty());
                    currentData.setValue(existing);
                }
                return Transaction.success(currentData);
            }

            @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error != null) callback.onError(error.getMessage());
                else callback.onSuccess(null);
            }
        });
    }
}
