package com.android.bikeme.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 18/03/2017.
 */
public class User implements Parcelable {

    public static final String USER_KEY = "user";
    public static final String TOTAL_POINTS_KEY = "total_points" ;
    // 0 y 1 899*0.4 = 359 - 2349*0.6 = 1409 - 5369*0.7=3758  - 11159*0.8 = 8927
    public static final int[] TOTAL_POINTS_LEVEL = new int[]{350,1400,3500,9000};

    public String uid;
    public String displayName;
    public String email;
    public int level;
    public String photo;
    public String aboutMe;
    public String socialNetworks;
    public String preferenceDays;
    public String preferenceHours;
    public String achievements;
    public String updated;
    public String created;

    public User()
    {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(String socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public String getPreferenceDays() {
        return preferenceDays;
    }

    public void setPreferenceDays(String preferenceDays) {
        this.preferenceDays = preferenceDays;
    }

    public String getPreferenceHours() {
        return preferenceHours;
    }

    public void setPreferenceHours(String preferenceHours) {
        this.preferenceHours = preferenceHours;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public HashMap<String,String> getSocialNetworksMap()
    {
        TypeToken<HashMap<String,String>> token = new TypeToken<HashMap<String,String>>(){};
        Gson gson = new Gson();
        return  gson.fromJson(socialNetworks,token.getType());
    }

    public void setSocialNetworksMap(HashMap<String,String> socialNetworks)
    {
        Gson gson = new Gson();
        this.socialNetworks = gson.toJson(socialNetworks);
    }


    public ArrayList<Integer> getPreferenceDaysList()
    {
        TypeToken<ArrayList<Integer>> token = new TypeToken<ArrayList<Integer>>(){};
        Gson gson = new Gson();
        return  gson.fromJson(preferenceDays,token.getType());
    }

    public void setPreferenceDaysList(ArrayList<Integer> preferenceDays)
    {
        Gson gson = new Gson();
        this.preferenceDays = gson.toJson(preferenceDays);
    }


    public ArrayList<Integer> getPreferenceHoursList()
    {
        TypeToken<ArrayList<Integer>> token = new TypeToken<ArrayList<Integer>>(){};
        Gson gson = new Gson();
        return  gson.fromJson(preferenceHours,token.getType());
    }

    public void setPreferenceHoursList(ArrayList<Integer> preferenceHours) {
        Gson gson = new Gson();
        this.preferenceHours = gson.toJson(preferenceHours);
    }

    public ArrayList<String> getAchievementsList()
    {
        TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>(){};
        Gson gson = new Gson();
        return  gson.fromJson(achievements,token.getType());
    }

    public void setAchievementsList(ArrayList<String> achievements)
    {
        Gson gson = new Gson();
        this.achievements = gson.toJson(achievements);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.displayName);
        dest.writeString(this.email);
        dest.writeInt(this.level);
        dest.writeString(this.photo);
        dest.writeString(this.aboutMe);
        dest.writeString(this.socialNetworks);
        dest.writeString(this.preferenceDays);
        dest.writeString(this.preferenceHours);
        dest.writeString(this.achievements);
        dest.writeString(this.updated);
        dest.writeString(this.created);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.displayName = in.readString();
        this.email = in.readString();
        this.level = in.readInt();
        this.photo = in.readString();
        this.aboutMe = in.readString();
        this.socialNetworks = in.readString();
        this.preferenceDays = in.readString();
        this.preferenceHours = in.readString();
        this.achievements = in.readString();
        this.updated = in.readString();
        this.created = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static AlertDialog getLevelsAchieved(Context context, int levelAchieved)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.DialogThemeUserProfile);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_levels_achieved, null);

        TextView levelAchievedText = (TextView)dialogView.findViewById(R.id.level_achieved_text);
        levelAchievedText.setText(context.getResources().getStringArray(R.array.user_levels)[levelAchieved]);

        ImageView typeChallengeImage = (ImageView)dialogView.findViewById(R.id.level_achieved_image);
        String imageName =  String.format("level_%s",String.valueOf(levelAchieved));
        typeChallengeImage.setImageDrawable(BikeMeApplication.getInstance().getDrawable(imageName));

        TextView experiencePointsAchievedText = (TextView)dialogView.findViewById(R.id.level_achieved_experience_points_text);
        experiencePointsAchievedText.setText(String.valueOf(TOTAL_POINTS_LEVEL[levelAchieved-1]));

        dialogBuilder.setPositiveButton(R.string.continue_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        return dialogBuilder.create();
    }
}