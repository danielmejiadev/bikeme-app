package com.android.bikeme.mvp.presenters.routes.route_detail;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;

import java.util.ArrayList;

/**
 * Created by Daniel on 22/04/2017.
 */
public interface RouteDetailPresenter {

    interface OnFinishedSaveRatingCallback {
        void onFinishedSaveRating(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved, Rating rating);
    }

    void onClickFabButton();
    void onRatingChangeClick(float rating);
    void onSaveRatingRoute(Rating rating);
}
