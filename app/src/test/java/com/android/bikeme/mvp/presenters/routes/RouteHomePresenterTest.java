package com.android.bikeme.mvp.presenters.routes;

import android.content.Context;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.interactors.routes.RouteHomeInteractor;
import com.android.bikeme.mvp.views.routes.RouteHomeView;
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
public class RouteHomePresenterTest {

    private RouteHomePresenter routeHomePresenter;
    @Mock
    private RouteHomeInteractor interactor;
    @Mock
    private RouteHomeView view;
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        routeHomePresenter = new RouteHomePresenterImpl(view,context,interactor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((RouteHomePresenter.onRouteDetailCallback) objects[1]).onFinished(new Route());
                return null;
            }
        }).when(interactor).getRoute(Mockito.anyString(),Mockito.any(RouteHomePresenter.onRouteDetailCallback.class));
    }

    @Test
    public void navigateToRouteDetailTest() {
        routeHomePresenter.routeDetailItemClicked(Mockito.anyString());

        Mockito.verify(interactor,Mockito.times(1)).getRoute(Mockito.anyString(),Mockito.any(RouteHomePresenter.onRouteDetailCallback.class));
        Mockito.verify(view).navigateToRouteDetail(Mockito.any(Route.class));
    }
}