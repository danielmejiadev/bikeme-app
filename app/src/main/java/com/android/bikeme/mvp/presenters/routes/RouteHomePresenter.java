package com.android.bikeme.mvp.presenters.routes;

import com.android.bikeme.classes.Route;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface RouteHomePresenter {

    interface onRouteDetailCallback {
        void onFinished(Route route);
    }

    void createRouteButtonClicked();
    void routeDetailItemClicked(String idRoute);
}
