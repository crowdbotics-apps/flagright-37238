package com.flagright.sdk.interfaces;

import com.flagright.sdk.models.RequestModal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers({"Content-Type: application/json"})
    @POST("device/metric")
    Call<Void> sendData(@Header("x-api-key") String content_type, @Body RequestModal dataModal);
}
