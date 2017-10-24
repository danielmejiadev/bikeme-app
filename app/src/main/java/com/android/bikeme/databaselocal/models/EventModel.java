package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.mvp.views.events.EventResponseLoader;


import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class EventModel extends BikeMeModel{

    public static final String GUESTS_KEY = "guests";
    public static final String EVENT_KEY = "event";
    public static final String HOUR_KEY = "hour";
    public static final String DATE_KEY = "date";
    public static final String DEPARTURE_KEY = "departure";
    public static final String ARRIVAL_KEY = "arrival";
    public static final String DISTANCE_KEY = "distance";
    public static final String GUEST_KEY = "guest";
    public static final String USERS_KEY = "users";
    public static final String EVENTS_TO_SHOW_KEY = "eventToShow";
    private static final String GROUP_INDEX = "pos";

    private ContentResolver contentResolver;
    private UserModel userModel;
    private GuestModel guestModel;

    public EventModel(ContentResolver contentResolver)
    {
        this.contentResolver= contentResolver;
        this.userModel = new UserModel(contentResolver);
        this.guestModel = new GuestModel(contentResolver);
    }

    public EventResponseLoader getEventsToShow(String currentUserUid)
    {
        Uri uri = DataBaseContract.Event.buildUriEventGroupByDate();
        String[] selectionArgs = new String[]{currentUserUid};
        Cursor cursor = contentResolver.query(uri, null, null, selectionArgs, null);

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, Object>>> events = new ArrayList<>();

        HashMap<Integer, ArrayList<HashMap<String, Object>>> groups = new LinkedHashMap<>();
        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        assert cursor != null;
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                String uid = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
                String name = cursor.getString(cursor.getColumnIndex(DataBaseContract.Event.COLUMN_NAME_EVENT));
                String route = cursor.getString(cursor.getColumnIndex(DataBaseContract.Event.COLUMN_ROUTE_ID));
                String dateString = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_DATE));
                int groupIndex = cursor.getInt(cursor.getColumnIndex(EventModel.GROUP_INDEX));
                int guest = cursor.getInt(cursor.getColumnIndex(EventModel.GUEST_KEY));
                String departure = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DEPARTURE));
                String arrival = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_ARRIVAL));
                double distance = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DISTANCE));

                HashMap<String, ArrayList> guestUsers = getGuestsUsersByEvent(uid);
                @SuppressWarnings("unchecked")
                ArrayList<Guest> guests = guestUsers.get(EventModel.GUESTS_KEY);
                @SuppressWarnings("unchecked")
                ArrayList<User> users =  guestUsers.get(EventModel.USERS_KEY);

                Event event = new Event();
                event.setUid(uid);
                event.setName(name);
                event.setRoute(route);
                event.setDate(dateString);
                event.setGuests(guests);

                Date date = bikeMeApplication.getDateTime(dateString);
                String longDate = bikeMeApplication.getLongDate(date);

                HashMap<String, Object> eventToShow = new LinkedHashMap<>();
                eventToShow.put(EVENT_KEY,event);
                eventToShow.put(HOUR_KEY,bikeMeApplication.getHourAmPm(date));
                eventToShow.put(DATE_KEY,longDate);
                eventToShow.put(DEPARTURE_KEY, departure);
                eventToShow.put(ARRIVAL_KEY, arrival);
                eventToShow.put(DISTANCE_KEY, distance);
                eventToShow.put(GUEST_KEY, guest);
                eventToShow.put(USERS_KEY, users);

                ArrayList<HashMap<String, Object>> group = groups.get(groupIndex);
                if(group==null)
                {
                    dates.add(longDate);
                    group = new ArrayList<>();
                    group.add(eventToShow);
                    groups.put(groupIndex,group);
                }
                else
                {
                    group.add(eventToShow);
                }
            }
        }

        for(Map.Entry<Integer, ArrayList<HashMap<String, Object>>> entry : groups.entrySet())
        {
            events.add(entry.getValue());
        }
        cursor.close();
        return new EventResponseLoader(dates,events);
    }

    public HashMap<String,ArrayList> getGuestsUsersByEvent(String idEvent)
    {
        Uri uri = DataBaseContract.Event.buildUriEventGuests(idEvent);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        assert  cursor != null;
        ArrayList<Guest> guests = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                guests.add(guestModel.getGuest(cursor));
                users.add(userModel.getUser(cursor));
            }
        }
        cursor.close();

        HashMap<String,ArrayList> usersGuests = new HashMap<>();
        usersGuests.put(EventModel.USERS_KEY,users);
        usersGuests.put(EventModel.GUESTS_KEY,guests);
        return  usersGuests;
    }

    public ContentProviderOperation insertOperationEvent(Event event)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Event.URI_CONTENT)
                .withValue(DataBaseContract.COLUMN_UID,event.getUid())
                .withValue(DataBaseContract.Event.COLUMN_NAME_EVENT, event.getName())
                .withValue(DataBaseContract.Event.COLUMN_ROUTE_ID, event.getRoute())
                .withValue(DataBaseContract.COLUMN_DATE, event.getDate())
                .build();
    }
 }