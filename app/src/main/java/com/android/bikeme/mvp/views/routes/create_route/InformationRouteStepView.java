package com.android.bikeme.mvp.views.routes.create_route;

/**
 * Created by Daniel on 01/05/2017.
 */
public interface InformationRouteStepView {
    void showErrorRouteName();
    void showErrorRouteRating();
    void showErrorRouteDescription();
    void showErrorRouteDeparture();
    void showErrorRouteArrival();
    void showRatingValue(String rating);
    void allowSave(boolean allow);
}
