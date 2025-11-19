package com.example.redi.common.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String address;
    private String phone;
    private String status;
    private String dateOrder;
    private String dateReceive;
    private Payment payment;
    // add field
    private long cancelAt; // timestamp

    private Map<String, CartItem> booklist = new HashMap<>();

    private String cancelReason;  //  Lý do hủy đơn
    private String review;        // Đánh giá của user

    public Order() {}

    // ===== GETTERS & SETTERS =====
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateOrder() { return dateOrder; }
    public void setDateOrder(String dateOrder) { this.dateOrder = dateOrder; }

    public String getDateReceive() { return dateReceive; }
    public void setDateReceive(String dateReceive) { this.dateReceive = dateReceive; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public long getCancelAt() { return cancelAt; }
    public void setCancelAt(long cancelAt) { this.cancelAt = cancelAt; }
    public Map<String, CartItem> getBooklist() { return booklist; }
    public void setBooklist(Map<String, CartItem> booklist) { this.booklist = booklist; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
