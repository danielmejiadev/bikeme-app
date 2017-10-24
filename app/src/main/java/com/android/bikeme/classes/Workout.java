package com.android.bikeme.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Daniel on 3 sep 2017.
 */
public class Workout implements Parcelable{

    public static final String NEW_WORKOUT_KEY = "new_workout";
    public static final String WORKOUT_KEY ="workout" ;

    private String uid;
    private String name;
    private String user;
    private int durationSeconds;
    private String beginDate;
    private String comment;
    private String routeLatLngList;
    private int totalDistanceMeters;
    private int averageSpeedKm;
    private int averageAltitudeMeters;
    private int typeRoute;

    public Workout()
    {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRouteLatLngList() {
        return routeLatLngList;
    }

    public ArrayList<LatLng> getRouteLatLngArrayList()
    {
        Gson gson = new Gson();
        TypeToken<ArrayList<LatLng>> token = new TypeToken<ArrayList<LatLng>>(){};

        return gson.fromJson(routeLatLngList,token.getType());
    }

    public void setRouteLatLngList(String routeLatLngList) {
        this.routeLatLngList = routeLatLngList;
    }

    public void setRouteLatLngArrayList(ArrayList<LatLng> routeLocations)
    {
        Gson gson = new Gson();
        this.routeLatLngList = gson.toJson(routeLocations);
    }

    public int getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(int totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public int getAverageSpeedKm() {
        return averageSpeedKm;
    }

    public void setAverageSpeedKm(int averageSpeedKm) {
        this.averageSpeedKm = averageSpeedKm;
    }

    public int getAverageAltitudeMeters() {
        return averageAltitudeMeters;
    }

    public void setAverageAltitudeMeters(int averageAltitudeMeters) {
        this.averageAltitudeMeters = averageAltitudeMeters;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getTypeRoute() {
        return typeRoute;
    }

    public void setTypeRoute(int typeRoute) {
        this.typeRoute = typeRoute;
    }


    public static double getDurationHours(int durationSeconds)
    {
        return (double)durationSeconds / 60 / 60;
    }


    public static String getDurationString(int durationSeconds)
    {
        int hours = durationSeconds / 60 / 60;
        int minutes = (durationSeconds / 60) % 60;
        int seconds = durationSeconds % 60;
        return  (hours > 0 ? hours + ":" : "") +
                (minutes > 0 ? String.format("%02d", minutes) + ":" : "") +
                (String.format("%02d", seconds));
    }

    public static final double getDistanceKm(int totalDistanceMeters)
    {
        return (double)totalDistanceMeters/1000;
    }

    public static final  String getDistanceKmString(int totalDistanceMeters)
    {
        return String.format("%.1f", getDistanceKm(totalDistanceMeters));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.user);
        dest.writeInt(this.durationSeconds);
        dest.writeString(this.beginDate);
        dest.writeString(this.comment);
        dest.writeString(this.routeLatLngList);
        dest.writeInt(this.totalDistanceMeters);
        dest.writeInt(this.averageSpeedKm);
        dest.writeInt(this.averageAltitudeMeters);
        dest.writeInt(this.typeRoute);
    }

    protected Workout(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.user = in.readString();
        this.durationSeconds = in.readInt();
        this.beginDate = in.readString();
        this.comment = in.readString();
        this.routeLatLngList = in.readString();
        this.totalDistanceMeters = in.readInt();
        this.averageSpeedKm = in.readInt();
        this.averageAltitudeMeters = in.readInt();
        this.typeRoute = in.readInt();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel source) {
            return new Workout(source);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };
}
