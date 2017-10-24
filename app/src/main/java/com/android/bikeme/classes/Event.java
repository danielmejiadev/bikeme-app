package com.android.bikeme.classes;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class Event implements Parcelable {

    public String uid;
    public String name;
    public String route;
    public String date;
    public ArrayList<Guest> guests;

    public Event()
    {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public ArrayList<Guest> getGuests() {
        return guests;
    }

    public void setGuests(ArrayList<Guest> guests) {
        this.guests = guests;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.route);
        dest.writeString(this.date);
        dest.writeList(this.guests);
    }

    protected Event(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.route = in.readString();
        this.date = in.readString();
        this.guests = new ArrayList<Guest>();
        in.readList(this.guests, Guest.class.getClassLoader());
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
