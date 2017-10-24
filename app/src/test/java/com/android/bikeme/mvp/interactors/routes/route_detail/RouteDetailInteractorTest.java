package com.android.bikeme.mvp.interactors.routes.route_detail;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RatingModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 17 oct 2017.
 */
public class RouteDetailInteractorTest {

    @Mock
    private RatingModel ratingModel;
    @Mock
    private UserModel userModel;
    @Mock
    private ChallengeModel challengeModel;
    @Mock
    private FirebaseUser currentUser;

    private RouteDetailInteractor routeDetailInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        routeDetailInteractor = new RouteDetailInteractorImpl(ratingModel,userModel,challengeModel,currentUser);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new ArrayList<Challenge>();
            }
        }).when(challengeModel).getChallengesAchieved(Mockito.any(HashMap.class),Mockito.any(User.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new ArrayList<Integer>();
            }
        }).when(userModel).getLevelsAchieved(Mockito.anyString());
    }

    @Test
    public void saveRatingRouteTest() {
        routeDetailInteractor.saveRatingRoute(Mockito.any(Rating.class), new RouteDetailPresenter.OnFinishedSaveRatingCallback() {
            @Override
            public void onFinishedSaveRating(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved, Rating rating) {
                Assert.assertNotNull(challengesAchieved);
                Assert.assertNotNull(levelsAchieved);
            }
        });
    }
}
