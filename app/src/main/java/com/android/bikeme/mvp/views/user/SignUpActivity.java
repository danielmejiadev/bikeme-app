package com.android.bikeme.mvp.views.user;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.mvp.interactors.user.SignUpInteractorImpl;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.presenters.user.SignUpPresenter;
import com.android.bikeme.mvp.presenters.user.SignUpPresenterImpl;

public class SignUpActivity extends BaseActivity implements SignUpView, View.OnClickListener {

    public static final String TAG = "SignUpActivity";

    private ProgressDialog mProgressDialog;
    private EditText inputName,inputEmail,inputPassword,inputRepeatPassword;
    private AppCompatButton buttonSignUpEmailPassword;
    private SignUpPresenter signUpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView title = (TextView)findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Lobster-Regular.ttf");
        title.setTypeface(type);

        setTitle("");

        buttonSignUpEmailPassword = (AppCompatButton)findViewById(R.id.email_password_sign_up_button);
        buttonSignUpEmailPassword.setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(inputName.getText().length() > 0 &&
                        inputEmail.getText().length() > 0 &&
                        inputPassword.getText().length() > 0 &&
                        inputRepeatPassword.getText().length() > 0)
                {
                    buttonSignUpEmailPassword.setBackgroundTintList(ContextCompat.getColorStateList(SignUpActivity.this,R.color.yellow));
                    buttonSignUpEmailPassword.setEnabled(true);
                }
            }
        };

        inputName = (EditText)findViewById(R.id.input_name_create);
        inputName.addTextChangedListener(textWatcher);
        inputEmail = (EditText)findViewById(R.id.input_email_create);
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword = (EditText)findViewById(R.id.input_password_create);
        inputPassword.addTextChangedListener(textWatcher);
        inputRepeatPassword = (EditText)findViewById(R.id.input_repeat_password_create);
        inputRepeatPassword.addTextChangedListener(textWatcher);

        ContentResolver contentResolver = BikeMeApplication.getInstance().getContentResolver();
        signUpPresenter = new SignUpPresenterImpl(this,this, new SignUpInteractorImpl(firebaseAuth,this,new UserModel(contentResolver)));
    }

    @Override
    public void onBackPressed()
    {
        finishActivityTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivityTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        String displayName = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();
        signUpPresenter.signInEmailPassword(displayName,email,password, repeatPassword);
    }

    @Override
    public void showErrorEmail(String message)
    {
        inputEmail.requestFocus();
        inputEmail.setError(message);
    }

    @Override
    public void showErrorPassword(String message)
    {
        inputPassword.requestFocus();
        inputPassword.setError(message);
    }

    @Override
    public void showMessage(String message)
    {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog()
    {
        mProgressDialog = BikeMeApplication.getInstance().getProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.registering_user));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog()
    {
        BikeMeApplication.getInstance().hideProgressDialog(mProgressDialog);
    }
}