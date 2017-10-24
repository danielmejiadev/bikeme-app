package com.android.bikeme.mvp.views.user;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BaseActivity;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.interactors.user.UserProfileInteractorImpl;
import com.android.bikeme.mvp.presenters.user.UserProfilePresenter;
import com.android.bikeme.mvp.presenters.user.UserProfilePresenterImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener, UserProfileView {

    private User user;
    private UserProfilePresenter userProfilePresenter;
    private LinearLayout social_layout;
    private int totalPoints;
    private TextView aboutMeText,preferenceDaysText,preferenceHoursText;
    private String[] socialNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup root = (ViewGroup)findViewById(R.id.content);
        setStateBarTint(root,toolbar,getStatusBarHeight(),hasTranslucentStatusBar());

        user = getIntent().getParcelableExtra(User.USER_KEY);
        totalPoints = getIntent().getIntExtra(User.TOTAL_POINTS_KEY,-1);

        UserModel userModel = new UserModel(getContentResolver());
        userProfilePresenter = new UserProfilePresenterImpl(this,new UserProfileInteractorImpl(userModel));
        socialNetworks = getResources().getStringArray(R.array.social_networks);

        CircleImageView photoImageView = (CircleImageView)findViewById(R.id.user_profile_photo);
        BikeMeApplication.getInstance().loadImage(Uri.parse(user.getPhoto()),photoImageView, ContextCompat.getDrawable(this, R.drawable.default_avatar));

        ((TextView)findViewById(R.id.user_profile_text_name)).setText(user.getDisplayName());

        ((TextView)findViewById(R.id.user_profile_text_email)).setText(user.getEmail());

        aboutMeText = (TextView)findViewById(R.id.user_profile_text_about_me);
        social_layout = (LinearLayout)findViewById(R.id.user_profile_social_layout);
        preferenceDaysText = (TextView)findViewById(R.id.user_profile_text_preference_days);
        preferenceHoursText = (TextView)findViewById(R.id.user_profile_text_preference_hours);

        setLevel();
        setButtonsEdit();
        setAboutMe(user.getAboutMe());
        setSocialNetworks(user.getSocialNetworksMap());
        setPreferenceDays(user.getPreferenceDaysList());
        setPreferenceHours(user.getPreferenceHoursList());
    }

    public void setLevel()
    {
        LinearLayout levelPointsLayout = (LinearLayout) findViewById(R.id.user_profile_level_layout);
        ImageView levelImage = (ImageView) findViewById(R.id.user_profile_level_image);
        ProgressBar levelProgressBar = (ProgressBar) findViewById(R.id.user_profile_progress_bar);
        TextView levelPointsText = (TextView) findViewById(R.id.user_profile_level_points_text);
        TextView levelUserText = (TextView) findViewById(R.id.user_profile_level_text);
        levelUserText.setText(getResources().getStringArray(R.array.user_levels)[user.getLevel()]);

        int userLevel = user.getLevel();

        if(userLevel==0)
        {
            levelImage.setVisibility(View.GONE);
        }
        else
        {
            levelImage.setBackground(BikeMeApplication.getInstance().getDrawable("level_"+userLevel));
        }

        if(userLevel<4 && totalPoints>=0)
        {
            int percent = totalPoints*100/User.TOTAL_POINTS_LEVEL[userLevel];
            levelProgressBar.setVisibility(View.VISIBLE);
            levelProgressBar.setProgress(percent);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                LayerDrawable drawable = (LayerDrawable) levelProgressBar.getProgressDrawable();
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.primary), PorterDuff.Mode.SRC_IN);
                levelProgressBar.setProgressDrawable(drawable);
            }
            levelPointsLayout.setVisibility(View.VISIBLE);
            levelPointsText.setText(getString(R.string.user_profile_progress_bar_level_text,totalPoints,User.TOTAL_POINTS_LEVEL[userLevel]));
        }
    }

    public void setButtonsEdit()
    {
        AppCompatButton editAboutMe = (AppCompatButton)findViewById(R.id.user_profile_edit_about_me);
        AppCompatButton editSocialNetworks = (AppCompatButton)findViewById(R.id.user_profile_edit_social);
        AppCompatButton editPreferenceDays = (AppCompatButton)findViewById(R.id.user_profile_edit_preferences_days);
        AppCompatButton editPreferenceHours = (AppCompatButton)findViewById(R.id.user_profile_edit_preferences_hour);

        if(currentUser.getUid().equals(user.getUid()))
        {
            editAboutMe.setOnClickListener(this);
            editSocialNetworks.setOnClickListener(this);
            editPreferenceDays.setOnClickListener(this);
            editPreferenceHours.setOnClickListener(this);
        }
        else
        {
            editAboutMe.setVisibility(View.GONE);
            editSocialNetworks.setVisibility(View.GONE);
            editPreferenceDays.setVisibility(View.GONE);
            editPreferenceHours.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAboutMe(String aboutMe)
    {
        if(!aboutMe.isEmpty())
        {
            aboutMeText.setText(aboutMe);
        }
        else
        {
            aboutMeText.setText(R.string.user_profile_about_me_text_default);
        }
    }

    @Override
    public void setSocialNetworks(HashMap<String,String>  socialNetworksMap)
    {
        social_layout.removeAllViews();
        if(!socialNetworksMap.isEmpty())
        {
            for(Map.Entry<String, String>  social : socialNetworksMap.entrySet())
            {
                social_layout.addView(getTextViewIcon(social.getKey(),social.getValue()));
            }
        }
        else
        {
            social_layout.addView(getTextViewIcon("network",getString(R.string.user_profile_social_networks_text_default)));
        }
    }

    @Override
    public void setPreferenceDays(ArrayList<Integer> preferenceDaysList)
    {
        if(!preferenceDaysList.isEmpty())
        {
            String preferenceDays = "";
            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
            for(int index=0; index < preferenceDaysList.size(); index++)
            {
                preferenceDays += daysOfWeek[preferenceDaysList.get(index)] + (index != preferenceDaysList.size()-1 ? ", " : ".");
            }
            preferenceDaysText.setText(preferenceDays);
        }
        else
        {
            preferenceDaysText.setText(R.string.user_profile_preference_days_text_default);
        }
    }

    @Override
    public void setPreferenceHours(ArrayList<Integer> preferenceHoursList)
    {
        if(!preferenceHoursList.isEmpty())
        {
            String preferenceHours = "";
            String[] hourOfDay = getResources().getStringArray(R.array.hours_of_day);
            for(int index=0; index < preferenceHoursList.size(); index++)
            {
                preferenceHours += hourOfDay[preferenceHoursList.get(index)] + (index != preferenceHoursList.size()-1 ? ", " : ".");
            }
            preferenceHoursText.setText(preferenceHours);
        }
        else
        {
            preferenceHoursText.setText(R.string.user_profile_preference_hours_text_default);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.user_profile_edit_about_me:
                userProfilePresenter.onClickEditAboutMe(user.getAboutMe());
                break;
            case R.id.user_profile_edit_social:
                userProfilePresenter.onClickEditSocialNetworks(user.getSocialNetworksMap());
                break;
            case R.id.user_profile_edit_preferences_days:
                userProfilePresenter.onClickEditPreferenceDays(user.getPreferenceDaysList());
                break;
            case R.id.user_profile_edit_preferences_hour:
                userProfilePresenter.onClickEditPreferenceHours(user.getPreferenceHoursList());
                break;
            default:
                break;
        }
    }

    @Override
    public void showEditAboutMe(String aboutMe)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_about_me, null);
        dialogBuilder.setView(dialogView);
        final EditText  aboutMeEditText = (EditText)dialogView.findViewById(R.id.dialog_about_me_edit_text);
        aboutMeEditText.setText(aboutMe);
        dialogBuilder.setPositiveButton(R.string.save_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String aboutMe = aboutMeEditText.getText().toString();
                userProfilePresenter.editAboutMe(currentUser.getUid(),aboutMe);
                user.setAboutMe(aboutMe);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showEditSocialNetworks(final HashMap<String,String> socialNetworksMap)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final HashMap<String,EditText> editTextSocialMap = new HashMap<>();
        for(String social: socialNetworks)
        {
            View dialogView = getLayoutInflater().inflate(R.layout.edit_text_icon_check_box, null);
            final ImageView imageView = (ImageView)dialogView.findViewById(R.id.icon);
            imageView.setImageDrawable(BikeMeApplication.getInstance().getDrawable("ic_"+social));
            final EditText socialEditText = (EditText)dialogView.findViewById(R.id.dialog_social_networks_edit_text);
            editTextSocialMap.put(social,socialEditText);
            CheckBox socialCheckBox = (CheckBox)dialogView.findViewById(R.id.dialog_social_networks_check_box);
            String userSocial = socialNetworksMap.get(social);
            if(userSocial!=null)
            {
                socialEditText.setText(userSocial);
                socialCheckBox.setChecked(true);
            }
            else
            {
                socialEditText.setHint(getString(R.string.dialog_social_networks_edit_hint));
                socialEditText.setEnabled(false);
                imageView.setColorFilter(ContextCompat.getColor(this,R.color.secondary_text));
            }

            socialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    socialEditText.setEnabled(isChecked);
                    if(!isChecked)
                    {
                        socialEditText.setText("");
                        imageView.setColorFilter(ContextCompat.getColor(UserProfileActivity.this,R.color.secondary_text));
                    }
                    else
                    {
                        imageView.setColorFilter(ContextCompat.getColor(UserProfileActivity.this,R.color.primary));
                    }
                }
            });
            linearLayout.addView(dialogView);
        }
        dialogBuilder.setTitle(R.string.social_networks_text);
        Drawable iconTitle = ContextCompat.getDrawable(this,R.drawable.ic_network);
        iconTitle.setColorFilter(ContextCompat.getColor(this,R.color.primary), PorterDuff.Mode.MULTIPLY);
        dialogBuilder.setIcon(iconTitle);
        dialogBuilder.setView(linearLayout);
        dialogBuilder.setPositiveButton(R.string.save_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                HashMap<String,String> socialNetworks = new HashMap<>();
                for(Map.Entry<String,EditText> editTextEntry : editTextSocialMap.entrySet())
                {
                    EditText editTextSocial = editTextEntry.getValue();
                    String text = editTextSocial.getText().toString();
                    if(editTextSocial.isEnabled() && !text.isEmpty())
                    {
                        socialNetworks.put(editTextEntry.getKey(),text);
                    }
                }
                user.setSocialNetworksMap(socialNetworks);
                userProfilePresenter.editSocialNetworks(currentUser.getUid(),socialNetworks);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showEditPreferenceDays(ArrayList<Integer> preferencesDaysList)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_preferences_days);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.primary), PorterDuff.Mode.MULTIPLY);

        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
        boolean[] daysAlreadySelected = new boolean[daysOfWeek.length];
        final ArrayList<Integer> daysSelected = new ArrayList<>();
        for(int i : preferencesDaysList)
        {
            daysAlreadySelected[i]=true;
            daysSelected.add(i);
        }

        dialogBuilder.setTitle(R.string.dialog_preference_days_title);
        dialogBuilder.setIcon(icon);
        dialogBuilder.setMultiChoiceItems(daysOfWeek, daysAlreadySelected, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public  void onClick(DialogInterface dialog, int which, boolean isChecked)
            {
                if (isChecked)
                {
                    daysSelected.add(which);
                } else if (daysSelected.contains(which))
                {
                    daysSelected.remove(Integer.valueOf(which));
                }
            }
        });
        dialogBuilder.setPositiveButton(R.string.save_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                user.setPreferenceDaysList(daysSelected);
                userProfilePresenter.editPreferenceDays(currentUser.getUid(),daysSelected);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showEditPreferenceHours(ArrayList<Integer> preferencesHourList)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.DialogThemeUserProfile);
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_preferences_hours);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.primary), PorterDuff.Mode.MULTIPLY);

        String[] hoursOfDay = getResources().getStringArray(R.array.hours_of_day);
        boolean[] hoursAlreadySelected = new boolean[hoursOfDay.length];
        final ArrayList<Integer> hoursSelected = new ArrayList<>();
        for(int i : preferencesHourList)
        {
            hoursAlreadySelected[i]=true;
            hoursSelected.add(i);
        }
        dialogBuilder.setTitle(R.string.dialog_preference_hours_title);
        dialogBuilder.setIcon(icon);
        dialogBuilder.setMultiChoiceItems(R.array.hours_of_day, hoursAlreadySelected, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public  void onClick(DialogInterface dialog, int which, boolean isChecked)
            {
                if (isChecked)
                {
                    hoursSelected.add(which);
                } else if (hoursSelected.contains(which))
                {
                    hoursSelected.remove(Integer.valueOf(which));
                }
            }
        });
        dialogBuilder.setPositiveButton(R.string.save_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                user.setPreferenceHoursList(hoursSelected);
                userProfilePresenter.editPreferenceHours(currentUser.getUid(),hoursSelected);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finishActivityTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public LinearLayout getTextViewIcon(String socialKey, String text)
    {
        LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.text_view_icon,null);
        ((ImageView)linearLayout.findViewById(R.id.icon)).setImageDrawable(BikeMeApplication.getInstance().getDrawable("ic_"+socialKey));
        ((TextView)linearLayout.findViewById(R.id.text)).setText(text);

        return linearLayout;
    }
}