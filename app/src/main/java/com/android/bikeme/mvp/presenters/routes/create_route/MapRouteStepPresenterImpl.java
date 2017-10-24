package com.android.bikeme.mvp.presenters.routes.create_route;

import android.content.Context;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.mvp.interactors.routes.create_route.MapRouteStepInteractor;
import com.android.bikeme.classes.Point;
import com.android.bikeme.mvp.views.routes.create_route.MapRouteStepView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;

/**
 * Created by Daniel on 29/04/2017.
 */
public class MapRouteStepPresenterImpl implements MapRouteStepPresenter, MapRouteStepPresenter.OnFinishedSnapToRoadsCallback, MapRouteStepPresenter.OnFinishedValidateRouteCallback {

    private MapRouteStepView mapRouteStepView;
    private MapRouteStepInteractor mapRouteStepInteractor;
    private Context context;

    public MapRouteStepPresenterImpl(MapRouteStepView mapRouteStepView, Context context, MapRouteStepInteractor mapRouteStepInteractor)
    {
        this.mapRouteStepView= mapRouteStepView;
        this.mapRouteStepInteractor=mapRouteStepInteractor;
        this.context = context;
    }

    @Override
    public void onMapClick(LatLng latLng, LatLngBounds bounds)
    {
        if(bounds.contains(latLng))
        {
            mapRouteStepView.addMarkerToMap(latLng);
        }
        else
        {
            mapRouteStepView.showError(context.getString(R.string.create_route_map_region_not_avaliable));
        }
    }

    @Override
    public void onFabButtonClick(GeoApiContext geoApiContext, ArrayList<com.google.maps.model.LatLng> pointsToRoute, boolean snappedRoute)
    {
        if(snappedRoute)
        {
            mapRouteStepView.resetData();
            mapRouteStepView.clearMap();
        }
        else
        {
            if(pointsToRoute.size() > 1)
            {
                mapRouteStepView.showProgressBar(context.getString(R.string.create_route_map_drawing_route));
                mapRouteStepInteractor.calculateRoute(geoApiContext,pointsToRoute,this);
            }
            else
            {
                mapRouteStepView.showError(context.getString(R.string.create_route_map_please_draw_route));
            }
        }
    }

    @Override
    public void onFinishedSnapToRoads(LatLng[] snappedPoints, double routeDistance)
    {
        mapRouteStepView.hideProgressBar();
        mapRouteStepView.clearMap();
        mapRouteStepView.drawSnappedRoute(snappedPoints, routeDistance);
    }

    @Override
    public void onErrorSnapToRoads()
    {
        mapRouteStepView.hideProgressBar();
        mapRouteStepView.showError(context.getString(R.string.error_try_again));
        mapRouteStepView.clearMap();
        mapRouteStepView.resetData();
    }


    @Override
    public void onGoToNextClick(ArrayList<Point> routePoints, double routeDistance, StepperLayout.OnNextClickedCallback callback)
    {
        mapRouteStepView.showProgressBar(context.getString(R.string.create_route_map_validation_route));
        mapRouteStepInteractor.validateRoute(routePoints, routeDistance, this, callback);
    }

    @Override
    public void onRouteValidate(StepperLayout.OnNextClickedCallback onNextClickedCallback)
    {
        mapRouteStepView.hideProgressBar();
        mapRouteStepView.goToNext(onNextClickedCallback);
    }

    @Override
    public void onRouteAlreadyExist()
    {
        mapRouteStepView.hideProgressBar();
        mapRouteStepView.clearMap();
        mapRouteStepView.resetData();
        mapRouteStepView.showError(context.getString(R.string.create_route_map_route_already_exits));
    }

    @Override
    public void onErrorValidateRoute()
    {
        mapRouteStepView.hideProgressBar();
        mapRouteStepView.showError(context.getString(R.string.create_route_map_error_validating_route));
    }
}