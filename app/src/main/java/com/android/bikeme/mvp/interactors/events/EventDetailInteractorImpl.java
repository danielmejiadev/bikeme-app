package com.android.bikeme.mvp.interactors.events;

import android.os.AsyncTask;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.EventModel;
import com.android.bikeme.databaselocal.models.GuestModel;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenter;

import java.util.ArrayList;

/**
 * Created by Daniel on 13 ago 2017.
 */
public class EventDetailInteractorImpl implements EventDetailInteractor{

    RouteModel routeModel;
    GuestModel guestModel;
    public  EventDetailInteractorImpl(RouteModel routeModel, GuestModel guestModel)
    {
        this.routeModel = routeModel;
        this.guestModel = guestModel;

    }


    @Override
    public void getPointForTravel(final String idRoute, final EventDetailPresenter.onEventTravelCallback onEventTravelCallback)
    {
        AsyncTask<Void, Void, ArrayList<Point>> taskDirectionRoute = new AsyncTask<Void, Void, ArrayList<Point>>()
        {
            @Override
            protected  ArrayList<Point>  doInBackground(Void... params)
            {
                return  routeModel.getPointsByRoute(idRoute);
            }

            @Override
            protected void onPostExecute(ArrayList<Point> points)
            {
                onEventTravelCallback.onFinished(points);
            }

        };
        taskDirectionRoute.execute();
    }

    @Override
    public void saveGuestEvent(Guest guest)
    {
        guestModel.saveGuestEvent(guest);
    }
}
