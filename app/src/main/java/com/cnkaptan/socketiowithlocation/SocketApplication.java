package com.cnkaptan.socketiowithlocation;

import android.app.Application;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import com.cnkaptan.socketiowithlocation.di.ApiComponent;
import com.cnkaptan.socketiowithlocation.di.ApiModule;
import com.cnkaptan.socketiowithlocation.di.AppModule;
import com.cnkaptan.socketiowithlocation.di.DaggerApiComponent;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;

public class SocketApplication extends Application {
    private ApiComponent apiComponent;
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        apiComponent = DaggerApiComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .build();
    }

    @Nullable
    public Socket createSocket(@NonNull int carId,@NonNull int driverId,@NonNull String uuid) {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{Polling.NAME};
            Uri urlUri = new Uri.Builder()
                    .scheme("http")
                    .encodedAuthority("10.0.1.216:5000")
                    .appendQueryParameter("car_id", String.valueOf(carId))
                    .appendQueryParameter("driver_id", String.valueOf(driverId))
                    .appendQueryParameter("uid", uuid).build();
            Log.e(SocketApplication.class.getSimpleName(), urlUri.toString());
            mSocket = IO.socket("http://10.0.1.216:5000");
            return mSocket;
        } catch (URISyntaxException e) {
            Log.e("App", e.getMessage());
            return null;
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public ApiComponent getApiComponent() {
        return apiComponent;
    }
}
