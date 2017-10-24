package com.android.bikeme.mvp.presenters.workout;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.mvp.interactors.workout.WorkoutDetailInteractor;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.android.bikeme.mvp.views.workout.workout_detail.WorkoutDetailView;

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
public class WorkoutDetailPresenterTest {

    private WorkoutDetailPresenter workoutDetailPresenter;
    @Mock
    private WorkoutDetailInteractor workoutDetailInteractor;
    @Mock
    private WorkoutDetailView workoutDetailView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        workoutDetailPresenter = new WorkoutDetailPresenterImpl(workoutDetailView,workoutDetailInteractor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] objects = invocation.getArguments();
                ((WorkoutDetailPresenter.onSaveUserWorkoutCallback) objects[1]).onUserWorkoutSaved(new ArrayList<Challenge>(),new ArrayList<Integer>());
                return null;
            }
        }).when(workoutDetailInteractor).saveUserWorkout(Mockito.any(Workout.class),
                Mockito.any(WorkoutDetailPresenter.onSaveUserWorkoutCallback.class));
    }

    @Test
    public void showChallengesLevelsAchievedTest() {
        workoutDetailPresenter.onSaveUserWorkout(new Workout());

        Mockito.verify(workoutDetailInteractor,Mockito.times(1)).saveUserWorkout(Mockito.any(Workout.class),
                Mockito.any(WorkoutDetailPresenter.onSaveUserWorkoutCallback.class));
        Mockito.verify(workoutDetailView).showChallengesLevelsAchieved(Mockito.any(ArrayList.class),Mockito.any(ArrayList.class));
    }
}
