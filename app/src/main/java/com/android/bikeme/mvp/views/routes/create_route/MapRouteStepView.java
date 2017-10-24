package com.android.bikeme.mvp.views.routes.create_route;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.stepstone.stepper.StepperLayout;

/**
 * Created by Daniel on 21/04/2017.
 */
public interface MapRouteStepView {

    void addMarkerToMap(LatLng latLng);
    void drawSnappedRoute(LatLng[] snappedPoints, double distance);
    void goToNext(StepperLayout.OnNextClickedCallback callback);

    void updateMarkersAvailable(int value);
    void showProgressBar(String message);
    void hideProgressBar();
    void showError(String message);
    void moveCameraBounds(LatLngBounds latLngBounds, int padding);
    void resetData();
    void clearMap();
}
