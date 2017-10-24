package com.android.bikeme.bikemeserverconnection;

import com.android.bikeme.classes.Challenge;
import com.android.bikeme.classes.Event;
import com.android.bikeme.classes.Guest;
import com.android.bikeme.classes.Point;
import com.android.bikeme.classes.Rating;
import com.android.bikeme.classes.Route;
import com.android.bikeme.classes.User;
import com.android.bikeme.classes.Workout;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Daniel on 19/03/2017.
 */
public class ServerResponse {

    public ArrayList<User> users;
    public ArrayList<Route> routes;
    public ArrayList<Rating> ratings;
    public ArrayList<Event> events;
    public ArrayList<Guest> guests;
    public ArrayList<Workout> workouts;
    public ArrayList<Challenge> challenges;
    public String serverDate;

    public ServerResponse(ArrayList<User> users, ArrayList<Route> routes, ArrayList<Rating> ratings, ArrayList<Event> events,
                          ArrayList<Guest> guests, ArrayList<Workout> workouts, ArrayList<Challenge> challenges, String serverDate)
    {
        this.users = users;
        this.workouts = workouts;

        this.routes = routes;
        this.ratings = ratings;

        this.events = events;
        this.guests = guests;

        this.challenges = challenges;
        this.serverDate = serverDate;
    }

    public ServerResponse() {}

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }


    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<Guest> getGuests()
    {
        return guests;
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

    public String getServerDate() {
        return serverDate;
    }


    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setGuests(ArrayList<Guest> guests) {
        this.guests = guests;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    public void setChallenges(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }
}