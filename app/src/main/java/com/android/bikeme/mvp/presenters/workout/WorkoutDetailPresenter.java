package com.android.bikeme.mvp.presenters.workout;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Workout;

import java.util.ArrayList;

/**
 * Created by Daniel on 4 sep 2017.
 */
public interface WorkoutDetailPresenter {

    interface onSaveUserWorkoutCallback
    {
       void onUserWorkoutSaved(ArrayList<Challenge> challengesAchieved,ArrayList<Integer> levelsAchieved);
    }
    void onSaveUserWorkout(Workout workout);
}
