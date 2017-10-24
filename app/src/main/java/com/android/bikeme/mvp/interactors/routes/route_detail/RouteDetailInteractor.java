package com.android.bikeme.mvp.interactors.routes.route_detail;

import com.android.bikeme.classes.Rating;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;

/**
 * Created by Daniel on 18 oct 2017.
 */
public interface RouteDetailInteractor {
    void saveRatingRoute(Rating rating, RouteDetailPresenter.OnFinishedSaveRatingCallback onFinishedSaveRatingCallback);
}
