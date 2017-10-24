package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Daniel on 23/03/2017.
 */
public class RouteModel extends BikeMeModel {

    private static final int DAYS_BEFORE_FOR_NEW_ROUTES = 30;
    public static final int MIN_CALIFICATIONS = 15;
    private static final double RADIUS_IN_METERS = 1000;
    private static final double RADIUS_EARTH = 6378000;

    public static final String UID_KEY = "uid";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_KEY = "image";
    public static final String AVERAGE_RATINGS_KEY = "average_ratings";

    public ContentResolver contentResolver;
    public RatingModel ratingModel;

    public RouteModel(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
        this.ratingModel = new RatingModel(contentResolver);
    }

    public void saveRouteCreatedRemoteToLocal(Route routeCreatedRemote)
    {
        ContentValues valuesRoute = new ContentValues();
        ContentValues valuesPoint;
        ContentValues valuesRating;

        valuesRoute.put(DataBaseContract.COLUMN_UID, routeCreatedRemote.getUid());
        valuesRoute.put(DataBaseContract.Route.COLUMN_CREATOR, routeCreatedRemote.getCreator());
        valuesRoute.put(DataBaseContract.Route.COLUMN_NAME_ROUTE, routeCreatedRemote.getName());
        valuesRoute.put(DataBaseContract.Route.COLUMN_DESCRIPTION_ROUTE, routeCreatedRemote.getDescription());
        valuesRoute.put(DataBaseContract.Route.COLUMN_DISTANCE, routeCreatedRemote.getDistance());
        valuesRoute.put(DataBaseContract.Route.COLUMN_LEVEL, routeCreatedRemote.getLevel());
        valuesRoute.put(DataBaseContract.Route.COLUMN_DEPARTURE, routeCreatedRemote.getDeparture());
        valuesRoute.put(DataBaseContract.Route.COLUMN_ARRIVAL, routeCreatedRemote.getArrival());
        valuesRoute.put(DataBaseContract.Route.COLUMN_IMAGE, routeCreatedRemote.getImage());
        valuesRoute.put(DataBaseContract.COLUMN_CREATED, routeCreatedRemote.getCreated());

        contentResolver.insert(DataBaseContract.Route.URI_CONTENT, valuesRoute);
        contentResolver.notifyChange(DataBaseContract.Route.URI_CONTENT, null, false);

        for (Point point : routeCreatedRemote.getPoints())
        {
            valuesPoint = new ContentValues();
            valuesPoint.put(DataBaseContract.Point.COLUMN_ID, point.getId());
            valuesPoint.put(DataBaseContract.Point.COLUMN_ROUTE_ID, point.getRoute());
            valuesPoint.put(DataBaseContract.Point.COLUMN_LATITUDE, point.getLatitude());
            valuesPoint.put(DataBaseContract.Point.COLUMN_LONGITUDE, point.getLongitude());
            contentResolver.insert(DataBaseContract.Point.URI_CONTENT, valuesPoint);
        }
        for (Rating rating : routeCreatedRemote.getRatings())
        {
            valuesRating = new ContentValues();
            valuesRating.put(DataBaseContract.Rating.COLUMN_ROUTE_ID, rating.getRoute());
            valuesRating.put(DataBaseContract.Rating.COLUMN_USER_ID, rating.getUser());
            valuesRating.put(DataBaseContract.Rating.COLUMN_CALIFICATION, rating.getCalification());
            valuesRating.put(DataBaseContract.Rating.COLUMN_RECOMMENDATION, rating.getRecommendation());
            valuesRating.put(DataBaseContract.COLUMN_DATE, rating.getDate());
            contentResolver.insert(DataBaseContract.Rating.URI_CONTENT, valuesRating);
        }
    }

    /**
     * Get suggests routes for show to user
     */
    public ArrayList<HashMap<String, Object>> getSuggestRoutes(String userId, Location location)
    {
        Uri uri = DataBaseContract.User.buildUriUserSuggestRoutes(userId);

        String[] selectionArgs;
        if (location == null)
        {
            selectionArgs = null;
        }
        else
        {
            selectionArgs = getRegionLocation(location);
        }
        Cursor cursor = contentResolver.query(uri, null, null, selectionArgs, null);
        assert cursor != null;
        return getRoutesForRecyclerView(cursor);
    }

    /**
     * Get new routes for show to user
     */
    public ArrayList<HashMap<String, Object>> getNewRoutes(String userId, Location location)
    {
        Uri uri = DataBaseContract.Route.buildUriNewRoutes();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_BEFORE_FOR_NEW_ROUTES);
        String dateDaysNewRouteBefore = BikeMeApplication.getInstance().getDateString(calendar.getTime());
        String[] selectionArgs;
        if (location == null)
        {
            selectionArgs = new String[]{dateDaysNewRouteBefore, String.valueOf(MIN_CALIFICATIONS), userId};
        }
        else
        {
            String[] locations = getRegionLocation(location);
            selectionArgs = new String[]{dateDaysNewRouteBefore, String.valueOf(MIN_CALIFICATIONS), userId, locations[0], locations[1], locations[2], locations[3]};
        }

