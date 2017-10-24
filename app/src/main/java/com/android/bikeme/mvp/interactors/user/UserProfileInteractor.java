package com.android.bikeme.mvp.interactors.user;

import android.content.ContentValues;

/**
 * Created by Daniel on 19 ago 2017.
 */
public interface UserProfileInteractor {

    void updateUser(String userId, String value, int key);
}
