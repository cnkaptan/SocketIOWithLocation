package com.cnkaptan.socketiowithlocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cnkaptan.socketiowithlocation.model.LocationData;
import com.cnkaptan.socketiowithlocation.model.LoginResponse;
import com.cnkaptan.socketiowithlocation.model.LoginResponseData;
import com.cnkaptan.socketiowithlocation.utils.LocationHelper;
import com.cnkaptan.socketiowithlocation.utils.LocationHelperView;
import com.cnkaptan.socketiowithlocation.utils.SocketHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MapsActivity extends FragmentActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback, SocketHelper.SocketStatusListener, LocationHelperView{

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationHelper mLocationHelper;
    public static final String LOGIN_RESPONSE = "login_response";
    private LoginResponse loginResponse;
    private Handler mHandler;
    private SocketHelper mSocketHelper;
    private TextView tvSocketStatus;
    public static Intent newIntent(Context context, LoginResponse loginResponse) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(LOGIN_RESPONSE, loginResponse);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvSocketStatus = (TextView)findViewById(R.id.tv_socket_status);
        loginResponse = (LoginResponse) getIntent().getParcelableExtra(LOGIN_RESPONSE);
        Log.e(TAG, "onCreate: " + loginResponse.toString());
        mLocationHelper = new LocationHelper(this);

        mLocationHelper.checkpermission();
        if (mLocationHelper.checkPlayServices()) {
            mLocationHelper.buildGoogleApiClient();
        }

        LoginResponseData data = loginResponse.getData();
        Socket mSocket = ((SocketApplication) getApplication()).createSocket(data.getCar_id(), data.getDriver_id(), data.getUid());
        mSocketHelper = new SocketHelper(mSocket,this);
        mSocketHelper.connect();
        mHandler = new Handler();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationHelper.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationHelper.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketHelper.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLocationHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @NonNull
    private Runnable sendLocation() {
        return new Runnable() {
            @Override
            public void run() {
                sendMessage();
            }
        };
    }

    public void sendMessage() {
        Location location = mLocationHelper.getLocation();
        if (location != null) {
           mSocketHelper.sendLocation(location);
           Log.i(TAG,"Send Message"+location.toString());
        }
        mHandler.postDelayed(sendLocation(), 3000);
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        mMap.clear();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void updateStatus(final SocketHelper.SocketStatus socketStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (socketStatus) {
                    case CONNECTED:
                        Log.e(TAG,"onConnected");
                        tvSocketStatus.setText(getText(R.string.connected));
                        sendMessage();
                        break;
                    case DISCONNECT:
                        Log.e(TAG, "diconnected");
                        tvSocketStatus.setText(getText(R.string.disconnected));
                        mHandler.removeCallbacks(sendLocation());
                        break;
                    case ERROR:
                        Log.e(TAG, "Error connecting");
                        tvSocketStatus.setText(getText(R.string.error));
                        mHandler.removeCallbacks(sendLocation());
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
