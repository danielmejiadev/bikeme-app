package com.android.bikeme.mvp.interactors.routes.create_route;

import android.util.Log;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenter;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 01/05/2017.
 */
public class CreateRouteInteractorImpl implements CreateRouteInteractor{

    public static final String TAG =  CreateRouteInteractorImpl.class.getSimpleName();

    private RouteModel routeModel;
    private UserModel userModel;
    private ChallengeModel challengeModel;
    private FirebaseUser currentUser;

    public CreateRouteInteractorImpl(RouteModel routeModel, UserModel userModel, ChallengeModel challengeModel,FirebaseUser currentUser)
    {
        this.routeModel=routeModel;
        this.userModel=userModel;
        this.challengeModel=challengeModel;
        this.currentUser=currentUser;
    }

    @Override
    public void saveRoute(final Route route, final CreateRoutePresenter.OnFinishedSaveRouteCallback onFinishedSaveRouteCallback)
    {
        RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
        EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
        Call<Route> routeCall = endPointsApi.insertRoute(route);
        routeCall.enqueue(new Callback<Route>()
        {
            @Override
            public void onResponse(Call<Route> call, Response<Route> response)
            {
                if(response.isSuccessful())
                {
                    Route routeCreated = response.body();
                    routeModel.saveRouteCreatedRemoteToLocal(routeCreated);
                    Log.i(TAG, routeCreated.getName());

                    HashMap<Integer,Integer> userChallengeParams = userModel.getUserChallengeParams(currentUser.getUid());
                    User currentUserModel = userModel.getUserById(currentUser.getUid());
                    ArrayList<Challenge> challengesAchieved = challengeModel.getChallengesAchieved(userChallengeParams,currentUserModel);
                    ArrayList<Integer> levelsAchieved = userModel.getLevelsAchieved(currentUser.getUid());
                    onFinishedSaveRouteCallback.onFinishedSaveRoute(challengesAchieved,levelsAchieved);
                }
                else
                {
                    Log.e(TAG, " Error al crear la ruta "+response.code());
                    onFinishedSaveRouteCallback.onErrorSaveRoute();
                }
            }

            @Override
            public void onFailure(Call<Route> call, Throwable t)
            {
                Log.e(TAG, t+" " +t.getMessage()+" "+t.getCause());
                onFinishedSaveRouteCallback.onErrorSaveRoute();
            }
        });
    }
}
