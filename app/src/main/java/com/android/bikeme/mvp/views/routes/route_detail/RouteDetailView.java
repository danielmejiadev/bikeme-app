package com.android.bikeme.mvp.views.routes.route_detail;

import com.android.bikeme.application.BaseActivityView;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;

import java.util.ArrayList;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface RouteDetailView extends BaseActivityView {

    void setRatingDone(Rating rating);
    void showRatingValue(String rating);
    void onGoToMapRoute();
    void showProgressDialog();
    void hideProgressDialog();
}