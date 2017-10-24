package com.android.bikeme.mvp.views.workout;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.bikeme.R;
import com.android.bikeme.bikemeutils.location.LocationUtils;
import com.android.bikeme.bikemeutils.location.LocationUpdatesCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class WorkoutBackgroundService extends Service implements LocationUpdatesCallback {

    private static final String TAG = WorkoutBackgroundService.class.getSimpleName();

    public static final float MINIMUM_DISTANCE_METERS = 5;
    public static final int INTERVAL_UPDATES_SECONDS = 20;
    private static final int NOTIFICATION_WORKOUT_ID = 2;
    public static final String WORKOUT_ACTIVITY_ACTION = "android.intent.action.ACTIVITY_WORKOUT";
    public static final String ON_BIKE = "on_bike";

    IBinder mBinder = new WorkoutBackgroundServiceBinder();
    public class WorkoutBackgroundServiceBinder extends Binder
    {
        public WorkoutBackgroundService getWorkoutBackgroundServiceInstance()
        {
            return WorkoutBackgroundService.this;
        }
    }

    private PowerManager.WakeLock mWakeLock;
    private boolean isRunning;
    private boolean isPaused;
    private WorkoutHomeView workoutHomeView;
    private LocationUtils locationUtils;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime;
    private Handler stopWatchHandler;

    private Location userPreviousPosition;

    private int durationSeconds;
    private ArrayList<LatLng> routeLatLngList;
    private int totalDistanceMeters;
    private int sumAltitudeMeters;
    private int maxAltitudeMeters;

    public Runnable stopWatchThread = new Runnable()
    {
        public void run()
        {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            durationSeconds = (int) (UpdateTime / 1000);
            workoutHomeView.updateStopWatch(durationSeconds);
            stopWatchHandler.postDelayed(this, 0);
        }
    };

    public void setWorkoutHomeView(WorkoutHomeView workoutHomeView)
    {
        this.workoutHomeView = workoutHomeView;
        if(workoutHomeView !=null)
        {
            workoutHomeView.updateDistance(totalDistanceMeters);
            workoutHomeView.updateStopWatch(durationSeconds);
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "Service created");

        isRunning = false;
        isPaused = false;

        locationUtils = new LocationUtils(this);
        locationUtils.buildLocationRequest(INTERVAL_UPDATES_SECONDS, MINIMUM_DISTANCE_METERS);

        userPreviousPosition = null;

        durationSeconds = 0;
        routeLatLngList = new ArrayList<>();
        totalDistanceMeters = 0;
        sumAltitudeMeters = 0;
        maxAltitudeMeters = 0;

        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        stopWatchHandler = new Handler();
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);

        if (this.mWakeLock == null)
        {
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        }

        if (!this.mWakeLock.isHeld())
        {
            this.mWakeLock.acquire();
        }

        if(isRunning)
        {
            Log.i(TAG, "onStartCommand Already Running");
            return START_NOT_STICKY;
        }
        else
        {
            Log.i(TAG, "onStartCommand Init Running");
            setNotification();
            isRunning = true;
        }

        return START_NOT_STICKY;
    }


    public void setNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_workout)
                .setContentTitle(getString(R.string.workout_in_process_text))
                .setContentText(getString(R.string.open_app_text));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, WorkoutHomeActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WorkoutHomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        startForeground(NOTIFICATION_WORKOUT_ID, builder.build());
    }

    public void startWorkout()
    {
        startStopWatch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WORKOUT_ACTIVITY_ACTION);
    }

    public void resumeWorkout()
    {
        startStopWatch();
        isPaused = false;
    }

    public void startStopWatch()
    {
        locationUtils.startLocationUpdates(this);
        StartTime = SystemClock.uptimeMillis();
        stopWatchHandler.postDelayed(stopWatchThread, 0);
    }

    public void pauseWorkout()
    {
        locationUtils.stopLocationUpdates();
        isPaused=true;
        TimeBuff += MillisecondTime;
        stopWatchHandler.removeCallbacks(stopWatchThread);
    }

    public void finishWorkout()
    {
        stopWatchHandler.removeCallbacks(stopWatchThread);
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    @Override
    public void locationChange(Location newLocation)
    {
        Log.i(TAG, "Location change ");
        if(routeLatLngList.isEmpty())
        {
            Log.i(TAG, "Empty Locations");
            int newAltitude = (int) newLocation.getAltitude();
            maxAltitudeMeters = newAltitude;
            sumAltitudeMeters += newAltitude;
            routeLatLngList.add(new LatLng(newLocation.getLatitude(),newLocation.getLongitude()));
            userPreviousPosition = newLocation;
        }
        else
        {
            Log.i(TAG, "Not Empty Locations");
            double distanceMeters = userPreviousPosition.distanceTo(newLocation);

            if(distanceMeters >= MINIMUM_DISTANCE_METERS)
            {
                Log.i(TAG, "User has moved "+distanceMeters+" Meteres It Is Necessary re calculate distanceMeters");

                int newAltitude = (int)newLocation.getAltitude();
                if(newAltitude > userPreviousPosition.getAltitude()) maxAltitudeMeters = newAltitude;
                sumAltitudeMeters += newAltitude;
                routeLatLngList.add(new LatLng(newLocation.getLatitude(),newLocation.getLongitude()));
                totalDistanceMeters +=distanceMeters;

                workoutHomeView.updateDistance(totalDistanceMeters);
                workoutHomeView.updateLocation(newLocation);
                userPreviousPosition = newLocation;
            }
            else
            {
                Log.i(TAG, "Distance "+distanceMeters+" Meter Too Small To. Ignore");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "on Destroy");
        this.isRunning = false;

        if (this.mWakeLock != null)
        {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }

        locationUtils.stopLocationUpdates();
        super.onDestroy();
    }

    public ArrayList<LatLng> getRouteLatLngList()
    {
        return routeLatLngList;
    }

    public int getTotalDistanceMeters()
    {
        return totalDistanceMeters;
    }

    public int getDurationSeconds()
    {
        return  durationSeconds;
    }

    public int getAverageAltitudeMeters()
    {
       return routeLatLngList.isEmpty() ?  0 : sumAltitudeMeters/ routeLatLngList.size();
    }

    public int getAverageSpeedKm()
    {
        return durationSeconds == 0 ? 0 : (int)((totalDistanceMeters/durationSeconds)*3.6);
    }

    public int getTypeRoute()
    {
        int typeRoute = 0;
        if(maxAltitudeMeters <= 1200)
        {
            typeRoute = 0;
        }
        else if(maxAltitudeMeters > 1200 && maxAltitudeMeters <= 2000)
        {
            typeRoute=1;
        }
        else if(maxAltitudeMeters > 2000)
        {
            typeRoute=2;
        }
        return typeRoute;
    }
}