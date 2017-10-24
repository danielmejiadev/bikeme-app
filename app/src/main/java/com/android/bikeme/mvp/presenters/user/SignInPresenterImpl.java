package com.android.bikeme.mvp.presenters.user;

import android.content.Context;
import android.util.Log;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.mvp.interactors.user.SignInInteractor;
import com.android.bikeme.mvp.views.user.SignInActivity;
import com.android.bikeme.mvp.views.user.SignInView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Daniel on 12/05/2017.
 */
public class SignInPresenterImpl implements SignInPresenter, SignInPresenter.OnFinishedCallbackGoogleSignIn, SignInPresenter.OnFinishedCallbackEmailPasswordSignIn {

    private Context context;
    private SignInView signInView;
    private SignInInteractor signInInteractor;

    public SignInPresenterImpl(Context context, SignInView signInView, SignInInteractor signInInteractor)
    {
        this.context = context;
        this.signInView=signInView;
        this.signInInteractor=signInInteractor;
    }


    @Override
    public void onSignUpClick()
    {
        signInView.goToSignUp();
    }

    @Override
    public void signInEmailPassword(String email, String password)
    {
        if(BikeMeApplication.getInstance().isOnline())
        {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                signInView.showProgressDialog();
                signInInteractor.authenticationWithEmailPassword(email,password, this);
            }
            else
            {
                signInView.showErrorEmail(context.getString(R.string.invalid_email_text));
            }
        }
        else
        {
            signInView.showMessage(context.getString(R.string.check_connection_text));
        }
    }

    @Override
    public void onFinishedEmailPasswordSignIn()
    {
        signInView.hideProgressDialog();
        signInView.goToHome();
    }

    @Override
    public void onErrorEmailPasswordSignIn(String error)
    {
        signInView.signOut();
        signInView.hideProgressDialog();
        signInView.showMessage(error);
    }

    @Override
    public void onGoogleSignInClick()
    {
        if(BikeMeApplication.getInstance().isOnline())
        {
            signInView.launchDialogSelectAccount();
        }
        else
        {
            signInView.showMessage(context.getString(R.string.check_connection_text));
        }
    }

    @Override
    public void validateGoogleAccount(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            signInView.showProgressDialog();
            signInInteractor.authenticationWithGoogle(result.getSignInAccount(),this);
        }
        else
        {
            Log.e(SignInActivity.TAG, "Failed get sign in google account " + result.getStatus());
            signInView.showMessage(context.getString(R.string.error_load_account_text));
        }
    }

    @Override
    public void onFinishedGoogleSignIn()
    {
        signInView.hideProgressDialog();
        signInView.goToHome();
    }

    @Override
    public void onErrorGoogleSignIn()
    {
        signInView.signOut();
        signInView.hideProgressDialog();
        signInView.showMessage(context.getString(R.string.error_sign_in_text));
    }
}
