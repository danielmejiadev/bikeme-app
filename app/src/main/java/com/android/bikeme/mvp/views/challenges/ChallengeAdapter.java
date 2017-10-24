package com.android.bikeme.mvp.views.challenges;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bikeme.R;
import com.android.bikeme.classes.Challenge;
import com.google.gson.Gson;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 21/04/2017.
 */
public class ChallengeAdapter extends SectionedRecyclerViewAdapter<ChallengeAdapter.HeaderViewHolder, ChallengeAdapter.ChallengeViewHolder, RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ArrayList<Challenge>> challenges;
    private String[] namesChallenges;
    private ArrayList<String> currentUserAchievements;
    private HashMap<Integer,Integer> userChallengesParams;

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView sectionName;

        public HeaderViewHolder(View view)
        {
            super(view);
            sectionName = (TextView) view.findViewById(R.id.section_name);
        }
    }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder
    {
        public TextView descriptionChallenge;
        public TextView percentChallenge;
        public TextView experiencePointsChallenge;
        public ImageView completedChallenge;

        public ChallengeViewHolder(View view)
        {
            super(view);
            descriptionChallenge = (TextView)view.findViewById(R.id.challenge_fragment_card_description_text);
            experiencePointsChallenge = (TextView) view.findViewById(R.id.challenge_fragment_card_experience_points_text);
            percentChallenge = (TextView)view.findViewById(R.id.challenge_fragment_card_percent_text);
            completedChallenge = (ImageView)view.findViewById(R.id.challenge_fragment_card_completed_image);
        }
    }

    public ChallengeAdapter(Context context)
    {
        this.challenges =new ArrayList<>();
        this.namesChallenges = new String[0];
        this.context = context;

    }

    public void addChallenges(String[] namesChallenges, ArrayList<ArrayList<Challenge>> challenges,
                              ArrayList<String> currentUserAchievements, HashMap<Integer,Integer> userChallengesParams)
    {
        this.namesChallenges = namesChallenges;
        this.currentUserAchievements = currentUserAchievements;
        this.userChallengesParams = userChallengesParams;

        this.challenges.clear();
        if (challenges != null && !challenges.isEmpty())
        {
            this.challenges.addAll(challenges);
            notifyDataSetChanged();
        }
    }


    @Override
    protected int getSectionCount()
    {
        return challenges.size();
    }

    @Override
    protected int getItemCountForSection(int section)
    {
        return challenges.get(section).size();
    }

    @Override
    public int getItemCount()
    {
        int itemCount = 0;
        for(int section = 0;section < getSectionCount();section++)
        {
            itemCount += getItemCountForSection(section);
        }
        return namesChallenges.length + itemCount;//Si fuese necensario, habría que sumar footers
    }

    @Override
    protected boolean hasFooterInSection(int section)
    {
        return false;
    }

    @Override
    protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_me_fragment_challenges_card_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    protected ChallengeViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_me_fragment_challenges_card_body, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HeaderViewHolder headerViewHolder, int section)
    {
        headerViewHolder.sectionName.setText(namesChallenges[section]);
    }

    @Override
    protected void onBindItemViewHolder(ChallengeViewHolder challengeViewHolder, int section, int position)
    {
        Challenge challenge = challenges.get(section).get(position);
        challengeViewHolder.descriptionChallenge.setText(challenge.getDescriptionText(context));
        challengeViewHolder.experiencePointsChallenge.setText(String.valueOf(challenge.getAward()));
        if(currentUserAchievements.contains(challenge.getUid()))
        {
            challengeViewHolder.percentChallenge.setVisibility(View.GONE);
            challengeViewHolder.completedChallenge.setVisibility(View.VISIBLE);
        }
        else
        {
            challengeViewHolder.percentChallenge.setVisibility(View.VISIBLE);
            challengeViewHolder.completedChallenge.setVisibility(View.GONE);
            int typeChallenge = challenge.getTypeChallenge();
            if(typeChallenge>0)
            {
                int value = userChallengesParams.get(typeChallenge);
                int percent = (value*100)/challenge.getCondition();
                String percentText = context.getString(R.string.percent_formatted_text,percent);
                challengeViewHolder.percentChallenge.setText(percentText);
            }
        }
    }

    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section)
    {
    }
}