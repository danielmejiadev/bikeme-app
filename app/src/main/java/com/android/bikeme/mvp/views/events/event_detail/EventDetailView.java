package com.android.bikeme.mvp.views.events.event_detail;

import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.User;

import java.util.ArrayList;

/**
 * Created by Daniel on 11 ago 2017.
 */
public interface EventDetailView {
    void navigateToEventDetailGuests(ArrayList<Guest> guests, ArrayList<User> users);
    void navigateToEventDetailTravel(ArrayList<Point> points);


}
