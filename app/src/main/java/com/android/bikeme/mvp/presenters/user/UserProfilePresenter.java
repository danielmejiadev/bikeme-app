package com.android.bikeme.mvp.presenters.user;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 19 ago 2017.
 */
public interface UserProfilePresenter {

    void onClickEditAboutMe(String aboutMe);
    void onClickEditSocialNetworks(HashMap<String,String> socialNetworks);
    void onClickEditPreferenceDays(ArrayList<Integer> preferenceDays);
    void onClickEditPreferenceHours(ArrayList<Integer> preferenceHours);

    void editAboutMe(String userId, String aboutMe);
    void editSocialNetworks(String userId, HashMap<String,String> socialNetworks);
    void editPreferenceDays(String userId, ArrayList<Integer> preferenceDays);
    void editPreferenceHours(String userId,ArrayList<Integer> preferenceHours);
}
