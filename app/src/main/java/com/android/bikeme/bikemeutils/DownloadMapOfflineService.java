package com.android.bikeme.bikemeutils;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

/**
 * Created by Daniel on 23/04/2017.
 */
public class DownloadMapOfflineService extends Service {

    private static final String TAG = DownloadMapOfflineService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;

    private static String STYLE_URL;
    private static float PIXEL_RATIO;
    public static final double MIN_ZOOM = 9;
    public static final double MAX_ZOOM = 15;
    private static final String JSON_CHARSET = "UTF-8";

    private static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder  mBuilder;
    private LatLngBounds boundsRegion;
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;
    private boolean isRunning;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "Service created");

        isRunning = false;

        offlineManager = OfflineManager.getInstance(this);

        LatLng southWest = new LatLng(BaseActivity.SOUTH_WEST_BOUND[0], BaseActivity.SOUTH_WEST_BOUND[1]);
        LatLng northEast = new LatLng(BaseActivity.NORTH_EAST_BOUND[0],BaseActivity.NORTH_EAST_BOUND[1]);

        boundsRegion = new LatLngBounds.Builder()
                .include(southWest)
                .include(northEast)
                .build();

        STYLE_URL = getString(R.string.mapbox_style_mapbox_streets);
        PIXEL_RATIO = getResources().getDisplayMetrics().density;
    }

    public int onStartCommand (Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if(isRunning)
        {
            Log.i(TAG, "onStartCommand Already Running");
            return START_STICKY;
        }
        else
        {
            Log.i(TAG, "onStartCommand Init Running");
            isRunning = true;
            initNotification();
            execute();
        }

        return START_STICKY;
    }

    public void initNotification()
    {
        mNotifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Mapa Region Offline")
                .setContentText("Descarga en proceso")
                .setSmallIcon(android.R.drawable.stat_sys_download_done);

        startForeground(1, mBuilder.build());
    }

    public void execute()
    {
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback()
            {
                @Override
                public void onList(final OfflineRegion[] offlineRegions)
                {
                    if (offlineRegions == null || offlineRegions.length == 0)
                    {
                        Log.i(TAG, "No hay ninguna region aun -> crearla y descargarla");
                        createAndDownloadRegion(TAG);
                    }
                    else
                    {
                        offlineRegions[0].getStatus(new OfflineRegion.OfflineRegionStatusCallback()
                        {
                            @Override
                            public void onStatus(OfflineRegionStatus status)
                            {
                                if (status.isComplete())
                                {
                                    Log.i(TAG, "La region ya esta descargada completamente -> No hacer nada");
                                }
                                else
                                {
                                    Log.i(TAG, "La region esta creada pero no se ha descargado completamente -> seguir descargando");
                                    offlineRegion = offlineRegions[0];
                                    downloadRegion();
                                }
                            }

                            @Override
                            public void onError(String error)
                            {
                                Log.e(TAG, "On error status region");
                            }
                        });
                    }
                }

                @Override
                public void onError(String error)
                {
                    Log.e(TAG, "On error list regions " + error);
                }
            });
    }

    private void createAndDownloadRegion(final String regionName)
    {
        // Define offline region parameters, including bounds, min/max zoom, and metadata
        OfflineTilePyramidRegionDefinition regionDefinition = new OfflineTilePyramidRegionDefinition(STYLE_URL,boundsRegion,MIN_ZOOM,MAX_ZOOM,PIXEL_RATIO);

        // Build a JSONObject using the name for region use it to create a metadata variable.
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception)
        {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        // Create the offline region and launch the download
        offlineManager.createOfflineRegion(regionDefinition, metadata, new OfflineManager.CreateOfflineRegionCallback()
        {
            @Override
            public void onCreate(OfflineRegion offlineRegion)
            {
                Log.i(TAG, "Offline region created: " + regionName);
                DownloadMapOfflineService.this.offlineRegion = offlineRegion;
                downloadRegion();
            }

            @Override
            public void onError(String error)
            {
                Log.e(TAG, "Error create region: " + error);
            }
        });
    }

    private void downloadRegion()
    {
        // Set up an observer to handle download progress and notify the user when the region is finished downloading
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver()
        {
            @Override
            public void onStatusChanged(OfflineRegionStatus status)
            {
                double percentage = status.getRequiredResourceCount() >= 0 ?
                        (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount())
                        : 0.0;

                if (status.isComplete())
                {
                    Log.i(TAG,"Region is totally downloaded");
                    mBuilder.setContentText("Descarga completa").setProgress(0,0,false);
                    mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                    BikeMeApplication.getInstance().savePrefMapOfflineDownloaded();
                    BikeMeApplication.getInstance().unregisterReceiverFromManifest(NetworkReceiver.class);
                    stopSelf();
                    return;
                }

                mBuilder.setProgress(100, (int)percentage, false);
                mBuilder.setContentText(String.format("Descarga en proceso %s %%",String.valueOf((int)percentage)));
                mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());

            }
            @Override
            public void onError(OfflineRegionError error)
            {
                Log.e(TAG, "onError status change reason: " + error.getReason());
                Log.e(TAG, "onError status change message: " + error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit)
            {
                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
            }
        });
        // Begin download
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG,"Service destroy");
        mNotifyManager.cancel(NOTIFICATION_ID);
        offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
        super.onDestroy();
    }
}