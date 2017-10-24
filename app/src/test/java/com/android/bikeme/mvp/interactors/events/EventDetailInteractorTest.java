package com.android.bikeme.mvp.interactors.events;

import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.GuestModel;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.mvp.interactors.routes.RouteHomeInteractorImpl;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenter;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;

import org.junit.Assert;
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
public class EventDetailInteractorTest {

    @Mock
    private RouteModel routeModel;
    @Mock
    private GuestModel guestModel;

    private EventDetailInteractor eventDetailInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventDetailInteractor = new EventDetailInteractorImpl(routeModel,guestModel);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new ArrayList<Point>();
            }
        }).when(routeModel).getPointsByRoute(Mockito.anyString());
    }

    @Test
    public void getPointForTravelTest() {
        eventDetailInteractor.getPointForTravel(Mockito.anyString(), new EventDetailPresenter.onEventTravelCallback(){

            @Override
            public void onFinished(ArrayList<Point> points) {
                Assert.assertNotNull(points);
            }

            @Override
            public void onError() {
                Assert.assertTrue(false);
            }
        });
    }
}
