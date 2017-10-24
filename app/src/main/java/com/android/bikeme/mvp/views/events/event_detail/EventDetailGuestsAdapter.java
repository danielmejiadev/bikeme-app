package com.android.bikeme.mvp.views.events.event_detail;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daniel on 13 ago 2017.
 */
public class EventDetailGuestsAdapter extends RecyclerView.Adapter<EventDetailGuestsAdapter.GuestViewHolder>{

    private ArrayList<Guest> guestToShow;
    private ArrayList<User> usersToShow;
    private Context context;
    private onGuestClickListener onGuestClickListener;

    public interface onGuestClickListener
    {
        void onGuestClick(User user);
    }

    public class GuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView guestName;
        public TextView actionGuest;
        public CircleImageView guestPhoto;

        public GuestViewHolder(View view)
        {
            super(view);
            guestName = (TextView) view.findViewById(R.id.event_guests_name);
            guestPhoto = (CircleImageView) view.findViewById(R.id.event_guest_photo);
            actionGuest = (TextView)view.findViewById(R.id.event_guests_action);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onGuestClickListener.onGuestClick(usersToShow.get(getAdapterPosition()));
        }

    }

    public EventDetailGuestsAdapter(Context context, ArrayList<Guest> guestToShow, ArrayList<User> usersToShow)
    {
        this.guestToShow = guestToShow;
        this.usersToShow = usersToShow;
        this.context = context;
    }

    public void setOnGuestListener(onGuestClickListener listener)
    {
        this.onGuestClickListener = listener;
    }

    @Override
    public int getItemCount()
    {
        return guestToShow.size();
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_detail_activity_guests_card, viewGroup, false);
        return new GuestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position)
    {
        holder.guestName.setText(usersToShow.get(position).getDisplayName());
        BikeMeApplication.getInstance().loadImage(Uri.parse(usersToShow.get(position).getPhoto()),holder.guestPhoto,context.getDrawable(R.drawable.default_avatar));
        String action = "";
        switch (guestToShow.get(position).getState())
        {
            case 0:
                action = context.getString(R.string.event_detail_maybe_go_text);
                break;
            case 1:
                action = context.getString(R.string.event_detail_go_text);
                break;
            case 2:
                action = context.getString(R.string.event_detail_not_go_text);
                break;
        }
        holder.actionGuest.setText(action);
    }
}
