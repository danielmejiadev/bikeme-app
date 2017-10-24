package com.android.bikeme.databaselocal.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.bikeme.databaselocal.databasesqlite.DataBase;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.databasesqlite.SqlStatements;

public class BikeMeContentProvider extends ContentProvider {

    private DataBase dataBase;
    private ContentResolver contentResolver;

    public static final UriMatcher uriMatcher;

    public static final int USERS_CASE = 100;
    public static final int USERS_ID_CASE = 101;
    public static final int USERS_RATINGS_CASE = 102;
    public static final int USERS_WORKOUTS_CASE = 103;
    public static final int USERS_CHALLENGES_PARAMS_CASE = 104;
    public static final int USER_ROUTES_SUGGEST = 105;
    public static final int USER_ROUTES_MINE = 106;

    public static final int WORKOUTS_CASE = 700;
    public static final int WORKOUTS_ID_CASE = 701;

    public static final int ROUTES_CASE = 200;
    public static final int ROUTES_ID_CASE = 201;
    public static final int ROUTES_RATINGS_CASE = 202;
    public static final int ROUTES_POINTS_CASE = 203;
    public static final int NEW_ROUTES_CASE = 204;

    public static final int RATINGS_CASE = 300;
    public static final int RATINGS_ID_CASE = 301;

    public static final int POINTS_CASE = 400;
    public static final int POINTS_ID_CASE = 401;

    public static final int EVENTS_CASE = 500;
    public static final int EVENTS_ID_CASE = 501;
    private static final int EVENTS_GUEST_CASE = 502;
    private static final int EVENTS_GROUP_CASE = 503;

    public static final int GUESTS_CASE = 600;
    public static final int GUESTS_ID_CASE = 601;

    public static final int CHALLENGES_CASE = 800;
    public static final int CHALLENGES_ID_CASE = 801;

