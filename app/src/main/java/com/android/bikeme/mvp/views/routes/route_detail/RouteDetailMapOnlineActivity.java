package com.android.bikeme.mvp.views.routes.route_detail;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class RouteDetailMapOnlineActivity extends BaseActivity implements OnMapReadyCallback {

    private ArrayList<Point> pointsRoute;
    private double routeDistance;
    private static final int PADDING_BOUNDARY_PIXELS = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail_map_online_activity_view);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content_route_detail_map_online);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_layout);
        mapFragment.getMapAsync(this);

        pointsRoute = getIntent().getExtras().getParcelableArrayList(Route.POINTS_KEY);
        routeDistance = getIntent().getExtras().getDouble(Route.DISTANCE_KEY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        LatLng southWest = new LatLng(SOUTH_WEST_BOUND[0],SOUTH_WEST_BOUND[1]);
        LatLng northEast = new LatLng(NORTH_EAST_BOUND[0],NORTH_EAST_BOUND[1]);
        LatLngBounds boundsRegion = new LatLngBounds(southWest,northEast);


        googleMap.setMaxZoomPreference(20);
        googleMap.setMinZoomPreference(10);
        googleMap.setLatLngBoundsForCameraTarget(boundsRegion);

        Point pointBegin = pointsRoute.get(0);
        LatLng positionBegin = new LatLng(pointBegin.getLatitude(),pointBegin.getLongitude());
        googleMap.addMarker(getMarker(positionBegin,BitmapDescriptorFactory.HUE_RED,getString(R.string.departure_text))).showInfoWindow();

        Point pointEnd = pointsRoute.get(pointsRoute.size()-1);
        LatLng positionEnd = new LatLng(pointEnd.getLatitude(), pointEnd.getLongitude());
        googleMap.addMarker(getMarker(positionEnd,BitmapDescriptorFactory.HUE_GREEN,getString(R.string.arrival_text))).showInfoWindow();

        PolylineOptions polylineOptions = new PolylineOptions()
                .width(8)
                .color(ContextCompat.getColor(this, R.color.primary));


        LatLngBounds.Builder boundsRoute = LatLngBounds.builder();
        for(Point point : pointsRoute)
        {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            polylineOptions.add(latLng);
            boundsRoute.include(latLng);
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsRoute.build(), PADDING_BOUNDARY_PIXELS));
        googleMap.addPolyline(polylineOptions);

        Point pointMiddle = pointsRoute.get(Math.round((pointsRoute.size()-1)/2));
        LatLng positionDistance = new LatLng(pointMiddle.getLatitude(), pointMiddle.getLongitude());
        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setRotation(90);
        iconGenerator.setContentRotation(-90);
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(BikeMeApplication.getInstance().getStringDistance(routeDistance))))
                .position(positionDistance)
                .anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finishActivityTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finishActivityTransition();
    }
}