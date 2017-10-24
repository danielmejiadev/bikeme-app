package com.android.bikeme.mvp.views.challenges;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 12 sep 2017.
 */
public class ChallengeResponseLoader {

    private ArrayList<ArrayList<Challenge>> challengesSections;
    private User user;
    private HashMap<Integer,Integer> userChallengesParams;

    public ChallengeResponseLoader()
    {

    }

    public HashMap<Integer, Integer> getUserChallengesParams() {
        return userChallengesParams;
    }

    public void setUserChallengesParams(HashMap<Integer, Integer> userChallengesParams) {
        this.userChallengesParams = userChallengesParams;
    }

    public ArrayList<ArrayList<Challenge>> getChallengesSections() {
        return challengesSections;
    }

    public void setChallengesSections(ArrayList<ArrayList<Challenge>> challengesSections) {
        this.challengesSections = challengesSections;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}