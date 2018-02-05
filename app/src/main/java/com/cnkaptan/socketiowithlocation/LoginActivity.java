package com.cnkaptan.socketiowithlocation;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cnkaptan.socketiowithlocation.model.LoginRequest;
import com.cnkaptan.socketiowithlocation.model.LoginRequestData;
import com.cnkaptan.socketiowithlocation.model.LoginResponse;
import com.cnkaptan.socketiowithlocation.model.ServerError;
import com.cnkaptan.socketiowithlocation.model.ServerErrorResponse;
import com.cnkaptan.socketiowithlocation.service.LoginApi;
import com.cnkaptan.socketiowithlocation.utils.DialogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @Inject
    LoginApi loginApi;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCarId;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((SocketApplication) getApplication()).getApiComponent().inject(this);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCarId = (EditText) findViewById(R.id.et_car_number);
    }

    public void login(View view) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String carId = etCarId.getText().toString();
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Enter Username");
            return;
        } else {
            etUsername.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter passwrod");
            return;
        } else {
            etPassword.setError(null);
        }

        if (TextUtils.isEmpty(carId)) {
            etCarId.setError("Enter Car Id");
            return;
        } else {
            etCarId.setError(null);
        }
        Call<ResponseBody> response = loginApi.login(new LoginRequest("login", new LoginRequestData(username, password, carId)));
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
             try{
                 Gson gson = new GsonBuilder().create();
                 if (response.code() == 200) {
                     LoginResponse loginResponse = gson.fromJson(response.body().string(), LoginResponse.class);
                     startActivity(MapsActivity.newIntent(LoginActivity.this.getApplicationContext(), loginResponse));
                 } else {
                     String json = response.errorBody().string();
                     ServerErrorResponse serverErrorResponse = gson.fromJson(json, ServerErrorResponse.class);
                     ServerError data = serverErrorResponse.getData();
                     if (alertDialog == null) {
                         alertDialog = DialogUtils.createAlerdDialog(LoginActivity.this, data.getCode(), data.getMessage());
                     } else {
                         alertDialog.dismiss();
                         alertDialog.setTitle(data.getCode());
                         alertDialog.setMessage(data.getMessage());
                     }
                     alertDialog.show();

                 }
             }catch (IOException io){
                 alertDialog = DialogUtils.createAlerdDialog(LoginActivity.this,"Error",io.getMessage());
             }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("login", t.getMessage(), t);
            }
        });
    }
}
