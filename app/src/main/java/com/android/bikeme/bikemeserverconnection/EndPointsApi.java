package com.android.bikeme.bikemeserverconnection;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.classes.Workout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Daniel on 19/03/2017.
 */
public interface EndPointsApi {

    @GET(RestApiConstants.GET_ALL_DATA)
    Call<ServerResponse> getAllData();


    // Operations whit users
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_USER + RestApiConstants.NEW)
    Call<User> insertUser(@Body User user);

    @PUT(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_USER + "{userUid}/" + RestApiConstants.UPDATE)
    Call<User> updateUser(@Path("userUid") String userUid, @Body User user);


    // Operations whit routes
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_ROUTE + RestApiConstants.IS_VALID)
    Call<Integer> isRouteValid(@Body ArrayList<Point> points, @Query("distance") double routeDistance);

    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_ROUTE + RestApiConstants.NEW)
    Call<Route> insertRoute(@Body Route route);

    // Operations whit ratings
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_ROUTE + RestApiConstants.ROOT_RATING + RestApiConstants.NEW)
    Call<ArrayList<Rating>> insertRatings(@Body ArrayList<Rating> ratings);

    // Operations whit guest
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_EVENT + RestApiConstants.ROOT_GUEST + RestApiConstants.NEW)
    Call<ArrayList<Guest>> insertGuests(@Body ArrayList<Guest> guests);


    // Operations whit workouts
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_USER + RestApiConstants.ROOT_WORKOUT + RestApiConstants.NEW)
    Call<ArrayList<Workout>> insertWorkouts(@Body ArrayList<Workout> workouts);


    // Operations whit problems
    @POST(RestApiConstants.ROOT_URL + RestApiConstants.ROOT_PROBLEM + RestApiConstants.NEW)
    Call<ArrayList<Problem>> insertProblems(@Body ArrayList<Problem> problems);
}
