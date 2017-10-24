package com.android.bikeme.mvp.presenters.events.event_detail;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.User;
import com.android.bikeme.mvp.interactors.events.EventDetailInteractor;
import com.android.bikeme.mvp.views.events.event_detail.EventDetailView;

import java.util.ArrayList;

/**
 * Created by Daniel on 13 ago 2017.
 */
public class EventDetailPresenterImpl implements EventDetailPresenter, EventDetailPresenter.onEventTravelCallback {

    EventDetailView eventDetailView;
    EventDetailInteractor eventDetailInteractor;

    public EventDetailPresenterImpl(EventDetailView eventDetailView, EventDetailInteractor eventDetailInteractor)
    {
        this.eventDetailView=eventDetailView;
        this.eventDetailInteractor = eventDetailInteractor;
    }


    @Override
    public void onGoToEventDetailGuests(ArrayList<Guest> guests, ArrayList<User> users)
    {
        eventDetailView.navigateToEventDetailGuests(guests,users);
    }

    @Override
    public void onSaveGuest(Guest guest)
    {
        eventDetailInteractor.saveGuestEvent(guest);
    }

    @Override
    public void onGoToEventTravel(String routeId)
    {
        eventDetailInteractor.getPointForTravel(routeId,this);
    }

    @Override
    public void onFinished(ArrayList<Point> points)
    {
        eventDetailView.navigateToEventDetailTravel(points);
    }

    @Override
    public void onError() {

    }
}
