package com.cnkaptan.socketiowithlocation.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cnkaptan.socketiowithlocation.model.LoginRequest;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class OfflineMockInterceptor implements Interceptor {
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    public static final String TAG = OfflineMockInterceptor.class.getSimpleName();
    private Context mContext;

    public OfflineMockInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.e(TAG,request.headers().toString());
        try {
            final Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            LoginRequest loginRequest = new GsonBuilder().create().fromJson(buffer.readUtf8(),LoginRequest.class);
            if (!loginRequest.getData().getUsername().equals("test")){
                return createFailureJson(chain);
            }
            if (!loginRequest.getData().getPassword().equals("test123")){
                return createFailureJson(chain);
            }
            if (!loginRequest.getData().getCar_number().equals("000end")){
                return createFailureJson(chain);
            }

        } catch (final IOException e) {
            return createFailureJson(chain);
        }

        return createSuccessResponse(chain);
    }

    @NonNull
    private String getSuccessJson() throws IOException {
        InputStream stream = mContext.getAssets().open("LoginSuccessResponse");
        return parseStream(stream);
    }

    @NonNull
    private String getFailureJson() throws IOException {
        InputStream stream = mContext.getAssets().open("LoginFailureResponse");
        return parseStream(stream);
    }

    @NonNull
    private Response createSuccessResponse(Chain chain) throws IOException {
        String json = getSuccessJson();
        Response response = new Response.Builder()
                /* Return "hello" to the api call */
                .body(ResponseBody.create(MEDIA_JSON, json))
                /* Additional methods to satisfy OkHttp */
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("Status True")
                .build();
                return response;
    }

    @NonNull
    private Response createFailureJson(Chain chain) throws IOException {
        String json = getFailureJson();
        Response response = new Response.Builder()
                /* Return "hello" to the api call */
                .body(ResponseBody.create(MEDIA_JSON, json))
                /* Additional methods to satisfy OkHttp */
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .message("Status false")
                .code(404)
                .build();
        return response;
    }

    private String parseStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            builder.append(line);
        }
        in.close();
        return builder.toString();
    }
}
