package com.cnkaptan.socketiowithlocation.service;

import com.cnkaptan.socketiowithlocation.model.LoginRequest;
import com.cnkaptan.socketiowithlocation.model.LoginResponse;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("deneme")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);
}
