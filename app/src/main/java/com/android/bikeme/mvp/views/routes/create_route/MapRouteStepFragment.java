package com.android.bikeme.mvp.views.routes.create_route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.mvp.interactors.routes.create_route.MapRouteStepInteractorImpl;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenter;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenterImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.android.ui.IconGenerator;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

public class MapRouteStepFragment extends Fragment implements BlockingStep, MapRouteStepView, OnMapReadyCallback,
        GoogleMap.OnMapClickListener, View.OnClickListener,GoogleMap.OnMarkerDragListener {

    public static final String TAG =  MapRouteStepFragment.class.getSimpleName();

    private static final int PADDING_BOUNDARY_PIXELS = 160;
    private static final int MAX_MARKERS_AVAILABLE = 25;

    private GeoApiContext geoApiContext;
    private GoogleMap googleMap;
    private LatLngBounds boundsRegion;
    private LatLngBounds boundsRoute;
    private ImageButton deleteMarkerZone;
    private Vibrator vibrator;
    private MapRouteStepPresenter mapRouteStepPresenter;
    private CreateRouteActivity parent;

    private double routeDistance;
    private boolean isRouteSnapped;
    private boolean isRouteValid;
    private int markersAvailable;
    private TextView makersAvailableText;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Point> routePoints;
    private Polyline polylineToRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View viewFragment = inflater.inflate(R.layout.create_route_fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_create_route);
        mapFragment.getMapAsync(this);

        geoApiContext = new GeoApiContext().setApiKey(getString(R.string.google_services_key));
        mapRouteStepPresenter = new MapRouteStepPresenterImpl(this,parent,new MapRouteStepInteractorImpl());
        makersAvailableText = (TextView)viewFragment.findViewById(R.id.create_route_map_markers_available_text);

        deleteMarkerZone =  (ImageButton)viewFragment.findViewById(R.id.ibDeleteMarker);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        floatingActionButton = (FloatingActionButton) viewFragment.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        resetData();
        return viewFragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        parent = (CreateRouteActivity)context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        LatLng southWest = new LatLng(3.884502, -76.337831);
        LatLng northEast = new LatLng(4.424216, -75.920681);
        boundsRegion = new LatLngBounds(southWest, northEast);

        moveCameraBounds(boundsRegion,0);
        this.googleMap.setMaxZoomPreference(20);
        this.googleMap.setMinZoomPreference(10);
        this.googleMap.setLatLngBoundsForCameraTarget(boundsRegion);
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMarkerDragListener(this);
        polylineToRoute = this.googleMap.addPolyline(new PolylineOptions().width(8).color(Color.RED));
    }

    @Override
    public VerificationError verifyStep()
    {
        if(isRouteSnapped && !routePoints.isEmpty())
        {
            return null;
        }
        else
        {
           return new VerificationError(parent.getString(R.string.create_route_map_please_draw_route));
        }
    }


    @Override
    public void onSelected()
    {
        Log.i(TAG,"OnStepFragmentSelect " +this.getTag());
    }

    @Override
    public void onError(@NonNull VerificationError error)
    {
        showError(error.getErrorMessage());
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback)
    {
        if(isRouteValid)
        {
            callback.goToNextStep();
        }
        else
        {
            mapRouteStepPresenter.onGoToNextClick(routePoints, routeDistance, callback);
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback)
    {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback)
    {
        callback.goToPrevStep();
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        if(markersAvailable>0 && !isRouteSnapped)
        {
            mapRouteStepPresenter.onMapClick(latLng, boundsRegion);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(isRouteSnapped)
        {
            resetData();
            clearMap();
        }
        else
        {
            ArrayList<com.google.maps.model.LatLng> pointsToRoute = new ArrayList<>();
            for(LatLng latLng : polylineToRoute.getPoints())
            {
                pointsToRoute.add(new com.google.maps.model.LatLng(latLng.latitude,latLng.longitude));
            }
            mapRouteStepPresenter.onFabButtonClick(geoApiContext, pointsToRoute, isRouteSnapped);
        }
    }

    @Override
    public void addMarkerToMap(LatLng latLng)
    {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
        marker.setDraggable(true);
        marker.setTag(latLng);

        List<LatLng> polylinePoints = polylineToRoute.getPoints();
        polylinePoints.add(latLng);
        polylineToRoute.setPoints(polylinePoints);

        updateMarkersAvailable(--markersAvailable);

    }

    @Override
    public void onMarkerDragStart(Marker marker)
    {
        deleteMarkerZone.setVisibility(View.VISIBLE);
        vibrator.vibrate(150);
    }

    @Override
    public void onMarkerDrag(Marker marker)
    {
        android.graphics.Point markerScreenPosition = googleMap.getProjection().toScreenLocation(marker.getPosition());
        if (overlap(markerScreenPosition, deleteMarkerZone))
        {

            deleteMarkerZone.setColorFilter(ContextCompat.getColor(getContext(), R.color.red_button), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            deleteMarkerZone.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        LatLng originalPosition = (LatLng)marker.getTag();
        assert originalPosition != null;
        deleteMarkerZone.setVisibility(View.GONE);
        android.graphics.Point markerScreenPosition = googleMap.getProjection().toScreenLocation(marker.getPosition());
        if (overlap(markerScreenPosition, deleteMarkerZone))
        {
            marker.remove();
            List<LatLng> polylinePoints = polylineToRoute.getPoints();
            polylinePoints.remove(originalPosition);
            polylineToRoute.setPoints(polylinePoints);
            updateMarkersAvailable(++markersAvailable);
        }
        else
        {
            marker.setPosition(originalPosition);
        }
    }

    private boolean overlap(android.graphics.Point point, ImageButton imageButton)
    {
        int[] imageCords = new int[2];
        imageButton.getLocationOnScreen(imageCords);
        boolean overlapX = (point.x <= imageCords[0] + imageButton.getWidth())  && (point.x >= imageCords[0] - imageButton.getWidth());
        boolean overlapY = (point.y <= imageCords[1] + imageButton.getHeight()) && (point.y >= imageCords[1] - imageButton.getHeight());
        return overlapX && overlapY;
    }

    @Override
    public void drawSnappedRoute(LatLng[] snappedPoints, double routeDistanceSnappedRoute)
    {
        isRouteSnapped =true;
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_refresh));
        routeDistance = routeDistanceSnappedRoute;

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(LatLng snappedPoint: snappedPoints)
        {
            routePoints.add(new Point(snappedPoint.latitude, snappedPoint.longitude));
            builder.include(snappedPoint);
        }
        boundsRoute = builder.build();
        moveCameraBounds(boundsRoute,PADDING_BOUNDARY_PIXELS);

        googleMap.addMarker(parent.getMarker(snappedPoints[0],BitmapDescriptorFactory.HUE_RED,getString(R.string.departure_text))).showInfoWindow();
        googleMap.addMarker(parent.getMarker(snappedPoints[snappedPoints.length-1],BitmapDescriptorFactory.HUE_GREEN,getString(R.string.arrival_text))).showInfoWindow();

        googleMap.addPolyline(new PolylineOptions()
                .width(8)
                .add(snappedPoints)
                .color(ContextCompat.getColor(this.getContext(), R.color.primary)));

        LatLng positionDistance = snappedPoints[Math.round((snappedPoints.length-1)/2)];
        IconGenerator iconGenerator = new IconGenerator(getContext());
        iconGenerator.setRotation(90);
        iconGenerator.setContentRotation(-90);
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(BikeMeApplication.getInstance().getStringDistance(routeDistance))))
                .position(positionDistance)
                .anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));
    }

    @Override
    public void goToNext(final StepperLayout.OnNextClickedCallback callback)
    {
        moveCameraBounds(boundsRoute,PADDING_BOUNDARY_PIXELS);
        googleMap.snapshot(new GoogleMap.SnapshotReadyCallback()
        {
            @Override
            public void onSnapshotReady(Bitmap bitmap)
            {
                    isRouteValid =true;
                    Route route = new Route();
                    route.setPoints(routePoints);
                    route.setDistance(routeDistance);
                    route.setImage(BikeMeApplication.getInstance().encodeBitmapToString(bitmap));
                    parent.setRoute(route);
                    callback.goToNextStep();
            }
        });
    }

    @Override
    public void updateMarkersAvailable(int value)
    {
        markersAvailable=value;
        makersAvailableText.setText(String.valueOf(markersAvailable));
    }

    @Override
    public void showProgressBar(String message)
    {
        parent.showProgress(message);
    }

    @Override
    public void hideProgressBar()
    {
        parent.hideProgress();
    }

    @Override
    public void showError(String message)
    {
        Toast.makeText(this.getContext(),message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void moveCameraBounds(LatLngBounds latLngBounds,int padding)
    {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
    }

    @Override
    public void resetData()
    {
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_in_route));
        isRouteSnapped = false;
        isRouteValid =false;
        routePoints = new ArrayList<>();
        routeDistance =0;
        updateMarkersAvailable(MAX_MARKERS_AVAILABLE);
    }

    @Override
    public void clearMap()
    {
        googleMap.clear();
        polylineToRoute = this.googleMap.addPolyline(new PolylineOptions().width(8).color(Color.RED));
    }
}