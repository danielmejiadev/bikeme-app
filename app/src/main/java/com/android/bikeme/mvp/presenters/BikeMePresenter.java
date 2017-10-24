package com.android.bikeme.mvp.presenters;

import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface BikeMePresenter {

    interface onUserProfileCallback {
        void onFinished(User user, int totalPoints);
    }
    void itemSelectedBottomNavigation(int fragmentSelected);
    void onItemUserProfile(String userId);
    void saveProblem(Problem problem);
}
