package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.RatingModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Date;

/**
 * Created by Daniel on 20 may 2017.
 */
public class SyncRating {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncRating.class.getSimpleName();

    public ContentResolver contentResolver;
    private RatingModel ratingModel;

    public SyncRating(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        ratingModel = new RatingModel(contentResolver);
    }

    /** Insert, update or delete ratings fetched from server */
    public  void remoteToLocalDatabaseSyncRating(ArrayList<Rating> ratingsRemote)
    {
        ArrayList<Rating> ratingsLocal = ratingModel.getRatings();
        ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
        LinkedHashMap<LinkedHashMap<String, String>, Rating> ratingRemoteHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> keyRating;
        for (Rating ratingRemote: ratingsRemote)
        {
            keyRating = new LinkedHashMap<>();
            keyRating.put("user",ratingRemote.getUser());
            keyRating.put("route", ratingRemote.getRoute());

            ratingRemoteHashMap.put(keyRating, ratingRemote);
        }
        Log.i(TAG, "Se encontraron " + ratingsLocal.size() + " ratings en el telefono");

        for(Rating ratingLocal : ratingsLocal)
        {
            keyRating = new LinkedHashMap<>();
            keyRating.put("user",ratingLocal.getUser());
            keyRating.put("route",ratingLocal.getRoute());
            Rating ratingMatchLocalRemote = ratingRemoteHashMap.get(keyRating);

            if (ratingMatchLocalRemote != null)
            {
                ratingRemoteHashMap.remove(keyRating);
                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                Date dateLocalUpdated = bikeMeApplication.getDateTime(ratingLocal.getDate());
                Date dateRemoteUpdated = bikeMeApplication.getDateTime(ratingMatchLocalRemote.getDate());
                boolean isRatingLocalRecommendation = (ratingLocal.getCalification() == 0 && ratingLocal.getRecommendation() > 0);
                boolean isRatingRemoteCalification = (ratingMatchLocalRemote.getCalification() >= 0 && ratingMatchLocalRemote.getRecommendation() == 0);

                if(dateRemoteUpdated.after(dateLocalUpdated) || (isRatingLocalRecommendation && isRatingRemoteCalification))
                {
                    Log.i(TAG, "Programando actualización del Rating: User: " + ratingLocal.getUser() + " Route: " + ratingLocal.getRoute());
                    databaseOperations.add(ratingModel.updateOperationRating(ratingLocal.getId(),ratingMatchLocalRemote));
                }
                else
                {
                    Log.i(TAG, "No hay acciones para este rating: User: " +  ratingLocal.getUser() + " Route: " + ratingLocal.getRoute());
                }
            }
            // De ser necesario else: Si esta pendiente por sincronizar cambios en el server no hacer nada, sino eliminarlo

        }

        Log.i(TAG, "Se encontraron " + ratingRemoteHashMap.size() + " ratings en el servidor que no estan en el telefono");
        for (Rating ratingRemote : ratingRemoteHashMap.values())
        {
            Log.i(TAG, "Programando inserción del rating: " + ratingRemote.getId());
            databaseOperations.add(ratingModel.insertOperationRating(ratingRemote));
        }

        if (!databaseOperations.isEmpty())
        {
            Log.i(TAG, "Aplicando operaciones...");
            ratingModel.applyOperations(contentResolver,databaseOperations);
            contentResolver.notifyChange(DataBaseContract.Rating.URI_CONTENT, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los ratings");
        }
    }

    /** Insert, update or delete local ratings in the server */
    public void localToRemoteRatingSync()
    {
        Uri uri = DataBaseContract.Rating.URI_CONTENT;
        int results = ratingModel.changeToSync(uri,contentResolver);
        Log.i(TAG, "Ratings puestos en cola de sincronizacion:" + results);

        ArrayList<Rating> ratingsPendingForInsert = ratingModel.getRatingsForSync();
        Log.i(TAG, "Se encontraron " +  ratingsPendingForInsert.size() + " ratings para insertar en el servidor");

        syncRatingsPendingForInsert(ratingsPendingForInsert);
    }


    /**  Post database server  all ratings pending for insert */
    private void syncRatingsPendingForInsert(final ArrayList<Rating> ratingsPendingForInsert)
    {
        if (ratingsPendingForInsert.size() > 0)
        {
            RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
            EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
            Call<ArrayList<Rating>> ratingsInsertCall = endPointsApi.insertRatings(ratingsPendingForInsert);
            ratingsInsertCall.enqueue(new Callback<ArrayList<Rating>>()
            {
                @Override
                public void onResponse(Call<ArrayList<Rating>> call, Response<ArrayList<Rating>> response)
                {
                    if(response.isSuccessful())
                    {
                        ArrayList<Rating> ratingsInserted = response.body();
                        for (int i = 0; i < ratingsInserted.size(); i++)
                        {
                            Rating ratingRemote = ratingsInserted.get(i);
                            if (ratingRemote == null)
                            {
                                Log.e(TAG, "Error al insertar ratings");
                            }
                            else
                            {
                                Rating ratingPendingForInsert = ratingsPendingForInsert.get(i);

                                ratingModel.changeToStateOkay(DataBaseContract.Rating.buildUriRating(String.valueOf(ratingPendingForInsert.getId())),contentResolver);
                                BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
                                Date dateLocalUpdated = bikeMeApplication.getDateTime(ratingPendingForInsert.getDate());
                                Date dateRemoteUpdated = bikeMeApplication.getDateTime(ratingRemote.getDate());
                                if(dateRemoteUpdated.after(dateLocalUpdated))
                                {
                                    Log.i(TAG, "Rating remoto mas actualizado o igual");

                                }
                                else
                                {
                                    Log.i(TAG, "Rating remoto actualizado correctamente: User: " + ratingRemote.getUser() + " Route: " + ratingRemote.getRoute());
                                }
                            }
                        }
                    }
                    else
                    {
                        Log.i(TAG,"Respuesta fallida al insertar rating " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Rating>> call, Throwable t)
                {
                    Log.e(TAG, "on Failure insert ratings : " + t.toString());
                }
            });
        }
        else
        {
            Log.i(TAG, "No se requiere sincronizacion de ratings para insertar");
        }
    }
}