package com.android.bikeme.databaseremote.syncronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Daniel on 18/03/2017.
 */
public class SynchronizationService extends Service {

    private static Synchronization synchronization = null;
    private static final Object lock = new Object();

    @Override
    public void onCreate()
    {
        synchronized (lock)
        {
            if (synchronization == null)
            {
                synchronization = new Synchronization(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return synchronization.getSyncAdapterBinder();
    }
}
