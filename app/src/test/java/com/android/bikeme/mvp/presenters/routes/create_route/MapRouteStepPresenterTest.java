package com.android.bikeme.mvp.presenters.routes.create_route;

import android.content.Context;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.interactors.routes.create_route.MapRouteStepInteractor;
import com.android.bikeme.mvp.views.routes.create_route.MapRouteStepView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

/**
 * Created by Daniel on 18 oct 2017.
 */
public class MapRouteStepPresenterTest {

    private MapRouteStepPresenter mapRouteStepPresenter;
    @Mock
    private MapRouteStepInteractor mapRouteStepInteractor;
    @Mock
    private MapRouteStepView mapRouteStepView;
    @Mock
    private Context context;
    @Mock
    private ArrayList<com.google.maps.model.LatLng> pointsToRoute;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mapRouteStepPresenter = new MapRouteStepPresenterImpl(mapRouteStepView,context,mapRouteStepInteractor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((MapRouteStepPresenter.OnFinishedSnapToRoadsCallback) objects[2])
                        .onFinishedSnapToRoads(new LatLng[]{},1.0);
                return null;
            }
        }).when(mapRouteStepInteractor).calculateRoute(Mockito.any(GeoApiContext.class),Mockito.any(ArrayList.class),
                Mockito.any(MapRouteStepPresenter.OnFinishedSnapToRoadsCallback.class));

        Mockito.doReturn(2).when(pointsToRoute).size();
    }

    @Test
    public void calculateRouteTest() {
        mapRouteStepPresenter.onFabButtonClick(new GeoApiContext(),pointsToRoute,true);

        Mockito.verify(mapRouteStepInteractor,Mockito.times(1)).calculateRoute(Mockito.any(GeoApiContext.class),
                Mockito.any(ArrayList.class),Mockito.any(MapRouteStepPresenter.OnFinishedSnapToRoadsCallback.class));
        Mockito.verify(mapRouteStepView).drawSnappedRoute(Mockito.any(LatLng[].class),Mockito.anyDouble());
    }
}
