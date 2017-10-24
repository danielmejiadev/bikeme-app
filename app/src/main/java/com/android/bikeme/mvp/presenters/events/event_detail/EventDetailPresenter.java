package com.android.bikeme.mvp.presenters.events.event_detail;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.User;

import java.util.ArrayList;

/**
 * Created by Daniel on 13 ago 2017.
 */
public interface EventDetailPresenter {

    void onGoToEventDetailGuests(ArrayList<Guest> guests, ArrayList<User> users);
    interface onEventTravelCallback {
        void onFinished(ArrayList<Point> points);
        void onError();
    }
    void onGoToEventTravel(String routeId);
    void onSaveGuest(Guest guest);


}
