package com.android.bikeme.mvp.views.events;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 11 ago 2017.
 */
public class EventResponseLoader {

    private ArrayList<String> differentDates;
    private ArrayList<ArrayList<HashMap<String,Object>>> events;

    public EventResponseLoader(ArrayList<String> differentDates, ArrayList<ArrayList<HashMap<String, Object>>> events)
    {
        this.differentDates = differentDates;
        this.events = events;
    }

    public ArrayList<String> getDifferentDates()
    {
        return differentDates;
    }

    public ArrayList<ArrayList<HashMap<String, Object>>> getEvents()
    {
        return events;
    }
}