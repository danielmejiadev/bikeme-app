package com.android.bikeme.mvp.views.workout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.DownloadMapOfflineService;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOfflineActivity;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapzen.android.lost.api.FusedLocationProviderApi;
import com.mapzen.android.lost.internal.FusedLocationProviderApiImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 20 sep 2017.
 */
public class WorkoutMapOffline implements OnMapReadyCallback {

    private static final int PADDING_BOUNDARY_PIXELS = 128;

    public interface MapOfflineReadyCallback{
        void onMapOfflineReady();
    }

    private MapView mMapView;
    private MapboxMap mapboxMap;
    private Polyline polyline;
    private ArrayList<LatLng> points;
    private MapOfflineReadyCallback mapOfflineReadyCallback;
    private Marker myLocationMarker;
    private Context context;

    public WorkoutMapOffline(MapView mMapView)
    {
        this.mMapView = mMapView;
        this.context = mMapView.getContext();
    }

    public void setMapOfflineReadyCallback(MapOfflineReadyCallback mapOnlineReadyCallback)
    {
        this.mapOfflineReadyCallback = mapOnlineReadyCallback;
    }

    public void getMapOffline()
    {
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap map)
    {
        mapboxMap = map;
        mapboxMap.setMaxZoomPreference(DownloadMapOfflineService.MAX_ZOOM);
        mapboxMap.setMinZoomPreference(DownloadMapOfflineService.MIN_ZOOM);

        LatLng southWest = new LatLng(BaseActivity.SOUTH_WEST_BOUND[0], BaseActivity.SOUTH_WEST_BOUND[1]);
        LatLng northEast = new LatLng(BaseActivity.NORTH_EAST_BOUND[0], BaseActivity.NORTH_EAST_BOUND[1]);

        LatLngBounds boundsRegion = new LatLngBounds.Builder()
                .include(southWest)
                .include(northEast)
                .build();

        mapboxMap.setLatLngBoundsForCameraTarget(boundsRegion);

        points = new ArrayList<>();

        if(mapOfflineReadyCallback!=null)
        {
            mapOfflineReadyCallback.onMapOfflineReady();
        }
    }

    public void drawMyLocationMarker(Location location)
    {
        if(myLocationMarker != null)
        {
            mapboxMap.removeMarker(myLocationMarker);
        }

       myLocationMarker = mapboxMap.addMarker(new MarkerOptions()
               .icon(IconFactory.getInstance(context).fromResource(R.drawable.mapbox_mylocation_icon_default))
               .position(new LatLng(location.getLatitude(),location.getLongitude())));
    }

    public void staticMapOnline()
    {
        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public void moveCameraLocation(Location location)
    {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),DownloadMapOfflineService.MAX_ZOOM));
    }

    public void moveCameraBounds(LatLngBounds latLngBounds)
    {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, PADDING_BOUNDARY_PIXELS));
    }

    public void drawMarker(MarkerOptions markerOptions)
    {
        mapboxMap.addMarker(markerOptions);
    }

    public void drawPolyline(LatLng point)
    {
        if(points.size() >= 2)
        {
            if(polyline==null)
            {
                polyline = mapboxMap.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .width(5)
                        .color(ContextCompat.getColor(mMapView.getContext(),R.color.primary)));
            }
            else
            {
                polyline.addPoint(point);
            }
        }
        else
        {
            points.add(point);
        }
    }

    public void restartMap()
    {
        if(polyline!=null)
        {
            mapboxMap.removePolyline(polyline);
        }
        points.clear();
        polyline = null;
    }
}
