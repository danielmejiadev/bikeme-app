package com.android.bikeme.mvp.views.workout.workout_history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.bikeme.R;
import com.android.bikeme.classes.Workout;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;


public class WorkoutHistoryRecyclerViewAdapter extends SectionedRecyclerViewAdapter<WorkoutHistoryRecyclerViewAdapter.HeaderViewHolder,WorkoutHistoryRecyclerViewAdapter.WorkoutViewHolder, RecyclerView.ViewHolder> {


    ArrayList<ArrayList<Workout>> workouts;
    ArrayList<String> headersDate;
    private OnWorkoutClickListener onWorkoutClickListener;
    String[] typeRoutes;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView sectionDate;

        HeaderViewHolder(View view)
        {
            super(view);

            sectionDate = (TextView) view.findViewById(R.id.section_name);
        }
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView workoutName;
        private final TextView workoutDuration;
        private final TextView workoutDistance;
        private final TextView workoutTypeRoute;
        Workout workout;


        public WorkoutViewHolder(View view)
        {
            super(view);
            workoutName = (TextView) view.findViewById(R.id.workout_name_history_card_body);
            workoutDuration = (TextView) view.findViewById(R.id.workout_duration_history_card_body);
            workoutDistance = (TextView) view.findViewById(R.id.workout_distance_history_card_body);
            workoutTypeRoute = (TextView) view.findViewById(R.id.workout_type_route_history_card_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onWorkoutClickListener.onWorkoutClick(workout);
        }
    }

    public WorkoutHistoryRecyclerViewAdapter(String[] typeRoutes)
    {
        this.workouts = new ArrayList<>();
        this.headersDate = new ArrayList<>();
        this.typeRoutes = typeRoutes;
    }

    @Override
    protected int getSectionCount() {
        return workouts.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return workouts.get(section).size();
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        for (int section = 0; section < getSectionCount(); section++) {
            itemCount += getItemCountForSection(section);
        }
        return headersDate.size() + itemCount;//Si hubiese habria que sumar footers
    }


    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sectioned_recycler_view_card_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    protected WorkoutViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_history_card_body, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HeaderViewHolder headerViewHolder, int section) {
        headerViewHolder.sectionDate.setText(headersDate.get(section));
    }

    @Override
    protected void onBindItemViewHolder(WorkoutViewHolder workoutViewHolder, int section, int position)
    {
        Workout workout = workouts.get(section).get(position);
        workoutViewHolder.workoutName.setText(workout.getName());
        workoutViewHolder.workoutDuration.setText(Workout.getDurationString(workout.getDurationSeconds()));
        workoutViewHolder.workoutDistance.setText(Workout.getDistanceKmString(workout.getTotalDistanceMeters()));
        workoutViewHolder.workoutTypeRoute.setText(typeRoutes[workout.getTypeRoute()]);
        workoutViewHolder.workout = workout;
    }

    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section) {
    }

    public void addWorkouts(ArrayList<String> dates, ArrayList<ArrayList<Workout>> workouts)
    {
        this.workouts.clear();
        this.headersDate.clear();

        if (workouts != null && dates != null)
        {
            this.workouts.addAll(workouts);
            this.headersDate.addAll(dates);
        }
        notifyDataSetChanged();
    }

    public void setOnWorkoutClickListener(OnWorkoutClickListener listener)
    {
        this.onWorkoutClickListener = listener;
    }
}
