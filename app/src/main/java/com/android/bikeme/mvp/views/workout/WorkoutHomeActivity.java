package com.android.bikeme.mvp.views.workout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.location.LastKnownLocationCallback;
import com.android.bikeme.bikemeutils.location.LocationSettingsCallback;
import com.android.bikeme.bikemeutils.location.LocationUtils;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaseremote.syncronization.Synchronization;
import com.android.bikeme.mvp.views.workout.workout_detail.WorkoutDetailActivity;
import com.android.bikeme.mvp.views.workout.workout_history.WorkoutHistoryActivity;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.mapboxsdk.maps.MapView;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutHomeActivity extends BaseActivity implements WorkoutHomeView, View.OnClickListener, ServiceConnection, WorkoutMapOnline.MapOnlineReadyCallback, LastKnownLocationCallback, WorkoutMapOffline.MapOfflineReadyCallback, LocationSettingsCallback {

    private static final String TAG = WorkoutHomeActivity.class.getSimpleName();

    private WorkoutBackgroundService workoutBackgroundService;
    private boolean isServiceBounded = false;
    private Intent intent;
    boolean alreadyRunning;
    private TextView distanceTextView, stopWatchTextView;
    private AppCompatButton startWorkoutButton, pauseWorkoutButton, finishWorkoutButton;
    private WorkoutMapOnline workoutMapOnline;
    private WorkoutMapOffline workoutMapOffline;
    private FirebaseUser currentUser;
    private LocationUtils locationUtils;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_home_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        locationUtils = new LocationUtils(this);
        locationUtils.buildLocationRequest(WorkoutBackgroundService.INTERVAL_UPDATES_SECONDS,WorkoutBackgroundService.MINIMUM_DISTANCE_METERS);

        intent = new Intent(this, WorkoutBackgroundService.class);
        alreadyRunning = BikeMeApplication.getInstance().isMyServiceRunning(WorkoutBackgroundService.class);

        isOnline = BikeMeApplication.getInstance().isOnline();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_layout);
        MapView mMapView = (MapView)findViewById(R.id.mapView);
        if(isOnline)
        {
            mMapView.setVisibility(View.GONE);

            workoutMapOnline = new WorkoutMapOnline(mapFragment);
            workoutMapOnline.setMapOnlineReadyCallback(this);
            workoutMapOnline.getMapOnline();
        }
        else
        {
            View mapFragmentView =  mapFragment.getView();
            assert mapFragmentView != null;
            mapFragmentView.setVisibility(View.GONE);

            mMapView.onCreate(savedInstanceState);
            workoutMapOffline = new WorkoutMapOffline(mMapView);
            workoutMapOffline.setMapOfflineReadyCallback(this);
            workoutMapOffline.getMapOffline();
        }

        distanceTextView = (TextView)findViewById(R.id.distance_text_view);
        stopWatchTextView = (TextView)findViewById(R.id.stop_watch_text_view);

        startWorkoutButton = (AppCompatButton) findViewById(R.id.start_workout_button);
        startWorkoutButton.setOnClickListener(this);
        pauseWorkoutButton = (AppCompatButton) findViewById(R.id.pause_workout_button);
        pauseWorkoutButton.setOnClickListener(this);
        finishWorkoutButton = (AppCompatButton) findViewById(R.id.finish_workout_button);
        finishWorkoutButton.setOnClickListener(this);
    }

    @Override
    public void onMapOnlineReady()
    {
        if(alreadyRunning)
        {
            Log.i(TAG,"Service already running re init activity whit map online");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }

        workoutMapOnline.enableMyLocation();
        locationUtils.getLastKnownLocation(this);
    }

    @Override
    public void onMapOfflineReady()
    {
        if(alreadyRunning)
        {
            Log.i(TAG,"Service already running re init activity map offline");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }

        locationUtils.getLastKnownLocation(this);
    }

    @Override
    public void lastKnownLocation(Location location)
    {
        if(isOnline)
        {
            workoutMapOnline.moveCameraLocation(location);
        }
        else
        {
            workoutMapOffline.drawMyLocationMarker(location);
            workoutMapOffline.moveCameraLocation(location);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder backgroundLocationService)
    {
        WorkoutBackgroundService.WorkoutBackgroundServiceBinder binder = (WorkoutBackgroundService.WorkoutBackgroundServiceBinder) backgroundLocationService;
        workoutBackgroundService = binder.getWorkoutBackgroundServiceInstance();
        workoutBackgroundService.setWorkoutHomeView(this);
        isServiceBounded = true;
        if(alreadyRunning)
        {
            if(workoutBackgroundService.isPaused())
            {
                setWorkoutStatePause();
            }
            else
            {
                setWorkoutStateRunning();
            }

            for(LatLng latLng : workoutBackgroundService.getRouteLatLngList())
            {
                if(isOnline)
                {
                    workoutMapOnline.drawPolyline(latLng);
                }
                else
                {
                    com.mapbox.mapboxsdk.geometry.LatLng mapBoxLatLng = new com.mapbox.mapboxsdk.geometry.LatLng(latLng.latitude,latLng.longitude);
                    workoutMapOffline.drawPolyline(mapBoxLatLng);
                }
            }
        }
        else
        {
            workoutBackgroundService.startWorkout();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName)
    {
        isServiceBounded = false;
    }


    @Override
    public void setWorkoutStateRunning()
    {
        Synchronization.stopAutoSync(this);

        pauseWorkoutButton.setVisibility(View.VISIBLE);
        finishWorkoutButton.setVisibility(View.VISIBLE);
        startWorkoutButton.setVisibility(View.GONE);
    }

    @Override
    public void setWorkoutStatePause()
    {
        pauseWorkoutButton.setVisibility(View.GONE);
        finishWorkoutButton.setVisibility(View.VISIBLE);
        startWorkoutButton.setVisibility(View.VISIBLE);
        startWorkoutButton.setText(R.string.workout_restart_text);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)startWorkoutButton.getLayoutParams();
        layoutParams.weight = (float)0.5;
        startWorkoutButton.setLayoutParams(layoutParams);
    }

    @Override
    public void setWorkoutStateRestartDefault()
    {
        Synchronization.startAutoSync(this);

        alreadyRunning = false;
        workoutBackgroundService = null;
        if(isOnline)
        {
            workoutMapOnline.restartMap();
        }
        else
        {
            workoutMapOffline.restartMap();
        }

        startWorkoutButton.setText(R.string.workout_start_text);
        stopWatchTextView.setText(R.string.default_workout_text);
        distanceTextView.setText(R.string.default_workout_text);

        pauseWorkoutButton.setVisibility(View.GONE);
        finishWorkoutButton.setVisibility(View.GONE);
        startWorkoutButton.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)startWorkoutButton.getLayoutParams();
        layoutParams.weight = (float)1.0;
        startWorkoutButton.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.start_workout_button:
                locationUtils.checkLocationSettings(this);
                break;
            case R.id.pause_workout_button:
                setWorkoutStatePause();
                workoutBackgroundService.pauseWorkout();
                break;
            case R.id.finish_workout_button:
                Workout workout = getWorkout();
                workoutBackgroundService.finishWorkout();
                if (isServiceBounded)
                {
                    Log.i(TAG, "Unbound service");
                    workoutBackgroundService.setWorkoutHomeView(null);
                    unbindService(this);
                    isServiceBounded = false;
                }
                stopService(intent);
                setWorkoutStateRestartDefault();
                onGoToWorkout(workout);
                break;
        }
    }

    @Override
    public void locationSettingsGranted(boolean isLocationSettingsGranted)
    {
        if(isLocationSettingsGranted)
        {
            setWorkoutStateRunning();
            if(workoutBackgroundService !=null)
            {
                workoutBackgroundService.resumeWorkout();
            }
            else
            {
                startService(intent);
                bindService(intent, this, Context.BIND_AUTO_CREATE);
            }
        }
        else
        {
            Toast.makeText(this,R.string.permission_error_workout_home,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void updateDistance(int totalDistanceMeters)
    {
        distanceTextView.setText(Workout.getDistanceKmString(totalDistanceMeters));
    }

    @Override
    public void updateLocation(Location location)
    {
        if(isOnline)
        {
            workoutMapOnline.moveCameraLocation(location);
            workoutMapOnline.drawPolyline(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        else
        {
            workoutMapOffline.drawMyLocationMarker(location);
            workoutMapOffline.moveCameraLocation(location);
            workoutMapOffline.drawPolyline(new com.mapbox.mapboxsdk.geometry.LatLng(location.getLatitude(),location.getLongitude()));
        }
    }

    @Override
    public void updateStopWatch(int durationSeconds)
    {
        stopWatchTextView.setText(Workout.getDurationString(durationSeconds));
    }

    public Workout getWorkout()
    {
        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        Date currentDateDate = bikeMeApplication.getCurrentDate();

        int durationSeconds = workoutBackgroundService.getDurationSeconds();
        Date beginDate = new Date(currentDateDate.getTime()-durationSeconds*1000);
        ArrayList<LatLng> routeLocations = workoutBackgroundService.getRouteLatLngList();
        int totalDistanceMeters = workoutBackgroundService.getTotalDistanceMeters();
        int averageSpeedKm = workoutBackgroundService.getAverageSpeedKm();
        int averageAltitudeMeters = workoutBackgroundService.getAverageAltitudeMeters();
        int typeRoute = workoutBackgroundService.getTypeRoute();
        String workoutName = getString(R.string.workout_name_text,bikeMeApplication.getDateString(beginDate),bikeMeApplication.getHourAmPm(beginDate));

        Workout workout = new Workout();
        workout.setName(workoutName);
        workout.setUser(currentUser.getUid());
        workout.setDurationSeconds(durationSeconds);
        workout.setBeginDate(bikeMeApplication.getDateTimeString(beginDate));
        workout.setComment("");
        workout.setRouteLatLngArrayList(routeLocations);
        workout.setTotalDistanceMeters(totalDistanceMeters);
        workout.setAverageSpeedKm(averageSpeedKm);
        workout.setAverageAltitudeMeters(averageAltitudeMeters);
        workout.setTypeRoute(typeRoute);
        return  workout;
    }

    @Override
    public void onGoToWorkout(Workout workout)
    {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra(Workout.WORKOUT_KEY,workout);
        intent.putExtra(Workout.NEW_WORKOUT_KEY,true);
        startActivityTransition(intent);
    }

    @Override
    public void onGoToWorkoutHistory()
    {
        startActivityTransition(new Intent(this, WorkoutHistoryActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_workout_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_go_to_history:
                onGoToWorkoutHistory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}