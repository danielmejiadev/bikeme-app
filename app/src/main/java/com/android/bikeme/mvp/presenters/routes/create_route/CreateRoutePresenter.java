package com.android.bikeme.mvp.presenters.routes.create_route;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Route;

import java.util.ArrayList;

/**
 * Created by Daniel on 01/05/2017.
 */
public interface CreateRoutePresenter {

    interface OnFinishedSaveRouteCallback {
        void onFinishedSaveRoute(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved);
        void onErrorSaveRoute();
    }

    void onSaveRoute(Route route);
    void onExitButtonClick();
}
