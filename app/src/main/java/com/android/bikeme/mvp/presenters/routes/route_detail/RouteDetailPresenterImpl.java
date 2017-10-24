package com.android.bikeme.mvp.presenters.routes.route_detail;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.mvp.interactors.routes.route_detail.RouteDetailInteractor;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailView;

import java.util.ArrayList;

/**
 * Created by Daniel on 22/04/2017.
 */
public class RouteDetailPresenterImpl implements RouteDetailPresenter, RouteDetailPresenter.OnFinishedSaveRatingCallback{

    RouteDetailView routeDetailView;
    RouteDetailInteractor routeDetailInteractor;
    public RouteDetailPresenterImpl(RouteDetailView routeDetailView, RouteDetailInteractor routeDetailInteractor)
    {
        this.routeDetailView=routeDetailView;
        this.routeDetailInteractor=routeDetailInteractor;
    }

    @Override
    public void onClickFabButton()
    {
        routeDetailView.onGoToMapRoute();
    }

    @Override
    public void onRatingChangeClick(float rating)
    {
        routeDetailView.showRatingValue(String.valueOf(rating));
    }

    @Override
    public void onSaveRatingRoute(Rating rating)
    {
        routeDetailView.showProgressDialog();
        routeDetailInteractor.saveRatingRoute(rating,this);
    }

    @Override
    public void onFinishedSaveRating(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved, Rating rating)
    {
        routeDetailView.hideProgressDialog();
        routeDetailView.setRatingDone(rating);
        routeDetailView.showChallengesLevelsAchieved(challengesAchieved,levelsAchieved);
    }
}
