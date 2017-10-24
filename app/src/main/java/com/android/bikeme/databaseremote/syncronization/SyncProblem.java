package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.ProblemModel;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 29 sep 2017.
 */
public class SyncProblem {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncProblem.class.getSimpleName();

    public ContentResolver contentResolver;
    private ProblemModel problemModel;

    public SyncProblem(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.problemModel = new ProblemModel(contentResolver);
    }
    /** Insert, update or delete local ratings in the server */
    public void localToRemoteProblemSync()
    {
        Uri uri = DataBaseContract.Problem.URI_CONTENT;
        int results = problemModel.changeToSync(uri,contentResolver);
        Log.i(TAG, "Problemas puestos en cola de sincronizacion:" + results);

        ArrayList<Problem> ratingsPendingForInsert = problemModel.getProblemsForSync();
        Log.i(TAG, "Se encontraron " +  ratingsPendingForInsert.size() + " problemas para insertar en el servidor");

        syncProblemsPendingForInsert(ratingsPendingForInsert);
    }


    /**  Post database server  all ratings pending for insert */
    private void syncProblemsPendingForInsert(final ArrayList<Problem> problemsPendingForInsert)
    {
        if (problemsPendingForInsert.size() > 0)
        {
            RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
            EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
            Call<ArrayList<Problem>> problemsInsertCall = endPointsApi.insertProblems(problemsPendingForInsert);
            problemsInsertCall.enqueue(new Callback<ArrayList<Problem>>()
            {
                @Override
                public void onResponse(Call<ArrayList<Problem>> call, Response<ArrayList<Problem>> response)
                {
                    if(response.isSuccessful())
                    {
                        ArrayList<Problem> problemsInserted = response.body();
                        for (int i = 0; i < problemsInserted.size(); i++)
                        {
                            Problem problemInserted = problemsInserted.get(i);
                            if (problemInserted == null)
                            {
                                Log.e(TAG, "Error al insertar problems");
                            }
                            else
                            {
                                Problem problemPendingForInsert = problemsPendingForInsert.get(i);
                                problemModel.changeToStateOkay(DataBaseContract.Problem.buildUriProblem(String.valueOf(problemPendingForInsert.getId())),contentResolver);
                                Log.i(TAG,"Problemas insertados correctamente en el servidor");

                            }
                        }
                    }
                    else
                    {
                        Log.i(TAG,"Respuesta fallida al insertar problem " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Problem>> call, Throwable t)
                {
                    Log.e(TAG, "on Failure insert problems : " + t.toString());
                }
            });
        }
        else
        {
            Log.i(TAG, "No se requiere sincronizacion de problemas para insertar");
        }
    }
}