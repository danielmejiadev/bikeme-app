package com.android.bikeme.mvp.interactors.routes;

import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;

/**
 * Created by Daniel on 20 may 2017.
 */
public interface RouteHomeInteractor {
    void getRoute(String idRoute, RouteHomePresenter.onRouteDetailCallback callback);
}
