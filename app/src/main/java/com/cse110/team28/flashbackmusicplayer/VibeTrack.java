package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;
import android.util.Pair;

import java.time.LocalDateTime;

/**
 * Created by ankurgupta on 3/9/18.
 */

//inner class pairing track info along with needed user info and tiebreaker rules
public class VibeTrack {
    //user listening to song
    private User user;

    //when and what track the user played
    private LocalDateTime time;
    private Location location;
    private Track track;

    //weight and tiebreakers used to make VibeMode playlist
    private int weight = 0;
    private int[] tiebreaker = {0,0,0};

    //constructor
    public VibeTrack(User theUser, Pair<LocalDateTime, Location> info, Track track) {
        user = theUser;
        time = info.first;
        location = info.second;
        this.track = track;
        weight = 0;
    }

    public void incWeight() {
        this.weight++;
    }

    public void resetWeight() {
        weight = 0;
        tiebreaker[0] = 0;
        tiebreaker[1] = 0;
        tiebreaker[2] = 0;
    }

    public int getWeight() {
        return weight;
    }

    //first tiebreaker
    public void nearby() {
        this.tiebreaker[0] = 1;
    }
    //second tiebreaker
    public void lastweek() {
        this.tiebreaker[1] = 1;
    }
    //third tiebreaker
    public void friend() {
        this.tiebreaker[2] = 1;
    }

    public int[] gettiebreaker() {
        return tiebreaker;
    }

    public Location getLocationPlayed() {
        return location;
    }

    public LocalDateTime getDate(){
        return time;
    }

    public User getUser() {
        return user;
    }

    public Track getTrack() {
        return track;
    }
}

