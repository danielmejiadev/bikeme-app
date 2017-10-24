package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.User;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaseremote.syncronization.Synchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Daniel on 03/05/2017.
 */
public class UserModel extends BikeMeModel {

    public static final int ABOUT_ME_KEY = 1;
    public static final int SOCIAL_NETWORKS_KEY = 2;
    public static final int PREFERENCE_DAYS_KEY = 3;
    public static final int PREFERENCE_HOURS_KEY = 4;
    public static final int ACHIEVEMENTS_KEY = 5;
    private static final int LEVEL_KEY = 6;


    private static final String TOTAL_ROUTES_CREATED_COLUMN = "totalRoutesCreated";
    private static final String TOTAL_ROUTES_RATED_COLUMN = "totalRoutesRated";
    private static final String TOTAL_DISTANCE_METERS_BY_WEEK_COLUMN = "totalDistanceMetersByWeek";
    private static final String TOTAL_DISTANCE_METERS_BY_MONTH_COLUMN = "totalDistanceMetersByMonth";
    private static final String TOTAL_DISTANCE_METERS_COLUMN = "totalDistanceMeters";
    private static final String TOTAL_DURATION_SECONDS_COLUMN = "totalDurationSeconds";
    private static final String TOTAL_POINTS_COLUMN = "totalPoints";

    public static final Integer TYPE_CHALLENGE_0_TYPE_WORKOUT_KEY = 0;
    public static final Integer TYPE_CHALLENGE_1_KM_BY_WORKOUT_KEY = 1;
    public static final Integer TYPE_CHALLENGE_2_HOURS_BY_WORKOUT_KEY = 2;
    public static final Integer TYPE_CHALLENGE_3_ROUTES_CREATED_KEY = 3;
    public static final Integer TYPE_CHALLENGE_4_ROUTES_RATED_KEY = 4;
    public static final Integer TYPE_CHALLENGE_5_TOTAL_KM_BY_WEEK_KEY = 5;
    public static final Integer TYPE_CHALLENGE_6_TOTAL_KM_BY_MONTH_KEY = 6;
    public static final Integer TYPE_CHALLENGE_7_TOTAL_KM_KEY = 7;
    public static final Integer TYPE_CHALLENGE_8_TOTAL_HOURS_KEY = 8;

    private ContentResolver contentResolver;

    public UserModel(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
    }

    public void insertUser(User user)
    {
        Uri uri = DataBaseContract.User.buildUriUser(user.getUid());
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert  cursor != null;
        if(cursor.getCount() == 0)
        {
            ContentValues valuesUser = new ContentValues();
            valuesUser.put(DataBaseContract.COLUMN_UID, user.getUid());
            valuesUser.put(DataBaseContract.User.COLUMN_DISPLAY_NAME, user.getDisplayName());
            valuesUser.put(DataBaseContract.User.COLUMN_EMAIL_USER, user.getEmail());
            valuesUser.put(DataBaseContract.User.COLUMN_PHOTO, user.getPhoto());
            valuesUser.put(DataBaseContract.User.COLUMN_LEVEL, user.getLevel());
            valuesUser.put(DataBaseContract.User.COLUMN_ABOUT_ME, user.getAboutMe());
            valuesUser.put(DataBaseContract.User.COLUMN_SOCIAL_NETWORKS, user.getSocialNetworks());
            valuesUser.put(DataBaseContract.User.COLUMN_PREFERENCE_DAYS, user.getPreferenceDays());
            valuesUser.put(DataBaseContract.User.COLUMN_PREFERENCE_HOURS, user.getPreferenceHours());
            valuesUser.put(DataBaseContract.User.COLUMN_ACHIEVEMENTS, user.getAchievements());
            valuesUser.put(DataBaseContract.COLUMN_UPDATED, user.getUpdated());
            valuesUser.put(DataBaseContract.COLUMN_CREATED, user.getCreated());
            contentResolver.insert(DataBaseContract.User.URI_CONTENT, valuesUser);
        }
        cursor.close();
    }

