package com.android.bikeme.mvp.presenters.routes.route_detail;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.mvp.interactors.routes.route_detail.RouteDetailInteractor;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

/**
 * Created by Daniel on 17 oct 2017.
 */
public class RouteDetailPresenterTest {

    private RouteDetailPresenter routeDetailPresenter;
    @Mock
    private RouteDetailInteractor routeDetailInteractor;
    @Mock
    private RouteDetailView routeDetailView;
    @Mock
    private Rating rating;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        routeDetailPresenter = new RouteDetailPresenterImpl(routeDetailView, routeDetailInteractor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((RouteDetailPresenter.OnFinishedSaveRatingCallback) objects[1]).onFinishedSaveRating(new ArrayList<Challenge>(),new ArrayList<Integer>(),new Rating());
                return null;
            }
        }).when(routeDetailInteractor).saveRatingRoute(Mockito.any(Rating.class),Mockito.any(RouteDetailPresenter.OnFinishedSaveRatingCallback.class));
    }

    @Test
    public void showChallengesLevelsAchievedTest() {
        routeDetailPresenter.onSaveRatingRoute(rating);

        Mockito.verify(routeDetailInteractor,Mockito.times(1)).saveRatingRoute(Mockito.any(Rating.class),Mockito.any(RouteDetailPresenter.OnFinishedSaveRatingCallback.class));
        Mockito.verify(routeDetailView).showChallengesLevelsAchieved(Mockito.any(ArrayList.class),Mockito.any(ArrayList.class));
    }
}