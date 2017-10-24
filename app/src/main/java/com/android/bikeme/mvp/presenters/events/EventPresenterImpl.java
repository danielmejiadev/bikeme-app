package com.android.bikeme.mvp.presenters.events;

import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Point;
import com.android.bikeme.mvp.views.events.EventFragment;
import com.android.bikeme.mvp.views.events.EventView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 12 ago 2017.
 */
public class EventPresenterImpl implements  EventPresenter{

    EventView eventView;

    public EventPresenterImpl(EventView eventView)
    {
        this.eventView = eventView;
    }

    @Override
    public void onGoToEventDetail(HashMap<String, Object> eventToShow)
    {
        eventView.navigateToEventDetail(eventToShow);
    }
}
