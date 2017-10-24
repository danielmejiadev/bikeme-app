package com.android.bikeme.databaseremote.syncronization;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.bikemeserverconnection.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 18/03/2017.
 */
public class Synchronization extends AbstractThreadedSyncAdapter {

    private static final String TAG = Synchronization.class.getSimpleName();

    public static final int REMOTE_TO_LOCAL_DATABASE_SYNC = 0;
    public static final int LOCAL_TO_REMOTE_DATABASE_SYNC = 1;
    private static final long INTERVAL_SYNC_MINUTES = 240L*60L;
    private static final long INTERVAL_OFFSET_MINUTES = 60L*60L;

    private ContentResolver contentResolver;

    public Synchronization(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        contentResolver =  context.getContentResolver();
    }
    
    /** Inicia manualmente la sincronización */
    public static void syncNow(int syncType)
    {
        Context context = BikeMeApplication.getInstance().getApplicationContext();
        boolean isSyncActive = ContentResolver.isSyncActive(getAccountSync(context), context.getString(R.string.provider_authority));
        if(!isSyncActive)
        {
            Bundle bundle = new Bundle();
            // Fuerza sincronizacion manual
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            // Que se realice de inmediato. Si no se pone este se puede esperar varios segundos hasta que el framework decida ejecutar la sincronizacion.
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            if (LOCAL_TO_REMOTE_DATABASE_SYNC == syncType)
            {
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
            }
            ContentResolver.requestSync(getAccountSync(context), context.getString(R.string.provider_authority), bundle);
        }
    }

    public static void startAutoSync(Context context)
    {
        //Remote To Local
        Bundle bundle = new Bundle();
        ContentResolver.setSyncAutomatically(getAccountSync(context), context.getString(R.string.provider_authority),true);
        ContentResolver.addPeriodicSync(getAccountSync(context),
                context.getString(R.string.provider_authority),
                bundle,
                INTERVAL_SYNC_MINUTES);

        //Local To Remote
        ContentResolver.setSyncAutomatically(getAccountSync(context), context.getString(R.string.provider_authority),true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.addPeriodicSync(getAccountSync(context),
                context.getString(R.string.provider_authority),
                bundle,
                INTERVAL_SYNC_MINUTES+INTERVAL_OFFSET_MINUTES);
    }

    public static void stopAutoSync(Context context)
    {
        ContentResolver.setSyncAutomatically(getAccountSync(context), context.getString(R.string.provider_authority),false);
    }

    /** * Crea u obtiene una cuenta existente */
    public static Account getAccountSync(Context context)
    {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getResources().getString(R.string.account_type));

        if (null == accountManager.getPassword(newAccount))
        {
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
        }
        Log.i(TAG, "Cuenta de usuario obtenida. Name:" + newAccount.name + " Type:" + newAccount.type);
        return newAccount;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, final ContentProviderClient contentProviderClient, final SyncResult syncResult)
    {
        boolean localToRemoteSync = bundle.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        if(localToRemoteSync)
        {
            Log.i(TAG, "SINCRONIZACION DEL TELEFONO AL SERVIDOR");
            SyncUser syncUser = new SyncUser(contentResolver);
            syncUser.localToRemoteUserSync();

            SyncWorkout syncWorkout = new SyncWorkout(contentResolver);
            syncWorkout.localToRemoteWorkoutSync();

            SyncRating syncRating = new SyncRating(contentResolver);
            syncRating.localToRemoteRatingSync();

            SyncGuest syncGuest = new SyncGuest(contentResolver);
            syncGuest.localToRemoteGuestSync();

            SyncProblem syncProblem = new SyncProblem(contentResolver);
            syncProblem.localToRemoteProblemSync();
        }
        else
        {
            Log.i(TAG, "SINCRONIZACION DEL SERVIDOR AL TELEFONO");

            RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
            EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
            Call<ServerResponse> serverResponseCall = endPointsApi.getAllData();
            serverResponseCall.enqueue(new Callback<ServerResponse>()
            {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response)
                {
                    ServerResponse serverResponse = response.body();

                    Log.i(TAG,"SERVER DATE");
                    BikeMeApplication.getInstance().savePrefDifferenceLocalServerDate(serverResponse.getServerDate());

                    Log.i(TAG, "USUARIOS");
                    SyncUser syncUser = new SyncUser(contentResolver);
                    syncUser.remoteToLocalDatabaseSyncUser(serverResponse.getUsers());

                    Log.i(TAG, "RUTAS");
                    SyncRoute syncRoute = new SyncRoute(contentResolver);
                    syncRoute.remoteToLocalDatabaseSyncRoute(serverResponse.getRoutes());

                    Log.i(TAG, "RATINGS");
                    SyncRating syncRating = new SyncRating(contentResolver);
                    syncRating.remoteToLocalDatabaseSyncRating(serverResponse.getRatings());

                    Log.i(TAG, "EVENTS");
                    SyncEvent syncEvent = new SyncEvent(contentResolver);
                    syncEvent.remoteToLocalDatabaseSyncEvent(serverResponse.getEvents());

                    Log.i(TAG, "GUEST");
                    SyncGuest syncGuest = new SyncGuest(contentResolver);
                    syncGuest.remoteToLocalDatabaseSyncGuest(serverResponse.getGuests());

                    Log.i(TAG, "WORKOUTS");
                    SyncWorkout syncWorkout = new SyncWorkout(contentResolver);
                    syncWorkout.remoteToLocalDatabaseSyncWorkout(serverResponse.getWorkouts());

                    Log.i(TAG, "CHALLENGES");
                    SyncChallenge syncChallenge = new SyncChallenge(contentResolver);
                    syncChallenge.remoteToLocalDatabaseSyncChallenge(serverResponse.getChallenges());
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t)
                {
                    Log.i(TAG, "on failure al traer todos los datos del servidor "+t.toString());
                }
            });
        }
    }
}
