package com.android.bikeme.mvp.views.routes.create_route;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.presenters.routes.create_route.InformationRouteStepPresenter;
import com.android.bikeme.mvp.presenters.routes.create_route.InformationRouteStepPresenterImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Daniel on 01/05/2017.
 */
public  class InformationRouteStepFragment extends Fragment implements BlockingStep, InformationRouteStepView, RatingBar.OnRatingBarChangeListener {

    public static final String TAG =  InformationRouteStepFragment.class.getSimpleName();

    private TextInputLayout inputLayoutName,inputLayoutDescription, inputLayoutDeparture, inputLayoutArrival;
    private EditText inputRouteName,inputRouteDescription,inputRouteDeparture,inputRouteArrival;
    private RatingBar inputRouteRating;
    private TextView textRouteRating;
    private Spinner inputRouteLevel;
    private boolean allowSave;

    private String nameRoute,descriptionRoute,departureRoute,arrivalRoute;
    private double ratingRoute;
    private int levelRoute;

    private InformationRouteStepPresenter informationRouteStepPresenter;
    private CreateRouteActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.create_route_fragment_information, container, false);
        inputLayoutName = (TextInputLayout)view.findViewById(R.id.input_layout_name);
        inputRouteName = (EditText) view.findViewById(R.id.input_route_name);

        inputLayoutDescription = (TextInputLayout)view.findViewById(R.id.input_layout_description);
        inputRouteDescription = (EditText) view.findViewById(R.id.input_route_description);

        inputLayoutDeparture = (TextInputLayout)view.findViewById(R.id.input_layout_departure);
        inputRouteDeparture = (EditText) view.findViewById(R.id.input_route_departure);

        inputLayoutArrival = (TextInputLayout)view.findViewById(R.id.input_layout_arrival);
        inputRouteArrival = (EditText) view.findViewById(R.id.input_route_arrival);

        inputRouteRating = (RatingBar) view.findViewById(R.id.input_route_rating);
        inputRouteRating.setOnRatingBarChangeListener(this);
        textRouteRating = (TextView) view.findViewById(R.id.text_route_rating);

        inputRouteLevel = (Spinner) view.findViewById(R.id.input_route_level);

        allowSave = false;
        informationRouteStepPresenter = new InformationRouteStepPresenterImpl(this);
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        parent = (CreateRouteActivity)context;
    }

    @Override
    public VerificationError verifyStep()
    {
        nameRoute = inputRouteName.getText().toString();
        descriptionRoute = inputRouteDescription.getText().toString();
        departureRoute = inputRouteDeparture.getText().toString();
        arrivalRoute = inputRouteArrival.getText().toString();
        ratingRoute = (double)Math.round((inputRouteRating.getRating()*10))/10.0;
        levelRoute =  inputRouteLevel.getSelectedItemPosition();
        informationRouteStepPresenter.onCompleteClick(nameRoute, ratingRoute , descriptionRoute, departureRoute, arrivalRoute,levelRoute);
        if(allowSave)
        {
            return null;
        }
        else
        {
            return new VerificationError(getString(R.string.create_route_error_fields));
        }
    }

    @Override
    public void onSelected()
    {
        Log.i(TAG,"OnStepFragmentSelect " +this.getTag());
    }

    @Override
    public void onError(@NonNull VerificationError error)
    {
        Log.i(TAG,"OnVerificationError"+error.getErrorMessage());
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback)
    {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback)
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert  currentUser != null;

        Route route = parent.getRoute();
        route.setCreator(currentUser.getUid());
        route.setName(nameRoute);
        route.setDescription(descriptionRoute);
        route.setDeparture(departureRoute);
        route.setArrival(arrivalRoute);
        route.setLevel(levelRoute);

        ArrayList<Rating> ratings = new ArrayList<>();
        Rating rating = new Rating();
        rating.setCalification(ratingRoute);
        rating.setUser(currentUser.getUid());
        BikeMeApplication bikeMeApplication =  BikeMeApplication.getInstance();
        rating.setDate(bikeMeApplication.getDateTimeString(bikeMeApplication.getCurrentDate()));

        ratings.add(rating);
        route.setRatings(ratings);

        parent.setRoute(route);
        callback.complete();
    }


    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback)
    {
        callback.goToPrevStep();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
    {
        informationRouteStepPresenter.onRatingChangeClick(rating);
    }

    @Override
    public void showErrorRouteName()
    {
        inputLayoutName.setError(getString(R.string.create_route_information_error_name));
    }

    @Override
    public void showErrorRouteRating()
    {
        textRouteRating.setError("");
        textRouteRating.setText(R.string.create_route_information_error_rating);
    }

    @Override
    public void showErrorRouteDescription()
    {
        inputLayoutDescription.setError(getString(R.string.create_route_information_error_description));
    }

    @Override
    public void showErrorRouteDeparture()
    {
        inputLayoutDeparture.setError(getString(R.string.create_route_information_error_departure));
    }

    @Override
    public void showErrorRouteArrival()
    {
        inputLayoutArrival.setError(getString(R.string.create_route_information_error_arrival));
    }

    @Override
    public void showRatingValue(String rating)
    {
        textRouteRating.setText(rating);
    }

    @Override
    public void allowSave(boolean allowSave)
    {
        this.allowSave = allowSave;
    }
}