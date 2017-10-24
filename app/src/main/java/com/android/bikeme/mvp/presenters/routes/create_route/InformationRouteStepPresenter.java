package com.android.bikeme.mvp.presenters.routes.create_route;

/**
 * Created by Daniel on 01/05/2017.
 */
public interface InformationRouteStepPresenter {

    void onRatingChangeClick(float rating);
    void onCompleteClick(String name, double rating, String description, String departure, String arrival, int level);

}
