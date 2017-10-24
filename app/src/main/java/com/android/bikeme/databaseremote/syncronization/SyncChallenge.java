package com.android.bikeme.databaseremote.syncronization;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RouteModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Daniel on 8 sep 2017.
 */
public class SyncChallenge {

    public static final String TAG = Synchronization.class.getSimpleName() +" "+ SyncChallenge.class.getSimpleName();

    public ContentResolver contentResolver;
    public ChallengeModel challengeModel;

    public SyncChallenge(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        this.challengeModel = new ChallengeModel(contentResolver);
    }

    /** Insert, update or delete challenges fetched from server */
    public  void remoteToLocalDatabaseSyncChallenge(ArrayList<Challenge> challengesRemote)
    {
        ArrayList<Challenge> challengesLocal = challengeModel.getChallenges();

        if(challengesRemote.size() > challengesLocal.size())
        {
            ArrayList<ContentProviderOperation> databaseOperations = new ArrayList<>();
            LinkedHashMap<String, Challenge> challengeRemoteHashMap = new LinkedHashMap<>();
            for (Challenge challengeRemote: challengesRemote)
            {
                challengeRemoteHashMap.put(challengeRemote.getUid(), challengeRemote);
            }

            Log.i(TAG, "Se encontraron " + challengesLocal.size() + " retos en el telefono que ya estan en el servidor");

            for(Challenge challengeLocal : challengesLocal)
            {
                Challenge challengeMatchLocalRemote = challengeRemoteHashMap.get(challengeLocal.getUid());

                if (challengeMatchLocalRemote != null)
                {
                    challengeRemoteHashMap.remove(challengeLocal.getUid());
                }
            }

            Log.i(TAG, "Se encontraron " + challengeRemoteHashMap.size() + " retos en el servidor que no estan en el telefono");
            for (Challenge challengeRemote : challengeRemoteHashMap.values())
            {
                Log.i(TAG, "Programando inserción de reto: " + challengeRemote.getUid());
                databaseOperations.add(challengeModel.insertOperationChallenge(challengeRemote));
            }

            if (!databaseOperations.isEmpty())
            {
                Log.i(TAG, "Aplicando operaciones...");
                challengeModel.applyOperations(contentResolver,databaseOperations);
                contentResolver.notifyChange(DataBaseContract.Challenge.URI_CONTENT, null, false);
                Log.i(TAG, "Sincronización finalizada.");
            }
            else
            {
                Log.i(TAG, "No se requiere sincronización para los retos");
            }
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización para los retos");
        }
    }
}