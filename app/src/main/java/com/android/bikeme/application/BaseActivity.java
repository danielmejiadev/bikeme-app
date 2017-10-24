package com.android.bikeme.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.bikeme.R;
import com.android.bikeme.bikemeutils.DownloadMapOfflineService;
import com.android.bikeme.bikemeutils.location.LocationSettingsCallback;
import com.android.bikeme.bikemeutils.location.LocationUtils;
import com.android.bikeme.bikemeutils.location.PermissionListener;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaseremote.syncronization.Synchronization;
import com.android.bikeme.mvp.views.BikeMeActivity;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOfflineActivity;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOnlineActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import java.util.ArrayList;

/**
 * Created by Daniel on 29/04/2017.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public static final double[] SOUTH_WEST_BOUND = new double[]{3.884502, -76.337831};
    public static final double[] NORTH_EAST_BOUND = new double[]{4.424216, -75.920681};

    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OnCreate Base Activity");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private SystemBarTintManager mSystemBarTint;
    private boolean mTranslucentStatus;
    private boolean mTranslucentStatusSet;

    public void setStateBarTint(ViewGroup root, Toolbar toolbar, int stateBarHeight, boolean translucentStatus)
    {
        //  ViewGroup root = (ViewGroup)findViewById(R.id.content_create_route_view);
        //setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());
        if (translucentStatus)
        {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            params.topMargin = -stateBarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = stateBarHeight;
        }
    }

    public SystemBarTintManager getSystemBarTint()
    {
        if (null == mSystemBarTint)
        {
            mSystemBarTint = new SystemBarTintManager(this);
        }
        return mSystemBarTint;
    }

    public int getStatusBarHeight()
    {
        return getSystemBarTint().getConfig().getStatusBarHeight();
    }


    @TargetApi(19)
    public boolean hasTranslucentStatusBar()
    {
        if (!mTranslucentStatusSet)
        {
            mTranslucentStatus = Build.VERSION.SDK_INT >= 19 && ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTranslucentStatusSet = true;
        }
        return mTranslucentStatus;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private LocationSettingsCallback locationSettingCallback;

    public void setLocationSettingCallback(LocationSettingsCallback locationSettingCallback)
    {
        this.locationSettingCallback = locationSettingCallback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        boolean isLocationSettingsGranted = false;
        switch (requestCode)
        {
            case LocationUtils.REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Log.i(LocationUtils.TAG, "El usuario permitió el cambio de ajustes de ubicación.");
                        isLocationSettingsGranted = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LocationUtils.TAG, "El usuario no permitió el cambio de ajustes de ubicación");
                        break;
                }
                break;
        }
        if(locationSettingCallback!=null)
        {
            locationSettingCallback.locationSettingsGranted(isLocationSettingsGranted);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public PermissionListener permissionListener;
    private int typeResponse;

    public void setPermissionListener(PermissionListener permissionListener, int typeResponse)
    {
        this.permissionListener = permissionListener;
        this.typeResponse = typeResponse;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == LocationUtils.REQUEST_LOCATION)
        {
            boolean permissionAssigned = grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            permissionListener.permissionResult(permissionAssigned, typeResponse);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int index;
    public ArrayList<Challenge> challengesAchieved;
    public ArrayList<Integer> levelsAchieved;

    public void showChallengesLevelsAchieved(final boolean finishAfter)
    {
        if(!challengesAchieved.isEmpty())
        {
            AlertDialog challengeAchievedDialog = challengesAchieved.get(index).getDialogChallengeAchieved(this);
            challengeAchievedDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialogInterface)
                {
                    index++;
                    if(index < challengesAchieved.size())
                    {
                        showChallengesLevelsAchieved(finishAfter);
                    }
                    else
                    {
                        index=0;
                        showLevelsAchieved(finishAfter);
                    }
                }
            });
            challengeAchievedDialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemeUserProfile;
            challengeAchievedDialog.show();
        }
        else
        {
            showLevelsAchieved(finishAfter);
        }
    }

    public void showLevelsAchieved(final boolean finishAfter)
    {
        if(!levelsAchieved.isEmpty())
        {
            AlertDialog levelAchievedDialog = User.getLevelsAchieved(this,levelsAchieved.get(index).intValue());
            levelAchievedDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialogInterface)
                {
                    index++;
                    if(index < levelsAchieved.size())
                    {
                        showLevelsAchieved(finishAfter);
                    }
                    else if(finishAfter)
                    {
                        finishActivityTransition();
                    }
                }
            });
            levelAchievedDialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemeUserProfile;
            levelAchievedDialog.show();
        }
        else if(finishAfter)
        {
            finishActivityTransition();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void startActivityTransition(Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            this.getWindow().setExitTransition(new Explode());
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
        }
        else
        {
            startActivity(intent);
        }
    }

    public void finishActivityTransition()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            finishAfterTransition();
        }
        else
        {
            finish();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void goToMapDetail(Intent intent)
    {
        if(BikeMeApplication.getInstance().isOnline())
        {
            intent.setClass(this,RouteDetailMapOnlineActivity.class);
            startActivityTransition(intent);
        }
        else
        {
            if(BikeMeApplication.getInstance().isMapOfflineDownloaded())
            {
                intent.setClass(this,RouteDetailMapOfflineActivity.class);
                startActivityTransition(intent);
            }
            else
            {
                Toast.makeText(this,R.string.map_offline_not_downloaded,Toast.LENGTH_SHORT).show();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public MarkerOptions getMarker(LatLng position, float color, String title)
    {
        return new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void goToHome()
    {
        Synchronization.startAutoSync(this);

        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        boolean serviceRunning = bikeMeApplication.isMyServiceRunning(DownloadMapOfflineService.class);
        boolean mapOfflineDownloaded = bikeMeApplication.isMapOfflineDownloaded();
        boolean onlineWifi = bikeMeApplication.isOnlineWifi();
        if(!serviceRunning && !mapOfflineDownloaded && onlineWifi)
        {
            Log.i(TAG,"start");
            startService(new Intent(this, DownloadMapOfflineService.class));
        }

        startActivity(new Intent(this, BikeMeActivity.class));
        finish();
    }
}