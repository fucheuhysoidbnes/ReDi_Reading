package com.example.redi.common.models;

public class VietQRRequest {
    private String accountNo;
    private String accountName;
    private String acqId;
    private int amount;
    private String addInfo;

    public VietQRRequest(String accountNo, String accountName, String acqId, int amount, String addInfo) {
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.acqId = acqId;
        this.amount = amount;
        this.addInfo = addInfo;
    }
}
