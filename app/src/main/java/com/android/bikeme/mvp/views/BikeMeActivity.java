package com.android.bikeme.mvp.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.location.LocationUpdatesCallback;
import com.android.bikeme.bikemeutils.location.LocationUtils;
import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.ProblemModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaseremote.syncronization.Synchronization;
import com.android.bikeme.mvp.interactors.BikeMeInteractorImpl;
import com.android.bikeme.mvp.presenters.BikeMePresenter;
import com.android.bikeme.mvp.presenters.BikeMePresenterImpl;
import com.android.bikeme.mvp.views.challenges.ChallengeFragment;
import com.android.bikeme.mvp.views.events.EventFragment;
import com.android.bikeme.mvp.views.routes.RouteHomeFragment;
import com.android.bikeme.mvp.views.user.SignInActivity;
import com.android.bikeme.mvp.views.user.UserProfileActivity;
import com.android.bikeme.mvp.views.workout.WorkoutHomeActivity;
import com.google.firebase.auth.FirebaseUser;

public class BikeMeActivity extends BaseActivity implements BikeMeView, BottomNavigationView.OnNavigationItemSelectedListener, LocationUpdatesCallback{

    public static final String TAG =  BikeMeActivity.class.getSimpleName();
    private static final int INTERVAL_UPDATES_SECONDS = 60;
    public static final float MINIMUM_DISTANCE_METERS = 80;

    private Fragment currentFragment;
    private BikeMePresenter bikeMePresenter;
    private Location currentLocation;
    private Fragment[] fragments = new Fragment[3];
    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(currentUser == null)
        {
            navigateToSignIn();
        }
        else
        {
            setContentView(R.layout.bike_me_activity_view);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ViewGroup root = (ViewGroup) findViewById(R.id.content);
            setStateBarTint(root, toolbar, getStatusBarHeight(), hasTranslucentStatusBar());

            fragments[0] = RouteHomeFragment.newInstance();

            currentFragment =  fragments[0];
            navigateToFragment(currentFragment);

            fragments[1] = EventFragment.newInstance();
            fragments[2] = ChallengeFragment.newInstance();

            UserModel userModel = new UserModel(getContentResolver());
            ProblemModel problemModel = new ProblemModel(getContentResolver());
            bikeMePresenter = new BikeMePresenterImpl(this, new BikeMeInteractorImpl(userModel,problemModel));

            locationUtils = new LocationUtils(this);
            locationUtils.buildLocationRequest(INTERVAL_UPDATES_SECONDS, MINIMUM_DISTANCE_METERS);
            locationUtils.checkLocationSettings(null);

            BottomNavigationView bottomNavigationBar = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar_activity_bike_me);
            bottomNavigationBar.setOnNavigationItemSelectedListener(this);

            Synchronization.syncNow(Synchronization.REMOTE_TO_LOCAL_DATABASE_SYNC);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(!locationUtils.isLocationUpdatesRunning())
        {
            locationUtils.startLocationUpdates(this);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(locationUtils.isLocationUpdatesRunning())
        {
            locationUtils.stopLocationUpdates();
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_toolbar_activity_bike_me, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        bikeMePresenter.itemSelectedBottomNavigation(item.getItemId());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_workout:
                navigateToWorkout();
                break;
            case R.id.item_profile:
                bikeMePresenter.onItemUserProfile(currentUser.getUid());
                break;
            case R.id.item_report_problem_toolbar:
                showInsertProblem();
                break;
            case R.id.item_sign_out_toolbar:
                navigateToSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fragmentSelected(int fragmentSelect)
    {
        switch (fragmentSelect)
        {
            case R.id.item_route_menu:
                if(!(currentFragment instanceof RouteHomeFragment))
                {
                    currentFragment = fragments[0];
                    navigateToFragment(currentFragment);
                    if(!locationUtils.isLocationUpdatesRunning())
                    {
                        locationUtils.startLocationUpdates(this);
                    }
                }
                break;
            case R.id.item_event_menu:
                if(!(currentFragment instanceof EventFragment))
                {
                    currentFragment = fragments[1];
                    navigateToFragment(currentFragment);
                    if(locationUtils.isLocationUpdatesRunning())
                    {
                        locationUtils.stopLocationUpdates();
                    }
                }
                break;

            case R.id.item_challenge_menu:
                if(!(currentFragment instanceof ChallengeFragment))
                {
                    currentFragment = fragments[2];
                    navigateToFragment(currentFragment);
                    if(locationUtils.isLocationUpdatesRunning())
                    {
                        locationUtils.stopLocationUpdates();
                    }
                }
                break;
        }
    }

    public void showInsertProblem()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_report_problem, null);
        dialogBuilder.setView(dialogView);
        final EditText insertProblemEditText = (EditText)dialogView.findViewById(R.id.dialog_report_problme_edit_text);
        dialogBuilder.setPositiveButton(R.string.save_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String problemString = insertProblemEditText.getText().toString();
                Problem problem = new Problem();
                problem.setDescription(problemString);
                problem.setUser(currentUser.getUid());
                problem.setDate(BikeMeApplication.getInstance().getDateTimeString(BikeMeApplication.getInstance().getCurrentDate()));
                bikeMePresenter.saveProblem(problem);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void navigateToFragment(Fragment fragment)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.frame_layout_activity_bike_me, fragment, fragment.getTag());
        ft.addToBackStack(fragment.getTag());
        ft.commit();
    }

    @Override
    public  void navigateToWorkout()
    {
        boolean isOnline = BikeMeApplication.getInstance().isOnline();
        boolean mapOfflineDownloaded = BikeMeApplication.getInstance().isMapOfflineDownloaded();
        if(!isOnline && !mapOfflineDownloaded)
        {
            Toast.makeText(this,R.string.map_offline_not_downloaded,Toast.LENGTH_SHORT).show();
        }
        else
        {
            startActivityTransition(new Intent(this, WorkoutHomeActivity.class));
        }
    }

    @Override
    public void navigateToUserProfile(User user, int totalPoints)
    {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(User.USER_KEY, user);
        intent.putExtra(User.TOTAL_POINTS_KEY, totalPoints);
        startActivityTransition(intent);
    }

    @Override
    public void navigateToSignIn()
    {
        firebaseAuth.signOut();
        Synchronization.stopAutoSync(this);
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finishActivityTransition();
    }

    @Override
    public void locationChange(Location newLocation)
    {
        if(currentLocation==null)
        {
            currentLocation = newLocation;
            getContentResolver().notifyChange(DataBaseContract.User.URI_LOCATION_NEW,null);
        }
        else
        {
            double distanceMeters = currentLocation.distanceTo(newLocation);
            if(distanceMeters>=MINIMUM_DISTANCE_METERS)
            {
                currentLocation=newLocation;
                getContentResolver().notifyChange(DataBaseContract.User.URI_LOCATION_NEW,null);
            }
        }
    }

    public FirebaseUser getCurrentUser()
    {
        return currentUser;
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }
}