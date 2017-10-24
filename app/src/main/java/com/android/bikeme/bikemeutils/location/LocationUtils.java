package com.android.bikeme.bikemeutils.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.bikeme.application.BaseActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Daniel on 31 ago 2017.
 */
public class LocationUtils extends LocationCallback implements PermissionListener, OnSuccessListener<Location> {

    public static final String TAG = LocationUtils.class.getSimpleName();
    private static final int LAST_KNOWN_LOCATION_REQUEST = 1;
    private static final int LOCATION_UPDATES_REQUEST = 2;

    public static final int REQUEST_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    private Context context;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationUpdatesCallback locationUpdatesCallback;
    private LastKnownLocationCallback lastKnownLocationCallback;
    private boolean isLocationUpdatesRunning;

    /**
     For get location updates
        locationUtils = LocationUtils.getInstance();
        locationUtils.init(this);
        locationUtils.buildLocationRequest(INTERVAL_UPDATES_SECONDS);
        locationUtils.checkLocationSettings();
        locationUtils.startLocationUpdates(LocationUpdatesCallback)
        locationUtils.stopLocationUpdates();

     For get last known location
         locationUtils.init(activity);
         locationUtils.buildLocationRequest(5);
         locationUtils.checkLocationSettings();
         locationUtils.getLastKnownLocation(LastKnownLocationCallback);

     locationUtils.checkLocationSettings() if you require check settings for GPS

     **/

    public LocationUtils(Context context)
    {
        this.context = context;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void buildLocationRequest(int interval, float smallestDisplacement)
    {
        this.mLocationRequest = new LocationRequest()
                .setInterval(interval*1000)
               // .setFastestInterval(interval*1000)
               // .setSmallestDisplacement(smallestDisplacement)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void checkLocationSettings(final LocationSettingsCallback locationSettingsCallback)
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse)
            {
                Log.i(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                if(locationSettingsCallback!=null)
                {
                    locationSettingsCallback.locationSettingsGranted(true);
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode)
                {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            BaseActivity parent = (BaseActivity)context;
                            parent.setLocationSettingCallback(locationSettingsCallback);
                            resolvable.startResolutionForResult((BaseActivity)context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ignored) {}
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Los ajustes de ubicación no son apropiados.");
                        break;
                }
            }
        });
    }

    public void getLastKnownLocation(LastKnownLocationCallback callback)
    {
        this.lastKnownLocationCallback = callback;
        if(isLocationPermissionGranted())
        {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this);
        }
        else
        {
           manageDeniedPermission(LAST_KNOWN_LOCATION_REQUEST);
        }
    }

    @Override
    public void onSuccess(Location location)
    {
        if(location!=null)
        {
            lastKnownLocationCallback.lastKnownLocation(location);
        }
    }

    @Override
    public void permissionResult(boolean hasPermission, int typeResponse)
    {
        switch (typeResponse)
        {
            case LAST_KNOWN_LOCATION_REQUEST:
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this);
                break;
            case LOCATION_UPDATES_REQUEST:
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,this,null);
                break;
        }
    }

    public void startLocationUpdates(LocationUpdatesCallback locationUpdatesCallback)
    {
        Log.i(TAG, "Actualizacion de ubicacion activado.");
        this.locationUpdatesCallback = locationUpdatesCallback;
        this.isLocationUpdatesRunning=true;
        if (isLocationPermissionGranted())
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,this,null);
        }
        else
        {
            manageDeniedPermission(LOCATION_UPDATES_REQUEST);
        }
    }

    public void stopLocationUpdates()
    {
        Log.i(TAG, "Actualizacion de ubicacion detenido.");
        this.isLocationUpdatesRunning=false;
        mFusedLocationClient.removeLocationUpdates(this);
    }

    public boolean isLocationUpdatesRunning()
    {
        return isLocationUpdatesRunning;
    }

    @Override
    public void onLocationResult(LocationResult locationResult)
    {
        for (Location location : locationResult.getLocations())
        {
            Log.i(TAG, "onLocationChanged. Lat "+location.getLatitude()+" , Lon: "+location.getLongitude());
            locationUpdatesCallback.locationChange(location);
        }
       super.onLocationResult(locationResult);
    }

    public boolean isLocationPermissionGranted()
    {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public void manageDeniedPermission(int typeResponse)
    {
        ((BaseActivity)context).setPermissionListener(this, typeResponse);
        if (ActivityCompat.shouldShowRequestPermissionRationale((BaseActivity)context, Manifest.permission.ACCESS_FINE_LOCATION))
        {

        }
        else
        {
            ActivityCompat.requestPermissions((BaseActivity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }
}