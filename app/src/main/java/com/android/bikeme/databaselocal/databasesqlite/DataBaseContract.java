package com.android.bikeme.databaselocal.databasesqlite;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Daniel on 18/03/2017.
 */
public final class DataBaseContract {

    public static final String  DATABASE_NAME = "BikeMeDatabase";
    public static final int DATABASE_VERSION  = 1;

    public final static String AUTHORITY = "com.android.bikeme";
    public static final Uri URI_BASE = Uri.parse("content://" + AUTHORITY);
    public static final String USERS = "users";
    public static final String WORKOUTS = "workouts";
    public static final String CHALLENGES_PARAMS = "challenges_params";

    public static final String ROUTES = "routes";
    public static final String POINTS = "points";
    public static final String RATINGS = "ratings";

    public static final String EVENTS = "events";
    public static final String GUESTS = "guests";

    public static final String CHALLENGES = "challenges";

    public static final String PROBLEMS = "problems";

    public static final String MINE = "mine";
    public static final String SUGGEST = "suggest";
    public static final String NEWS = "news";
    public static final String GROUP = "group";
    private static final String LOCATION_NEW = "location";
    public final static String SINGLE_MIME = "vnd.android.cursor.item/vnd." + AUTHORITY;
    public final static String MULTIPLE_MIME = "vnd.android.cursor.dir/vnd." + AUTHORITY;

    public static final String REFERENCE_ROUTE_ID = String.format("REFERENCES %s(%s) ON DELETE CASCADE", Route.TABLE_NAME,  DataBaseContract.COLUMN_UID);
    public static final String REFERENCE_USER_ID = String.format("REFERENCES %s(%s) ON DELETE CASCADE", User.TABLE_NAME, DataBaseContract.COLUMN_UID);
    public static final String REFERENCE_EVENT_ID = String.format("REFERENCES %s(%s) ON DELETE CASCADE", Event.TABLE_NAME, DataBaseContract.COLUMN_UID);


    public static final int STATE_OK = 0;
    public static final int STATE_SYNC = 1;

    public static final int INSERT_OK = 0;
    public static final int INSERT_PENDING = 1;

    public static final String COLUMN_UID             = "uid";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_UPDATED         = "updated";
    public static final String COLUMN_DATE             = "date";
    public static final String COLUMN_STATE_SYNC = "state_sync";
    public final static String COLUMN_INSERT_STATE = "insert_state";
    public static final String AVERAGE_RATINGS = "average_ratings";

    private DataBaseContract()
    {

    }

    public static String generateMime(String id)
    {
        if (id != null)
        {
            return MULTIPLE_MIME + id;
        } else
        {
            return null;
        }
    }

    public static String generateMimeItem(String id)
    {
        if (id != null)
        {
            return SINGLE_MIME + id;
        } else
        {
            return null;
        }
    }

    public static class User
    {
        public static final String TABLE_NAME             = "user";
        public static final String COLUMN_DISPLAY_NAME    = "displayName";
        public static final String COLUMN_EMAIL_USER      = "email";
        public static final String COLUMN_LEVEL           = "level";
        public static final String COLUMN_PHOTO           = "photo";
        public static final String COLUMN_ABOUT_ME        = "aboutMe";
        public static final String COLUMN_SOCIAL_NETWORKS = "socialNetworks";
        public static final String COLUMN_PREFERENCE_DAYS = "preferenceDays";
        public static final String COLUMN_PREFERENCE_HOURS= "preferenceHours";
        public static final String COLUMN_ACHIEVEMENTS   = "achievements";


        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(USERS).build();
        public static final Uri URI_LOCATION_NEW = URI_BASE.buildUpon().appendPath(LOCATION_NEW).build();

