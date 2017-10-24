package com.android.bikeme.mvp.interactors.user;

import android.content.ContentValues;
import android.content.Context;

import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.databaseremote.syncronization.Synchronization;

/**
 * Created by Daniel on 19 ago 2017.
 */
public class UserProfileInteractorImpl implements UserProfileInteractor {

    private UserModel userModel;

    public UserProfileInteractorImpl(UserModel userModel)
    {
        this.userModel = userModel;
    }

    @Override
    public void updateUser(String userId, String value, int key)
    {
        userModel.updateUser(userId,value,key);
    }
}
