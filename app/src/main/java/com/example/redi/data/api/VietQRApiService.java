package com.example.redi.data.api;

import com.example.redi.common.models.VietQRRequest;
import com.example.redi.common.models.VietQRResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VietQRApiService {

    @Headers({
            "x-client-id: YOUR_CLIENT_ID",
            "x-api-key: YOUR_API_KEY",
            "Content-Type: application/json"
    })
    @POST("v2/generate")
    Call<VietQRResponse> generateQR(@Body VietQRRequest request);
}
