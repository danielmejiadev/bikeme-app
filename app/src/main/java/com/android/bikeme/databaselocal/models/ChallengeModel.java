package com.android.bikeme.databaselocal.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.User;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.mvp.views.challenges.ChallengeResponseLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Daniel on 8 sep 2017.
 */
public class ChallengeModel extends BikeMeModel {

    private ContentResolver contentResolver;
    private UserModel userModel;

    public ChallengeModel(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
        userModel = new UserModel(contentResolver);
    }

    public ChallengeResponseLoader getChallengesToShow(String currentUserId)
    {
        Uri uri = DataBaseContract.Challenge.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;

        ArrayList<ArrayList<Challenge>> challengesSections = new ArrayList<>();
        HashMap<Integer, ArrayList<Challenge>> challengesGroups = new LinkedHashMap<>();
        while (cursor.moveToNext())
        {
            Challenge challenge = getChallenge(cursor);
            int typeChallenge = challenge.getTypeChallenge();
            ArrayList<Challenge> group = challengesGroups.get(typeChallenge);
            if(group==null)
            {
                group = new ArrayList<>();
                group.add(challenge);
                challengesGroups.put(typeChallenge,group);
            }
            else
            {
                group.add(challenge);
            }
        }
        cursor.close();

        for(Map.Entry<Integer, ArrayList<Challenge>> entry : challengesGroups.entrySet())
        {
            challengesSections.add(entry.getValue());
        }

        ChallengeResponseLoader challengeResponseLoader = new ChallengeResponseLoader();
        challengeResponseLoader.setChallengesSections(challengesSections);
        challengeResponseLoader.setUser(userModel.getUserById(currentUserId));
        challengeResponseLoader.setUserChallengesParams(userModel.getUserChallengeParams(currentUserId));
        return challengeResponseLoader;
    }

    public ArrayList<Challenge> getChallenges()
    {
        ArrayList<Challenge> challenges = new ArrayList<>();
        Uri uri = DataBaseContract.Challenge.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext())
        {
            challenges.add(getChallenge(cursor));
        }
        cursor.close();
        return challenges;
    }

    public Challenge getChallenge(Cursor cursor)
    {
        String uid = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_UID));
        int typeChallenge = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Challenge.COLUMN_TYPE_CHALLENGE));
        int condition = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Challenge.COLUMN_CONDITION));
        int award = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Challenge.COLUMN_AWARD));

        Challenge challenge = new Challenge();
        challenge.setUid(uid);
        challenge.setTypeChallenge(typeChallenge);
        challenge.setCondition(condition);
        challenge.setAward(award);
        return challenge;
    }

    public ArrayList<Challenge> getChallengesAchieved(HashMap<Integer, Integer> userChallengesParams, User currentUser)
    {
        ArrayList<String> selectionArguments = currentUser.getAchievementsList();

        String selection = "";
        if(!selectionArguments.isEmpty())
        {
            String[] params = new String[selectionArguments.size()];
            Arrays.fill(params,"?");
            selection += DataBaseContract.COLUMN_UID + " NOT IN ("+TextUtils.join(",", params)+") AND ";
        }

        selection += " ( ";
        for(Map.Entry<Integer, Integer> userChallengeParam : userChallengesParams.entrySet())
        {
            int typeChallenge = userChallengeParam.getKey();
            int condition = userChallengeParam.getValue();
            if(typeChallenge==0)
            {
                selection += "(" +DataBaseContract.Challenge.COLUMN_TYPE_CHALLENGE+" = ?  AND " +
                        DataBaseContract.Challenge.COLUMN_CONDITION+" = ? ) ";
            }
            else
            {
                selection += "OR (" +DataBaseContract.Challenge.COLUMN_TYPE_CHALLENGE+" = ?  AND " +
                        DataBaseContract.Challenge.COLUMN_CONDITION+" <= ? ) ";
            }
            selectionArguments.add(String.valueOf(typeChallenge));
            selectionArguments.add(String.valueOf(condition));
        }
        selection+= " ) ";

        String[] selectionArgs = selectionArguments.toArray(new String[selectionArguments.size()]);

        ArrayList<Challenge> challengesAchieved = new ArrayList<>();
        ArrayList<String> currentUserAchievementsList = currentUser.getAchievementsList();
        Uri uri = DataBaseContract.Challenge.URI_CONTENT;
        Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
        assert cursor != null;
        while (cursor.moveToNext())
        {
            Challenge challenge = getChallenge(cursor);

            challengesAchieved.add(challenge);
            currentUserAchievementsList.add(challenge.getUid());
        }
        cursor.close();

        if(!challengesAchieved.isEmpty())
        {
            currentUser.setAchievementsList(currentUserAchievementsList);
            userModel.updateUser(currentUser.getUid(),currentUser.getAchievements(),UserModel.ACHIEVEMENTS_KEY);
            //This used for notify fragments challenge update values
            contentResolver.notifyChange(DataBaseContract.Challenge.URI_CONTENT, null, false);
        }
        return challengesAchieved;
    }

    public ContentProviderOperation insertOperationChallenge(Challenge challenge)
    {
        return ContentProviderOperation.newInsert(DataBaseContract.Challenge.URI_CONTENT)
                .withValue(DataBaseContract.COLUMN_UID, challenge.getUid())
                .withValue(DataBaseContract.Challenge.COLUMN_TYPE_CHALLENGE, challenge.getTypeChallenge())
                .withValue(DataBaseContract.Challenge.COLUMN_CONDITION, challenge.getCondition())
                .withValue(DataBaseContract.Challenge.COLUMN_AWARD, challenge.getAward())
                .build();
    }
}
