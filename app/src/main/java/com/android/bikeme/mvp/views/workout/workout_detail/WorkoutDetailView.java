package com.android.bikeme.mvp.views.workout.workout_detail;

import com.android.bikeme.application.BaseActivityView;
import com.android.bikeme.classes.Challenge;

import java.util.ArrayList;

/**
 * Created by Daniel on 11 sep 2017.
 */
public interface WorkoutDetailView extends BaseActivityView {

    void showProgressDialog();
    void hideProgressDialog();
}
