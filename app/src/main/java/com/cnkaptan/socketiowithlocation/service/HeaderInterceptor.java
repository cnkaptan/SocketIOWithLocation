package com.cnkaptan.socketiowithlocation.service;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request headerResuest = request.newBuilder()
                .addHeader("App-Version","0.0.1")
                .addHeader("App-Type","driver")
                .addHeader("Os-Name","android").build();
        return chain.proceed(headerResuest);
    }
}
