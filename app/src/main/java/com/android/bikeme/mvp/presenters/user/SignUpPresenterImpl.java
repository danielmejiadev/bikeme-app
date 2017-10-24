package com.android.bikeme.mvp.presenters.user;

import android.content.Context;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.mvp.interactors.user.SignUpInteractor;
import com.android.bikeme.mvp.views.user.SignUpView;

/**
 * Created by Daniel on 14/05/2017.
 */
public class SignUpPresenterImpl implements SignUpPresenter, SignUpPresenter.OnFinishedCallbackEmailPasswordSignUp{

    private Context context;
    private SignUpView signUpView;
    private SignUpInteractor signUpInteractor;

    public SignUpPresenterImpl(Context context, SignUpView signUpView, SignUpInteractor signUpInteractor)
    {
        this.context = context;
        this.signUpView=signUpView;
        this.signUpInteractor=signUpInteractor;
    }


    @Override
    public void signInEmailPassword(String displayName, String email, String password, String repeatPassword)
    {
        if(BikeMeApplication.getInstance().isOnline())
        {
            boolean emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            boolean passwordOk = password.equals(repeatPassword);
            boolean passwordStrong = password.length() >= 6;

            if(!emailOk)
            {
                signUpView.showErrorEmail(context.getString(R.string.invalid_email_text));
            }
            if(!passwordOk)
            {
                signUpView.showErrorPassword(context.getString(R.string.passwords_not_equal_text));
            }
            if(!passwordStrong)
            {
                signUpView.showErrorPassword(context.getString(R.string.password_weak_text));
            }
            if(emailOk && passwordOk && passwordStrong)
            {
                signUpView.showProgressDialog();
                signUpInteractor.registerWithEmailPassword(displayName,email, password, this);
            }
        }
        else
        {
            signUpView.showMessage(context.getString(R.string.check_connection_text));
        }
    }

    @Override
    public void onFinishedEmailPasswordSignUp()
    {
        signUpView.hideProgressDialog();
        signUpView.goToHome();
    }

    @Override
    public void onErrorEmailPasswordSignUp(String error)
    {
        signUpView.hideProgressDialog();
        signUpView.showMessage(context.getString(R.string.recording_error_text));
    }

    @Override
    public void onErrorEmailAlreadyExist()
    {
        signUpView.hideProgressDialog();
        signUpView.showErrorEmail(context.getString(R.string.email_not_available_text));
    }
}
