package com.android.bikeme.mvp.interactors.routes.create_route;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenter;
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
 * Created by Daniel on 18 oct 2017.
 */
public class CreateRouteInteractorTest {

    @Mock
    private RouteModel routeModel;
    @Mock
    private UserModel userModel;
    @Mock
    private ChallengeModel challengeModel;
    @Mock
    private FirebaseUser currentUser;

    private CreateRouteInteractor createRouteInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        createRouteInteractor = new CreateRouteInteractorImpl(routeModel,userModel,challengeModel,currentUser);

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
    public void saveRouteTest() {
        createRouteInteractor.saveRoute(new Route(), new CreateRoutePresenter.OnFinishedSaveRouteCallback() {
            @Override
            public void onFinishedSaveRoute(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved) {
                Assert.assertNotNull(challengesAchieved);
                Assert.assertNotNull(levelsAchieved);
            }

            @Override
            public void onErrorSaveRoute() {
                Assert.assertTrue(false);
            }
        });
    }
}