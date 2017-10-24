package com.android.bikeme.mvp.presenters.routes;

import android.content.Context;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.interactors.routes.RouteHomeInteractor;
import com.android.bikeme.mvp.views.routes.RouteHomeView;

/**
 * Created by Daniel on 23/03/2017.
 */
public class RouteHomePresenterImpl implements RouteHomePresenter, RouteHomePresenter.onRouteDetailCallback {

    private RouteHomeView routeHomeView;
    private RouteHomeInteractor routeHomeInteractor;
    private Context context;

    public RouteHomePresenterImpl(RouteHomeView routeHomeView, Context context, RouteHomeInteractor routeHomeInteractor)
    {
        this.routeHomeView = routeHomeView;
        this.context = context;
        this.routeHomeInteractor=routeHomeInteractor;
    }

    @Override
    public void createRouteButtonClicked()
    {
        if(BikeMeApplication.getInstance().isOnlineWifi())
        {
            routeHomeView.navigateToCreateRoute();
        }
        else
        {
            routeHomeView.showError(context.getString(R.string.check_connection_text));
        }
    }

    @Override
    public void routeDetailItemClicked(String idRoute)
    {
        routeHomeView.showProgressDialog();
        routeHomeInteractor.getRoute(idRoute, this);
    }

    @Override
    public void onFinished(Route route)
    {
        routeHomeView.hideProgressDialog();
        routeHomeView.navigateToRouteDetail(route);
    }
}