package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.GuestModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class SyncGuest {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncGuest.class.getSimpleName();

    private ContentResolver contentResolver;
    private GuestModel guestModel;

    public SyncGuest(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.guestModel = new GuestModel(contentResolver);
    }

    /** Insert, update or delete guests fetched from server */
    public  void remoteToLocalDatabaseSyncGuest(ArrayList<Guest> guestsRemote)
    {
        ArrayList<Guest> guestsLocal = guestModel.getGuests();
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<LinkedHashMap<String, String>, Guest> guestRemoteHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> keyGuest;
        for (Guest guestRemote: guestsRemote)
        {
            keyGuest = new LinkedHashMap<>();
            keyGuest.put("user",guestRemote.getUser());
            keyGuest.put("event", guestRemote.getEvent());
            guestRemoteHashMap.put(keyGuest, guestRemote);
        }
        Log.i(TAG, "Se encontraron " + guestsLocal.size() + " guests en el telefono");

        for (Guest guestLocal : guestsLocal)
        {
            keyGuest = new LinkedHashMap<>();
            keyGuest.put("user",guestLocal.getUser());
            keyGuest.put("event",guestLocal.getEvent());
            Guest guestMatchLocalRemote = guestRemoteHashMap.get(keyGuest);

            if (guestMatchLocalRemote != null)
            {
                guestRemoteHashMap.remove(keyGuest);
                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                Date dateLocalUpdated = bikeMeApplication.getDateTime(guestLocal.getDate());
                Date dateRemoteUpdated = bikeMeApplication.getDateTime(guestMatchLocalRemote.getDate());

                if(dateRemoteUpdated.after(dateLocalUpdated))
                {
                    Log.i(TAG, "Programando actualización del guest: User: " + guestLocal.getUser() + " Event: " + guestLocal.getEvent());
                    databaseOperations.add(guestModel.updateOperationGuest(guestLocal.getId(),guestMatchLocalRemote));
                }
                else
                {
                    Log.i(TAG, "No hay acciones para este Guest: User: " + guestLocal.getUser() + " Event: " + guestLocal.getEvent());
                }
            }
            // De ser necesario else: Si esta pendiente por sincronizar cambios en el server no hacer nada, sino eliminarlo

        }

        Log.i(TAG, "Se encontraron " + guestRemoteHashMap.size() + " guests en el servidor que no estan en el telefono");
        for (Guest guestRemote : guestRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción del guest: " + guestRemote.getId());
            databaseOperations.add(guestModel.insertOperationGuest(guestRemote));
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            guestModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.Guest.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los guests");
        }
    }


    /** update local guests in the server */
    public void localToRemoteGuestSync()
    {
        Uri uri = DataBaseContract.Guest.URI_CONTENT;
        int results = guestModel.changeToSync(uri,contentResolver);
        Log.i(TAG, "Guests puestos en cola de sincronizacion:" + results);

        ArrayList<Guest> guestsPendingForInsert = guestModel.getGuestForSync();
        Log.i(TAG, "Se encontraron " +  guestsPendingForInsert.size() + " guests para insertar en el servidor");

        syncGuestsPendingForInsert(guestsPendingForInsert);
    }

    /**  Post database server  all ratings pending for insert */
    private void syncGuestsPendingForInsert(final ArrayList<Guest> guestsPendingForInsert)
    {
        if (guestsPendingForInsert.size() > 0)
        {
            RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
            EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
            Call<ArrayList<Guest>> guestsInsertCall = endPointsApi.insertGuests(guestsPendingForInsert);
            guestsInsertCall.enqueue(new Callback<ArrayList<Guest>>()
            {
                @Override
                public void onResponse(Call<ArrayList<Guest>> call, Response<ArrayList<Guest>> response)
                {
                    if(response.isSuccessful())
                    {
                        ArrayList<Guest> guestInserted = response.body();
                        for (int i = 0; i < guestInserted.size(); i++)
                        {
                            Guest guestRemote = guestInserted.get(i);
                            if (guestRemote == null)
                            {
                                Log.e(TAG, "Error al insertar guests");
                            }
                            else
                            {
                                Guest guestPendingForInsert = guestsPendingForInsert.get(i);
                                guestModel.changeToStateOkay(DataBaseContract.Guest.buildUriGuest(String.valueOf(guestPendingForInsert.getId())),contentResolver);
                                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                                Date dateLocalUpdated = bikeMeApplication.getDateTime(guestPendingForInsert.getDate());
                                Date dateRemoteUpdated = bikeMeApplication.getDateTime(guestRemote.getDate());
                                if(dateRemoteUpdated.after(dateLocalUpdated))
                                {
                                    Log.i(TAG, "Guest remoto mas actualizado o igual");

                                }
                                else
                                {
                                    Log.i(TAG, "Guest remoto actualizado correctamente: User: " +  guestRemote.getUser() + " Event: " + guestRemote.getEvent());
                                }
                            }
                        }
                    }
                    else
                    {
                        Log.i(TAG,"Respuesta fallida al insertar guest " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Guest>> call, Throwable t)
                {
                Log.e(TAG, "on Failure insert ratings : " + t.toString());

                }
            });
        }
        else
        {
            Log.i(TAG, "No se requiere sincronizacion de guest para insertar");
        }
    }
}