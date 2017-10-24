package com.android.bikeme.mvp.views.workout.workout_detail;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaselocal.models.WorkoutModel;
import com.android.bikeme.mvp.interactors.workout.WorkoutDetailDetailInteractorImpl;
import com.android.bikeme.mvp.presenters.workout.WorkoutDetailPresenter;
import com.android.bikeme.mvp.presenters.workout.WorkoutDetailPresenterImpl;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOfflineActivity;
import com.android.bikeme.mvp.views.workout.WorkoutMapOffline;
import com.android.bikeme.mvp.views.workout.WorkoutMapOnline;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;

/**
 * Created by Daniel on 3 sep 2017.
 */
public class WorkoutDetailActivity extends BaseActivity implements WorkoutMapOnline.MapOnlineReadyCallback, WorkoutDetailView, WorkoutMapOffline.MapOfflineReadyCallback {

    private Workout workout;
    private boolean newWorkout;

    private EditText workoutNameEditText, workoutCommentEditText;
    private WorkoutMapOnline workoutMapOnline;
    private WorkoutMapOffline workoutMapOffline;
    ProgressDialog mProgressDialog;
    WorkoutDetailPresenter workoutDetailPresenter;
    boolean isOnline;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_detail_activity);

        workout = getIntent().getParcelableExtra(Workout.WORKOUT_KEY);
        newWorkout = getIntent().getBooleanExtra(Workout.NEW_WORKOUT_KEY,true);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!newWorkout);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        WorkoutModel workoutModel = new WorkoutModel(getContentResolver());
        UserModel userModel = new UserModel(getContentResolver());
        ChallengeModel challengeModel = new ChallengeModel(getContentResolver());
        workoutDetailPresenter = new WorkoutDetailPresenterImpl(this,new WorkoutDetailDetailInteractorImpl(workoutModel,userModel,challengeModel));

        TextView workoutDurationTextView = (TextView) findViewById(R.id.workout_detail_duration_text_view);
        TextView workoutDistanceTextView = (TextView) findViewById(R.id.workout_detail_distance_text_view);
        TextView workoutAverageAltitudeTextView = (TextView) findViewById(R.id.workout_detail_altitude_text_view);
        TextView workoutSpeedTextView = (TextView) findViewById(R.id.workout_detail_speed_text_view);
        TextView workoutTypeRouteTextView = (TextView) findViewById(R.id.workout_detail_type_route_text_view);
        TextView workoutDateTextView = (TextView) findViewById(R.id.workout_detail_date_text_view);
        workoutNameEditText = (EditText)findViewById(R.id.workout_name_detail_edit_text);
        workoutCommentEditText = (EditText)findViewById(R.id.workout_detail_comment_text_text);

        if(workout !=null)
        {
            BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();

            workoutNameEditText.setText(workout.getName());
            workoutNameEditText.setEnabled(newWorkout);
            workoutDurationTextView.setText(Workout.getDurationString(workout.getDurationSeconds()));
            workoutDistanceTextView.setText(Workout.getDistanceKmString(workout.getTotalDistanceMeters()));
            workoutAverageAltitudeTextView.setText(String.valueOf(workout.getAverageAltitudeMeters()));
            workoutSpeedTextView.setText(String.valueOf(workout.getAverageSpeedKm()));
            workoutTypeRouteTextView.setText(getResources().getStringArray(R.array.type_routes)[workout.getTypeRoute()]);
            workoutDateTextView.setText(bikeMeApplication.getDateString(bikeMeApplication.getDateTime(workout.getBeginDate())));
            String workoutComment = workout.getComment();
            if(workoutComment.isEmpty() && !newWorkout)
            {
                workoutCommentEditText.setText(getString(R.string.not_comment_text));
            }
            else
            {
                workoutCommentEditText.setText(workoutComment);
            }
            workoutCommentEditText.setEnabled(newWorkout);

            isOnline = BikeMeApplication.getInstance().isOnlineWifi();
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
                workoutMapOffline  = new WorkoutMapOffline(mMapView);
                workoutMapOffline.setMapOfflineReadyCallback(this);
                workoutMapOffline.getMapOffline();
            }
        }
    }

    @Override
    public void onMapOnlineReady()
    {
        workoutMapOnline.staticMapOnline();
        ArrayList<LatLng> latLngArrayList = workout.getRouteLatLngArrayList();
        if(!latLngArrayList.isEmpty())
        {
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for(LatLng latLng : latLngArrayList)
            {
                workoutMapOnline.drawPolyline(latLng);
                builder.include(latLng);
            }
            workoutMapOnline.drawMarker(getMarker(latLngArrayList.get(0), BitmapDescriptorFactory.HUE_RED,getString(R.string.departure_text)));
            workoutMapOnline.drawMarker(getMarker(latLngArrayList.get(latLngArrayList.size()-1), BitmapDescriptorFactory.HUE_GREEN,getString(R.string.arrival_text)));
            workoutMapOnline.moveCameraBounds(builder.build());
        }
    }

    @Override
    public void onMapOfflineReady()
    {
        workoutMapOffline.staticMapOnline();
        ArrayList<LatLng> latLngArrayList = workout.getRouteLatLngArrayList();
        if(latLngArrayList.size() >= 2)
        {
            com.mapbox.mapboxsdk.geometry.LatLngBounds.Builder builder = new com.mapbox.mapboxsdk.geometry.LatLngBounds.Builder();
            for(LatLng latLng : latLngArrayList)
            {
                com.mapbox.mapboxsdk.geometry.LatLng mapBoxLatLng = new com.mapbox.mapboxsdk.geometry.LatLng(latLng.latitude,latLng.longitude);
                workoutMapOffline.drawPolyline(mapBoxLatLng);
                builder.include(mapBoxLatLng);
            }
            workoutMapOffline.moveCameraBounds(builder.build());

            LatLng pointBegin = latLngArrayList.get(0);
            MarkerOptions markerOptionsBegin = new MarkerOptions()
                    .position(new com.mapbox.mapboxsdk.geometry.LatLng(pointBegin.latitude, pointBegin.longitude))
                    .title(getString(R.string.departure_text));

            workoutMapOffline.drawMarker(markerOptionsBegin);


            Icon icon = IconFactory.getInstance(this).fromBitmap(RouteDetailMapOfflineActivity.getMarkerGreen(this));
            LatLng pointEnd = latLngArrayList.get(latLngArrayList.size()-1);
            MarkerOptions markerOptionsEnd = new MarkerOptions()
                                            .icon(icon)
                                            .position(new com.mapbox.mapboxsdk.geometry.LatLng(pointEnd.latitude, pointEnd.longitude))
                                            .title(getString(R.string.arrival_text));

            workoutMapOffline.drawMarker(markerOptionsEnd);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(newWorkout)
        {
            getMenuInflater().inflate(R.menu.menu_workout_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_discard:
                finishActivityTransition();
                break;
            case R.id.action_save:
                workout.setName(workoutNameEditText.getText().toString());
                workout.setComment(workoutCommentEditText.getText().toString());
                workoutDetailPresenter.onSaveUserWorkout(workout);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showChallengesLevelsAchieved(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved)
    {
        this.index = 0;
        this.challengesAchieved = challengesAchieved;
        this.levelsAchieved = levelsAchieved;
        showChallengesLevelsAchieved(true);
    }

    @Override
    public void showProgressDialog()
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.saving_text));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }
}