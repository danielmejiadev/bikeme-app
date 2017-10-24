package com.android.bikeme.mvp.presenters.routes.create_route;

import android.content.Context;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.interactors.routes.create_route.CreateRouteInteractor;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenterImpl;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.android.bikeme.mvp.views.routes.create_route.CreateRouteView;

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
public class CreateRoutePresenterTest {

    private CreateRoutePresenter createRoutePresenter;
    @Mock
    private CreateRouteInteractor createRouteInteractor;
    @Mock
    private CreateRouteView createRouteView;
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        createRoutePresenter = new CreateRoutePresenterImpl(context,createRouteView,createRouteInteractor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((CreateRoutePresenter.OnFinishedSaveRouteCallback) objects[1]).onFinishedSaveRoute(new ArrayList<Challenge>(),new ArrayList<Integer>());
                return null;
            }
        }).when(createRouteInteractor).saveRoute(Mockito.any(Route.class),Mockito.any(CreateRoutePresenter.OnFinishedSaveRouteCallback.class));
    }

    @Test
    public void createRouteTest() {
        createRoutePresenter.onSaveRoute(Mockito.any(Route.class));

        Mockito.verify(createRouteInteractor,Mockito.times(1)).saveRoute(Mockito.any(Route.class),Mockito.any(CreateRoutePresenter.OnFinishedSaveRouteCallback.class));
        Mockito.verify(createRouteView).showChallengesLevelsAchieved(Mockito.any(ArrayList.class),Mockito.any(ArrayList.class));
    }
}
