package com.android.bikeme.mvp.interactors.workout;

import android.os.AsyncTask;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Workout;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaselocal.models.WorkoutModel;
import com.android.bikeme.mvp.presenters.workout.WorkoutDetailPresenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 4 sep 2017.
 */
public class WorkoutDetailDetailInteractorImpl implements WorkoutDetailInteractor {

    private WorkoutModel workoutModel;
    private UserModel userModel;
    private ChallengeModel challengeModel;

    public WorkoutDetailDetailInteractorImpl(WorkoutModel workoutModel,UserModel userModel, ChallengeModel challengeModel)
    {
        this.workoutModel = workoutModel;
        this.userModel = userModel;
        this.challengeModel = challengeModel;
    }

    @Override
    public void saveUserWorkout(final Workout workout, final WorkoutDetailPresenter.onSaveUserWorkoutCallback onSaveUserWorkoutCallback)
    {
        final AsyncTask<Void, Void, ArrayList[]> taskSaveUserWorkout = new AsyncTask<Void, Void, ArrayList[]>()
        {
            @Override
            protected ArrayList[] doInBackground(Void... params)
            {
                ArrayList<Challenge> challengesAchieved = new ArrayList<>();
                ArrayList<Integer> levelsAchieved = new ArrayList<>();
                if(workoutModel.saveUserWorkout(workout)!=null)
                {
                    int typeWorkout=-1;
                    int workoutDistanceKm = (int)Workout.getDistanceKm(workout.getTotalDistanceMeters());
                    double workoutDurationHours = Workout.getDurationHours(workout.getDurationSeconds());
                    if(workoutDurationHours >= 0)
                    {
                        typeWorkout = workout.getTypeRoute();
                    }
                    challengesAchieved = getChallenges(workout.getUser(),typeWorkout,workoutDistanceKm,(int)workoutDurationHours);
                    levelsAchieved = userModel.getLevelsAchieved(workout.getUser());
                }

                return new ArrayList[]{challengesAchieved,levelsAchieved};
            }

            @Override
            protected void onPostExecute(ArrayList[] result)
            {
                @SuppressWarnings("unchecked")
                ArrayList<Challenge> challengesAchieved = result[0];

                @SuppressWarnings("unchecked")
                ArrayList<Integer> levelsAchieved = result[1];
                onSaveUserWorkoutCallback.onUserWorkoutSaved(challengesAchieved,levelsAchieved);
            }

        };
        taskSaveUserWorkout.execute();
    }
    
    public ArrayList<Challenge> getChallenges(String userId, int typeWorkout, int workoutDistanceKm, int workoutDurationHours)
    {
        HashMap<Integer,Integer>  userChallengesParams = userModel.getUserChallengeParams(userId);
        userChallengesParams.put(UserModel.TYPE_CHALLENGE_0_TYPE_WORKOUT_KEY,typeWorkout);
        userChallengesParams.put(UserModel.TYPE_CHALLENGE_1_KM_BY_WORKOUT_KEY,workoutDistanceKm);
        userChallengesParams.put(UserModel.TYPE_CHALLENGE_2_HOURS_BY_WORKOUT_KEY,workoutDurationHours);

        return challengeModel.getChallengesAchieved(userChallengesParams,userModel.getUserById(userId));
    }
}

