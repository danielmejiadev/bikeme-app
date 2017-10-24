package com.android.bikeme.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Daniel on 18/03/2017.
 */
public class Route implements Parcelable {

    public static final String ROUTE_KEY = "route";
    public static final String POINTS_KEY = "points";
    public static final String DISTANCE_KEY = "distance";


    public String uid;
    public String creator;
    public String name;
    public String description;
    public int level;
    public String departure;
    public String arrival;
    public double distance;
    public String image;
    public String created;
    public ArrayList<Rating> ratings;
    public ArrayList<Point> points;

    public Route()
    {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public static double averageRatings(ArrayList<Rating> ratings)
    {
        double sum = 0;
        int number = 0;
        for(Rating rating: ratings)
        {
            if(rating.getRecommendation() == 0 && rating.getCalification() > 0)
            {
                sum += rating.getCalification();
                number +=1;
            }
        }
        return  ((double)Math.round((sum/number)*10)/10.0);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.uid);
        dest.writeString(this.creator);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeDouble(this.distance);
        dest.writeInt(this.level);
        dest.writeString(this.departure);
        dest.writeString(this.arrival);
        dest.writeByteArray(Base64.decode(this.image, Base64.DEFAULT));
        //dest.writeString(this.image);
        dest.writeString(this.created);
        dest.writeTypedList(this.ratings);
        dest.writeTypedList(this.points);
    }

    protected Route(Parcel in) {
        this.uid = in.readString();
        this.creator = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.distance = in.readDouble();
        this.level = in.readInt();
        this.departure = in.readString();
        this.arrival = in.readString();
        //this.image = in.readString();
        this.image = Base64.encodeToString(in.createByteArray(), Base64.DEFAULT);
        this.created = in.readString();
        this.ratings = in.createTypedArrayList(Rating.CREATOR);
        this.points = in.createTypedArrayList(Point.CREATOR);
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
