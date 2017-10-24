package com.android.bikeme.mvp.presenters.user;

import android.content.ContentValues;

import com.android.bikeme.databaselocal.models.UserModel;
import com.android.bikeme.mvp.interactors.user.UserProfileInteractor;
import com.android.bikeme.mvp.views.user.UserProfileView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 19 ago 2017.
 */
public class UserProfilePresenterImpl  implements UserProfilePresenter{

    UserProfileView userProfileView;
    UserProfileInteractor userProfileInteractor;
    Gson gson;


    public UserProfilePresenterImpl(UserProfileView userProfileView, UserProfileInteractor userProfileInteractor)
    {
        this.userProfileView = userProfileView;
        this.userProfileInteractor = userProfileInteractor;
        gson = new Gson();
    }

    @Override
    public void onClickEditAboutMe(String aboutMe)
    {
        userProfileView.showEditAboutMe(aboutMe);
    }

    @Override
    public void onClickEditSocialNetworks(HashMap<String,String> socialNetworks)
    {
        userProfileView.showEditSocialNetworks(socialNetworks);
    }

    @Override
    public void onClickEditPreferenceDays(ArrayList<Integer> preferenceDays)
    {
        userProfileView.showEditPreferenceDays(preferenceDays);
    }

    @Override
    public void onClickEditPreferenceHours(ArrayList<Integer> preferenceHours)
    {
        userProfileView.showEditPreferenceHours(preferenceHours);
    }

    @Override
    public void editAboutMe(String userId, String aboutMe)
    {
        userProfileView.setAboutMe(aboutMe);
        userProfileInteractor.updateUser(userId,aboutMe, UserModel.ABOUT_ME_KEY);
    }

    @Override
    public void editSocialNetworks(String userId, HashMap<String, String> socialNetworks)
    {
        userProfileView.setSocialNetworks(socialNetworks);
        userProfileInteractor.updateUser(userId,gson.toJson(socialNetworks),UserModel.SOCIAL_NETWORKS_KEY);

    }

    @Override
    public void editPreferenceDays(String userId, ArrayList<Integer> preferenceDays)
    {
        userProfileView.setPreferenceDays(preferenceDays);
        userProfileInteractor.updateUser(userId,gson.toJson(preferenceDays), UserModel.PREFERENCE_DAYS_KEY);

    }

    @Override
    public void editPreferenceHours(String userId, ArrayList<Integer> preferenceHours)
    {
        userProfileView.setPreferenceHours(preferenceHours);
        userProfileInteractor.updateUser(userId,gson.toJson(preferenceHours), UserModel.PREFERENCE_HOURS_KEY);
    }
}