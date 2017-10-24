package com.android.bikeme.mvp.views.events.event_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.bikemeutils.CustomRecyclerView;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.EventModel;
import com.android.bikeme.mvp.views.user.UserProfileActivity;

import java.util.ArrayList;

public class EventDetailGuestsActivity extends BaseActivity implements EventDetailGuestsAdapter.onGuestClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity_guests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        ArrayList<Guest> guests = getIntent().getParcelableArrayListExtra(EventModel.GUESTS_KEY);
        ArrayList<User> users = getIntent().getParcelableArrayListExtra(EventModel.USERS_KEY);

        EventDetailGuestsAdapter eventDetailGuestsAdapter = new EventDetailGuestsAdapter(this, guests, users);
        eventDetailGuestsAdapter.setOnGuestListener(this);

        View emptyView = findViewById(R.id.empty_view);

        CustomRecyclerView recyclerView = (CustomRecyclerView) findViewById(R.id.custom_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(eventDetailGuestsAdapter);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setHasFixedSize(true);
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

    @Override
    public void onGuestClick(User user)
    {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(User.USER_KEY, user);
        startActivityTransition(intent);
    }
}