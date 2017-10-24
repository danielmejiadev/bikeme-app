package com.android.bikeme.mvp.interactors.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.bikeme.R;
import com.android.bikeme.bikemeserverconnection.EndPointsApi;
import com.android.bikeme.bikemeserverconnection.RetrofitRestApiClient;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.classes.User;
import com.android.bikeme.mvp.presenters.user.SignUpPresenter;
import com.android.bikeme.mvp.views.user.SignInActivity;
import com.android.bikeme.mvp.views.user.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 14/05/2017.
 */
public class SignUpInteractorImpl implements SignUpInteractor{

    private FirebaseAuth firebaseAuth;
    private Context context;
    private UserModel userModel;

    public SignUpInteractorImpl(FirebaseAuth firebaseAuth,Context context, UserModel userModel)
    {
        this.firebaseAuth = firebaseAuth;
        this.context = context;
        this.userModel = userModel;
    }

    @Override
    public void registerWithEmailPassword(final String displayName, String email, String password, final SignUpPresenter.OnFinishedCallbackEmailPasswordSignUp onFinishedCallbackEmailPasswordSignUp)
    {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                    currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Log.i(SignUpActivity.TAG, "Sign up success");
                                final User user = new User();
                                user.setUid(currentUser.getUid());
                                user.setDisplayName(currentUser.getDisplayName());
                                user.setEmail(currentUser.getEmail());

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
                                            Log.i(SignUpActivity.TAG, "sign up local server okay");
                                            onFinishedCallbackEmailPasswordSignUp.onFinishedEmailPasswordSignUp();
                                        }
                                        else
                                        {
                                            Log.e(SignUpActivity.TAG, "response body null to create user server");
                                            onFinishedCallbackEmailPasswordSignUp.onErrorEmailPasswordSignUp(context.getString(R.string.error_create_account_text));
                                            currentUser.delete();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t)
                                    {
                                        Log.e(SignUpActivity.TAG, "onFailure to create user server", t);
                                        onFinishedCallbackEmailPasswordSignUp.onErrorEmailPasswordSignUp(context.getString(R.string.error_create_account_text));
                                        currentUser.delete();
                                    }
                                });
                            }
                            else
                            {
                                Log.e(SignUpActivity.TAG, "onFailure to update displayName");
                                onFinishedCallbackEmailPasswordSignUp.onErrorEmailPasswordSignUp(context.getString(R.string.error_create_account_text));
                                currentUser.delete();
                            }
                        }
                    });
                }
                else
                {
                    FirebaseException firebaseException = (FirebaseException)task.getException();
                    if(firebaseException instanceof FirebaseAuthUserCollisionException)
                    {
                        FirebaseAuthUserCollisionException firebaseAuthUserCollisionException = (FirebaseAuthUserCollisionException)firebaseException;
                        Log.e(SignUpActivity.TAG, "Email already exist "+firebaseAuthUserCollisionException.getErrorCode());
                        onFinishedCallbackEmailPasswordSignUp.onErrorEmailAlreadyExist();
                    }
                    else
                    {
                        Log.e(SignUpActivity.TAG, "exception"+firebaseException);
                        onFinishedCallbackEmailPasswordSignUp.onErrorEmailPasswordSignUp(context.getString(R.string.error_create_account_text));
                    }
                }
            }
        });
    }
}