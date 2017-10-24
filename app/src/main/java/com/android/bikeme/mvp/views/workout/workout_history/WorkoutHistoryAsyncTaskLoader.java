package com.android.bikeme.mvp.views.workout.workout_history;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaselocal.models.WorkoutModel;

import java.util.HashMap;

/**
 * Created by Daniel on 4 sep 2017.
 */
public class WorkoutHistoryAsyncTaskLoader extends AsyncTaskLoader<HashMap<String,Object>> {

    private HashMap<String,Object> cachedData;
    WorkoutModel workoutModel;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    String currentUserUid;

    private ContentObserver observer = new ContentObserver(new Handler())
    {
        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            Log.i("Loader Event"," Cambio detectado en workouts: " +uri.toString());
            swipeRefreshLayout.setRefreshing(true);
            onContentChanged();
        }
    };


    public WorkoutHistoryAsyncTaskLoader(Context context, SwipeRefreshLayout swipeRefreshLayout, String currentUserUid)
    {
        super(context);
        this.context=context;
        this.workoutModel = new WorkoutModel(context.getContentResolver());
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.currentUserUid = currentUserUid;

        Log.i("Loader event","register observer event");

        context.getContentResolver().registerContentObserver(DataBaseContract.Workout.URI_CONTENT, false, observer);
    }

    @Override
    public HashMap<String,Object> loadInBackground()
    {
        return workoutModel.getUserWorkouts(currentUserUid);
    }

    @Override
    protected void onStartLoading()
    {
        if (cachedData != null)
        {
            Log.i("Loader event ","Data in cache");
            // Deliver any previously loaded data immediately.
            deliverResult(cachedData);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (takeContentChanged() || cachedData == null)
        {
            Log.i("Loader event","load");
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

        Log.i("Loader event","unregister observer event");
        context.getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public void onCanceled(HashMap<String,Object> contactList)
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
    public void deliverResult(HashMap<String,Object> quickCalls)
    {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources();
        } else if (isStarted()) {
            super.deliverResult(quickCalls);
            this.cachedData = quickCalls;
        }
    }
}
