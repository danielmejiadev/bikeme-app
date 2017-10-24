package com.android.bikeme.mvp.interactors.routes.create_route;

import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenter;

/**
 * Created by Daniel on 01/05/2017.
 */
public interface CreateRouteInteractor  {
    void saveRoute(Route route, CreateRoutePresenter.OnFinishedSaveRouteCallback callbackPresenter);
}
