package com.cnkaptan.socketiowithlocation.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public final class PermissionUtils {

    public static final String TAG = PermissionUtils.class.getSimpleName();
    private Activity current_activity;
    private PermissionResultCallback permissionResultCallback;
    private List<String> mPermissionList = new ArrayList<>();
    private List<String> mListPermissionsNeeded = new ArrayList<>();
    private String mDialogContent = "";
    private int reqCode;

    public PermissionUtils(Activity context,PermissionResultCallback permissionResultCallback) {
        this.current_activity = context;
        this.permissionResultCallback = permissionResultCallback;
    }

    public void check_permission(List<String> permissionList, String dialogContent, int reqCode) {
        this.mPermissionList = permissionList;
        this.mDialogContent = dialogContent;
        this.reqCode = reqCode;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissionList, reqCode)) {
                permissionResultCallback.PermissionGranted(reqCode);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        } else {
            permissionResultCallback.PermissionGranted(reqCode);
            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }

    }

    private boolean checkAndRequestPermissions(List<String> permissions, int request_code) {

        if (permissions.size() > 0) {
            mListPermissionsNeeded = new ArrayList<>();
            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity, permissions.get(i));
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    mListPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!mListPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(current_activity, mListPermissionsNeeded.toArray(new String[mListPermissionsNeeded.size()]), request_code);
                return false;
            }
        }

        return true;
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions = new ArrayList<>();

                    for (int i = 0; i < mListPermissionsNeeded.size(); i++) {
                        if (perms.get(mListPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(current_activity, mListPermissionsNeeded.get(i)))
                                pending_permissions.add(mListPermissionsNeeded.get(i));
                            else {
                                Log.i("Go to settings", "and enable permissions");
                                permissionResultCallback.NeverAskAgain(reqCode);
                                Toast.makeText(current_activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if (pending_permissions.size() > 0) {
                        showMessageOKCancel(mDialogContent,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                check_permission(mPermissionList, mDialogContent, reqCode);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson", "not fully given");
                                                if (mPermissionList.size() == pending_permissions.size())
                                                    permissionResultCallback.PermissionDenied(reqCode);
                                                else
                                                    permissionResultCallback.PartialPermissionGranted(reqCode, pending_permissions);
                                                break;
                                        }


                                    }
                                });

                    } else {
                        Log.i("all", "permissions granted");
                        Log.i("proceed", "to next step");
                        permissionResultCallback.PermissionGranted(reqCode);

                    }


                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(current_activity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    interface PermissionResultCallback {
        void PermissionGranted(int request_code);
        void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
        void PermissionDenied(int request_code);
        void NeverAskAgain(int request_code);
    }
}