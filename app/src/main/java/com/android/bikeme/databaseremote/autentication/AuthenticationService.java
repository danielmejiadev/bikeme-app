package com.android.bikeme.databaseremote.autentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Daniel on 18/03/2017.
 */
public class AuthenticationService extends Service {

    private AuthenticationAccount authenticationAccount;

    @Override
    public void onCreate()
    {
        authenticationAccount = new AuthenticationAccount(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return authenticationAccount.getIBinder();
    }
}