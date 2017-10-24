package com.android.bikeme.mvp.views.events;

import com.android.bikeme.classes.Event;

import java.util.HashMap;

/**
 * Created by Daniel on 11 ago 2017.
 */
public interface EventView {

    void navigateToEventDetail(HashMap<String, Object> eventToShow);
}
