package com.example.redi.common.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {
    private String cartId;
    private String userId;
    private long createdAt;
    private Map<String, CartItem> booklist = new HashMap<>();

    public Cart() {}

    // ====== GETTERS & SETTERS ====== //
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, CartItem> getBooklist() { return booklist; }
    public void setBooklist(Map<String, CartItem> booklist) { this.booklist = booklist; }
}
