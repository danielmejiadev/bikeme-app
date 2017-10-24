package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaseremote.syncronization.Synchronization;
import com.android.bikeme.mvp.views.BikeMeActivity;

import java.util.ArrayList;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class GuestModel extends BikeMeModel{

    public ContentResolver contentResolver;

    public GuestModel(ContentResolver contentResolver)
    {
        this.contentResolver= contentResolver;
    }

    public void saveGuestEvent(Guest guest)
    {
        ContentValues valuesGuest = new ContentValues();

        valuesGuest.put(DataBaseContract.Guest.COLUMN_EVENT_ID, guest.getEvent());
        valuesGuest.put(DataBaseContract.Guest.COLUMN_USER_ID, guest.getUser());
        valuesGuest.put(DataBaseContract.Guest.COLUMN_STATE, guest.getState());
        valuesGuest.put(DataBaseContract.COLUMN_DATE, guest.getDate());
        valuesGuest.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_PENDING);

        contentResolver.insert(DataBaseContract.Guest.URI_CONTENT, valuesGuest);
        contentResolver.notifyChange(DataBaseContract.Guest.URI_CONTENT,null,false);

        Synchronization.syncNow(Synchronization.LOCAL_TO_REMOTE_DATABASE_SYNC);
    }

    public ArrayList<Guest> getGuests()
    {
        Uri uri = DataBaseContract.Guest.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null,null,null);
        assert  cursor != null;
        ArrayList <Guest> guests = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                guests.add(getGuest(cursor));
            }
        }
        cursor.close();
        return  guests;
    }

    public ArrayList<Guest> getGuestForSync()
    {
        Uri uri = DataBaseContract.Guest.URI_CONTENT;
        String selectionPendingForInsert = DataBaseContract.COLUMN_INSERT_STATE +"=? AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgsPendingForInsert = new String[]{DataBaseContract.INSERT_PENDING+"", DataBaseContract.STATE_SYNC+""};
        Cursor cursor = contentResolver.query(uri, null, selectionPendingForInsert, selectionArgsPendingForInsert, null);
        assert  cursor != null;
        ArrayList <Guest> guests = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                guests.add(getGuest(cursor));
            }
        }
        cursor.close();
        return  guests;
    }

    public Guest getGuest(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Guest._ID));
        String userId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Guest.COLUMN_USER_ID));
        String eventId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Guest.COLUMN_EVENT_ID));
        int state = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Guest.COLUMN_STATE));
        String date = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_DATE));

        Guest guest = new Guest();
        guest.setId(id);
        guest.setEvent(eventId);
        guest.setUser(userId);
        guest.setState(state);
        guest.setDate(date);

        return guest;
    }

    public ContentProviderOperation insertOperationGuest(Guest guest)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Guest.URI_CONTENT)
                .withValue(DataBaseContract.Guest.COLUMN_USER_ID, guest.getUser())
                .withValue(DataBaseContract.Guest.COLUMN_EVENT_ID, guest.getEvent())
                .withValue(DataBaseContract.Guest.COLUMN_STATE, guest.getState())
                .withValue(DataBaseContract.COLUMN_DATE,guest.getDate())
                .build();
    }

    public ContentProviderOperation updateOperationGuest(int oldGuestId, Guest newGuest)
    {
        Uri existingUri = DataBaseContract.Guest.buildUriGuest(String.valueOf(oldGuestId));
        return ContentProviderOperation.newUpdate(existingUri)
                .withValue(DataBaseContract.Guest.COLUMN_STATE, newGuest.getState())
                .withValue(DataBaseContract.COLUMN_DATE, newGuest.getDate())
                .withValue(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_OK)
                .withValue(DataBaseContract.COLUMN_STATE_SYNC, DataBaseContract.STATE_OK)
                .build();
    }
}