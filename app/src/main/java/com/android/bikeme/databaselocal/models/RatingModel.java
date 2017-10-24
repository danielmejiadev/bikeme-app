package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaseremote.syncronization.Synchronization;

import java.util.ArrayList;

/**
 * Created by Daniel on 21 may 2017.
 */
public class RatingModel extends BikeMeModel {


    public ContentResolver contentResolver;

    public RatingModel(ContentResolver contentResolver)
    {
        this.contentResolver= contentResolver;
    }

    public void saveRatingRoute(Rating rating)
    {
        ContentValues valuesRating = new ContentValues();

        valuesRating.put(DataBaseContract.Rating.COLUMN_ROUTE_ID, rating.getRoute());
        valuesRating.put(DataBaseContract.Rating.COLUMN_USER_ID, rating.getUser());
        valuesRating.put(DataBaseContract.Rating.COLUMN_CALIFICATION, rating.getCalification());
        valuesRating.put(DataBaseContract.COLUMN_DATE, rating.getDate());
        valuesRating.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_PENDING);

        contentResolver.insert(DataBaseContract.Rating.URI_CONTENT, valuesRating);
        contentResolver.notifyChange(DataBaseContract.Rating.URI_CONTENT,null,false);

        Synchronization.syncNow(Synchronization.LOCAL_TO_REMOTE_DATABASE_SYNC);
    }

    public ArrayList<Rating> getRatings()
    {
        Uri uri = DataBaseContract.Rating.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        ArrayList<Rating> ratings = new ArrayList<>();
        assert cursor != null;
        while (cursor.moveToNext())
        {
            ratings.add(getRating(cursor));
        }
        cursor.close();
        return ratings;
    }

    /**  get ratings for sync */
    public ArrayList<Rating> getRatingsForSync()
    {
        Uri uri = DataBaseContract.Rating.URI_CONTENT;
        String selectionPendingForInsert = DataBaseContract.COLUMN_INSERT_STATE +"=? AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgsPendingForInsert = new String[]{DataBaseContract.INSERT_PENDING+"", DataBaseContract.STATE_SYNC+""};
        Cursor cursor = contentResolver.query(uri, null, selectionPendingForInsert, selectionArgsPendingForInsert, null);
        assert  cursor != null;
        ArrayList <Rating> ratings = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
               ratings.add(getRating(cursor));
            }
        }
        cursor.close();
        return  ratings;
    }

    public Rating getRating(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Rating._ID));
        String userId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Rating.COLUMN_USER_ID));
        String routeId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Rating.COLUMN_ROUTE_ID));
        double calification = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Rating.COLUMN_CALIFICATION));
        double recommendation = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Rating.COLUMN_RECOMMENDATION));
        String date = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_DATE));

        Rating rating = new Rating();
        rating.setId(id);
        rating.setUser(userId);
        rating.setRoute(routeId);
        rating.setCalification(calification);
        rating.setRecommendation(recommendation);
        rating.setDate(date);

        return rating;
    }

    public ContentProviderOperation insertOperationRating(Rating rating)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Rating.URI_CONTENT)
                .withValue(DataBaseContract.Rating.COLUMN_USER_ID, rating.getUser())
                .withValue(DataBaseContract.Rating.COLUMN_ROUTE_ID, rating.getRoute())
                .withValue(DataBaseContract.Rating.COLUMN_CALIFICATION, rating.getCalification())
                .withValue(DataBaseContract.Rating.COLUMN_RECOMMENDATION, rating.getRecommendation())
                .withValue(DataBaseContract.COLUMN_DATE, rating.getDate())
                .build();
    }

    public ContentProviderOperation updateOperationRating(int oldRatingId, Rating newRating)
    {
        Uri existingUri = DataBaseContract.Rating.buildUriRating(String.valueOf(oldRatingId));
        return ContentProviderOperation.newUpdate(existingUri)
                .withValue(DataBaseContract.Rating.COLUMN_CALIFICATION, newRating.getCalification())
                .withValue(DataBaseContract.Rating.COLUMN_RECOMMENDATION, newRating.getRecommendation())
                .withValue(DataBaseContract.COLUMN_DATE, newRating.getDate())
                .withValue(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_OK)
                .withValue(DataBaseContract.COLUMN_STATE_SYNC, DataBaseContract.STATE_OK)
                .build();
    }
}