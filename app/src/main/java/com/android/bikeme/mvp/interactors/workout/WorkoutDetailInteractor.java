package com.android.bikeme.mvp.interactors.workout;

import com.android.bikeme.classes.Workout;
import com.android.bikeme.mvp.presenters.workout.WorkoutDetailPresenter;

/**
 * Created by Daniel on 4 sep 2017.
 */
public interface WorkoutDetailInteractor {

    void saveUserWorkout(Workout workout, WorkoutDetailPresenter.onSaveUserWorkoutCallback onSaveUserWorkoutCallback);
}
