package com.android.bikeme.mvp.views.routes.create_route;

import com.android.bikeme.application.BaseActivityView;
import com.android.bikeme.classes.Challenge;

import java.util.ArrayList;

/**
 * Created by Daniel on 01/05/2017.
 */
public interface CreateRouteView extends BaseActivityView {
    void showHelp();
    void validateExit();
    void showMessage(String message);
    void showProgress(String message);
    void hideProgress();
}
