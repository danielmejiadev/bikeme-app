package com.android.bikeme.mvp.presenters.routes.create_route;

import com.android.bikeme.mvp.views.routes.create_route.InformationRouteStepView;

/**
 * Created by Daniel on 01/05/2017.
 */
public class InformationRouteStepPresenterImpl implements InformationRouteStepPresenter {

    private InformationRouteStepView informationRouteStepView;

    public InformationRouteStepPresenterImpl(InformationRouteStepView informationRouteStepView)
    {
        this.informationRouteStepView=informationRouteStepView;
    }

    @Override
    public void onRatingChangeClick(float rating)
    {
        informationRouteStepView.showRatingValue(String.valueOf(rating));
    }

    @Override
    public void onCompleteClick(String name, double rating, String description, String departure, String arrival,  int level)
    {
        boolean nameOk = !name.isEmpty();
        boolean ratingOk = rating > 0;
        boolean descriptionOk = !description.isEmpty();
        boolean departureOk=!departure.isEmpty();
        boolean arrivalOk =!arrival.isEmpty();

        if(!nameOk)
        {
            informationRouteStepView.showErrorRouteName();
        }

        if(!descriptionOk)
        {
            informationRouteStepView.showErrorRouteDescription();
        }

        if(!departureOk)
        {
            informationRouteStepView.showErrorRouteDeparture();
        }

        if(!arrivalOk)
        {
            informationRouteStepView.showErrorRouteArrival();
        }

        if(!ratingOk)
        {
            informationRouteStepView.showErrorRouteRating();
        }

        informationRouteStepView.allowSave(nameOk && descriptionOk && ratingOk && departureOk && arrivalOk);
    }
}
