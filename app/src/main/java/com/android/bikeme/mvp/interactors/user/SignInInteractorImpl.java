package com.android.bikeme.mvp.interactors.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.classes.User;
import com.android.bikeme.mvp.presenters.user.SignInPresenter;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.mvp.views.user.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 12/05/2017.
 */
public class SignInInteractorImpl implements  SignInInteractor{

    private FirebaseAuth firebaseAuth;
    private Context context;
    private UserModel userModel;
    public SignInInteractorImpl(FirebaseAuth firebaseAuth,Context context, UserModel userModel)
    {
        this.firebaseAuth = firebaseAuth;
        this.context = context;
        this.userModel = userModel;
    }

    @Override
    public void authenticationWithEmailPassword(String email, String password, final SignInPresenter.OnFinishedCallbackEmailPasswordSignIn onFinishedCallbackEmailPasswordSignIn)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    assert currentUser.getPhotoUrl() != null;
                    final User user = new User();
                    user.setUid(currentUser.getUid());
                    user.setDisplayName(currentUser.getDisplayName());
                    user.setEmail(currentUser.getEmail());
                    Log.i(SignInActivity.TAG, "Sign in email and password ");

                    RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
                    EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
                    Call<User> userCall = endPointsApi.insertUser(user);
                    userCall.enqueue(new Callback<User>()
                    {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response)
                        {
                            User user = response.body();
                            if(response.isSuccessful() && user!=null)
                            {
                                userModel.insertUser(user);
                                Log.i(SignInActivity.TAG, "sign in email and password server and local okay");
                                onFinishedCallbackEmailPasswordSignIn.onFinishedEmailPasswordSignIn();
                            }
                            else
                            {
                                Log.e(SignInActivity.TAG, "response body null to create user server email and password");
                                onFinishedCallbackEmailPasswordSignIn.onErrorEmailPasswordSignIn(context.getString(R.string.error_sign_in_text));
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t)
                        {
                            Log.e(SignInActivity.TAG, "onFailure to create user server email and password", t);
                            onFinishedCallbackEmailPasswordSignIn.onErrorEmailPasswordSignIn(context.getString(R.string.error_sign_in_text));
                        }
                    });
                }
                else
                {
                    Exception tasException = task.getException();
                    if(tasException instanceof FirebaseAuthInvalidUserException)
                    {
                        FirebaseAuthInvalidUserException firebaseAuthInvalidUserException = (FirebaseAuthInvalidUserException)tasException;
                        Log.e(SignInActivity.TAG, firebaseAuthInvalidUserException.getErrorCode());

                        switch (firebaseAuthInvalidUserException.getErrorCode())
                        {
                            case "ERROR_USER_DISABLED":
                                Log.e(SignInActivity.TAG, "User disable");
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                Log.e(SignInActivity.TAG, "User do not exist");
                                break;
                            case "ERROR_USER_TOKEN_EXPIRED":
                                Log.e(SignInActivity.TAG, "User token expired");
                                break;
                            case "ERROR_INVALID_USER_TOKEN":
                                Log.e(SignInActivity.TAG, "User token invalid");
                                break;
                        }
                    }
                    else
                    {
                        if(tasException instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Log.e(SignInActivity.TAG, "credentials error");
                        }
                    }
                    onFinishedCallbackEmailPasswordSignIn.onErrorEmailPasswordSignIn(context.getString(R.string.email_password_incorrect_text));
                }
            }
        });
    }

    @Override
    public void authenticationWithGoogle(GoogleSignInAccount acct, final SignInPresenter.OnFinishedCallbackGoogleSignIn onFinishedCallbackGoogleSignIn)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    assert currentUser.getPhotoUrl() != null;
                    final User user = new User();
                    user.setUid(currentUser.getUid());
                    user.setDisplayName(currentUser.getDisplayName());
                    user.setEmail(currentUser.getEmail());
                    user.setPhoto(currentUser.getPhotoUrl().toString());
                    Log.i(SignInActivity.TAG, "Sign in google account success ");

                    RetrofitRestApiClient retrofitRestApiClient  = RetrofitRestApiClient.getInstance();
                    EndPointsApi endPointsApi = retrofitRestApiClient.getEndPointsApi();
                    Call<User> userCall = endPointsApi.insertUser(user);
                    userCall.enqueue(new Callback<User>()
                    {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response)
                        {
                            User user = response.body();
                            if(response.isSuccessful() && user!=null)
                            {
                                userModel.insertUser(user);
                                Log.i(SignInActivity.TAG, "sign in server and local okay");
                                onFinishedCallbackGoogleSignIn.onFinishedGoogleSignIn();
                            }
                            else
                            {
                                Log.e(SignInActivity.TAG, "response body null to create user server");
                                onFinishedCallbackGoogleSignIn.onErrorGoogleSignIn();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t)
                        {
                            Log.e(SignInActivity.TAG, "onFailure to create user server", t);
                            onFinishedCallbackGoogleSignIn.onErrorGoogleSignIn();
                        }
                    });
                }
                else
                {
                    Log.e(SignInActivity.TAG, "Sign in google account failed", task.getException());
                    onFinishedCallbackGoogleSignIn.onErrorGoogleSignIn();
                }
            }
        });
    }
}