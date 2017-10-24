package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.android.bikeme.classes.Event;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.EventModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class SyncEvent {

    private static final String TAG = Synchronization.class.getSimpleName();

    public ContentResolver contentResolver;
    public EventModel eventModel;

    public SyncEvent(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.eventModel = new EventModel(contentResolver);
    }

    /** Insert, update or delete events fetched from server */
    public  void remoteToLocalDatabaseSyncEvent(ArrayList<Event> eventsRemote)
    {
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<String, Event> eventRemoteHashMap = new LinkedHashMap<>();
        for (Event eventRemote: eventsRemote)
        {
            eventRemoteHashMap.put(eventRemote.getUid(), eventRemote);
        }

        Uri uri = DataBaseContract.Event.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;

        Log.i(TAG, "Se encontraron " + cursor.getCount() + " eventos en el telefono que ya estan en el servidor");

        while (cursor.moveToNext())
        {
            String idEvent = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
            Event eventMatchLocalRemote = eventRemoteHashMap.get(idEvent);

            if (eventMatchLocalRemote != null)
            {
                eventRemoteHashMap.remove(idEvent);
            }
        }
        cursor.close();

        Log.i(TAG, "Se encontraron " + eventRemoteHashMap.size() + " eventos en el servidor que no estan en el telefono");
        for (Event eventRemote : eventRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción del evento: " + eventRemote.getName());
            databaseOperations.add(eventModel.insertOperationEvent(eventRemote));
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            eventModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.Event.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los eventos");
        }
    }
}