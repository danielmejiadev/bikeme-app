package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaseremote.syncronization.Synchronization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Daniel on 5 sep 2017.
 */
public class WorkoutModel extends BikeMeModel{

    public static final String DATES_KEY = "dates";
    public static final String WORKOUTS_KEY = "workouts";

    ContentResolver contentResolver;

    public WorkoutModel(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
    }

    public Uri saveUserWorkout(Workout workout)
    {
        ContentValues valuesWorkout = new ContentValues();

        valuesWorkout.put(DataBaseContract.COLUMN_UID, UUID.randomUUID().toString());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_NAME,workout.getName());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_USER_ID,workout.getUser());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_DURATION_SECONDS,workout.getDurationSeconds());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_BEGIN_DATE, workout.getBeginDate());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_COMMENT, workout.getComment());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_ROUTE_LAT_LNG_LIST,workout.getRouteLatLngList());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_TOTAL_DISTANCE_METERS,workout.getTotalDistanceMeters());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_AVERAGE_SPEED_KM,workout.getAverageSpeedKm());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_AVERAGE_ALTITUDE_METERS,workout.getAverageAltitudeMeters());
        valuesWorkout.put(DataBaseContract.Workout.COLUMN_TYPE_ROUTE,workout.getTypeRoute());
        valuesWorkout.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_PENDING);

        contentResolver.notifyChange(DataBaseContract.Workout.URI_CONTENT,null,false);
        //This used for notify fragments challenge update values
        contentResolver.notifyChange(DataBaseContract.Challenge.URI_CONTENT, null, false);
        Uri uriResponse = contentResolver.insert(DataBaseContract.Workout.URI_CONTENT, valuesWorkout);

        Synchronization.syncNow(Synchronization.LOCAL_TO_REMOTE_DATABASE_SYNC);

        return uriResponse;
    }

    public ArrayList<Workout> getWorkouts()
    {
        Uri uri = DataBaseContract.Workout.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        ArrayList<Workout> workouts = new ArrayList<>();

        assert cursor != null;
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
               workouts.add(getWorkout(cursor));
            }
        }
        cursor.close();
        return workouts;
    }


    public HashMap<String,Object> getUserWorkouts(String currentUserUid)
    {
        Uri uri = DataBaseContract.User.buildUriUserWorkouts(currentUserUid);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        HashMap<String,Object> response = new HashMap<>();

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<ArrayList<Workout>> workouts = new ArrayList<>();
        HashMap<Integer, ArrayList<Workout>> groups = new LinkedHashMap<>();

        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        assert cursor != null;
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                Workout workout = getWorkout(cursor);
                int numberGroup = cursor.getInt(cursor.getColumnIndex("numberGroup"));

                Date date = bikeMeApplication.getDateTime(workout.getBeginDate());
                String longDate = bikeMeApplication.getLongDate(date);

                ArrayList<Workout> group = groups.get(numberGroup);
                if(group==null)
                {
                    dates.add(longDate);
                    group = new ArrayList<>();
                    group.add(workout);
                    groups.put(numberGroup,group);
                }
                else
                {
                    group.add(workout);
                }
            }
        }
        cursor.close();
        for(Map.Entry<Integer, ArrayList<Workout>> group : groups.entrySet())
        {
            workouts.add(group.getValue());
        }

        response.put(DATES_KEY, dates);
        response.put(WORKOUTS_KEY, workouts);

        return response;
    }

    public ArrayList<Workout> getWorkoutsForSync()
    {
        Uri uri = DataBaseContract.Workout.URI_CONTENT;
        String selectionPendingForInsert = DataBaseContract.COLUMN_INSERT_STATE +"=? AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgsPendingForInsert = new String[]{DataBaseContract.INSERT_PENDING+"", DataBaseContract.STATE_SYNC+""};
        Cursor cursor = contentResolver.query(uri, null, selectionPendingForInsert, selectionArgsPendingForInsert, null);
        assert  cursor != null;
        ArrayList <Workout> workouts = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                workouts.add(getWorkout(cursor));
            }
        }
        cursor.close();
        return  workouts;
    }


    public Workout getWorkout(Cursor cursor)
    {
        String uid = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
        String name = cursor.getString(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_NAME));
        String user = cursor.getString(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_USER_ID));
        int durationSeconds = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_DURATION_SECONDS));
        String beginDate = cursor.getString(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_BEGIN_DATE));
        String comment = cursor.getString(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_COMMENT));
        String routeLatLngList = cursor.getString(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_ROUTE_LAT_LNG_LIST));
        int totalDistanceMeters = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_TOTAL_DISTANCE_METERS));
        int averageSpeedKm = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_AVERAGE_SPEED_KM));
        int averageAltitudeMeters =cursor.getInt(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_AVERAGE_ALTITUDE_METERS));
        int typeRoute = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Workout.COLUMN_TYPE_ROUTE));

        Workout workout = new Workout();
        workout.setUid(uid);
        workout.setName(name);
        workout.setUser(user);
        workout.setDurationSeconds(durationSeconds);
        workout.setBeginDate(beginDate);
        workout.setComment(comment);
        workout.setRouteLatLngList(routeLatLngList);
        workout.setTotalDistanceMeters(totalDistanceMeters);
        workout.setAverageSpeedKm(averageSpeedKm);
        workout.setAverageAltitudeMeters(averageAltitudeMeters);
        workout.setTypeRoute(typeRoute);
        return workout;
    }

    public ContentProviderOperation insertOperationWorkout(Workout workout)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Workout.URI_CONTENT)
                .withValue(DataBaseContract.COLUMN_UID, workout.getUid())
                .withValue(DataBaseContract.Workout.COLUMN_NAME,workout.getName())
                .withValue(DataBaseContract.Workout.COLUMN_USER_ID,workout.getUser())
                .withValue(DataBaseContract.Workout.COLUMN_DURATION_SECONDS,workout.getDurationSeconds())
                .withValue(DataBaseContract.Workout.COLUMN_BEGIN_DATE, workout.getBeginDate())
                .withValue(DataBaseContract.Workout.COLUMN_COMMENT, workout.getComment())
                .withValue(DataBaseContract.Workout.COLUMN_ROUTE_LAT_LNG_LIST,workout.getRouteLatLngList())
                .withValue(DataBaseContract.Workout.COLUMN_TOTAL_DISTANCE_METERS,workout.getTotalDistanceMeters())
                .withValue(DataBaseContract.Workout.COLUMN_AVERAGE_SPEED_KM,workout.getAverageSpeedKm())
                .withValue(DataBaseContract.Workout.COLUMN_AVERAGE_ALTITUDE_METERS,workout.getAverageAltitudeMeters())
                .withValue(DataBaseContract.Workout.COLUMN_TYPE_ROUTE,workout.getTypeRoute())
                .build();
    }
}