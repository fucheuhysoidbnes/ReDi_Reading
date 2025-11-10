package com.example.redi.common.models;

import java.io.Serializable;

public class Payment implements Serializable {
    private String method;       // cod | bank_transfer
    private String status;       // pending | paid | failed
    private long amount;
    private String transactionId;
    private String bankName;
    private String qrImageUrl;
    private long expiredAt;
    private String paymentUrl;

    public Payment() {}

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getQrImageUrl() { return qrImageUrl; }
    public void setQrImageUrl(String qrImageUrl) { this.qrImageUrl = qrImageUrl; }

    public long getExpiredAt() { return expiredAt; }
    public void setExpiredAt(long expiredAt) { this.expiredAt = expiredAt; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
}
