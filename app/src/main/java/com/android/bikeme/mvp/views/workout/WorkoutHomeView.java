package com.android.bikeme.mvp.views.workout;

import android.location.Location;

import com.android.bikeme.classes.Workout;
/**
 * Created by Daniel on 1 sep 2017.
 */
public interface WorkoutHomeView {

        void onGoToWorkout(Workout workout);
        void onGoToWorkoutHistory();
        void updateDistance(int totalDistanceMeters);
        void updateLocation(Location location);
        void updateStopWatch(int durationSeconds);
        void setWorkoutStateRunning();
        void setWorkoutStatePause();
        void setWorkoutStateRestartDefault();
}
