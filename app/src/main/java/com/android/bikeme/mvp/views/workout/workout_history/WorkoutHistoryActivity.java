package com.android.bikeme.mvp.views.workout.workout_history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.bikemeutils.CustomRecyclerView;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.models.WorkoutModel;
import com.android.bikeme.mvp.views.workout.workout_detail.WorkoutDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutHistoryActivity extends BaseActivity implements WorkoutHistoryRecyclerViewAdapter.OnWorkoutClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<HashMap<String, Object>> {

    private static final int WORKOUT_LOADER_ID = 300;
    private SwipeRefreshLayout refreshLayout;
    private WorkoutHistoryRecyclerViewAdapter workoutHistoryRecyclerViewAdapter;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        View emptyView = findViewById(R.id.empty_view);

        workoutHistoryRecyclerViewAdapter = new WorkoutHistoryRecyclerViewAdapter(getResources().getStringArray(R.array.type_routes));
        workoutHistoryRecyclerViewAdapter.setOnWorkoutClickListener(this);

        CustomRecyclerView recyclerView = (CustomRecyclerView) findViewById(R.id.custom_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(workoutHistoryRecyclerViewAdapter);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        getSupportLoaderManager().initLoader(WORKOUT_LOADER_ID, null,this);
    }

    @Override
    public Loader<HashMap<String, Object>> onCreateLoader(int id, Bundle args)
    {
        return new WorkoutHistoryAsyncTaskLoader(this, refreshLayout, currentUser.getUid());
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data)
    {
        refreshLayout.setRefreshing(false);
        @SuppressWarnings("unchecked")
        ArrayList<String> dates = (ArrayList<String>) data.get(WorkoutModel.DATES_KEY);
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<Workout>> workouts = (ArrayList<ArrayList<Workout>>) data.get(WorkoutModel.WORKOUTS_KEY);
        workoutHistoryRecyclerViewAdapter.addWorkouts(dates, workouts);
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, Object>> loader)
    {
        workoutHistoryRecyclerViewAdapter.addWorkouts(null, null);
    }

    @Override
    public void onRefresh()
    {
        getSupportLoaderManager().restartLoader(WORKOUT_LOADER_ID, null, this);
    }

    @Override
    public void onWorkoutClick(Workout workout)
    {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra(Workout.NEW_WORKOUT_KEY, false);
        intent.putExtra(Workout.WORKOUT_KEY, workout);
        startActivityTransition(intent);
    }
}