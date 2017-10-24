package com.android.bikeme.mvp.interactors.routes;

import android.os.AsyncTask;

import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;

/**
 * Created by Daniel on 20 may 2017.
 */
public class RouteHomeInteractorImpl implements RouteHomeInteractor {

    private RouteModel routeModel;

    public RouteHomeInteractorImpl(RouteModel routeModel)
    {
        this.routeModel = routeModel;
    }

    @Override
    public void getRoute(final String idRoute, final RouteHomePresenter.onRouteDetailCallback callback)
    {
        AsyncTask<Void, Void, Route> taskDirectionRoute = new AsyncTask<Void, Void, Route>()
        {
            @Override
            protected Route  doInBackground(Void... params)
            {
                return  routeModel.getRouteById(idRoute);
            }

            @Override
            protected void onPostExecute(Route route)
            {
               callback.onFinished(route);
            }

        };
        taskDirectionRoute.execute();
    }
}
