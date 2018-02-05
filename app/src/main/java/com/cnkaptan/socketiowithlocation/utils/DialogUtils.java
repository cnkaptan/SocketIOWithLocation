package com.cnkaptan.socketiowithlocation.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogUtils {
    public static AlertDialog createAlerdDialog(Context context, String header, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(header)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        return alertDialog;
    }
}
