package com.android.bikeme.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Daniel on 18/03/2017.
 */
public class Rating implements Parcelable {

    public int id;
    public String user;
    public String route;
    public double recommendation;
    public double calification;
    public String date;

    public Rating()
    {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public double getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(double recommendation) {
        this.recommendation = recommendation;
    }

    public double getCalification() {
        return calification;
    }

    public void setCalification(double calification) {
        this.calification = calification;
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
        dest.writeString(this.route);
        dest.writeDouble(this.recommendation);
        dest.writeDouble(this.calification);
        dest.writeString(this.date);
    }

    protected Rating(Parcel in) {
        this.id = in.readInt();
        this.user = in.readString();
        this.route = in.readString();
        this.recommendation = in.readDouble();
        this.calification = in.readDouble();
        this.date = in.readString();
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
