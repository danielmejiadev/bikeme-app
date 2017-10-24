package com.android.bikeme.mvp.interactors.events;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenter;

/**
 * Created by Daniel on 13 ago 2017.
 */
public interface EventDetailInteractor {

    void getPointForTravel(String idRoute, EventDetailPresenter.onEventTravelCallback onEventTravelCallback);
    void saveGuestEvent(Guest guest);

}
