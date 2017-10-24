package com.android.bikeme.bikemeserverconnection;

/**
 * Created by Daniel on 19/03/2017.
 */
public final class RestApiConstants {

    public static final String ROOT_URL = "http://ec2-34-215-91-140.us-west-2.compute.amazonaws.com/bikeme/";
    public static final String API_KEY_DEVELOPMENT = "adf03aabc88d806dcc02a1a1dc26b72d7c1a14a1";
    public static final String API_KEY_PRODUCTION = "a1513671144e6835245d701d0d28bae13b1b3da9";
    public static final String GET_ALL_DATA =  ROOT_URL + "recommendersystem";

    public static final String ROOT_USER = "users/";
    public static final String ROOT_WORKOUT = "workouts/";
    public static final String ROOT_ROUTE = "routes/";
    public static final String ROOT_EVENT = "events/";
    public static final String ROOT_RATING = "ratings/";
    public static final String ROOT_GUEST = "guests/";
    public static final String ROOT_PROBLEM = "problems/";

    public static final String IS_VALID =  "valid";
    public static final String NEW =  "new";
    public static final String UPDATE =  "update";
}
