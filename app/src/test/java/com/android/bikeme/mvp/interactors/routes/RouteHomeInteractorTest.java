package com.android.bikeme.mvp.interactors.routes;

import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by Daniel on 17 oct 2017.
 */
public class RouteHomeInteractorTest {

    @Mock
    private RouteModel routeModel;

    private RouteHomeInteractor routeHomeInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        routeHomeInteractor = new RouteHomeInteractorImpl(routeModel);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new Route();
            }
        }).when(routeModel).getRouteById(Mockito.anyString());
    }

    @Test
    public void getRouteTest() {
        routeHomeInteractor.getRoute(Mockito.anyString(), new RouteHomePresenter.onRouteDetailCallback() {
            @Override
            public void onFinished(Route route) {
                Assert.assertNotNull(route);
            }
        });
    }
}