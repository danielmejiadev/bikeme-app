package com.android.bikeme.application;

import com.android.bikeme.classes.Challenge;

import java.util.ArrayList;

/**
 * Created by Daniel on 23 sep 2017.
 */
public interface BaseActivityView {
    void showChallengesLevelsAchieved(ArrayList<Challenge> challengesAchieved, ArrayList<Integer> levelsAchieved);
}
