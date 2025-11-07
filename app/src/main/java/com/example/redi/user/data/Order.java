package com.example.redi.user.data;

public class Order {

    private String orderId;      // ID đơn hàng
    private String userId;       // ID người dùng (để biết ai đặt)
    private String orderDate;    // Ngày đặt
    private String totalPrice;   // Tổng tiền
    private String status;       // Trạng thái (Đã thanh toán, Đang giao, v.v.)
    private String paymentMethod; // Phương thức thanh toán
    private String deliveryAddress; // Địa chỉ giao hàng

    // ⚙️ Bắt buộc: constructor rỗng cho Firebase
    public Order() {
    }

    // Constructor tiện cho việc tạo đơn hàng mới
    public Order(String orderId, String userId, String orderDate, String totalPrice,
                 String status, String paymentMethod, String deliveryAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
    }

    // 🧩 Getter & Setter (Firebase cần có để đọc/ghi dữ liệu)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
