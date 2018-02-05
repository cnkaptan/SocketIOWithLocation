package com.cnkaptan.socketiowithlocation.di;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cnkaptan.socketiowithlocation.BuildConfig;
import com.cnkaptan.socketiowithlocation.service.HeaderInterceptor;
import com.cnkaptan.socketiowithlocation.service.LoginApi;
import com.cnkaptan.socketiowithlocation.utils.OfflineMockInterceptor;

import java.io.File;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = AppModule.class)
public class ApiModule {
    @Nullable
    private static final String BASE_URL = "http://localhost:5000/";
    private static final String CACHE_DIR = "HttpResponseCache";
    private static final long CACHE_SIZE = 10 * 1024 * 1024;    // 10 MB
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    @Provides
    @Singleton
    String provideBaseUrl() {
        return BASE_URL;
    }

    @Provides
    @Singleton
    OkHttpClient provideLogOkHttpClient(@NonNull Context context) {
        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }
        okHttpClientBuilder.addInterceptor(new HeaderInterceptor());
        okHttpClientBuilder.addInterceptor(new OfflineMockInterceptor(context));
        final File baseDir = context.getCacheDir();
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, CACHE_DIR);
            okHttpClientBuilder.cache(new Cache(cacheDir, CACHE_SIZE));
        }
        return okHttpClientBuilder.build();
    }

    @Provides
    @Singleton
    @NonNull
    Retrofit provideRetrofit(@NonNull String baseUrl,@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    LoginApi provideApi(Retrofit retrofit){
        return retrofit.create(LoginApi.class);
    }
}

