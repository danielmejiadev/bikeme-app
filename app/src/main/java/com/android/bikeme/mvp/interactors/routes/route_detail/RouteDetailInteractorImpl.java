package com.android.bikeme.mvp.interactors.routes.route_detail;

import android.os.AsyncTask;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RatingModel;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 17 may 2017.
 */
public class RouteDetailInteractorImpl implements RouteDetailInteractor {

    private RatingModel ratingModel;
    private UserModel userModel;
    private ChallengeModel challengeModel;
    private FirebaseUser currentUser;

    public RouteDetailInteractorImpl(RatingModel ratingModel, UserModel userModel, ChallengeModel challengeModel, FirebaseUser currentUser)
    {
        this.ratingModel = ratingModel;
        this.userModel = userModel;
        this.challengeModel = challengeModel;
        this.currentUser = currentUser;
    }

    @Override
    public void saveRatingRoute(final Rating rating, final RouteDetailPresenter.OnFinishedSaveRatingCallback onFinishedSaveRatingCallback)
    {
        AsyncTask<Void, Void,ArrayList[]> taskSaveRating = new AsyncTask<Void, Void, ArrayList[]>()
        {
            @Override
            protected ArrayList[] doInBackground(Void... params)
            {
                ratingModel.saveRatingRoute(rating);

                HashMap<Integer,Integer> userChallengeParams = userModel.getUserChallengeParams(currentUser.getUid());
                User currentUserModel = userModel.getUserById(currentUser.getUid());
                ArrayList<Challenge> challengesAchieved = challengeModel.getChallengesAchieved(userChallengeParams,currentUserModel);
                ArrayList<Integer> levelsAchieved = userModel.getLevelsAchieved(currentUser.getUid());
                return new ArrayList[]{challengesAchieved,levelsAchieved};
            }

            @Override
            protected void onPostExecute(ArrayList[] result)
            {
                @SuppressWarnings("unchecked")
                ArrayList<Challenge> challengesAchieved = result[0];

                @SuppressWarnings("unchecked")
                ArrayList<Integer> levelsAchieved = result[1];
                onFinishedSaveRatingCallback.onFinishedSaveRating(challengesAchieved,levelsAchieved,rating);
            }
        };
        taskSaveRating.execute();
    }
}
