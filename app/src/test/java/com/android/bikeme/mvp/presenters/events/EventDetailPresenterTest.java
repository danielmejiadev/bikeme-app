package com.android.bikeme.mvp.presenters.events.event_detail;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.mvp.interactors.events.EventDetailInteractor;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenter;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenterImpl;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.android.bikeme.mvp.views.events.event_detail.EventDetailView;

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
public class EventDetailPresenterTest {

    private EventDetailPresenter eventDetailPresenter;
    @Mock
    private EventDetailInteractor eventDetailInteractor;
    @Mock
    private EventDetailView eventDetailView;
    @Mock
    private Rating rating;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventDetailPresenter = new EventDetailPresenterImpl(eventDetailView,eventDetailInteractor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((EventDetailPresenter.onEventTravelCallback) objects[1]).onFinished(new ArrayList<Point>());
                return null;
            }
        }).when(eventDetailInteractor).getPointForTravel(Mockito.anyString(),Mockito.any(EventDetailPresenter.onEventTravelCallback.class));
    }

    @Test
    public void onGoToEventTravelTest() {
        eventDetailPresenter.onGoToEventTravel("");

        Mockito.verify(eventDetailInteractor,Mockito.times(1)).getPointForTravel(Mockito.anyString(),
                Mockito.any(EventDetailPresenter.onEventTravelCallback.class));
        Mockito.verify(eventDetailView).navigateToEventDetailTravel(Mockito.any(ArrayList.class));
    }
}
