package com.android.bikeme.mvp.views.challenges;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.bikeme.R;
import com.android.bikeme.bikemeutils.CustomRecyclerView;
import com.android.bikeme.mvp.views.BikeMeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChallengeFragment extends Fragment implements LoaderManager.LoaderCallbacks<ChallengeResponseLoader>, SwipeRefreshLayout.OnRefreshListener {

    private static final int CHALLENGE_LOADER_ID = 600;
    public static final String TAG = ChallengeFragment.class.getSimpleName();

    private ChallengeAdapter challengeAdapter;
    private SwipeRefreshLayout refreshLayout;
    private FirebaseUser currentUser;

    public ChallengeFragment()
    {
    }

    public static ChallengeFragment newInstance()
    {
        return new ChallengeFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        currentUser = ((BikeMeActivity)context).getCurrentUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        challengeAdapter = new ChallengeAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.bike_me_fragment_challenges, container, false);

        View emptyView = view.findViewById(R.id.empty_view);

        CustomRecyclerView recyclerView = (CustomRecyclerView) view.findViewById(R.id.custom_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(challengeAdapter);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        fab.setVisibility(View.GONE);

        getLoaderManager().initLoader(CHALLENGE_LOADER_ID, null, this);

        return view;
    }

    @Override
    public Loader<ChallengeResponseLoader> onCreateLoader(int id, Bundle args)
    {
        return new ChallengeAsyncTaskLoader(getContext(),refreshLayout,currentUser.getUid());
    }

    @Override
    public void onLoadFinished(Loader<ChallengeResponseLoader> loader, ChallengeResponseLoader data)
    {
        refreshLayout.setRefreshing(false);
        challengeAdapter.addChallenges(getResources().getStringArray(R.array.challenge_names),
                                       data.getChallengesSections(),
                                       data.getUser().getAchievementsList(),
                                       data.getUserChallengesParams());
    }

    @Override
    public void onLoaderReset(Loader<ChallengeResponseLoader> loader)
    {
        challengeAdapter.addChallenges(null,null,null,null);
    }

    @Override
    public void onRefresh()
    {
        getLoaderManager().restartLoader(CHALLENGE_LOADER_ID,null,this);
    }
}
