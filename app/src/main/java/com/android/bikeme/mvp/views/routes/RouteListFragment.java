package com.android.bikeme.mvp.views.routes;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.bikeme.bikemeutils.CustomRecyclerView;
import com.android.bikeme.mvp.views.BikeMeActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 19 may 2017.
 */
public class RouteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, Object>>>{

    private static final String ARG_SECTION_TYPE = "section_type";
    private static final int LOADER_ID = 100;

    private RouteListAdapter routeListAdapter;
    private SwipeRefreshLayout refreshLayout;
    private int type;
    private BikeMeActivity parent;

    public RouteListFragment()
    {

    }

    public static RouteListFragment newInstance(int sectionType)
    {
        RouteListFragment routeListFragment = new RouteListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_TYPE, sectionType);
        routeListFragment.setArguments(args);
        return routeListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        routeListAdapter = new RouteListAdapter();
        routeListAdapter.setOnClickListener((RouteHomeFragment) getParentFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.bike_me_fragment_routes_list, container, false);

        View emptyView = view.findViewById(R.id.empty_view);

        CustomRecyclerView recyclerView = (CustomRecyclerView) view.findViewById(R.id.custom_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(routeListAdapter);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(this);

        return  view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getInt(ARG_SECTION_TYPE);
        parent = (BikeMeActivity)getActivity();
        getLoaderManager().initLoader(LOADER_ID+type,null,this);
    }

    @Override
    public Loader<ArrayList<HashMap<String, Object>>> onCreateLoader(int id, Bundle args)
    {
        Log.i(RouteHomeFragment.TAG, type+ " on create loader");
        return new RouteListAsyncTaskLoader(parent, refreshLayout, type);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<HashMap<String, Object>>> loader, ArrayList<HashMap<String, Object>> data)
    {
        Log.i(RouteHomeFragment.TAG, type+ " on load finished");
        refreshLayout.setRefreshing(false);
        routeListAdapter.addRoutes(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<HashMap<String, Object>>> loader)
    {
        Log.i(RouteHomeFragment.TAG, type + " on loader reset");
        routeListAdapter.addRoutes(null);
    }

    @Override
    public void onRefresh()
    {
        getLoaderManager().restartLoader(LOADER_ID+type,null,this);
    }
}