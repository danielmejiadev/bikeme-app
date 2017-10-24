package com.android.bikeme.mvp.views.routes;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.RouteModel;
import com.android.bikeme.mvp.views.BikeMeActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 21 may 2017.
 */
public class RouteListAsyncTaskLoader extends AsyncTaskLoader<ArrayList<HashMap<String, Object>>> {

    private ArrayList<HashMap<String, Object>> cachedData;
    private RouteModel routeModel;
    private BikeMeActivity parent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int type;
    private FirebaseUser currentUser;

    private ContentObserver observer = new ContentObserver(new Handler())
    {
        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            Log.i(RouteHomeFragment.TAG, type +" Cambio detectado en: " +uri.toString());
            swipeRefreshLayout.setRefreshing(true);
            onContentChanged();
        }
    };

    public RouteListAsyncTaskLoader(BikeMeActivity parent, SwipeRefreshLayout swipeRefreshLayout, int type)
    {
        super(parent);

        this.parent =parent;
        this.currentUser = parent.getCurrentUser();
        this.routeModel = new RouteModel(parent.getContentResolver());
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.type = type;

        Log.i(RouteHomeFragment.TAG,"register observer " +type);

        parent.getContentResolver().registerContentObserver(DataBaseContract.Route.URI_CONTENT, false, observer);
        parent.getContentResolver().registerContentObserver(DataBaseContract.Rating.URI_CONTENT, false, observer);
        //This notification send in bike activity when change location
        parent.getContentResolver().registerContentObserver(DataBaseContract.User.URI_LOCATION_NEW, false, observer);
    }

    @Override
    public ArrayList<HashMap<String, Object>>  loadInBackground()
    {
        ArrayList<HashMap<String, Object>> response = null;

        switch (type)
        {
            case 0:
                response = routeModel.getSuggestRoutes(currentUser.getUid(),parent.getCurrentLocation());
                break;
            case 1:
                response = routeModel.getNewRoutes(currentUser.getUid(),parent.getCurrentLocation());
                break;
            case 2:
                response = routeModel.getMineRoutes(currentUser.getUid());
                break;
        }
        return response;
    }

    @Override
    protected void onStartLoading()
    {
        if (cachedData != null)
        {
            Log.i(RouteHomeFragment.TAG,"Data in cache");
            // Deliver any previously loaded data immediately.
            deliverResult(cachedData);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (takeContentChanged() || cachedData == null)
        {
            Log.i(RouteHomeFragment.TAG,"load");
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading()
    {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset()
    {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'frequentlyCallList'.
        if (cachedData != null)
        {
            releaseResources();
            cachedData = null;
        }

        Log.i(RouteHomeFragment.TAG,"unregister observer "+type);
        parent.getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public void onCanceled(ArrayList<HashMap<String, Object>> contactList)
    {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(contactList);
        // rollbackContentChanged(); // FOR API 18

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources();
    }

    private void releaseResources()
    {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
        cachedData = null;
    }

    @Override
    public void deliverResult(ArrayList<HashMap<String, Object>> quickCalls)
    {
        if (isReset())
        {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources();
        }
        else if (isStarted())
        {
            super.deliverResult(quickCalls);
            this.cachedData = quickCalls;
        }
    }
}