        public static Uri buildUriUser(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static String getUserId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildUriUserRatings(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(RATINGS).build();
        }

        public static Uri buildUriUserWorkouts(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(WORKOUTS).build();
        }

        public static Uri buildUriUserParams(String userId)
        {
            return URI_CONTENT.buildUpon().appendPath(userId).appendPath(CHALLENGES_PARAMS).build();
        }

        public static Uri buildUriUserSuggestRoutes(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(ROUTES).appendPath(SUGGEST).build();
        }

        public static Uri buildUriUserRoutesMine(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(ROUTES).appendPath(MINE).build();
        }

        public static final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + User.TABLE_NAME +
                        " (" +
                        DataBaseContract.COLUMN_UID             + " TEXT PRIMARY KEY, " +
                        User.COLUMN_DISPLAY_NAME                + " TEXT, " +
                        User.COLUMN_EMAIL_USER                  + " TEXT, " +
                        User.COLUMN_LEVEL                       + " INTEGER, " +
                        User.COLUMN_PHOTO                       + " TEXT,  " +
                        User.COLUMN_ABOUT_ME                    + " TEXT,  " +
                        User.COLUMN_SOCIAL_NETWORKS             + " TEXT,  " +
                        User.COLUMN_PREFERENCE_DAYS             + " TEXT,  " +
                        User.COLUMN_PREFERENCE_HOURS            + " TEXT,  " +
                        User.COLUMN_ACHIEVEMENTS                + " TEXT,  " +
                        DataBaseContract.COLUMN_UPDATED         + " TEXT,  " +
                        DataBaseContract.COLUMN_CREATED         + " TEXT,  " +
                        DataBaseContract.COLUMN_STATE_SYNC      + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.STATE_OK  +", "+
                        DataBaseContract.COLUMN_INSERT_STATE    + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.INSERT_OK +" "+
                        " )";

        public static final String SQL_DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + User.TABLE_NAME;
    }

    public static class Workout
    {
        public static final String TABLE_NAME = "workout";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_DURATION_SECONDS = "durationSeconds";
        public static final String COLUMN_BEGIN_DATE = "beginDate";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_ROUTE_LAT_LNG_LIST = "routeLatLngList";
        public static final String COLUMN_TOTAL_DISTANCE_METERS = "totalDistanceMeters";
        public static final String COLUMN_AVERAGE_SPEED_KM = "averageSpeedKm";
        public static final String COLUMN_AVERAGE_ALTITUDE_METERS = "averageAltitudeMeters";
        public static final String COLUMN_TYPE_ROUTE = "typeRoute";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(WORKOUTS).build();

