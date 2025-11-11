package com.example.redi.common.models;

public class VietQRResponse {
    private String code;
    private String desc;
    private Data data;

    public String getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        private String qrCode;
        private String qrDataURL;
        public String getQrCode() { return qrCode; }
        public String getQrDataURL() { return qrDataURL; }
    }
}
