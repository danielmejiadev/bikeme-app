package com.android.bikeme.mvp.views.user;

/**
 * Created by Daniel on 12/05/2017.
 */
public interface SignUpView {

    void goToHome();
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
    void showErrorEmail(String message);
    void showErrorPassword(String message);
}
