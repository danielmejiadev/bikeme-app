package com.android.bikeme.mvp.views.challenges;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaselocal.models.ChallengeModel;

/**
 * Created by Daniel on 10 ago 2017.
 */
public class ChallengeAsyncTaskLoader extends AsyncTaskLoader<ChallengeResponseLoader> {

    private ChallengeResponseLoader cachedData;
    private ChallengeModel challengeModel;
    private Context context;
    private String currentUserUid;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ContentObserver observer = new ContentObserver(new Handler())
    {
        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            Log.i(ChallengeFragment.TAG," Cambio detectado en retos: " +uri.toString());
            swipeRefreshLayout.setRefreshing(true);
            onContentChanged();
        }
    };


    public ChallengeAsyncTaskLoader(Context context, SwipeRefreshLayout swipeRefreshLayout,String currentUserUid)
    {
        super(context);
        this.context=context;
        this.challengeModel = new ChallengeModel(context.getContentResolver());
        this.currentUserUid = currentUserUid;
        this.swipeRefreshLayout = swipeRefreshLayout;

        Log.i(ChallengeFragment.TAG,"register observer challenge");
        context.getContentResolver().registerContentObserver(DataBaseContract.Challenge.URI_CONTENT, false, observer);
    }

    @Override
    public ChallengeResponseLoader loadInBackground()
    {
        return challengeModel.getChallengesToShow(currentUserUid);
    }

    @Override
    protected void onStartLoading()
    {
        if (cachedData != null)
        {
            Log.i(ChallengeFragment.TAG,"Data in cache");
            // Deliver any previously loaded data immediately.
            deliverResult(cachedData);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (takeContentChanged() || cachedData == null)
        {
            Log.i(ChallengeFragment.TAG,"load");
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

        Log.i(ChallengeFragment.TAG,"unregister observer event");
        context.getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public void onCanceled(ChallengeResponseLoader  contactList)
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
    public void deliverResult(ChallengeResponseLoader quickCalls)
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