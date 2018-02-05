package com.cnkaptan.socketiowithlocation.di;

import com.cnkaptan.socketiowithlocation.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class,AppModule.class})
public interface ApiComponent {
    void inject(LoginActivity loginActivity);
}
