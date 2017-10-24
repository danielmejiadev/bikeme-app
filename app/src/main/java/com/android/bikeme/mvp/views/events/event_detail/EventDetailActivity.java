package com.android.bikeme.mvp.views.events.event_detail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.EventModel;
import com.android.bikeme.databaselocal.models.GuestModel;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.mvp.interactors.events.EventDetailInteractorImpl;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenter;
import com.android.bikeme.mvp.presenters.events.event_detail.EventDetailPresenterImpl;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOfflineActivity;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailMapOnlineActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EventDetailActivity extends BaseActivity implements EventDetailView, PopupMenu.OnMenuItemClickListener {

    private Event event;
    private double distance;
    private ArrayList<User> users;
    private EventDetailPresenter eventDetailPresenter;
    private Guest currentUserGuest;
    private FirebaseUser currentUser;
    boolean alreadyHappened;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content_event_detail);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        eventDetailPresenter = new EventDetailPresenterImpl(this, new EventDetailInteractorImpl(new RouteModel(getContentResolver()), new GuestModel(getContentResolver())));

        @SuppressWarnings("unchecked")
        HashMap<String, Object> eventToShow = (HashMap<String, Object>) getIntent().getSerializableExtra(EventModel.EVENTS_TO_SHOW_KEY);

        @SuppressWarnings("unchecked")
        ArrayList<User> usersObject = (ArrayList<User>)eventToShow.get(EventModel.USERS_KEY);

        event = (Event)eventToShow.get(EventModel.EVENT_KEY);
        String hour = String.valueOf(eventToShow.get(EventModel.HOUR_KEY));
        String longDate = String.valueOf(eventToShow.get(EventModel.DATE_KEY));
        distance = (double)eventToShow.get(EventModel.DISTANCE_KEY);
        String start = String.valueOf(eventToShow.get(EventModel.DEPARTURE_KEY));
        String finish = String.valueOf(eventToShow.get(EventModel.ARRIVAL_KEY));
        users = usersObject;

        alreadyHappened=false;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView)findViewById(R.id.event_detail_name)).setText(event.getName());
        ((TextView)findViewById(R.id.event_detail_date)).setText(longDate);
        ((TextView)findViewById(R.id.event_detail_hour)).setText(hour);
        ((TextView)findViewById(R.id.event_detail_start)).setText(start);
        ((TextView)findViewById(R.id.event_detail_finish)).setText(finish);
        ((TextView)findViewById(R.id.event_detail_distance)).setText(BikeMeApplication.getInstance().getStringDistance(distance));

        setDaysToGo(((TextView)findViewById(R.id.event_detail_days_to_go)));

        for(Guest guest : event.getGuests())
        {
            if(guest.getUser().equals(currentUser.getUid()))
            {
                currentUserGuest = guest;
                break;
            }
        }

        setActionText(currentUserGuest);
    }

    public void setDaysToGo(TextView daysToGo)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(BikeMeApplication.getInstance().getCurrentDate());
        int dayCurrentDate = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(BikeMeApplication.getInstance().getDateTime(event.getDate()));
        int dayEventDate = calendar.get(Calendar.DAY_OF_YEAR);

        int differenceDays = dayEventDate-dayCurrentDate;
        if(differenceDays==0)
        {
            daysToGo.setText(R.string.event_today_text);
        }
        else if(differenceDays>0)
        {
            String plural= "";
            String pluralDays = "";
            if(differenceDays>1)
            {
                plural = "n";
                pluralDays="s";
            }
            daysToGo.setText(String.format(getString(R.string.event_days_before_text), plural, differenceDays, pluralDays));
        }
        else
        {
            String pluralDays = "";
            if(differenceDays<-1)
            {
                pluralDays="s";
            }
            daysToGo.setText(String.format(getString(R.string.event_days_after_text),-1*differenceDays, pluralDays));
            alreadyHappened=true;
        }
    }

    public void setActionText(Guest guest)
    {
        String valueForButton = "";
        if(guest == null)
        {
            valueForButton = getString(R.string.event_detail_want_go_text);
        }
        else
        {
            switch (guest.getState())
            {
                case 0:
                    valueForButton = getString(R.string.event_detail_maybe_go_text);
                    break;
                case 1:
                    valueForButton =  getString(R.string.event_detail_go_text);
                    break;
                case 2:
                    valueForButton =  getString(R.string.event_detail_not_go_text);
                    break;
            }
        }
        ((TextView)findViewById(R.id.event_detail_action)).setText(valueForButton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finishActivityTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickButtonPath(View view)
    {
        eventDetailPresenter.onGoToEventTravel(event.getRoute());
    }

    public void onClickButtonDecide(View view)
    {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_event_detail_guest_options, popup.getMenu());
        int idItemToHide = 0;
        if(currentUserGuest != null)
        {
            switch (currentUserGuest.getState())
            {
                case 0:
                    idItemToHide = R.id.maybe_go;
                    break;
                case 1:
                    idItemToHide = R.id.go;
                    break;
                case 2:
                    idItemToHide = R.id.not_go;
                    break;
            }
            popup.getMenu().findItem(idItemToHide).setEnabled(false);
            popup.getMenu().findItem(idItemToHide).setVisible(false);
        }
        if(alreadyHappened) popup.getMenu().setGroupEnabled(R.id.event_detail_menu_group,false);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(currentUserGuest==null)
        {
            currentUserGuest = new Guest();
            currentUserGuest.setEvent(event.getUid());
            currentUserGuest.setUser(currentUser.getUid());
        }
        switch (item.getItemId())
        {
            case R.id.maybe_go:
                currentUserGuest.setState(0);
                break;
            case R.id.go:
                currentUserGuest.setState(1);
                break;
            case R.id.not_go:
                currentUserGuest.setState(2);
                break;
        }
        BikeMeApplication bikeMeApplication = BikeMeApplication.getInstance();
        currentUserGuest.setDate(bikeMeApplication.getDateTimeString(bikeMeApplication.getCurrentDate()));
        eventDetailPresenter.onSaveGuest(currentUserGuest);
        setActionText(currentUserGuest);
        return  true;
    }

    public void onClickButtonGuests(View view)
    {
        eventDetailPresenter.onGoToEventDetailGuests(event.getGuests(),users);
    }

    @Override
    public void navigateToEventDetailGuests(ArrayList<Guest> guests, ArrayList<User> users)
    {
        Intent intent = new Intent(this, EventDetailGuestsActivity.class);
        intent.putExtra(EventModel.USERS_KEY, users);
        if(currentUserGuest!=null)
        {
            guests.remove(currentUserGuest);
        }
        intent.putExtra(EventModel.GUESTS_KEY,guests);
        startActivityTransition(intent);
    }

    @Override
    public void navigateToEventDetailTravel(ArrayList<Point> points)
    {
        Intent intent = new Intent();
        intent.putExtra(Route.POINTS_KEY,points);
        intent.putExtra(Route.DISTANCE_KEY,distance);
        goToMapDetail(intent);
    }
}