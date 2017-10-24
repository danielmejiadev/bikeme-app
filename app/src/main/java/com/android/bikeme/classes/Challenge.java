package com.android.bikeme.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;

/**
 * Created by Daniel on 8 sep 2017.
 */
public class Challenge implements Parcelable {

    public String uid;
    public int typeChallenge;
    public int condition;
    public int award;

    public Challenge()
    {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTypeChallenge() {
        return typeChallenge;
    }

    public void setTypeChallenge(int typeChallenge) {
        this.typeChallenge = typeChallenge;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getAward() {
        return award;
    }

    public void setAward(int award) {
        this.award = award;
    }

    public String getDescriptionText(Context context)
    {
        Resources resources = context.getResources();
        String[] typeRoutes = resources.getStringArray(R.array.type_routes);
        String[] descriptionsChallenges = resources.getStringArray(R.array.challenge_descriptions);
        String conditionText;
        if(typeChallenge==0)
        {
            conditionText =  "en " + typeRoutes[condition];
        }
        else
        {
            conditionText = String.valueOf(condition);
        }
        return String.format(descriptionsChallenges[typeChallenge],conditionText);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeInt(this.typeChallenge);
        dest.writeInt(this.condition);
        dest.writeInt(this.award);
    }

    protected Challenge(Parcel in) {
        this.uid = in.readString();
        this.typeChallenge = in.readInt();
        this.condition = in.readInt();
        this.award = in.readInt();
    }

    public static final Creator<Challenge> CREATOR = new Creator<Challenge>() {
        @Override
        public Challenge createFromParcel(Parcel source) {
            return new Challenge(source);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };

    public Drawable getTypeChallengeImage()
    {
        String imageName = "challenge_";
        switch (typeChallenge)
        {
            case 6:
                imageName+=5;
                break;
            case 7:
                imageName+=1;
                break;
            case 8:
                imageName+=1;
                break;
            default:
                imageName+=typeChallenge;
                break;
        }
        return  BikeMeApplication.getInstance().getDrawable(imageName);
    }

    public AlertDialog getDialogChallengeAchieved(Context context)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.DialogThemeUserProfile);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_challenges_achieved, null);
        ImageView typeChallengeImage = (ImageView)dialogView.findViewById(R.id.challenge_achieved_type_challenge_image);
        typeChallengeImage.setImageDrawable(getTypeChallengeImage());

        TextView experiencePointsAchievedText = (TextView)dialogView.findViewById(R.id.challenge_achieved_experience_points_text);
        experiencePointsAchievedText.setText(String.valueOf(award));

        TextView descriptionText = (TextView)dialogView.findViewById(R.id.challenge_achieved_description_text);
        descriptionText.setText(getDescriptionText(context));

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
