package com.example.redi.common.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String book_id;
    private String title;
    private String imageUrl;
    private int price;
    private int qty;

    public CartItem() {}

    public CartItem(String book_id, String title, String imageUrl, int price, int qty) {
        this.book_id = book_id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.qty = qty;
    }

    // ====== GETTERS & SETTERS ====== //
    public String getBook_id() { return book_id; }
    public void setBook_id(String book_id) { this.book_id = book_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
