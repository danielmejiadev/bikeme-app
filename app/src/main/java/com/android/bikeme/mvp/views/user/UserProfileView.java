package com.android.bikeme.mvp.views.user;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 19 ago 2017.
 */
public interface UserProfileView {

    void setAboutMe(String aboutMe);
    void setSocialNetworks(HashMap<String,String> socialNetworks);
    void setPreferenceDays(ArrayList<Integer> preferenceDays);
    void setPreferenceHours(ArrayList<Integer> preferenceHours);

    void showEditAboutMe(String aboutMe);
    void showEditSocialNetworks(HashMap<String,String> socialNetworks);
    void showEditPreferenceDays(ArrayList<Integer> preferenceDays);
    void showEditPreferenceHours(ArrayList<Integer> preferenceHours);
}
