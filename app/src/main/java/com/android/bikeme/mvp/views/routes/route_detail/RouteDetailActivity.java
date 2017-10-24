package com.android.bikeme.mvp.views.routes.route_detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.databaselocal.models.ChallengeModel;
import com.android.bikeme.databaselocal.models.RatingModel;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.interactors.routes.route_detail.RouteDetailInteractorImpl;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenter;
import com.android.bikeme.mvp.presenters.routes.route_detail.RouteDetailPresenterImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Date;

public class RouteDetailActivity extends BaseActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, RouteDetailView, PopupMenu.OnMenuItemClickListener {

    private Route route;
    private Rating userRating;
    private RouteDetailPresenter routeDetailPresenter;
    private FirebaseUser currentUser;

    private ImageButton routeRatingSettings;
    private TextView  titleRouteRating,dateRouteRating,textRouteRating;
    private RatingBar inputRouteRating;
    private Button buttonSubmitRating;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail_activity_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content_route_detail);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setOnClickListener(this);

        route = getIntent().getExtras().getParcelable(Route.ROUTE_KEY);

        RatingModel ratingModel = new RatingModel(getContentResolver());
        UserModel userModel = new UserModel(getContentResolver());
        ChallengeModel challengeModel = new ChallengeModel(getContentResolver());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        routeDetailPresenter = new RouteDetailPresenterImpl(this, new RouteDetailInteractorImpl(ratingModel,userModel,challengeModel,currentUser));

        setRouteUI(route);

        routeRatingSettings = (ImageButton)findViewById(R.id.rating_settings);
        titleRouteRating = (TextView)findViewById(R.id.title_route_rating);
        dateRouteRating = (TextView)findViewById(R.id.date_route_rating);
        inputRouteRating = (RatingBar)findViewById(R.id.input_route_rating);
        textRouteRating = (TextView)findViewById(R.id.route_rating_text_view);
        buttonSubmitRating = (Button)findViewById(R.id.button_submit_rating);

        CircleImageView photoRater = (CircleImageView)findViewById(R.id.photo_rater);
        BikeMeApplication.getInstance().loadImage(currentUser.getPhotoUrl(),photoRater,ContextCompat.getDrawable(this, R.drawable.default_avatar));

        verifyRouteAlreadyRated();
    }

    public void setRouteUI(Route route)
    {
        setTitle(route.getName());

        ImageView routeImage = (ImageView)findViewById(R.id.expandedImage);
        routeImage.setImageBitmap(BikeMeApplication.getInstance().decodeStringToBitmap(route.getImage()));

        RatingBar ratingBarRoute = (RatingBar)findViewById(R.id.rating_bar_route);
        ratingBarRoute.setRating((float)Route.averageRatings(route.getRatings()));

        TextView ratingRouteTextView = (TextView)findViewById(R.id.rating_text_view);
        ratingRouteTextView.setText(String.valueOf(Route.averageRatings(route.getRatings())));

        TextView levelRouteTextView = (TextView)findViewById(R.id.level_route_text_view);
        levelRouteTextView.setText(getResources().getStringArray(R.array.route_levels)[route.getLevel()]);

        TextView departureRouteTextView = (TextView)findViewById(R.id.route_detail_departure);
        departureRouteTextView.setText(route.getDeparture());

        TextView arrivalRouteTextView = (TextView)findViewById(R.id.route_detail_arrival);
        arrivalRouteTextView.setText(route.getArrival());

        TextView distanceRouteTextView = (TextView)findViewById(R.id.route_detail_distance);
        distanceRouteTextView.setText(BikeMeApplication.getInstance().getStringDistance(route.getDistance()));

        TextView descriptionRouteTextView = (TextView)findViewById(R.id.description_route);
        descriptionRouteTextView.setText(route.getDescription());
    }

    public void verifyRouteAlreadyRated()
    {
        for(Rating rating : route.getRatings())
        {
            if(rating.getUser().equals(currentUser.getUid()) && rating.getRecommendation() == 0 && rating.getCalification() > 0)
            {
                userRating = rating;
                break;
            }
        }

        if(userRating != null)
        {
            setRatingDone(userRating);
        }
        else
        {
            setForRate(0);
        }
    }

    @Override
    public void setRatingDone(Rating rating)
    {
        titleRouteRating.setText(currentUser.getDisplayName());

        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        Date ratingDate = bikeMeApplication.getDateTime(rating.getDate());
        String ratingDateString = getString(R.string.route_detail_rated_text,
                                            bikeMeApplication.getDateString(ratingDate),
                                            bikeMeApplication.getHourAmPm(ratingDate));
        dateRouteRating.setText(ratingDateString);

        double calification = ((double)Math.round((rating.getCalification())*10)/10.0);
        inputRouteRating.setRating((float)calification);
        inputRouteRating.setIsIndicator(true);

        textRouteRating.setText(String.valueOf(calification));

        buttonSubmitRating.setVisibility(View.INVISIBLE);
        routeRatingSettings.setVisibility(View.VISIBLE);
    }

    public void setForRate(double calification)
    {
        inputRouteRating.setRating((float)calification);
        inputRouteRating.setOnRatingBarChangeListener(this);
        inputRouteRating.setIsIndicator(false);

        textRouteRating.setText(String.valueOf(calification));

        buttonSubmitRating.setVisibility(View.VISIBLE);
        buttonSubmitRating.setOnClickListener(this);

        routeRatingSettings.setVisibility(View.INVISIBLE);

        dateRouteRating.setText("");

        if(calification==0)
        {
            buttonSubmitRating.setEnabled(false);
            buttonSubmitRating.setTextColor(ContextCompat.getColor(this, R.color.divider));
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit_rating:
                setForRate(userRating.getCalification());
                return true;
            default:
                return false;
        }
    }

    public void showPopupMenu(View view)
    {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_route_detail_rating_options, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.floating_action_button:
                routeDetailPresenter.onClickFabButton();
                break;
            case R.id.button_submit_rating:
                rateRoute();
                break;
        }
    }

    public void rateRoute()
    {
        userRating = new Rating();
        userRating.setRoute(route.getUid());
        userRating.setCalification(inputRouteRating.getRating());
        userRating.setUser(currentUser.getUid());
        BikeMeApplication bikeMeApplication =  BikeMeApplication.getInstance();
        userRating.setDate(bikeMeApplication.getDateTimeString(bikeMeApplication.getCurrentDate()));
        routeDetailPresenter.onSaveRatingRoute(userRating);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
    {
        if(rating>0)
        {
            routeDetailPresenter.onRatingChangeClick(rating);
            if(!buttonSubmitRating.isEnabled())
            {
                buttonSubmitRating.setEnabled(true);
                buttonSubmitRating.setTextColor(ContextCompat.getColor(this, R.color.primary));
            }

        }
        else
        {
            if(fromUser)ratingBar.setRating((float)0.5);
        }
    }

    @Override
    public void showRatingValue(String rating)
    {
        textRouteRating.setText(rating);
    }

    @Override
    public void onGoToMapRoute()
    {
        Intent intent = new Intent();
        intent.putExtra(Route.POINTS_KEY,route.getPoints());
        intent.putExtra(Route.DISTANCE_KEY,route.getDistance());
        goToMapDetail(intent);
    }

    @Override
    public void showChallengesLevelsAchieved(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved)
    {
        this.index = 0;
        this.challengesAchieved = challengesAchieved;
        this.levelsAchieved = levelsAchieved;
        showChallengesLevelsAchieved(false);
    }

    @Override
    public void showProgressDialog()
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.saving_text));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }
}