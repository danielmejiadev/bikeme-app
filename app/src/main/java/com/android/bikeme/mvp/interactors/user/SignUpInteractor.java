package com.android.bikeme.mvp.interactors.user;

import com.android.bikeme.mvp.presenters.user.SignUpPresenter;

/**
 * Created by Daniel on 14/05/2017.
 */
public interface SignUpInteractor {
    void registerWithEmailPassword(String displayName, String email, String password, SignUpPresenter.OnFinishedCallbackEmailPasswordSignUp onFinishedCallbackEmailPasswordSignUp);

}
