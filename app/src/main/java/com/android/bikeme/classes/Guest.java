package com.android.bikeme.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 5 ago 2017.
 */
public class Guest implements Parcelable {

    public int id;
    public String user;
    public String event;
    public int state;
    public String date;

    public Guest()
    {

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.user);
        dest.writeString(this.event);
        dest.writeInt(this.state);
        dest.writeString(this.date);
    }

    protected Guest(Parcel in) {
        this.id = in.readInt();
        this.user = in.readString();
        this.event = in.readString();
        this.state = in.readInt();
        this.date = in.readString();
    }

    public static final Creator<Guest> CREATOR = new Creator<Guest>() {
        @Override
        public Guest createFromParcel(Parcel source) {
            return new Guest(source);
        }

        @Override
        public Guest[] newArray(int size) {
            return new Guest[size];
        }
    };
}
