package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.android.bikeme.classes.Point;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.RouteModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Daniel on 21/03/2017.
 */
public class SyncRoute {

    private static final String TAG = Synchronization.class.getSimpleName();

    public ContentResolver contentResolver;
    public RouteModel routeModel;

    public SyncRoute(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.routeModel = new RouteModel(contentResolver);
    }

    /** Insert, update or delete routes fetched from server */
    public  void remoteToLocalDatabaseSyncRoute(ArrayList<Route> routesRemote)
    {
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<String, Route> routeRemoteHashMap = new LinkedHashMap<>();
        for (Route routeRemote: routesRemote)
        {
            routeRemoteHashMap.put(routeRemote.getUid(), routeRemote);
        }

        Uri uri = DataBaseContract.Route.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;

        Log.i(TAG, "Se encontraron " + cursor.getCount() + " rutas en el telefono que ya estan en el servidor");

        while (cursor.moveToNext())
        {
            String idRoute = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
            Route routeMatchLocalRemote = routeRemoteHashMap.get(idRoute);

            if (routeMatchLocalRemote != null)
            {
                routeRemoteHashMap.remove(idRoute);
            }
        }
        cursor.close();

        Log.i(TAG, "Se encontraron " + routeRemoteHashMap.size() + " rutas en el servidor que no estan en el telefono");
        for (Route routeRemote : routeRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción de la ruta: " + routeRemote.getName());
            databaseOperations.add(routeModel.insertOperationRoute(routeRemote));

            for(Point pointRemote: routeRemote.getPoints())
            {
                Log.i(TAG, "Programando inserción del punto: " + pointRemote.getId());
                databaseOperations.add(routeModel.insertOperationPoint(pointRemote));
            }
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            routeModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.Route.URI_CONTENT, null, false);
            contentResolver.notifyChange(DataBaseContract.Point.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para las rutas");
        }
    }
}