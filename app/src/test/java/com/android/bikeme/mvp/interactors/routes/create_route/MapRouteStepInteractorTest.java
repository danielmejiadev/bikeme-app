package com.android.bikeme.mvp.interactors.routes.create_route;

import android.util.Log;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RatingModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.interactors.routes.route_detail.RouteDetailInteractorImpl;
import com.android.bikeme.mvp.presenters.routes.create_route.MapRouteStepPresenter;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Daniel on 18 oct 2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class MapRouteStepInteractorTest {

    private MapRouteStepInteractor mapRouteStepInteractor;

    @Mock
    private GeoApiContext geoApiContext;
    @Mock
    private ArrayList<com.google.maps.model.LatLng> pointsToRoute;

    private DirectionsRoute directionsRoute;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Log.class);
        mapRouteStepInteractor = Mockito.spy(new MapRouteStepInteractorImpl());
        directionsRoute = Mockito.spy(new DirectionsRoute());
        directionsRoute.overviewPolyline = Mockito.spy(new EncodedPolyline(""));
        Mockito.doReturn(new ArrayList<>()).when(directionsRoute.overviewPolyline).decodePath();
        Mockito.doReturn(directionsRoute).when(mapRouteStepInteractor).directionsApi(geoApiContext, pointsToRoute);
        Mockito.doReturn(1.0).when(mapRouteStepInteractor).calculateDistance(Mockito.any(DirectionsRoute.class));
        Mockito.doReturn(new LatLng[]{}).when(mapRouteStepInteractor).simplifyRoute(Mockito.any(ArrayList.class));
    }

    @Test
    public void saveRatingRouteTest() {
        mapRouteStepInteractor.calculateRoute(geoApiContext,pointsToRoute,new MapRouteStepPresenter.OnFinishedSnapToRoadsCallback() {
            @Override
            public void onFinishedSnapToRoads(LatLng[] snappedPoints, double routeDistance) {
                Assert.assertNotNull(snappedPoints);
                Assert.assertTrue(routeDistance>0);
            }

            @Override
            public void onErrorSnapToRoads() {
                Assert.assertTrue(false);
            }
        });
    }
}