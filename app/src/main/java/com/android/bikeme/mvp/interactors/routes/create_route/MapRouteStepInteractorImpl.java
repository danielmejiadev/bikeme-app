package com.android.bikeme.mvp.interactors.routes.create_route;

import android.os.AsyncTask;
import android.util.Log;

import com.android.bikeme.classes.Point;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenter;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenterImpl;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.PolylineUtils;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 29/04/2017.
 */
public class MapRouteStepInteractorImpl implements MapRouteStepInteractor {

    private static final String TAG =  MapRouteStepInteractorImpl.class.getSimpleName();
    private static final Integer ROUTE_VALID_KEY = 0 ;

    public MapRouteStepInteractorImpl()
    {

    }

    @Override
    public void calculateRoute(final GeoApiContext geoApiContext, final ArrayList<com.google.maps.model.LatLng> pointsToRoute, final MapRouteStepPresenter.OnFinishedSnapToRoadsCallback onFinishedSnapToRoadsCallback)
    {
        AsyncTask<Void, Void, DirectionsRoute> taskDirectionRoute = new AsyncTask<Void, Void, DirectionsRoute>()
        {
            @Override
            protected DirectionsRoute doInBackground(Void... params)
            {
                try
                {
                    return directionsApi(geoApiContext, pointsToRoute);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Error en la peticion", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(DirectionsRoute route)
            {
                if(route == null)
                {
                    onFinishedSnapToRoadsCallback.onErrorSnapToRoads();
                }
                else
                {
                    double routeDistance = calculateDistance(route);
                    LatLng[] simplifiedRoutePoints = simplifyRoute(route.overviewPolyline.decodePath());
                    onFinishedSnapToRoadsCallback.onFinishedSnapToRoads(simplifiedRoutePoints,routeDistance);
                }
            }
        };
        taskDirectionRoute.execute();
    }

    @Override
    public DirectionsRoute directionsApi(final GeoApiContext geoApiContext, final ArrayList<com.google.maps.model.LatLng> pointsToRoute) throws Exception
    {
        com.google.maps.model.LatLng originPoint = pointsToRoute.get(0);
        com.google.maps.model.LatLng destinationPoint = pointsToRoute.get(pointsToRoute.size()-1);
        pointsToRoute.remove(pointsToRoute.size()-1);
        pointsToRoute.remove(0);
        com.google.maps.model.LatLng[] wayPoints = new com.google.maps.model.LatLng[pointsToRoute.size()];
        for(int i=0; i<pointsToRoute.size();i++)
        {
            wayPoints[i]=pointsToRoute.get(i);
        }

        DirectionsResult resultRequest = DirectionsApi.newRequest(geoApiContext)
                .origin(originPoint)
                .destination(destinationPoint)
                .waypoints(wayPoints)
                .await();

        return  resultRequest.routes[0];
    }

    @Override
    public LatLng[] simplifyRoute(List<com.google.maps.model.LatLng> points)
    {
        Position[] before = new Position[points.size()];
        for (int i = 0; i < points.size(); i++)
        {
            before[i] = Position.fromLngLat(points.get(i).lng, points.get(i).lat);
        }

        Position[] after = PolylineUtils.simplify(before,0.0001);
        LatLng[] result = new LatLng[after.length];
        for (int i = 0; i < after.length; i++)
        {
            result[i] = new LatLng(after[i].getLatitude(), after[i].getLongitude());
        }
        return result;
    }

    @Override
    public double calculateDistance(DirectionsRoute route)
    {
        double routeDistance = 0;
        for(DirectionsLeg  leg: route.legs)
        {
            routeDistance += leg.distance.inMeters;
        }
        return  routeDistance;
    }



    @Override
    public void validateRoute(ArrayList<Point> routePoints, double routeDistance, final MapRouteStepPresenter.OnFinishedValidateRouteCallback onFinishedValidateRouteCallback, final StepperLayout.OnNextClickedCallback callback)
    {
        RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
        EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
        Call<Integer> routeCall = endPointsApi.isRouteValid(routePoints,routeDistance);
        routeCall.enqueue(new Callback<Integer>()
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                if(response.isSuccessful())
                {
                    if (response.body().intValue() == ROUTE_VALID_KEY)
                    {
                        Log.i(TAG, "La ruta no existe, es valida");
                        onFinishedValidateRouteCallback.onRouteValidate(callback);
                    }
                    else
                    {
                        Log.i(TAG, "La ruta existe, no es valida");
                        onFinishedValidateRouteCallback.onRouteAlreadyExist();
                    }
                }
                else
                {
                    onFinishedValidateRouteCallback.onErrorValidateRoute();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                Log.e(TAG, t.toString());
                onFinishedValidateRouteCallback.onErrorValidateRoute();
            }
        });
    }
}