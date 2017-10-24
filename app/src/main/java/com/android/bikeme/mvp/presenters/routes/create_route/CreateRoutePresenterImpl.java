package com.android.bikeme.mvp.presenters.routes.create_route;

import android.content.Context;

import com.android.bikeme.R;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.mvp.interactors.routes.create_route.CreateRouteInteractor;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.views.routes.create_route.CreateRouteView;

import java.util.ArrayList;

/**
 * Created by Daniel on 01/05/2017.
 */
public class CreateRoutePresenterImpl implements CreateRoutePresenter, CreateRoutePresenter.OnFinishedSaveRouteCallback {

    public CreateRouteView createRouteView;
    public CreateRouteInteractor createRouteInteractor;
    public Context context;

    public CreateRoutePresenterImpl(Context context, CreateRouteView createRouteView, CreateRouteInteractor createRouteInteractor)
    {
        this.context = context;
        this.createRouteView=createRouteView;
        this.createRouteInteractor=createRouteInteractor;
    }

    @Override
    public void onSaveRoute(Route route)
    {
        createRouteView.showProgress(context.getString(R.string.saving_text));
        createRouteInteractor.saveRoute(route,this);
    }

    @Override
    public void onExitButtonClick()
    {
        createRouteView.validateExit();
    }

    @Override
    public void onFinishedSaveRoute(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved)
    {
        createRouteView.hideProgress();
        createRouteView.showMessage(context.getString(R.string.create_route_sucessfully));
        createRouteView.showChallengesLevelsAchieved(challengesAchieved,levelsAchieved);
    }

    @Override
    public void onErrorSaveRoute()
    {
        createRouteView.hideProgress();
        createRouteView.showMessage(context.getString(R.string.create_route_error));
        createRouteView.showChallengesLevelsAchieved(new ArrayList<Challenge>(),new ArrayList<Integer>());
    }
}
