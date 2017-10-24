package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.WorkoutModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class SyncWorkout {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncWorkout.class.getSimpleName();

    public ContentResolver contentResolver;
    WorkoutModel workoutModel;

    public SyncWorkout(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        workoutModel = new WorkoutModel(contentResolver);
    }

    /** Insert, update or delete guests fetched from server */
    public  void remoteToLocalDatabaseSyncWorkout(ArrayList<Workout> workoutsRemote)
    {
        ArrayList<Workout> workoutsLocal = workoutModel.getWorkouts();
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<String, Workout> workoutRemoteHashMap = new LinkedHashMap<>();
        for (Workout workoutRemote: workoutsRemote)
        {
            workoutRemoteHashMap.put(workoutRemote.getUid(),workoutRemote);
        }
        Log.i(TAG, "Se encontraron " + workoutsLocal.size() + " workouts en el telefono");


        for (Workout workoutLocal : workoutsLocal)
        {
            Workout workoutRemoteLocalMatch = workoutRemoteHashMap.get(workoutLocal.getUid());
            if (workoutRemoteLocalMatch != null)
            {
                workoutRemoteHashMap.remove(workoutLocal.getUid());
            }
        }

        Log.i(TAG, "Se encontraron " + workoutRemoteHashMap.size() + " workouts en el servidor que no estan en el telefono");
        for (Workout workoutRemote : workoutRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción del workout: " + workoutRemote.getName());
            databaseOperations.add(workoutModel.insertOperationWorkout(workoutRemote));
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            workoutModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.Workout.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los workouts");
        }
    }


    /** update local workous in the server */
    public void localToRemoteWorkoutSync()
    {
        Uri uri = DataBaseContract.Workout.URI_CONTENT;
        int results = workoutModel.changeToSync(uri,contentResolver);
        Log.i(TAG, "Workouts puestos en cola de sincronizacion:" + results);

        ArrayList<Workout> workoutsPendingForInsert = workoutModel.getWorkoutsForSync();
        Log.i(TAG, "Se encontraron " +  workoutsPendingForInsert.size() + " workouts para insertar en el servidor");

        syncWorkoutsPendingForInsert(workoutsPendingForInsert);
    }

    /**  Post database server  all workouts ending for insert */
    private void syncWorkoutsPendingForInsert(final ArrayList<Workout> workoutsPendingForInsert)
    {
        Gson gson = new Gson();
        Log.i(TAG,gson.toJson(workoutsPendingForInsert));
        if (workoutsPendingForInsert.size() > 0)
        {
            RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
            EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
            Call<ArrayList<Workout>> workoutsInsertCall = endPointsApi.insertWorkouts(workoutsPendingForInsert);
            workoutsInsertCall.enqueue(new Callback<ArrayList<Workout>>()
            {
                @Override
                public void onResponse(Call<ArrayList<Workout>> call, Response<ArrayList<Workout>> response)
                {
                    if (response.isSuccessful())
                    {
                        ArrayList<Workout> workoutsInserted = response.body();
                        for (int i = 0; i < workoutsInserted.size(); i++)
                        {
                            Workout workoutRemote = workoutsInserted.get(i);
                            if (workoutRemote == null)
                            {
                                Log.e(TAG, "Error al insertar workouts");
                            }
                            else
                            {
                                Workout workoutPendingForInsert = workoutsPendingForInsert.get(i);
                                workoutModel.changeToStateOkay(DataBaseContract.Workout.buildUriWorkout(workoutPendingForInsert.getUid()), contentResolver);
                            }
                        }
                    }
                    else
                    {
                        Log.i(TAG, "Respuesta fallida al insertar workout " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Workout>> call, Throwable t)
                {
                    Log.e(TAG, "on Failure insert workouts : " + t.toString());
                }
            });
        }
        else
        {
            Log.i(TAG, "No se requiere sincronizacion de workout para insertar");
        }
    }
}