package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by ankurgupta on 3/9/18.
 */

public class VibeMode {

    // LOOK AT VIBETRACK CLASS BEFORE LOOKING HERE, VIBEMODE USES VIBETRACK

    //list of vibetrack objects
    ArrayList<VibeTrack> tracks;

    //meters to be considered in range
    private static double DISTANCE_THRESHOLD = 304.8;

    //minHeap, (larger the weight of track, higher up on minHeap it is)
    private  PriorityQueue<VibeTrack> queue;

    private User mainUser;

    // constructor, in which the CURRENT user logged into their google account
    // gets a playlist based upon OTHER users, using the location and current date
    public VibeMode(User currUser) {

        mainUser = currUser;

        //set the weights for all vibetracks, and creates vibetracks
        //refreshVibeMode(currTime, currLocation);
    }

    //recreates the weights and priorityqueue for the vibetracks dependent upon the
    // location, date, and friend info
    public void refreshVibeMode(LocalDateTime currTime, Location currLocation) {

        //initalize list
        tracks = new ArrayList<VibeTrack>();

        //MAKE ALL VIBE TRACKS

        //go through all users except the user logged in
        for(Integer ID: User.getAllUsers().keySet()) {
            if(mainUser.getID() != ID) {
                //get the other user, and iterate through all the songs they listened to
                User otherUser = User.getAllUsers().get(ID);
                //for each song the other user listened to, create vibetrack using the
                //track listened to and when/where it was listened to
                for(Map.Entry<Track, Pair<LocalDateTime, Location> > currEntry : otherUser.getUserSongs().entrySet() ) {
                    VibeTrack currTrack = new VibeTrack(otherUser, currEntry.getValue(), currEntry.getKey());
                    Log.d("WEIGHT TIME" ,   " " + currEntry.getValue().first + " " + otherUser.getName());
                    tracks.add(currTrack);
                }
            }
        }

        queue = new PriorityQueue<>(100, new TrackComparator());

        //set weights for all tracks in queue by comparing tracks day, time, and locationServices to current day, time and locationServices
        //thus, weights range from 0 to 3

        //update weight if track from other user played within 1000 feet of current users location
        weightLocation(currLocation);

        //update weight if track from other user played within a week of today
        weightWeek(currTime);

        //update weight if track played by friend of current user
        weightFriend(User.getAllFriends());

        //add to queue
        for (VibeTrack track : tracks) {
            queue.add(track);
            Log.d("FINAL WEIGHT", track.getTrack().getTitle() + ":" + Arrays.toString(track.gettiebreaker()));
        }

//
    }



    //if the song was last played within 1000 feet of currLocation, increment weight

    public void weightLocation(Location currLocation) {
        if (currLocation != null){
            for(VibeTrack track: tracks) {
                //if played within 1000 feet
                if(track.getLocationPlayed() != null  && currLocation.distanceTo(track.getLocationPlayed()) <= DISTANCE_THRESHOLD) {
                    Log.d("LOCATION WEIGHT", track.getUser().getName() + ":" + track.getTrack().getTitle());
                    track.incWeight();
                    track.nearby();
                }
            }
        }
    }

    //if track listened to within last week, increment
    public void weightWeek(LocalDateTime currTime) {
        for (VibeTrack track : tracks) {
            //check diff is 7 days or less
            if(currTime != null && track.getDate() != null) {
                long days = ChronoUnit.DAYS.between(currTime, track.getDate());
                Log.d("DIFFERENCE" ,   " " + days + " " + track.getUser().getName());
                if (-7 <= days && days <= 7) {
                    Log.d("TIME WEIGHT", track.getUser().getName() + ":" + track.getTrack().getTitle());
                    track.incWeight();
                    track.lastweek();
                }
            }
        }
    }

    //if track was played by a friend, increment weight
    public void weightFriend(HashMap<Integer, User> friends) {
        for(VibeTrack track : tracks) {
            //check if the user associated with vibe track is a friend
            if( friends.containsValue(track.getUser())) {
                Log.d("FRIEND WEIGHT", track.getUser().getName() + ":" + track.getTrack().getTitle());
                track.incWeight();
                track.friend();
            }
        }
    }
    //getter for queue
    public PriorityQueue<VibeTrack> getQueue() { return queue; }

    public ArrayList<VibeTrack> getTracks() {
        return tracks;
    }



    //comparator for queue, implemented as minHeap
    public class TrackComparator implements Comparator<VibeTrack>
    {
        @Override
        public int compare(VibeTrack x, VibeTrack y)
        {
            // 1. check weights
            if (x.getWeight() > y.getWeight())
            {
                return -1;
            }
            else if (x.getWeight() < y.getWeight())
            {
                return 1;
            }

            // 2. check if one is nearby and other is not
            else if (x.gettiebreaker()[0] > y.gettiebreaker()[0]) {
                return -1;
            }
            else if (x.gettiebreaker()[0] < y.gettiebreaker()[0]) {
                return 1;
            }

            // 3. Check  last week
            else if (x.gettiebreaker()[1] > y.gettiebreaker()[1]) {
                return -1;
            }
            else if (x.gettiebreaker()[1] < y.gettiebreaker()[1]) {
                return 1;
            }

            // 4. check if friend
            else if (x.gettiebreaker()[2] > y.gettiebreaker()[2]) {
                return -1;
            }
            else if (x.gettiebreaker()[2] < y.gettiebreaker()[2]) {
                return 1;
            }
            return 0;
        }
    }
}