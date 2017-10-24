package com.android.bikeme.mvp.interactors.routes.create_route;

import com.android.bikeme.classes.Point;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenter;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 29/04/2017.
 */
public interface MapRouteStepInteractor {
    void calculateRoute(GeoApiContext geoApiContext, ArrayList<LatLng> pointsToRoute, MapRouteStepPresenter.OnFinishedSnapToRoadsCallback callbackPresenter);
    void validateRoute(ArrayList<Point> routePoints, double routeDistance, MapRouteStepPresenter.OnFinishedValidateRouteCallback callbackValidateRoute, StepperLayout.OnNextClickedCallback callback);
    DirectionsRoute directionsApi(final GeoApiContext geoApiContext, final ArrayList<com.google.maps.model.LatLng> pointsToRoute) throws Exception;
    double calculateDistance(DirectionsRoute route);
    com.google.android.gms.maps.model.LatLng[] simplifyRoute(List<LatLng> points);
}
