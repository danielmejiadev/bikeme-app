package com.android.bikeme.mvp.views.workout;

import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by Daniel on 2 sep 2017.
 */
public class WorkoutMapOnline implements OnMapReadyCallback{


    public static final int MY_LOCATION_ZOOM = 17;
    private static final int PADDING_BOUNDARY_PIXELS = 128;

    public interface MapOnlineReadyCallback{
        void onMapOnlineReady();
    }

    private GoogleMap googleMap;
    private MapOnlineReadyCallback mapOnlineReadyCallback;
    private Polyline polyline;
    private SupportMapFragment supportMapFragment;

    public WorkoutMapOnline(SupportMapFragment supportMapFragment)
    {
        this.supportMapFragment = supportMapFragment;
    }

    public void setMapOnlineReadyCallback(MapOnlineReadyCallback mapOnlineReadyCallback)
    {
        this.mapOnlineReadyCallback=mapOnlineReadyCallback;
    }

    public void getMapOnline()
    {
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        googleMap = map;

        LatLng southWest = new LatLng(BaseActivity.SOUTH_WEST_BOUND[0],BaseActivity.SOUTH_WEST_BOUND[1]);
        LatLng northEast = new LatLng(BaseActivity.NORTH_EAST_BOUND[0],BaseActivity.NORTH_EAST_BOUND[1]);
        LatLngBounds latLngBounds = new LatLngBounds(southWest,northEast);

        googleMap.setMaxZoomPreference(20);
        googleMap.setMinZoomPreference(10);
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);

        polyline = googleMap.addPolyline(new PolylineOptions()
                .width(8)
                .color(ContextCompat.getColor(supportMapFragment.getContext(), R.color.primary)));

        if(mapOnlineReadyCallback!=null)
        {
            mapOnlineReadyCallback.onMapOnlineReady();
        }

    }

    public void enableMyLocation()
    {
        googleMap.setMyLocationEnabled(true);
    }

    public void staticMapOnline()
    {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public void moveCameraLocation(Location location)
    {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),MY_LOCATION_ZOOM));
    }

    public void moveCameraBounds(LatLngBounds latLngBounds)
    {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, PADDING_BOUNDARY_PIXELS));
    }

    public void drawMarker(MarkerOptions markerOptions)
    {
        googleMap.addMarker(markerOptions).showInfoWindow();
    }

    public void drawPolyline(LatLng latLng)
    {
        List<LatLng> polylinePoints = polyline.getPoints();
        polylinePoints.add(latLng);
        polyline.setPoints(polylinePoints);
    }

    public void restartMap()
    {
        googleMap.clear();
        polyline = googleMap.addPolyline(new PolylineOptions()
                .width(8)
                .color(ContextCompat.getColor(supportMapFragment.getContext(), R.color.primary)));
    }
}