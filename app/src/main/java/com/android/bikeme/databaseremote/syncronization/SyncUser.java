package com.android.bikeme.databaseremote.syncronization;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.UserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 20/03/2017.
 */
public class SyncUser {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncUser.class.getSimpleName();

    public ContentResolver contentResolver;
    public UserModel userModel;

    public SyncUser(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.userModel = new UserModel(contentResolver);
    }

    /** Insert, update or delete users fetched from server */
    public  void remoteToLocalDatabaseSyncUser(ArrayList<User> usersRemote)
    {
        ArrayList<User> usersLocal = userModel.getUsers();
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<String, User> usersRemoteHashMap = new LinkedHashMap<>();
        for (User userRemote: usersRemote)
        {
            usersRemoteHashMap.put(userRemote.getUid(), userRemote);
        }

        Log.i(TAG, "Se encontraron " + usersLocal.size() + " usuarios en el telefono");
        for(User userLocal : usersLocal)
        {
            User userMatchLocalRemote = usersRemoteHashMap.get(userLocal.getUid());

            if (userMatchLocalRemote != null)
            {
                usersRemoteHashMap.remove(userLocal.getUid());
                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                Date dateLocalUpdated = bikeMeApplication.getDateTime(userLocal.getUpdated());
                Date dateRemoteUpdated = bikeMeApplication.getDateTime(userMatchLocalRemote.getUpdated());

                if(dateRemoteUpdated.after(dateLocalUpdated))
                {
                    Log.i(TAG, "Programando actualización del usuario " + userLocal.getDisplayName());
                    databaseOperations.add(userModel.updateOperationUser(userLocal.getUid(),userMatchLocalRemote));
                }
                else
                {
                    Log.i(TAG, "No hay acciones para este usuario: " +userLocal.getDisplayName());
                }
            }
            // De ser necesario else: Si esta pendiente por sincronizar cambios en el server no hacer nada, sino eliminarlo

        }

        Log.i(TAG, "Se encontraron " + usersRemoteHashMap.size() + " usuarios en el servior que no estan en el telefono");
        for (User userRemote : usersRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción del usuario: " + userRemote.getDisplayName());
            databaseOperations.add(userModel.insertOperationUser(userRemote));
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            userModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.User.URI_CONTENT, null, false);
            contentResolver.notifyChange(DataBaseContract.Challenge.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los usuarios");
        }
    }

    /** update local users in the server */
    public void localToRemoteUserSync()
    {
        Uri uri = DataBaseContract.User.URI_CONTENT;
        int results = userModel.changeToSync(uri,contentResolver);
        Log.i(TAG, "Usuarios puestos en cola de sincronizacion:" + results);

        ArrayList<User> usersPendingForInsertChanges = userModel.getUsersForSync();
        Log.i(TAG, "Se encontraron " +  usersPendingForInsertChanges.size() + " usuarios para actualizar en el servidor");

        syncRatingsPendingForInsert(usersPendingForInsertChanges);
    }

    /**  Post database server  all users pending for inser */
    private void syncRatingsPendingForInsert(final ArrayList<User> usersPendingForInsertChanges)
    {
        if (usersPendingForInsertChanges.size() > 0)
        {
            for(final User userPendingForInsertChanges : usersPendingForInsertChanges)
            {
                RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
                EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
                Call<User> userInsertChangesCall = endPointsApi.updateUser(userPendingForInsertChanges.getUid(), userPendingForInsertChanges);
                userInsertChangesCall.enqueue(new Callback<User>()
                {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response)
                    {
                        if(response.isSuccessful())
                        {
                            User userUpdatedRemote = response.body();
                            if (userUpdatedRemote == null)
                            {
                                Log.e(TAG, "Error al actualizar el usuario");
                            }
                            else
                            {
                                userModel.changeToStateOkay(DataBaseContract.User.buildUriUser(userPendingForInsertChanges.getUid()),contentResolver);
                                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                                Date dateLocalUpdated = bikeMeApplication.getDateTime(userPendingForInsertChanges.getUpdated());
                                Date dateRemoteUpdated = bikeMeApplication.getDateTime(userUpdatedRemote.getUpdated());
                                if(dateRemoteUpdated.after(dateLocalUpdated))
                                {
                                    Log.i(TAG, "Usuario remoto mas actualizado o igual");

                                }
                                else
                                {
                                    Log.i(TAG, "Usuario remoto actualizado correctamente " + userUpdatedRemote.getDisplayName());
                                }
                            }
                        }
                        else
                        {
                            Log.i(TAG,"Respuesta fallida al actualizar usuario " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t)
                    {
                        Log.e(TAG, "on Failure update user : " + t.toString());
                    }
                });
            }
        }
        else
        {
            Log.i(TAG, "No se requiere sincronizacion de usuarios para actualizar");
        }
    }
}