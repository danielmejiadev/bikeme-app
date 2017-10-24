package com.android.bikeme.mvp.views.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.CustomRecyclerView;
import com.android.bikeme.databaselocal.models.EventModel;
import com.android.bikeme.mvp.presenters.events.EventPresenter;
import com.android.bikeme.mvp.presenters.events.EventPresenterImpl;
import com.android.bikeme.mvp.views.BikeMeActivity;
import com.android.bikeme.mvp.views.events.event_detail.EventDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;

public class EventFragment extends Fragment implements EventAdapter.onEventClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<EventResponseLoader>,
        EventView{

    public static final String TAG = EventFragment.class.getSimpleName();
    private static final int EVENT_LOADER_ID = 200;
    private EventAdapter eventAdapter;
    private SwipeRefreshLayout refreshLayout;
    private EventPresenter eventPresenter;
    private FirebaseUser currentUser;
    private BikeMeActivity parent;

    public EventFragment()
    {
    }

    public static EventFragment newInstance()
    {
        return  new EventFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        parent = (BikeMeActivity)context;
        currentUser = parent.getCurrentUser();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        eventAdapter = new EventAdapter();
        eventAdapter.setOnEventClickListener(this);
        eventPresenter = new EventPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.bike_me_fragment_events, container, false);

        View emptyView = view.findViewById(R.id.empty_view);

        CustomRecyclerView recyclerView = (CustomRecyclerView) view.findViewById(R.id.custom_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(eventAdapter);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        fab.setVisibility(View.GONE);

        getLoaderManager().initLoader(EVENT_LOADER_ID, null, this);

        return view;
    }

    @Override
    public Loader<EventResponseLoader> onCreateLoader(int id, Bundle args)
    {
        Log.i(TAG, "on create loader");
       return new EventAsyncTaskLoader(BikeMeApplication.getInstance().getApplicationContext(), refreshLayout, currentUser.getUid());
    }

    @Override
    public void onLoadFinished(Loader<EventResponseLoader> loader, EventResponseLoader data)
    {
        Log.i(TAG, "on load finished");
        refreshLayout.setRefreshing(false);
        eventAdapter.addEvents(data.getDifferentDates(),data.getEvents());
    }

    @Override
    public void onLoaderReset(Loader<EventResponseLoader> loader)
    {
        Log.i(TAG, "on loader reset");
        eventAdapter.addEvents(null,null);
    }

    @Override
    public void onRefresh()
    {
       getLoaderManager().restartLoader(EVENT_LOADER_ID,null,this);
    }

    @Override
    public void onEventClick(HashMap<String, Object> eventToShow)
    {
       eventPresenter.onGoToEventDetail(eventToShow);
    }

    @Override
    public void navigateToEventDetail(HashMap<String, Object> eventToShow)
    {
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra(EventModel.EVENTS_TO_SHOW_KEY, eventToShow);
        parent.startActivityTransition(intent);
    }
}