        public static Uri buildUriWorkout(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static String getWorkoutId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static final String SQL_CREATE_WORKOUT_TABLE =
                "CREATE TABLE " + Workout.TABLE_NAME +
                        " (" +
                        DataBaseContract.COLUMN_UID            + " TEXT PRIMARY KEY, " +
                        Workout.COLUMN_NAME                    + " TEXT, " +
                        Workout.COLUMN_USER_ID                 + " TEXT "  + DataBaseContract.REFERENCE_USER_ID  +", " +
                        Workout.COLUMN_DURATION_SECONDS        + " INTEGER, " +
                        Workout.COLUMN_BEGIN_DATE              + " TEXT, " +
                        Workout.COLUMN_COMMENT                 + " TEXT, " +
                        Workout.COLUMN_ROUTE_LAT_LNG_LIST      + " TEXT, " +
                        Workout.COLUMN_TOTAL_DISTANCE_METERS   + " INTEGER, " +
                        Workout.COLUMN_AVERAGE_SPEED_KM        + " INTEGER, " +
                        Workout.COLUMN_AVERAGE_ALTITUDE_METERS + " INTEGER, " +
                        Workout.COLUMN_TYPE_ROUTE              + " INTEGER, " +
                        DataBaseContract.COLUMN_STATE_SYNC     + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.STATE_OK  +", "+
                        DataBaseContract.COLUMN_INSERT_STATE   + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.INSERT_OK +
                        " ) ";

        public static final String SQL_DELETE_WORKOUT_TABLE = "DROP TABLE IF EXISTS " + Workout.TABLE_NAME;


    }



    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static class Route
    {
        public static final String TABLE_NAME               = "route";
        public static final String COLUMN_NAME_ROUTE        = "name";
        public static final String COLUMN_CREATOR           = "creator_id";
        public static final String COLUMN_DESCRIPTION_ROUTE = "description";
        public static final String COLUMN_DISTANCE          = "distance";
        public static final String COLUMN_LEVEL             = "level";
        public static final String COLUMN_DEPARTURE         = "departure";
        public static final String COLUMN_ARRIVAL           = "arrival";
        public static final String COLUMN_IMAGE             = "image";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(ROUTES).build();

        public static Uri buildUriRoute(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static Uri buildUriRouteRatings(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(RATINGS).build();
        }

        public static Uri buildUriRoutePoints(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(POINTS).build();
        }

        public static Uri buildUriNewRoutes()
        {
            return URI_BASE.buildUpon().appendPath(SUGGEST).appendPath(NEWS).build();
        }

        public static String getRouteId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static final String SQL_CREATE_ROUTE_TABLE =
                "CREATE TABLE " + Route.TABLE_NAME +
                        " (" +
                        DataBaseContract.COLUMN_UID             + " TEXT PRIMARY KEY, " +
                        Route.COLUMN_CREATOR                    + " TEXT "  + DataBaseContract.REFERENCE_USER_ID  +", " +
                        Route.COLUMN_NAME_ROUTE                 + " TEXT, " +
                        Route.COLUMN_DESCRIPTION_ROUTE          + " TEXT, " +
                        Route.COLUMN_DISTANCE                   + " DOUBLE, " +
                        Route.COLUMN_LEVEL                      + " INTEGER, " +
                        Route.COLUMN_IMAGE                      + " TEXT,  " +
                        Route.COLUMN_DEPARTURE                  + " TEXT,  " +
                        Route.COLUMN_ARRIVAL                    + " TEXT,  " +
                        DataBaseContract.COLUMN_CREATED         + " TEXT "+
                        " )";

        public static final String SQL_DELETE_ROUTE_TABLE = "DROP TABLE IF EXISTS " + Route.TABLE_NAME;
    }

    public static class Rating implements BaseColumns
    {
        public static final String TABLE_NAME = "rating";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_ROUTE_ID = "route_id";
        public static final String COLUMN_CALIFICATION = "calification";
        public static final String COLUMN_RECOMMENDATION = "recommendation";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(RATINGS).build();

        public static Uri buildUriRating(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static final String SQL_CREATE_RATING_TABLE =
                "CREATE TABLE " + Rating.TABLE_NAME +
                        " (" +
                        Rating._ID                              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Rating.COLUMN_USER_ID                   + " TEXT "  + DataBaseContract.REFERENCE_USER_ID  +", " +
                        Rating.COLUMN_ROUTE_ID                  + " TEXT "  + DataBaseContract.REFERENCE_ROUTE_ID +", " +
                        Rating.COLUMN_CALIFICATION              + " DOUBLE, " +
                        Rating.COLUMN_RECOMMENDATION            + " DOUBLE DEFAULT 0, " +
                        DataBaseContract.COLUMN_STATE_SYNC      + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.STATE_OK  +", "+
                        DataBaseContract.COLUMN_INSERT_STATE    + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.INSERT_OK +", "+
                        DataBaseContract.COLUMN_DATE            + " TEXT, "+
                        "UNIQUE ( "+Rating.COLUMN_USER_ID +" , "+Rating.COLUMN_ROUTE_ID+" ) ON CONFLICT REPLACE " +
                        " ) ";

        public static final String SQL_DELETE_RATING_TABLE = "DROP TABLE IF EXISTS " + Rating.TABLE_NAME;
    }

    public static class Point {
        public static final String TABLE_NAME = "point";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ROUTE_ID = "route_id";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(POINTS).build();

        public static Uri buildUriPoint(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static int getPointId(Uri uri)
        {
            return Integer.parseInt(uri.getLastPathSegment());
        }

        public static final String SQL_CREATE_POINT_TABLE =
                "CREATE TABLE " + Point.TABLE_NAME +
                        " (" +
                        Point.COLUMN_ID                         + " INT PRIMARY KEY, " +
                        Point.COLUMN_ROUTE_ID                   + " TEXT "  + DataBaseContract.REFERENCE_ROUTE_ID +", " +
                        Point.COLUMN_LATITUDE                   + " DOUBLE, " +
                        Point.COLUMN_LONGITUDE                  + " DOUBLE " +
                        " )";

        public static final String SQL_DELETE_POINT_TABLE = "DROP TABLE IF EXISTS " + Point.TABLE_NAME;
    }





    /////////////////////////////////////////////////////////////
    public static class Event
    {
        public static final String TABLE_NAME               = "event";
        public static final String COLUMN_NAME_EVENT        = "name";
        public static final String COLUMN_ROUTE_ID          = "route_id";


        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(EVENTS).build();

        public static Uri buildUriEvent(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static Uri buildUriEventGuests(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).appendPath(GUESTS).build();
        }

        public static Uri buildUriEventGroupByDate()
        {
            return URI_BASE.buildUpon().appendPath(GROUP).appendPath(EVENTS).build();
        }


        public static String getEventId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static final String SQL_CREATE_EVENT_TABLE =
                "CREATE TABLE " + Event.TABLE_NAME +
                        " (" +
                        DataBaseContract.COLUMN_UID             + " TEXT PRIMARY KEY, " +
                        Event.COLUMN_NAME_EVENT                 + " TEXT, " +
                        Event.COLUMN_ROUTE_ID                   + " TEXT "  + DataBaseContract.REFERENCE_ROUTE_ID +", " +
                        DataBaseContract.COLUMN_DATE                       + " TEXT " +
                        " )";

        public static final String SQL_DELETE_EVENT_TABLE = "DROP TABLE IF EXISTS " + Event.TABLE_NAME;
    }

    public static class Guest implements BaseColumns
    {
        public static final String TABLE_NAME = "guest";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_STATE = "state";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(GUESTS).build();

        public static Uri buildUriGuest(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static final String SQL_CREATE_GUEST_TABLE =
                "CREATE TABLE " + Guest.TABLE_NAME +
                        " (" +
                        Guest._ID                              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Guest.COLUMN_USER_ID                   + " TEXT "  + DataBaseContract.REFERENCE_USER_ID  +", " +
                        Guest.COLUMN_EVENT_ID                  + " TEXT "  + DataBaseContract.REFERENCE_EVENT_ID +", " +
                        Guest.COLUMN_STATE                     + " INTEGER, " +
                        DataBaseContract.COLUMN_DATE           + " TEXT, " +
                        DataBaseContract.COLUMN_STATE_SYNC     + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.STATE_OK  +", "+
                        DataBaseContract.COLUMN_INSERT_STATE   + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.INSERT_OK +", "+
                        "UNIQUE ( "+Guest.COLUMN_USER_ID +" , "+Guest.COLUMN_EVENT_ID+" ) ON CONFLICT REPLACE " +
                        " ) ";

        public static final String SQL_DELETE_GUEST_TABLE = "DROP TABLE IF EXISTS " + Guest.TABLE_NAME;

    }







    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class Challenge
    {
        public static final String TABLE_NAME = "challenge";
        public static final String COLUMN_TYPE_CHALLENGE = "typeChallenge";
        public static final String COLUMN_CONDITION = "condition";
        public static final String COLUMN_AWARD = "award";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(CHALLENGES).build();

        public static Uri buildUriChallenge(String id) {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }

        public static final String SQL_CREATE_CHALLENGE_TABLE =
                "CREATE TABLE " + Challenge.TABLE_NAME +
                        " (" +
                        DataBaseContract.COLUMN_UID     + " TEXT PRIMARY KEY, " +
                        Challenge.COLUMN_TYPE_CHALLENGE + " INTEGER, " +
                        Challenge.COLUMN_CONDITION      + " INTEGER, " +
                        Challenge.COLUMN_AWARD          + " INTEGER " +
                        " ) ";

        public static final String SQL_DELETE_CHALLENGE_TABLE = "DROP TABLE IF EXISTS " + Challenge.TABLE_NAME;
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class Problem implements BaseColumns
    {
        public static final String TABLE_NAME = "problem";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_USER_ID = "user_id";

        public static final Uri URI_CONTENT = URI_BASE.buildUpon().appendPath(PROBLEMS).build();

        public static Uri buildUriProblem(String id)
        {
            return URI_CONTENT.buildUpon().appendPath(id).build();
        }
        public static final String SQL_CREATE_PROBLEM_TABLE =
                "CREATE TABLE " + Problem.TABLE_NAME +
                        " (" +
                        Problem._ID                            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Problem.COLUMN_DESCRIPTION             + " TEXT, " +
                        Problem.COLUMN_USER_ID                 + " TEXT "  + DataBaseContract.REFERENCE_USER_ID  +", " +
                        DataBaseContract.COLUMN_DATE           + " TEXT, " +
                        DataBaseContract.COLUMN_STATE_SYNC     + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.STATE_OK  +", "+
                        DataBaseContract.COLUMN_INSERT_STATE   + " INTEGER NOT NULL DEFAULT "+ DataBaseContract.INSERT_OK +
                        " ) ";

        public static final String SQL_DELETE_PROBLEM_TABLE = "DROP TABLE IF EXISTS " + Problem.TABLE_NAME;


    }
}
