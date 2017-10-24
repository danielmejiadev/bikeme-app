package com.android.bikeme.mvp.presenters;

import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.User;
import com.android.bikeme.mvp.interactors.BikeMeInteractor;
import com.android.bikeme.mvp.views.BikeMeView;

/**
 * Created by Daniel on 21/04/2017.
 */
public class BikeMePresenterImpl implements  BikeMePresenter, BikeMePresenter.onUserProfileCallback{

    private BikeMeView bikeMeView;
    private BikeMeInteractor bikeMeInteractor;

    public BikeMePresenterImpl(BikeMeView bikeMeView, BikeMeInteractor bikeMeInteractor)
    {
        this.bikeMeView = bikeMeView;
        this.bikeMeInteractor = bikeMeInteractor;
    }

    @Override
    public void itemSelectedBottomNavigation(int fragmentSelected)
    {
        bikeMeView.fragmentSelected(fragmentSelected);
    }

    @Override
    public void onItemUserProfile(String userId)
    {
        bikeMeInteractor.getUser(userId,this);
    }

    @Override
    public void saveProblem(Problem problem)
    {
        bikeMeInteractor.saveProblem(problem);
    }

    @Override
    public void onFinished(User user,int totalPoints)
    {
        bikeMeView.navigateToUserProfile(user, totalPoints);
    }
}