    public void updateUser(String userId, String value, int key)
    {
        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        Uri uri = DataBaseContract.User.buildUriUser(userId);
        ContentValues contentValues = new ContentValues();
        switch (key)
        {
            case ABOUT_ME_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_ABOUT_ME,value);
                break;
            case SOCIAL_NETWORKS_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_SOCIAL_NETWORKS,value);
                break;
            case PREFERENCE_DAYS_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_PREFERENCE_DAYS,value);
                break;
            case PREFERENCE_HOURS_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_PREFERENCE_HOURS,value);
                break;
            case ACHIEVEMENTS_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_ACHIEVEMENTS,value);
                break;
            case LEVEL_KEY:
                contentValues.put(DataBaseContract.User.COLUMN_LEVEL,Integer.parseInt(value));
                break;
        }
        contentValues.put(DataBaseContract.COLUMN_UPDATED, bikeMeApplication.getDateTimeString(bikeMeApplication.getCurrentDate()));
        contentValues.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_PENDING);
        contentResolver.update(uri,contentValues, null,null);

        Synchronization.syncNow(Synchronization.LOCAL_TO_REMOTE_DATABASE_SYNC);

    }

    public User getUserById(String uid)
    {
        Uri uri = DataBaseContract.User.buildUriUser(uid);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        assert cursor != null;
        User user = null;
        if(cursor.moveToFirst())
        {
           user = getUser(cursor);
        }
        cursor.close();
        return user;
    }

    public ArrayList<User> getUsers()
    {
        Uri uri = DataBaseContract.User.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        ArrayList<User> users = new ArrayList<>();
        assert cursor != null;
        while (cursor.moveToNext())
        {
            users.add(getUser(cursor));
        }
        cursor.close();
        return users;
    }

    public ArrayList<User> getUsersForSync()
    {
        Uri uri = DataBaseContract.User.URI_CONTENT;
        String selectionPendingForInsert = DataBaseContract.COLUMN_INSERT_STATE +"=? AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgsPendingForInsert = new String[]{DataBaseContract.INSERT_PENDING+"", DataBaseContract.STATE_SYNC+""};
        Cursor cursor = contentResolver.query(uri, null, selectionPendingForInsert, selectionArgsPendingForInsert, null);
        ArrayList<User> users = new ArrayList<>();
        assert cursor != null;
        while (cursor.moveToNext())
        {
            users.add(getUser(cursor));
        }
        cursor.close();
        return users;
    }

    public User getUser(Cursor cursor)
    {
        String id = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
        String displayName = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_DISPLAY_NAME));
        String email = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_EMAIL_USER));
        String photo = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_PHOTO));
        int level = cursor.getInt(cursor.getColumnIndex(DataBaseContract.User.COLUMN_LEVEL));
        String aboutMe  = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_ABOUT_ME));
        String socialNetworks = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_SOCIAL_NETWORKS));
        String preferenceDays = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_PREFERENCE_DAYS));
        String preferenceHours = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_PREFERENCE_HOURS));
        String achievements = cursor.getString(cursor.getColumnIndex(DataBaseContract.User.COLUMN_ACHIEVEMENTS));
        String updated = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UPDATED));
        String created = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_CREATED));

        User user = new User();
        user.setUid(id);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setLevel(level);
        user.setPhoto(photo);
        user.setAboutMe(aboutMe);
        user.setSocialNetworks(socialNetworks);
        user.setPreferenceDays(preferenceDays);
        user.setPreferenceHours(preferenceHours);
        user.setAchievements(achievements);
        user.setUpdated(updated);
        user.setCreated(created);

        return user;
    }

    public HashMap<Integer,Integer> getUserChallengeParams(String userId)
    {
        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        Date currentDate = bikeMeApplication.getCurrentDate();
        String[] selectionArgs = new String[4];
        selectionArgs = getDatesWeekInterval(selectionArgs, currentDate,bikeMeApplication);
        selectionArgs = getDatesMonthInterval(selectionArgs, currentDate,bikeMeApplication);

        Uri uri = DataBaseContract.User.buildUriUserParams(userId);
        HashMap<Integer, Integer> userChallengesParams = new LinkedHashMap<>();
        Cursor cursor = contentResolver.query(uri, null, null, selectionArgs, null);
        assert cursor != null;
        if(cursor.moveToFirst())
        {
            int totalRoutesCreated = cursor.getInt(cursor.getColumnIndex(TOTAL_ROUTES_CREATED_COLUMN));
            int totalRoutesRated = cursor.getInt(cursor.getColumnIndex(TOTAL_ROUTES_RATED_COLUMN));
            int totalDistanceMetersByWeek = cursor.getInt(cursor.getColumnIndex(TOTAL_DISTANCE_METERS_BY_WEEK_COLUMN));
            int totalDistanceMetersByMonth = cursor.getInt(cursor.getColumnIndex(TOTAL_DISTANCE_METERS_BY_MONTH_COLUMN));
            int totalDistanceMeters = cursor.getInt(cursor.getColumnIndex(TOTAL_DISTANCE_METERS_COLUMN));
            int totalDurationSeconds = cursor.getInt(cursor.getColumnIndex(TOTAL_DURATION_SECONDS_COLUMN));

            userChallengesParams.put(TYPE_CHALLENGE_0_TYPE_WORKOUT_KEY,-1);
            userChallengesParams.put(TYPE_CHALLENGE_1_KM_BY_WORKOUT_KEY,0);
            userChallengesParams.put(TYPE_CHALLENGE_2_HOURS_BY_WORKOUT_KEY, 0);
            userChallengesParams.put(TYPE_CHALLENGE_3_ROUTES_CREATED_KEY,totalRoutesCreated);
            userChallengesParams.put(TYPE_CHALLENGE_4_ROUTES_RATED_KEY,totalRoutesRated);
            userChallengesParams.put(TYPE_CHALLENGE_5_TOTAL_KM_BY_WEEK_KEY,(int)Workout.getDistanceKm(totalDistanceMetersByWeek));
            userChallengesParams.put(TYPE_CHALLENGE_6_TOTAL_KM_BY_MONTH_KEY,(int)Workout.getDistanceKm(totalDistanceMetersByMonth));
            userChallengesParams.put(TYPE_CHALLENGE_7_TOTAL_KM_KEY, (int)Workout.getDistanceKm(totalDistanceMeters));
            userChallengesParams.put(TYPE_CHALLENGE_8_TOTAL_HOURS_KEY,(int)Workout.getDurationHours(totalDurationSeconds));
        }
        cursor.close();
        return userChallengesParams;
    }


    public String[] getDatesWeekInterval(String[] dateWeekInterval, Date currentDate, BikeMeApplication bikeMeApplication)
    {
        Calendar calendarWeek = Calendar.getInstance();
        calendarWeek.setTime(currentDate);

        // Clear would not reset the hour of day !
        calendarWeek.set(Calendar.HOUR_OF_DAY, 0);
        calendarWeek.clear(Calendar.MINUTE);
        calendarWeek.clear(Calendar.SECOND);
        calendarWeek.clear(Calendar.MILLISECOND);

        // Get start of this week
        calendarWeek.set(Calendar.DAY_OF_WEEK, calendarWeek.getFirstDayOfWeek());
        dateWeekInterval[0] = bikeMeApplication.getDateTimeString(calendarWeek.getTime());

        // Get start of next week
        calendarWeek.add(Calendar.WEEK_OF_YEAR, 1);
        dateWeekInterval[1] = bikeMeApplication.getDateTimeString(calendarWeek.getTime());
        return dateWeekInterval;
    }

    public String[] getDatesMonthInterval(String[] dateMonthInterval,Date currentDate, BikeMeApplication bikeMeApplication)
    {
        Calendar calendarMonth = Calendar.getInstance();
        calendarMonth.setTime(currentDate);

        // Clear would not reset the hour of day !
        calendarMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarMonth.clear(Calendar.MINUTE);
        calendarMonth.clear(Calendar.SECOND);
        calendarMonth.clear(Calendar.MILLISECOND);

        // get start of the month
        calendarMonth.set(Calendar.DAY_OF_MONTH, 1);
        dateMonthInterval[2] = bikeMeApplication.getDateTimeString(calendarMonth.getTime());

        // get start of the next month
        calendarMonth.add(Calendar.MONTH, 1);
        dateMonthInterval[3] = bikeMeApplication.getDateTimeString(calendarMonth.getTime());
        return  dateMonthInterval;
    }

    public ArrayList<Integer> getLevelsAchieved(String userId)
    {
        User currentUser = getUserById(userId);
        int level = currentUser.getLevel();
        ArrayList<Integer> levelsAchieved = new ArrayList<>();
        if(level < 4)
        {
            int totalPoints = getTotalPoints(currentUser.getAchievementsList());
            int[] levelPoints = User.TOTAL_POINTS_LEVEL;
            for(int i=level; i<levelPoints.length; i++)
            {
                if(totalPoints >= levelPoints[i])
                {
                    levelsAchieved.add(i+1);
                }
            }
        }
        if(!levelsAchieved.isEmpty())
        {
            updateUser(userId,String.valueOf(levelsAchieved.get(levelsAchieved.size()-1)),LEVEL_KEY);
        }
        return levelsAchieved;
    }

    public int getTotalPoints(ArrayList<String> userAchievements)
    {
        int totalPoints = 0;
        if (!userAchievements.isEmpty())
        {
            Uri uri = DataBaseContract.Challenge.URI_CONTENT;
            String[] columns = new String[]{String.format("COALESCE(SUM(%s),0) %s",DataBaseContract.Challenge.COLUMN_AWARD, TOTAL_POINTS_COLUMN)};

            String[] params = new String[userAchievements.size()];
            Arrays.fill(params, "?");
            String selection = DataBaseContract.COLUMN_UID + " IN (" + TextUtils.join(",", params) + ")";

            Cursor cursor = contentResolver.query(uri, columns, selection, userAchievements.toArray(new String[userAchievements.size()]), null);
            assert cursor != null;
            cursor.moveToFirst();
            totalPoints = cursor.getInt(cursor.getColumnIndex(TOTAL_POINTS_COLUMN));
            cursor.close();
        }
        return totalPoints;
    }

    public ContentProviderOperation insertOperationUser(User user)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.User.URI_CONTENT)
                .withValue(DataBaseContract.COLUMN_UID, user.getUid())
                .withValue(DataBaseContract.User.COLUMN_DISPLAY_NAME, user.getDisplayName())
                .withValue(DataBaseContract.User.COLUMN_EMAIL_USER, user.getEmail())
                .withValue(DataBaseContract.User.COLUMN_PHOTO, user.getPhoto())
                .withValue(DataBaseContract.User.COLUMN_LEVEL, user.getLevel())
                .withValue(DataBaseContract.User.COLUMN_ABOUT_ME, user.getAboutMe())
                .withValue(DataBaseContract.User.COLUMN_SOCIAL_NETWORKS, user.getSocialNetworks())
                .withValue(DataBaseContract.User.COLUMN_PREFERENCE_DAYS, user.getPreferenceDays())
                .withValue(DataBaseContract.User.COLUMN_PREFERENCE_HOURS, user.getPreferenceHours())
                .withValue(DataBaseContract.User.COLUMN_ACHIEVEMENTS, user.getAchievements())
                .withValue(DataBaseContract.COLUMN_UPDATED, user.getUpdated())
                .withValue(DataBaseContract.COLUMN_CREATED, user.getCreated())
                .build();
    }

    public ContentProviderOperation updateOperationUser(String oldUserUid, User newUser)
    {
        Uri existingUri = DataBaseContract.User.buildUriUser(oldUserUid);
        return ContentProviderOperation.newUpdate(existingUri)
                .withValue(DataBaseContract.User.COLUMN_DISPLAY_NAME, newUser.getDisplayName())
                .withValue(DataBaseContract.User.COLUMN_EMAIL_USER, newUser.getEmail())
                .withValue(DataBaseContract.User.COLUMN_PHOTO, newUser.getPhoto())
                .withValue(DataBaseContract.User.COLUMN_LEVEL, newUser.getLevel())
                .withValue(DataBaseContract.User.COLUMN_ABOUT_ME, newUser.getAboutMe())
                .withValue(DataBaseContract.User.COLUMN_SOCIAL_NETWORKS, newUser.getSocialNetworks())
                .withValue(DataBaseContract.User.COLUMN_PREFERENCE_DAYS, newUser.getPreferenceDays())
                .withValue(DataBaseContract.User.COLUMN_PREFERENCE_HOURS, newUser.getPreferenceHours())
                .withValue(DataBaseContract.User.COLUMN_ACHIEVEMENTS, newUser.getAchievements())
                .withValue(DataBaseContract.COLUMN_UPDATED, newUser.getUpdated())
                .withValue(DataBaseContract.COLUMN_CREATED, newUser.getCreated())
                .withValue(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_OK)
                .withValue(DataBaseContract.COLUMN_STATE_SYNC, DataBaseContract.STATE_OK)
                .build();
    }

}