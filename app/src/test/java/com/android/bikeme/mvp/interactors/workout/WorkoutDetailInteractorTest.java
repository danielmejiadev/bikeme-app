package com.android.bikeme.mvp.interactors.workout;

import android.net.Uri;
import android.util.Log;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaselocal.models.WorkoutModel;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenter;
import com.android.bikeme.mvp.presenters.workout.WorkoutDetailPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 18 oct 2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Uri.class)
public class WorkoutDetailInteractorTest {

    @Mock
    private WorkoutModel workoutModel;
    @Mock
    private UserModel userModel;
    @Mock
    private ChallengeModel challengeModel;

    private WorkoutDetailInteractor workoutDetailInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Uri.class);

        workoutDetailInteractor = new WorkoutDetailDetailInteractorImpl(workoutModel,userModel,challengeModel);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Uri.parse("");
            }
        }).when(workoutModel).saveUserWorkout(Mockito.any(Workout.class));

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
        workoutDetailInteractor.saveUserWorkout(new Workout(), new WorkoutDetailPresenter.onSaveUserWorkoutCallback() {
            @Override
            public void onUserWorkoutSaved(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved) {
                Assert.assertNotNull(challengesAchieved);
                Assert.assertNotNull(levelsAchieved);
            }
        });
    }
}
