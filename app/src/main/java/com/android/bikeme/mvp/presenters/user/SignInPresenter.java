package com.android.bikeme.mvp.presenters.user;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Daniel on 12/05/2017.
 */
public interface SignInPresenter {


    interface OnFinishedCallbackGoogleSignIn{
        void onFinishedGoogleSignIn();
        void onErrorGoogleSignIn();
    }

    interface OnFinishedCallbackEmailPasswordSignIn{
        void onFinishedEmailPasswordSignIn();
        void onErrorEmailPasswordSignIn(String error);
    }

    void onGoogleSignInClick();
    void validateGoogleAccount(GoogleSignInResult result);
    void signInEmailPassword(String email, String password);
    void onSignUpClick();
}
