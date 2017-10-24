package com.android.bikeme.mvp.views.user;

/**
 * Created by Daniel on 12/05/2017.
 */
public interface SignInView {

    void goToHome();
    void goToSignUp();
    void launchDialogSelectAccount();
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
    void signOut();
    void showErrorEmail(String message);
}
