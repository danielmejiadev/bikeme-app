package com.android.bikeme.mvp.presenters.workout;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.mvp.interactors.workout.WorkoutDetailInteractor;
import com.android.bikeme.mvp.views.workout.workout_detail.WorkoutDetailView;

import java.util.ArrayList;

/**
 * Created by Daniel on 4 sep 2017.
 */
public class WorkoutDetailPresenterImpl implements WorkoutDetailPresenter, WorkoutDetailPresenter.onSaveUserWorkoutCallback {


    private WorkoutDetailInteractor workoutDetailInteractor;
    private WorkoutDetailView workoutDetailView;

    public WorkoutDetailPresenterImpl(WorkoutDetailView workoutDetailView, WorkoutDetailInteractor workoutDetailInteractor)
    {
        this.workoutDetailView = workoutDetailView;
        this.workoutDetailInteractor = workoutDetailInteractor;
    }

    @Override
    public void onSaveUserWorkout(Workout workout)
    {
        workoutDetailView.showProgressDialog();
        workoutDetailInteractor.saveUserWorkout(workout,this);
    }

    @Override
    public void onUserWorkoutSaved(ArrayList<Challenge> challengesAchieved,ArrayList<Integer> levelsAchieved)
    {
        workoutDetailView.hideProgressDialog();
        workoutDetailView.showChallengesLevelsAchieved(challengesAchieved,levelsAchieved);
    }
}