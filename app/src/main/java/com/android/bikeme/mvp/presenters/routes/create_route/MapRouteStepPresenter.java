package com.android.bikeme.mvp.presenters.routes.create_route;

import com.android.bikeme.classes.Point;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 29/04/2017.
 */
public interface MapRouteStepPresenter {

    interface OnFinishedSnapToRoadsCallback {
        void onFinishedSnapToRoads(LatLng[] snappedPoints, double routeDistance);
        void onErrorSnapToRoads();
    }

    interface OnFinishedValidateRouteCallback {
        void onRouteValidate(StepperLayout.OnNextClickedCallback callback);
        void onRouteAlreadyExist();
        void onErrorValidateRoute();
    }

    void onMapClick(LatLng latLng, LatLngBounds bounds);
    void onFabButtonClick(GeoApiContext geoApiContext, ArrayList<com.google.maps.model.LatLng> pointsToRoute, boolean snappedRoute);
    void onGoToNextClick(ArrayList<Point> routePoints, double routeDistance, StepperLayout.OnNextClickedCallback callback);
}
