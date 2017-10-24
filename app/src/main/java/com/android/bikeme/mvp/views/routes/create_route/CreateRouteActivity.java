package com.android.bikeme.mvp.views.routes.create_route;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.interactors.routes.create_route.CreateRouteInteractor;
import com.android.bikeme.mvp.interactors.routes.create_route.CreateRouteInteractorImpl;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.classes.Route;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenter;
import com.android.bikeme.mvp.presenters.routes.create_route.CreateRoutePresenterImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import java.util.ArrayList;

public class CreateRouteActivity extends BaseActivity implements StepperLayout.StepperListener, CreateRouteView {

    public static final String TAG =  CreateRouteActivity.class.getSimpleName();
    private static final int MAP_FRAGMENT_KEY = 0;

    private Route route;
    private CreateRoutePresenter createRoutePresenter;
    private ProgressDialog mProgressDialog;
    private int currentStep;
    private StepperLayout mStepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route_activity_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content_create_route_view);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        route = new Route();
        RouteModel routeModel = new RouteModel(getContentResolver());
        UserModel userModel = new UserModel(getContentResolver());
        ChallengeModel challengeModel = new ChallengeModel(getContentResolver());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        CreateRouteInteractor createRouteInteractor = new CreateRouteInteractorImpl(routeModel,userModel,challengeModel,currentUser);
        createRoutePresenter = new CreateRoutePresenterImpl(this,this,createRouteInteractor);

        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new MyStepperAdapter(getSupportFragmentManager(), this));
        mStepperLayout.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_create_route_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.create_route_help:
                showHelp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if(currentStep==MAP_FRAGMENT_KEY)
        {
            createRoutePresenter.onExitButtonClick();
        }
        else
        {
            mStepperLayout.setCurrentStepPosition(0);
        }
    }

    @Override
    public void onCompleted(View completeButton)
    {
        createRoutePresenter.onSaveRoute(route);
    }

    @Override
    public void onError(VerificationError verificationError)
    {
        Log.i(TAG, "OnVerificationError "+verificationError.getErrorMessage());
    }

    @Override
    public void onStepSelected(int newStepPosition)
    {
        Log.i(TAG, "OnStepSelect "+newStepPosition);
        this.currentStep = newStepPosition;
        int title_text;
        if(currentStep==MAP_FRAGMENT_KEY)
        {
            title_text = R.string.title_activity_route_detail_map;
        }
        else
        {
            title_text = R.string.create_route_information_title;
        }
        setTitle(title_text);
    }

    @Override
    public void onReturn()
    {
        finishActivityTransition();
    }

    @Override
    public void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress(String message)
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void hideProgress()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }

    @Override
    public void showChallengesLevelsAchieved(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved)
    {
        this.index = 0;
        this.challengesAchieved = challengesAchieved;
        this.levelsAchieved = levelsAchieved;
        showChallengesLevelsAchieved(true);
    }

    @Override
    public void showHelp()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        View dialogView;
        if(currentStep == MAP_FRAGMENT_KEY)
        {
            dialogView = getLayoutInflater().inflate(R.layout.create_route_fragment_map_help, null);

        }
        else
        {
            dialogView = getLayoutInflater().inflate(R.layout.create_route_fragment_information_help, null);
        }
        dialogBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton(R.string.continue_text,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });

        dialogBuilder.create();
        dialogBuilder.show();
    }

    @Override
    public void validateExit()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.warning_text)
                .setMessage(R.string.create_route_exit_dialog_text)
                .setPositiveButton(R.string.continue_text,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                finishActivityTransition();
                            }
                        })
                .setNegativeButton(R.string.cancel_text,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

        builder.create();
        builder.show();
    }

    public Route getRoute()
    {
        return route;
    }

    public void setRoute(Route route)
    {
        this.route = route;
    }

    public static class MyStepperAdapter extends AbstractFragmentStepAdapter {


        public MyStepperAdapter(FragmentManager fm, Context context)
        {
            super(fm, context);
        }

        @Override
        public Step createStep(int position)
        {
            if(position == MAP_FRAGMENT_KEY)
            {
                return new MapRouteStepFragment();
            }
            else
            {
                return new InformationRouteStepFragment();
            }
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @NonNull
        @Override
        public StepViewModel getViewModel(@IntRange(from = 0) int position)
        {
            //Override this method to set Step title for the Tabs, not necessary for other stepper types
            return new StepViewModel.Builder(context).create();
        }
    }
}
