package com.example.redi.common.models;

public class Book {
    private String book_id;
    private String title;
    private String description;
    private String imageUrl;
    private int price;
    private int quantity;


    public Book() {}

    // GETTERS & SETTERS
    public String getBook_id() { return book_id; }
    public void setBook_id(String book_id) { this.book_id = book_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}