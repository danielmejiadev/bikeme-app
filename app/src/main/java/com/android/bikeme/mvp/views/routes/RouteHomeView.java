package com.android.bikeme.mvp.views.routes;

import com.android.bikeme.classes.Route;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface RouteHomeView {

    void navigateToCreateRoute();
    void navigateToRouteDetail(Route route);
    void showError(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
