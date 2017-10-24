package com.android.bikeme.mvp.views.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.bikemeutils.DownloadMapOfflineService;
import com.android.bikeme.databaseremote.syncronization.Synchronization;
import com.android.bikeme.mvp.interactors.user.SignInInteractorImpl;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.user.SignInPresenter;
import com.android.bikeme.mvp.presenters.user.SignInPresenterImpl;
import com.android.bikeme.mvp.views.BikeMeActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, SignInView, View.OnClickListener {

    public static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInPresenter signInPresenter;
    private EditText inputEmail,inputPassword;
    private AppCompatButton buttonSignInEmailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity_view);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        signInPresenter = new SignInPresenterImpl(this,this,new SignInInteractorImpl(firebaseAuth,this,new UserModel(getContentResolver())));

        TextView title = (TextView)findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Lobster-Regular.ttf");
        title.setTypeface(type);

        buttonSignInEmailPassword = (AppCompatButton)findViewById(R.id.email_password_sign_in_button);
        buttonSignInEmailPassword.setOnClickListener(this);

        findViewById(R.id.link_sign_up_button).setOnClickListener(this);

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        TextView textSignInButton = ((TextView) ((com.google.android.gms.common.SignInButton)findViewById(R.id.google_sign_in_button)).getChildAt(0));
        textSignInButton.setText(getString(R.string.google_text));
        textSignInButton.setTextSize(16);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(inputEmail.getText().length() > 0 && inputPassword.getText().length() > 0)
                {
                    buttonSignInEmailPassword.setBackgroundTintList(ContextCompat.getColorStateList(SignInActivity.this,R.color.yellow));
                    buttonSignInEmailPassword.setEnabled(true);
                }
            }
        };

        inputEmail = (EditText)findViewById(R.id.input_email);
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword = (EditText)findViewById(R.id.input_password);
        inputPassword.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        signOut();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.email_password_sign_in_button:
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                signInPresenter.signInEmailPassword(email,password);
                break;

            case R.id.link_sign_up_button:
                signInPresenter.onSignUpClick();
                break;

            case R.id.google_sign_in_button:
                signInPresenter.onGoogleSignInClick();
                break;
        }
    }

    @Override
    public void goToSignUp()
    {
        inputEmail.setText("");
        inputPassword.setText("");
        startActivityTransition(new Intent(this,SignUpActivity.class));
    }

    @Override
    public void launchDialogSelectAccount()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInPresenter.validateGoogleAccount(result);
        }
    }

    @Override
    public void signOut()
    {
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
        {
            @Override
            public void onConnected(@Nullable Bundle bundle)
            {
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            }

            @Override
            public void onConnectionSuspended(int i) {}
        });
    }

    @Override
    public void showErrorEmail(String message)
    {
        inputEmail.requestFocus();
        inputEmail.setError(message);
    }

   @Override
   public void showMessage(String message)
   {
       Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
   }

    @Override
    public void showProgressDialog()
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.starting_text));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_LONG).show();
    }
}