    public static final int PROBLEMS_CASE = 900;
    public static final int PROBLEMS_ID_CASE = 901;



    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS, USERS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*", USERS_ID_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*/"+DataBaseContract.RATINGS, USERS_RATINGS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*/"+DataBaseContract.WORKOUTS, USERS_WORKOUTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*/"+DataBaseContract.CHALLENGES_PARAMS, USERS_CHALLENGES_PARAMS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*/"+DataBaseContract.ROUTES+"/"+DataBaseContract.SUGGEST, USER_ROUTES_SUGGEST);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.USERS+"/*/"+DataBaseContract.ROUTES+"/"+DataBaseContract.MINE, USER_ROUTES_MINE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.WORKOUTS, WORKOUTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.WORKOUTS+"/*", WORKOUTS_ID_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.ROUTES, ROUTES_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.ROUTES+"/*", ROUTES_ID_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.ROUTES+"/*/"+DataBaseContract.RATINGS, ROUTES_RATINGS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.ROUTES+"/*/"+DataBaseContract.POINTS, ROUTES_POINTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.SUGGEST+"/"+DataBaseContract.NEWS, NEW_ROUTES_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.RATINGS, RATINGS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.RATINGS+"/*", RATINGS_ID_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.POINTS, POINTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.POINTS+"/*", POINTS_ID_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.EVENTS, EVENTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.EVENTS+"/*", EVENTS_ID_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.EVENTS+"/*/"+DataBaseContract.GUESTS, EVENTS_GUEST_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.GROUP+"/"+DataBaseContract.EVENTS, EVENTS_GROUP_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.GUESTS, GUESTS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.GUESTS+"/*", GUESTS_ID_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.CHALLENGES, CHALLENGES_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.CHALLENGES+"/*", CHALLENGES_ID_CASE);

        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.PROBLEMS, PROBLEMS_CASE);
        uriMatcher.addURI(DataBaseContract.AUTHORITY, DataBaseContract.PROBLEMS+"/*", PROBLEMS_ID_CASE);
    }

    public BikeMeContentProvider()
    {

    }

    @Override
    public boolean onCreate()
    {
        dataBase = new DataBase(getContext());
        contentResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri))
        {
            case USERS_CASE:
                cursor = db.query(DataBaseContract.User.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.User.URI_CONTENT);
                break;
            case USERS_ID_CASE:
                String idUser = DataBaseContract.User.getUserId(uri);
                cursor = db.query(DataBaseContract.User.TABLE_NAME, projection, DataBaseContract.COLUMN_UID + " = " + "'"+idUser+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.User.URI_CONTENT);
                break;
            case USERS_RATINGS_CASE:
                String idUserRating = DataBaseContract.User.getUserId(uri);
                cursor = db.query(DataBaseContract.Rating.TABLE_NAME, projection, DataBaseContract.Rating.COLUMN_USER_ID + " = " + "'"+idUserRating+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.User.URI_CONTENT);
                break;
            case USERS_WORKOUTS_CASE:
                String idUserWorkout = DataBaseContract.User.getUserId(uri);
                cursor = db.rawQuery(SqlStatements.getSQLUserWorkouts(idUserWorkout),null);
                cursor.setNotificationUri(contentResolver, DataBaseContract.User.URI_CONTENT);
                break;
            case USERS_CHALLENGES_PARAMS_CASE:
                String idUserParams = DataBaseContract.User.getUserId(uri);
                cursor = db.rawQuery(SqlStatements.getSQLUserChallengesParams(idUserParams,selectionArgs),null);
                cursor.setNotificationUri(contentResolver, DataBaseContract.User.URI_CONTENT);
                break;

            case WORKOUTS_CASE:
                cursor = db.query(DataBaseContract.Workout.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Workout.URI_CONTENT);
                break;
            case WORKOUTS_ID_CASE:
                String idWorkout = DataBaseContract.Workout.getWorkoutId(uri);
                cursor = db.query(DataBaseContract.Workout.TABLE_NAME, projection, DataBaseContract.COLUMN_UID + " = " + "'"+idWorkout+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Workout.URI_CONTENT);
                break;

            case ROUTES_CASE:
                cursor = db.query(DataBaseContract.Route.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Route.URI_CONTENT);
                break;
            case ROUTES_ID_CASE:
                String idRoute = DataBaseContract.Route.getRouteId(uri);
                cursor = db.query(DataBaseContract.Route.TABLE_NAME, projection, DataBaseContract.COLUMN_UID + " = " + "'"+idRoute+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Route.URI_CONTENT);
                break;
            case ROUTES_RATINGS_CASE:
                String idRouteRating = DataBaseContract.Route.getRouteId(uri);
                cursor = db.query(DataBaseContract.Rating.TABLE_NAME, projection, DataBaseContract.Rating.COLUMN_ROUTE_ID + " = " + "'"+idRouteRating+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Rating.URI_CONTENT);
                break;
            case ROUTES_POINTS_CASE:
                String idRoutePoint = DataBaseContract.Route.getRouteId(uri);
                cursor = db.query(DataBaseContract.Point.TABLE_NAME, projection, DataBaseContract.Point.COLUMN_ROUTE_ID + " = " + "'"+idRoutePoint+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Point.URI_CONTENT);
                break;

            case POINTS_CASE:
                cursor = db.query(DataBaseContract.Point.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Point.URI_CONTENT);
                break;

            case RATINGS_CASE:
                cursor = db.query(DataBaseContract.Rating.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Rating.URI_CONTENT);
                break;

            case EVENTS_CASE:
                cursor = db.query(DataBaseContract.Event.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Event.URI_CONTENT);
                break;
            case EVENTS_ID_CASE:
                String idEvent = DataBaseContract.Event.getEventId(uri);
                cursor = db.query(DataBaseContract.Event.TABLE_NAME, projection, DataBaseContract.COLUMN_UID + " = " + "'"+idEvent+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Event.URI_CONTENT);
                break;
            case EVENTS_GUEST_CASE:
                String idEventGuest = DataBaseContract.Event.getEventId(uri);
                SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
                sqLiteQueryBuilder.setTables(DataBaseContract.User.TABLE_NAME+ " INNER JOIN "+ DataBaseContract.Guest.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(DataBaseContract.COLUMN_UID+"="+DataBaseContract.Guest.COLUMN_USER_ID);
                cursor = sqLiteQueryBuilder.query(db,projection, DataBaseContract.Guest.COLUMN_EVENT_ID + " = " + "'"+idEventGuest+"'", selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Point.URI_CONTENT);
                break;
            case EVENTS_GROUP_CASE:
                cursor = db.rawQuery(SqlStatements.getSQLEventsGroupByDate(selectionArgs[0]),null);
                cursor.setNotificationUri(contentResolver, uri);
                break;

            case GUESTS_CASE:
                cursor = db.query(DataBaseContract.Guest.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Guest.URI_CONTENT);
                break;

            case CHALLENGES_CASE:
                cursor = db.query(DataBaseContract.Challenge.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Challenge.URI_CONTENT);
                break;

            case PROBLEMS_CASE:
                cursor = db.query(DataBaseContract.Problem.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(contentResolver, DataBaseContract.Problem.URI_CONTENT);
                break;

            case USER_ROUTES_SUGGEST:
                String idUserToSuggest = DataBaseContract.User.getUserId(uri);
                cursor = db.rawQuery(SqlStatements.getSQLSuggestRoutes(idUserToSuggest,selectionArgs),null);
                cursor.setNotificationUri(contentResolver, uri);
                break;
            case NEW_ROUTES_CASE:
                cursor = db.rawQuery(SqlStatements.getSQLRoutesNews(selectionArgs),null);
                cursor.setNotificationUri(contentResolver, uri);
                break;
            case USER_ROUTES_MINE:
                String idUserRoutesMine = DataBaseContract.User.getUserId(uri);
                cursor = db.rawQuery(SqlStatements.getSQLRoutesMine(idUserRoutesMine),null);
                cursor.setNotificationUri(contentResolver, uri);
                break;
            default:
                throw new IllegalArgumentException("URI unsupport: " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)
    {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        int match = uriMatcher.match(uri);
        ContentValues contentValues;
        if (values != null)
        {
            contentValues = new ContentValues(values);
        } else
        {
            contentValues = new ContentValues();
        }
        Uri uriReturn = null;
        switch (match)
        {
            case USERS_CASE:
                long rowIdUser = db.insert(DataBaseContract.User.TABLE_NAME, null, contentValues);
                if (rowIdUser > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.User.URI_CONTENT,rowIdUser);
                    contentResolver.notifyChange(uriReturn, null, false);
                }
                break;

            case WORKOUTS_CASE:
                long rowIdWorkouts = db.insert(DataBaseContract.Workout.TABLE_NAME, null, contentValues);
                if (rowIdWorkouts > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Workout.URI_CONTENT,rowIdWorkouts);
                    // Notify in Sync workouts for just one notification and also in workout model when insert locally
                    //contentResolver.notifyChange(DataBaseContract.Workout.URI_CONTENT, null, false);
                }
                break;

            case ROUTES_CASE:
                long rowIdRoute = db.insert(DataBaseContract.Route.TABLE_NAME, null, contentValues);
                if (rowIdRoute > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Route.URI_CONTENT,rowIdRoute);
                    // Notify in Sync routes for just one notification and also in Route model when insert locally
                    //contentResolver.notifyChange(DataBaseContract.Route.URI_CONTENT, null, false);
                }
                break;
            case RATINGS_CASE:
                long rowIdRating = db.insert(DataBaseContract.Rating.TABLE_NAME, null, contentValues);
                if (rowIdRating > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Rating.URI_CONTENT,rowIdRating);
                    // Notify in Sync ratings for just one notification and also in rating model when insert locally
                    //contentResolver.notifyChange(DataBaseContract.Rating.URI_CONTENT, null, false);
                }
                break;
            case POINTS_CASE:
            long rowIdPoint = db.insert(DataBaseContract.Point.TABLE_NAME, null, contentValues);
            if (rowIdPoint > 0)
            {
                uriReturn  = ContentUris.withAppendedId(DataBaseContract.Point.URI_CONTENT,rowIdPoint);
                contentResolver.notifyChange(uriReturn, null, false);
            }
            break;
            case EVENTS_CASE:
                long rowIdEvent = db.insert(DataBaseContract.Event.TABLE_NAME, null, contentValues);
                if (rowIdEvent > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Event.URI_CONTENT,rowIdEvent);
                }
                break;
            case GUESTS_CASE:
                long rowIdGuest = db.insert(DataBaseContract.Guest.TABLE_NAME, null, contentValues);
                if (rowIdGuest > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Guest.URI_CONTENT,rowIdGuest);
                    // Notify in Sync guest for just one notification and also in guest model when insert locally
                }
                break;
            case CHALLENGES_CASE:
                long rowIdChallenge = db.insert(DataBaseContract.Challenge.TABLE_NAME, null, contentValues);
                if (rowIdChallenge > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Challenge.URI_CONTENT,rowIdChallenge);
                }
                break;

            case PROBLEMS_CASE:
                long rowIdProblem = db.insert(DataBaseContract.Problem.TABLE_NAME, null, contentValues);
                if (rowIdProblem > 0)
                {
                    uriReturn  = ContentUris.withAppendedId(DataBaseContract.Challenge.URI_CONTENT,rowIdProblem);
                }
                break;
            default:
                throw new IllegalArgumentException("URI unsupport: " + uri);
        }
        return uriReturn;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int affected;
        switch (match)
        {
            case USERS_CASE:
                affected = db.update(DataBaseContract.User.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USERS_ID_CASE:
                String idUser = DataBaseContract.User.getUserId(uri);
                affected = db.update(DataBaseContract.User.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "= '" + idUser + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case WORKOUTS_CASE:
                affected = db.update(DataBaseContract.Workout.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WORKOUTS_ID_CASE:
                String idWorkout = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Workout.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "= '" + idWorkout + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case ROUTES_CASE:
                affected = db.update(DataBaseContract.Route.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTES_ID_CASE:
                String idRoute = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Route.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "= '" + idRoute + "'" +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case RATINGS_CASE:
                affected = db.update(DataBaseContract.Rating.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RATINGS_ID_CASE:
                String idRating = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Rating.TABLE_NAME, values,
                       DataBaseContract.Rating._ID + "=" + idRating + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                      selectionArgs);
                break;

            case POINTS_CASE:
                affected = db.update(DataBaseContract.Point.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POINTS_ID_CASE:
                String idPoint = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Point.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "= '" + idPoint +"'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case EVENTS_CASE:
                affected = db.update(DataBaseContract.Event.TABLE_NAME, values, selection, selectionArgs);
                break;
            case EVENTS_ID_CASE:
                String idEvent = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Event.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "= '" + idEvent + "'" +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case GUESTS_CASE:
                affected = db.update(DataBaseContract.Guest.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GUESTS_ID_CASE:
                String idGuest = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Guest.TABLE_NAME, values,
                        DataBaseContract.Guest._ID + "=" + idGuest + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            case CHALLENGES_CASE:
                affected = db.update(DataBaseContract.Challenge.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHALLENGES_ID_CASE:
                String idChallenge = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Challenge.TABLE_NAME, values,
                        DataBaseContract.COLUMN_UID + "='" + idChallenge +"'" +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);

            case PROBLEMS_CASE:
                affected = db.update(DataBaseContract.Problem.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PROBLEMS_ID_CASE:
                String idProblem = uri.getPathSegments().get(1);
                affected = db.update(DataBaseContract.Problem.TABLE_NAME, values,
                        DataBaseContract.Problem._ID + "=" + idProblem  +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI unsupport: " + uri);
        }
        return affected;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int affected;
        switch (match)
        {
            case USERS_ID_CASE:
                String idUser = DataBaseContract.User.getUserId(uri);
                affected = db.delete(
                        DataBaseContract.User.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idUser + "'" +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;

            case WORKOUTS_ID_CASE:
                String idWorkout = uri.getPathSegments().get(1);
                affected = db.delete(
                        DataBaseContract.User.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idWorkout + "'" +(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;


            case ROUTES_ID_CASE:
                long idRoute = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Route.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idRoute + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;

            case RATINGS_ID_CASE:
                long idRating = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Rating.TABLE_NAME,
                        DataBaseContract.Rating._ID + "=" + idRating + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);;
                break;

            case POINTS_ID_CASE:
                long idPoint = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Point.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idPoint + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;

            case EVENTS_ID_CASE:
                long idEvents = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Event.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idEvents + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;

            case GUESTS_ID_CASE:
                long idGuests = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Guest.TABLE_NAME,
                        DataBaseContract.Guest._ID + "=" + idGuests + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;

            case CHALLENGES_ID_CASE:
                long idChallenges = ContentUris.parseId(uri);
                affected = db.delete(
                        DataBaseContract.Challenge.TABLE_NAME,
                        DataBaseContract.COLUMN_UID + "= '" + idChallenges + "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                contentResolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("URI unsupport: " + uri);
        }
        return affected;
    }

    @Override
    public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case USERS_CASE:
                return DataBaseContract.generateMime("users");
            case USERS_ID_CASE:
                return DataBaseContract.generateMimeItem("users");
            case USERS_RATINGS_CASE:
                return DataBaseContract.generateMime("ratings");

            case ROUTES_CASE:
                return DataBaseContract.generateMime("routes");
            case ROUTES_ID_CASE:
                return DataBaseContract.generateMimeItem("routes");
            case ROUTES_RATINGS_CASE:
                return DataBaseContract.generateMime("ratings");
            case ROUTES_POINTS_CASE:
                return DataBaseContract.generateMime("points");

            case RATINGS_CASE:
                return DataBaseContract.generateMime("ratings");

            case POINTS_CASE:
                return DataBaseContract.generateMime("points");

            case EVENTS_CASE:
                return DataBaseContract.generateMime("events");
            case EVENTS_ID_CASE:
                return DataBaseContract.generateMimeItem("events");
            case EVENTS_GUEST_CASE:
                return DataBaseContract.generateMime("guests");

            case GUESTS_CASE:
                return DataBaseContract.generateMime("guests");

            default:
                throw new UnsupportedOperationException("Uri desconocida =>" + uri);
        }
    }
}
