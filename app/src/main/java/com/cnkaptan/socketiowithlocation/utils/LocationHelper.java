package com.cnkaptan.socketiowithlocation.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.cnkaptan.socketiowithlocation.MapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationHelper implements PermissionUtils.PermissionResultCallback , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final String TAG = LocationHelper.class.getSimpleName();
    private Activity mActivity;
    private boolean isPermissionGranted;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private List<String> permissions= Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);
    private PermissionUtils permissionUtils;
    private LocationHelperView locationHelperView;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private LocationRequest mLocationRequest;

    public LocationHelper(Activity activity){
        this.mActivity = activity;
        this.locationHelperView = (LocationHelperView)activity;
        permissionUtils = new PermissionUtils(activity,this);
    }

    public void checkpermission() {
        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);
    }

    private boolean isPermissionGranted() {
        return isPermissionGranted;
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(mActivity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(mActivity,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                showToast("This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @Nullable
    public Location getLocation() {
        return mLastLocation;
    }

    /**
     * Method used to build GoogleApiClient
     */

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if (mGoogleApiClient !=null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    private GoogleApiClient getGoogleApiCLient() {
        return mGoogleApiClient;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        mLastLocation=getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public void PermissionGranted(int request_code) {
        Log.i(PermissionUtils.TAG,"Permission granted");
        isPermissionGranted=true;
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> grantedPermissions) {
        Log.i(PermissionUtils.TAG,"Partial permission granted");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i(PermissionUtils.TAG,"Permission denied");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i(PermissionUtils.TAG,"Never ask again");
    }

    private void showToast(String message) {
        Toast.makeText(mActivity.getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isPermissionGranted()) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        if (mLastLocation != null) {
            Log.e(TAG, "onConnected: "+mLastLocation.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, " ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            mLastLocation = location;
            locationHelperView.handleNewLocation(location);
        }
        Log.i(TAG, "onLocationChanged: "+location.toString() );
    }
}
