package com.android.bikeme.mvp.interactors;

import android.os.AsyncTask;
import com.android.bikeme.classes.Problem;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ProblemModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.BikeMePresenter;

/**
 * Created by Daniel on 17 ago 2017.
 */
public class BikeMeInteractorImpl implements BikeMeInteractor{

    private UserModel userModel;
    private ProblemModel problemModel;

    public BikeMeInteractorImpl(UserModel userModel, ProblemModel problemModel)
    {
        this.userModel = userModel;
        this.problemModel = problemModel;
    }

    @Override
    public void getUser(final String userID, final BikeMePresenter.onUserProfileCallback onUserProfileCallback)
    {
        AsyncTask<Void, Void, Object[]> taskDirectionRoute = new AsyncTask<Void, Void, Object[]>()
        {
            @Override
            protected Object[] doInBackground(Void... params)
            {
                User user = userModel.getUserById(userID);
                int totalPoints = userModel.getTotalPoints(user.getAchievementsList());
                return  new Object[]{user,totalPoints};
            }

            @Override
            protected void onPostExecute(Object[] response)
            {
                onUserProfileCallback.onFinished((User)response[0],(int)response[1]);
            }

        };
        taskDirectionRoute.execute();
    }

    @Override
    public void saveProblem(Problem problem)
    {
        problemModel.insertProblem(problem);
    }
}
