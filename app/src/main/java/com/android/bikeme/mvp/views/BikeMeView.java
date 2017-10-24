package com.android.bikeme.mvp.views;

import android.support.v4.app.Fragment;

import com.android.bikeme.classes.User;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface BikeMeView {


    void fragmentSelected(int fragmentSelect);
    void navigateToFragment(Fragment fragment);

    void navigateToWorkout();
    void navigateToUserProfile(User user, int totalPoints);
    void navigateToSignIn();
}
