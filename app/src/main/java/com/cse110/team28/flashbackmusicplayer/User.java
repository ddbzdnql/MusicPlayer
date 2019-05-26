package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;

import java.time.LocalDateTime;
import java.util.HashMap;
import android.util.Pair;

/**
 * Created by ankurgupta on 3/5/18.
 */

public class User {

    public static int IDCounter = 1000;
    //ID number to identify specific user
    private int ID;

    //name of specific user
    private String name;

    //list of all users, including the main user itself
    public static HashMap<Integer, User> allUsers = new HashMap<Integer, User>();

    //seperate map of all the friends of the user
    public static HashMap<Integer, User> friends = new HashMap<Integer, User>();

    //hashmap of all tracks the user has listened to, with its time played
    private HashMap<Track, Pair<LocalDateTime, Location> > songsPlayed = new HashMap<Track, Pair<LocalDateTime, Location>>();

    //whether to appear anonymous
    private boolean anonymous = false;

    //constructor for a user, add it to the map of all Users
    public User(boolean anon, String thename) {
        name = thename;
        ID = getIDCounter();
        User.incCounter();
        anonymous = anon;
        allUsers.put(ID, this);

    }

    //add a users friend/nonfriend to the map system
    public static void addUser(boolean friend, boolean anon, String thename) {
        User newUser = new User(anon, thename);
        if(friend) {
            friends.put(newUser.getID(), newUser);
        }
    }

    public static void addUser(boolean friend, User theuser){
        if (friend){
            friends.put(theuser.getID(), theuser);
        }
    }

    //once song is listened to, associate with it to the specific user
    public void logSong(Track track, LocalDateTime time, Location loc) {
        this.songsPlayed.put(track, new Pair(time, loc));
    }

    //get list of songs played by user
    public HashMap<Track, Pair<LocalDateTime, Location> > getUserSongs() {
        return this.songsPlayed;
    }

    //clear log of songs played for specific user
    public void clearLog() {
        songsPlayed = new HashMap<Track, Pair<LocalDateTime, Location>>();
    }

    //get ID of user
    public int getID() {
        return ID;
    }

    //set ID of user
    public void setID(int id) {
        ID = id;
    }

    //get name of user
    public String getName() {
        return name;
    }

    //set name of user
    public void setName(String name) {
        this.name = name;
    }

    //return user map
    public static HashMap<Integer, User> getAllUsers() {
        return allUsers;
    }

    //return friend map
    public static HashMap<Integer, User> getAllFriends() {
        return friends;
    }

    public static void clearMaps() {
        allUsers = new HashMap<Integer, User>();
        friends = new HashMap<Integer, User>();
        IDCounter = 1000;
    }
    //check if user is anonymous
    public boolean isAnonymous() {
        return anonymous;
    }

    //set anonymous boolean
    public void setAnonymous(boolean anon ) {
        anonymous = anon;
    }

    public static int getIDCounter() {
        return IDCounter;
    }

    public static void incCounter() {
        IDCounter++;
    }


    @Override
    public boolean equals(Object other) {
        return getName().equals(((User)other).getName());
    }
}
