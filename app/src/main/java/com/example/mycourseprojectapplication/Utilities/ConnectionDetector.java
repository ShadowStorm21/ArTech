package com.example.mycourseprojectapplication.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
        checkConnection();
    }

    private boolean isInternetConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting())
        {
            return true;
        }

        return false;
    }

    private void checkConnection() {

        if (!isInternetConnected()) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("No Connection")
                    .setMessage("Please check your network connection.")
                    .setCancelable(false)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (isInternetConnected()) {
                                dialog.dismiss();
                            } else {
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    }).show();
        }
    }
    }
