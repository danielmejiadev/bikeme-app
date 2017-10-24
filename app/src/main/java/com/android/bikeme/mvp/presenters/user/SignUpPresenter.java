package com.android.bikeme.mvp.presenters.user;

/**
 * Created by Daniel on 14/05/2017.
 */
public interface SignUpPresenter {


    interface OnFinishedCallbackEmailPasswordSignUp{
        void onFinishedEmailPasswordSignUp();
        void onErrorEmailPasswordSignUp(String error);
        void onErrorEmailAlreadyExist();
    }

    void signInEmailPassword(String displayName, String email, String password, String repeatPassword);
}
