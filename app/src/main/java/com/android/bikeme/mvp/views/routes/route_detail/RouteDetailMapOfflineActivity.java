package com.android.bikeme.mvp.views.routes.route_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.DownloadMapOfflineService;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.google.maps.android.ui.IconGenerator;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class RouteDetailMapOfflineActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PADDING_BOUNDARY_PIXELS = 92;

    private ArrayList<Point> pointsRoute;
    private double routeDistance;
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail_map_offline_activity_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content_route_detail_map_offline);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        pointsRoute = getIntent().getExtras().getParcelableArrayList(Route.POINTS_KEY);
        routeDistance = getIntent().getExtras().getDouble(Route.DISTANCE_KEY);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap)
    {
        IconFactory iconFactory = IconFactory.getInstance(this);

        LatLng southWest = new LatLng(SOUTH_WEST_BOUND[0], SOUTH_WEST_BOUND[1]);
        LatLng northEast = new LatLng(NORTH_EAST_BOUND[0], NORTH_EAST_BOUND[1]);

        LatLngBounds boundsRegion = new LatLngBounds.Builder()
                .include(southWest)
                .include(northEast)
                .build();

        mapboxMap.setMaxZoomPreference(DownloadMapOfflineService.MAX_ZOOM);
        mapboxMap.setMinZoomPreference(DownloadMapOfflineService.MIN_ZOOM);
        mapboxMap.setLatLngBoundsForCameraTarget(boundsRegion);

        Point pointBegin = pointsRoute.get(0);
        Point pointEnd = pointsRoute.get(pointsRoute.size() - 1);

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(pointBegin.getLatitude(), pointBegin.getLongitude()))
                .title(getString(R.string.departure_text)));


        Icon icon = iconFactory.fromBitmap(getMarkerGreen(this));

        mapboxMap.addMarker(new MarkerOptions()
                .icon(icon)
                .position(new LatLng(pointEnd.getLatitude(), pointEnd.getLongitude()))
                .title(getString(R.string.arrival_text)));

        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(ContextCompat.getColor(this, R.color.primary));

        LatLngBounds.Builder boundsRoute = new LatLngBounds.Builder();
        for (Point point : pointsRoute)
        {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            polylineOptions.add(latLng);
            boundsRoute.include(latLng);
        }

        mapboxMap.addPolyline(polylineOptions);
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsRoute.build(), PADDING_BOUNDARY_PIXELS));

        Point pointMiddle = pointsRoute.get(Math.round((pointsRoute.size() - 1) / 2));
        LatLng positionDistance = new LatLng(pointMiddle.getLatitude(), pointMiddle.getLongitude());
        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setRotation(90);
        iconGenerator.setContentRotation(-90);

        mapboxMap.addMarker(new MarkerOptions()
                .icon(iconFactory.fromBitmap(iconGenerator.makeIcon(BikeMeApplication.getInstance().getStringDistance(routeDistance))))
                .position(positionDistance));
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

    public static Bitmap getMarkerGreen(Context context)
    {
        Drawable vectorDrawable = context.getDrawable(R.drawable.map_box_green_marker);
        Drawable drawableDefault = context.getDrawable(R.drawable.mapbox_marker_icon_default);
        assert drawableDefault != null;
        Bitmap bitmap = Bitmap.createBitmap(drawableDefault.getIntrinsicWidth(), drawableDefault.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return bitmap;
    }
}