        Cursor cursor = contentResolver.query(uri, null, null, selectionArgs, null);
        assert cursor != null;
        return getRoutesForRecyclerView(cursor);
    }

    /**
     * get routes whiout charge points only to show "
     */
    public ArrayList<HashMap<String, Object>> getMineRoutes(String userId)
    {
        Uri uri = DataBaseContract.User.buildUriUserRoutesMine(userId);

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        assert cursor != null;
        return getRoutesForRecyclerView(cursor);
    }

    public String[] getRegionLocation(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        double value = (RADIUS_IN_METERS / RADIUS_EARTH) * (180 / Math.PI);
        double minLatitude = latitude - value;
        double minLongitude = longitude - (value / Math.cos(Math.toRadians(latitude)));

        double maxLatitude = latitude + value;
        double maxLongitude = longitude + (value / Math.cos(Math.toRadians(latitude)));

        return new String[]{String.valueOf(minLatitude), String.valueOf(minLongitude), String.valueOf(maxLatitude), String.valueOf(maxLongitude)};
    }


    public ArrayList<HashMap<String, Object>> getRoutesForRecyclerView(Cursor cursor)
    {
        ArrayList<HashMap<String, Object>> routesMap = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                String uid = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
                String name = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_NAME_ROUTE));
                String description = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DESCRIPTION_ROUTE));
                String image = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_IMAGE));
                double average_ratings = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.AVERAGE_RATINGS));
                average_ratings = ((double) Math.round((average_ratings) * 10) / 10.0);

                HashMap<String, Object> routeMap = new LinkedHashMap<>();
                routeMap.put(RouteModel.UID_KEY, uid);
                routeMap.put(RouteModel.NAME_KEY, name);
                routeMap.put(RouteModel.DESCRIPTION_KEY, description);
                routeMap.put(RouteModel.IMAGE_KEY, image);
                routeMap.put(RouteModel.AVERAGE_RATINGS_KEY, average_ratings);

                routesMap.add(routeMap);
            }
        }
        cursor.close();
        return routesMap;
    }

    /** get routes */
    public Route getRouteById(String routeId)
    {
        Uri uri = DataBaseContract.Route.buildUriRoute(routeId);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;
        Route route = new Route();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                route = getRoute(cursor);
            }
        }
        cursor.close();
        return route;
    }

    public Route getRoute(Cursor cursor)
    {
        String uid = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
        String creator = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_CREATOR));
        String name = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_NAME_ROUTE));
        String description = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DESCRIPTION_ROUTE));
        double distance = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DISTANCE));
        int level = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_LEVEL));
        String departure = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_DEPARTURE));
        String arrival = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_ARRIVAL));
        String image = cursor.getString(cursor.getColumnIndex(DataBaseContract.Route.COLUMN_IMAGE));
        String created = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_CREATED));
        ArrayList<Point> pointRoute = getPointsByRoute(uid);
        ArrayList<Rating> ratings = getRatingsByRoute(uid);

        Route route = new Route();
        route.setUid(uid);
        route.setCreator(creator);
        route.setName(name);
        route.setDescription(description);
        route.setDistance(distance);
        route.setLevel(level);
        route.setDeparture(departure);
        route.setArrival(arrival);
        route.setImage(image);
        route.setCreated(created);
        route.setPoints(pointRoute);
        route.setRatings(ratings);

        return route;
    }

    public ArrayList<Point> getPointsByRoute(String idRoute)
    {
        Uri uri = DataBaseContract.Route.buildUriRoutePoints(idRoute);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;
        ArrayList<Point> points = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                points.add(getPoint(cursor));
            }
        }
        cursor.close();
        return points;
    }

    public Point getPoint(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Point.COLUMN_ID));
        double latitude = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Point.COLUMN_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DataBaseContract.Point.COLUMN_LONGITUDE));
        String routeId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Point.COLUMN_ROUTE_ID));

        Point point = new Point();
        point.setId(id);
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setRoute(routeId);
        return point;
    }

    public ArrayList<Rating> getRatingsByRoute(String idRoute)
    {
        Uri uri = DataBaseContract.Route.buildUriRouteRatings(idRoute);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;
        ArrayList<Rating> ratings = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ratings.add(ratingModel.getRating(cursor));
            }
        }
        cursor.close();
        return ratings;
    }

    public ContentProviderOperation insertOperationRoute(Route route)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Route.URI_CONTENT)
                .withValue(DataBaseContract.COLUMN_UID, route.getUid())
                .withValue(DataBaseContract.Route.COLUMN_CREATOR, route.getCreator())
                .withValue(DataBaseContract.Route.COLUMN_NAME_ROUTE, route.getName())
                .withValue(DataBaseContract.Route.COLUMN_DESCRIPTION_ROUTE, route.getDescription())
                .withValue(DataBaseContract.Route.COLUMN_DISTANCE, route.getDistance())
                .withValue(DataBaseContract.Route.COLUMN_LEVEL, route.getLevel())
                .withValue(DataBaseContract.Route.COLUMN_DEPARTURE, route.getDeparture())
                .withValue(DataBaseContract.Route.COLUMN_ARRIVAL, route.getArrival())
                .withValue(DataBaseContract.Route.COLUMN_IMAGE, route.getImage())
                .withValue(DataBaseContract.COLUMN_CREATED, route.getCreated())
                .build();
    }

    public ContentProviderOperation insertOperationPoint(Point point)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Point.URI_CONTENT)
                .withValue(DataBaseContract.Point.COLUMN_ID, point.getId())
                .withValue(DataBaseContract.Point.COLUMN_LATITUDE, point.getLatitude())
                .withValue(DataBaseContract.Point.COLUMN_LONGITUDE, point.getLongitude())
                .withValue(DataBaseContract.Point.COLUMN_ROUTE_ID, point.getRoute())
                .build();
    }
}