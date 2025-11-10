package com.example.redi.data.api;

import com.example.redi.common.models.VietQRRequest;
import com.example.redi.common.models.VietQRResponse;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class VietQRRepository {

    private static final String BASE_URL = "https://api.vietqr.io/";
    private final VietQRApiService api;

    public VietQRRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(VietQRApiService.class);
    }

    public void generateQR(VietQRRequest request, Callback<VietQRResponse> callback) {
        api.generateQR(request).enqueue(callback);
    }
}
