package com.android.bikeme.mvp.interactors.user;

import com.android.bikeme.mvp.presenters.user.SignInPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Daniel on 12/05/2017.
 */
public interface SignInInteractor {

    void authenticationWithGoogle(GoogleSignInAccount acct, SignInPresenter.OnFinishedCallbackGoogleSignIn onFinishedCallbackGoogle);
    void authenticationWithEmailPassword(String email, String password, SignInPresenter.OnFinishedCallbackEmailPasswordSignIn onFinishedCallbackEmailPasswordSignIn);

}
