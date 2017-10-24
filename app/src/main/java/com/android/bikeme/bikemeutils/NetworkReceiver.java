package com.android.bikeme.bikemeutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.bikeme.application.BikeMeApplication;
import com.google.firebase.auth.FirebaseAuth;

public class NetworkReceiver extends BroadcastReceiver {

    public static final String TAG = NetworkReceiver.class.getSimpleName();

    public NetworkReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG,"receive");

        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        boolean isSignIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean serviceRunning = bikeMeApplication.isMyServiceRunning(DownloadMapOfflineService.class);
        boolean mapOfflineDownloaded = bikeMeApplication.isMapOfflineDownloaded();
        boolean onlineWifi = bikeMeApplication.isOnlineWifi();

        if(onlineWifi)
        {
            Log.i(TAG,"receive wifi");
            if(isSignIn && !mapOfflineDownloaded && !serviceRunning)
            {
                Log.i(TAG,"receive wifi and sign in and not downloaded and not running-Start");
                context.startService(new Intent(context,DownloadMapOfflineService.class));
            }
        }
        else if(serviceRunning)
        {
            Log.i(TAG,"receive no wifi and already running-Stop");
            context.stopService(new Intent(context,DownloadMapOfflineService.class));
        }
    }
}