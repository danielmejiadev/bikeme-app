package com.android.bikeme.mvp.views.events;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Event;
import com.android.bikeme.databaselocal.models.EventModel;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 10 ago 2017.
 */
public class EventAdapter extends SectionedRecyclerViewAdapter<EventAdapter.HeaderViewHolder, EventAdapter.EventViewHolder, RecyclerView.ViewHolder> {


    private ArrayList<ArrayList<HashMap<String, Object>>> eventsItems;
    private ArrayList<String> headersDate;
    private onEventClickListener onEventClickListener;

    public interface onEventClickListener
    {
        void onEventClick(HashMap<String, Object> eventToShow);
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

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView eventHour;
        private final TextView eventName;
        private final TextView eventGuest;
        private final ImageView eventGuestIcon;
        private final TextView eventStart;
        private final AppCompatButton buttonSeeMore;
        public int section;
        public int position;


        public EventViewHolder(View view)
        {
            super(view);
            eventHour = (TextView)view.findViewById(R.id.hour_event);
            eventName = (TextView) view.findViewById(R.id.name_event);
            eventGuest = (TextView) view.findViewById(R.id.guest_event_text);
            eventGuestIcon = (ImageView) view.findViewById(R.id.guest_icon);
            eventStart = (TextView) view.findViewById(R.id.start_event);
            buttonSeeMore = (AppCompatButton) view.findViewById(R.id.button_see_more);
            buttonSeeMore.setOnClickListener(this);
            Typeface type = Typeface.createFromAsset(BikeMeApplication.getInstance().getAssets(),"fonts/digital-7.ttf");
            eventHour.setTypeface(type);
        }

        @Override
        public void onClick(View view)
        {
            onEventClickListener.onEventClick(eventsItems.get(section).get(position));
        }
    }

    public EventAdapter()
    {
        this.eventsItems = new ArrayList<>();
        this.headersDate = new ArrayList<>();
    }

    @Override
    protected int getSectionCount()
    {
        return eventsItems.size();
    }

    @Override
    protected int getItemCountForSection(int section)
    {
        return eventsItems.get(section).size();
    }

    @Override
    public int getItemCount()
    {
        int itemCount = 0;
        for(int section = 0;section < getSectionCount();section++)
        {
            itemCount += getItemCountForSection(section);
        }
        return headersDate.size()+itemCount;//Si hubiese habria que sumar footers
    }


    @Override
    protected boolean hasFooterInSection(int section)
    {
        return false;
    }

    @Override
    protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sectioned_recycler_view_card_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    protected EventViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_me_fragment_events_card_body, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HeaderViewHolder headerViewHolder, int section)
    {
        headerViewHolder.sectionDate.setText(headersDate.get(section));
    }

    @Override
    protected void onBindItemViewHolder(EventViewHolder eventViewHolder, int section, int position)
    {
        HashMap<String, Object> eventToShow = eventsItems.get(section).get(position);
        eventViewHolder.eventName.setText(((Event)eventToShow.get(EventModel.EVENT_KEY)).getName());
        eventViewHolder.eventHour.setText(String.valueOf(eventToShow.get(EventModel.HOUR_KEY)));
        eventViewHolder.eventStart.setText(String.valueOf(eventToShow.get(EventModel.DEPARTURE_KEY)));
        eventViewHolder.section = section;
        eventViewHolder.position = position;
        if((int)eventToShow.get(EventModel.GUEST_KEY) == 0)
        {
            eventViewHolder.eventGuest.setVisibility(View.GONE);
            eventViewHolder.eventGuestIcon.setVisibility(View.GONE);
        }
        else
        {
            eventViewHolder.eventGuest.setVisibility(View.VISIBLE);
            eventViewHolder.eventGuestIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section)
    {
    }

    public void addEvents(ArrayList<String> dates, ArrayList<ArrayList<HashMap<String, Object>>> events)
    {
        this.eventsItems.clear();
        this.headersDate.clear();

        if (events != null && dates != null)
        {
            this.eventsItems.addAll(events);
            this.headersDate.addAll(dates);
        }
        notifyDataSetChanged();
    }

    public void setOnEventClickListener(onEventClickListener listener)
    {
        this.onEventClickListener = listener;
    }
}