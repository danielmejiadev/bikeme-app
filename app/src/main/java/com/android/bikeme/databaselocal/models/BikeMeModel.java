package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;

import java.util.ArrayList;

/**
 * Created by Daniel on 21 ago 2017.
 */
public class BikeMeModel {

    public int changeToSync(Uri uri, ContentResolver contentResolver)
    {
        String selection = DataBaseContract.COLUMN_INSERT_STATE + "=?  AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgs = new String[]{DataBaseContract.INSERT_PENDING + "", DataBaseContract.STATE_OK + ""};

        ContentValues values = new ContentValues();
        values.put(DataBaseContract.COLUMN_STATE_SYNC, DataBaseContract.STATE_SYNC);

        return contentResolver.update(uri, values, selection, selectionArgs);
    }

    public void changeToStateOkay(Uri uri, ContentResolver contentResolver)
    {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_OK);
        values.put(DataBaseContract.COLUMN_STATE_SYNC, DataBaseContract.STATE_OK);
        contentResolver.update(uri, values, null,null);
    }

    public void applyOperations(ContentResolver contentResolver, ArrayList<ContentProviderOperation> databaseOperations)
    {
        try
        {
            contentResolver.applyBatch(DataBaseContract.AUTHORITY, databaseOperations);
        }
        catch (RemoteException | OperationApplicationException e)
        {
            e.printStackTrace();
        }
    }
}
