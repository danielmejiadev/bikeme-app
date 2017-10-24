package com.android.bikeme.mvp.interactors;

import com.android.bikeme.classes.Problem;
import com.android.bikeme.mvp.presenters.BikeMePresenter;
import com.android.bikeme.mvp.views.BikeMeActivity;

/**
 * Created by Daniel on 17 ago 2017.
 */
public interface BikeMeInteractor {
    void getUser(String userID, BikeMePresenter.onUserProfileCallback onUserProfileCallback);
    void saveProblem(Problem problem);

}
