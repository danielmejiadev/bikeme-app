package com.android.bikeme.mvp.views.routes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.classes.Route;
import com.android.bikeme.mvp.interactors.routes.RouteHomeInteractorImpl;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenter;
import com.android.bikeme.mvp.presenters.routes.RouteHomePresenterImpl;
import com.android.bikeme.mvp.views.routes.create_route.CreateRouteActivity;
import com.android.bikeme.mvp.views.routes.route_detail.RouteDetailActivity;

public class RouteHomeFragment extends Fragment implements RouteListAdapter.OnRouteClickListener, RouteHomeView, View.OnClickListener {

    public static final String TAG = RouteHomeFragment.class.getSimpleName();

    private RouteHomePresenter routeHomePresenter;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private RadioGroup tabsGroup;
    private ProgressDialog mProgressDialog;
    private BaseActivity parent;

    public RouteHomeFragment() {}

    public static RouteHomeFragment newInstance()
    {
        return new RouteHomeFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        parent = (BaseActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        RouteModel routeModel = new RouteModel(getActivity().getContentResolver());
        routeHomePresenter = new RouteHomePresenterImpl(this,getContext(),new RouteHomeInteractorImpl(routeModel));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view  = LayoutInflater.from(getContext()).inflate(R.layout.bike_me_fragment_routes, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.routes_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabsGroup = (RadioGroup)view.findViewById(R.id.routes_tabs_group);
        tabsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int idButton)
            {
                switch (idButton)
                {
                    case R.id.tab_suggest:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_new:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_mine:
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        tabsGroup.check(R.id.tab_suggest);
                        break;
                    case 1:
                        tabsGroup.check(R.id.tab_new);
                        break;
                    case 2:
                        tabsGroup.check(R.id.tab_mine);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.floating_action_button);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add));
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);
        return  view;
    }

    @Override
    public void onClick(View view)
    {
        routeHomePresenter.createRouteButtonClicked();
    }

    @Override
    public void onRouteClick(String idRoute)
    {
       routeHomePresenter.routeDetailItemClicked(idRoute);
    }

    @Override
    public void navigateToCreateRoute()
    {
        Intent intent = new Intent(this.getContext(), CreateRouteActivity.class);
        parent.startActivityTransition(intent);
    }

    @Override
    public void navigateToRouteDetail(Route route)
    {
        Intent intent = new Intent(parent, RouteDetailActivity.class);
        intent.putExtra(Route.ROUTE_KEY, route);
        parent.startActivityTransition(intent);
    }

    @Override
    public void showError(String message)
    {
        Toast.makeText(this.getContext(),message, Toast.LENGTH_LONG).show();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return  RouteListFragment.newInstance(position);
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.suggest_text);
                case 1:
                    return getResources().getString(R.string.new_text);
                case 2:
                    return getResources().getString(R.string.mine_text);
            }
            return null;
        }
    }

    @Override
    public void showProgressDialog()
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.loading_text));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }
}