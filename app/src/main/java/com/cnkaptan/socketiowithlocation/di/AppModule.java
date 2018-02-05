package com.cnkaptan.socketiowithlocation.di;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @NonNull
    private final Context mContext;

    public AppModule(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    Context provideContext(){
        return this.mContext;
    }
}
