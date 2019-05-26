package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;
import android.provider.MediaStore;
import android.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.people.v1.model.Name;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by studying on 3/10/18.
 */

public class DBService {

    private static final String LOCATION_USERS = "users";
    private static final String LOCATION_TRACKS = "tracks";

    private static FirebaseDatabase mainDB;
    private static DatabaseReference userRef;
    private static DatabaseReference trackRef;
    protected static User curUser;
    private static DataSnapshot database;

    protected static GoogleSignInAccount acct = null;
    protected static List<Name> contacts;

    public static void provideAccount(GoogleSignInAccount gAcct){
        acct = gAcct;
        setup();
        buildUser();
        buildDatabase();
    }
    public static void provideContacts(List<Name> gContacts){
        contacts = gContacts;

        if (acct != null && contacts != null){

            buildUserWithFriends();
        }
    }

    public static void setup(){
        mainDB = FirebaseDatabase.getInstance();
        userRef = mainDB.getReference(LOCATION_USERS);
        trackRef = mainDB.getReference(LOCATION_TRACKS);
    }

    public static void buildUser(){
        if (acct != null){
            pullUser(acct.getDisplayName());
        }
    }

    public static void buildUserWithFriends(){
        if (acct != null && userRef != null){
            final List<Name> thecontacts = contacts;
            final DatabaseReference user = userRef;
            user.addValueEventListener(new ValueEventListener() {
                ValueEventListener listener;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listener = this;
                    for (Name name : thecontacts){
                        String queryName = name.getDisplayName();
                        if (dataSnapshot.hasChild(queryName)){
                            DatabaseReference curFriend = user.child(acct.getDisplayName()).child("friends").child(queryName);
                            curFriend.setValue("");
                            //pullTrack(queryName, track.getAlbum() + "_" +track.getTitle());
                        }
                    }
                    updateContacts();
                    user.removeEventListener(listener);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public static void buildDatabase(){
        mainDB.getReference().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                database = dataSnapshot;
                DataSnapshot tracks = database.child("tracks");
                // shoule be put into the library.
                for (DataSnapshot track : tracks.getChildren()){
                    String[] trackInfo = track.getKey().split("_");
                    Track toComp = new Track();
                    toComp.setAlbum(trackInfo[0]);
                    toComp.setTitle(trackInfo[1]);
                    toComp.setArtist(track.child("Artist").getValue(String.class));
                    if (!Library.remoteSongLib.contains(toComp)){
                        //if (!MediaViewActivity.songs.contains(toComp)){
                            toComp.setIsLocal(false);
                            Library.remoteSongLib.add(toComp);
                            System.out.println(toComp.getTitle() + " from " + toComp.getAlbum());
                        //}
                    }
                    else{
                        toComp = Library.remoteSongLib.get(Library.remoteSongLib.indexOf(toComp));
                    }
                    if (track.hasChild("Media")){
                        toComp.setUrl(track.child("Media").getValue(String.class));
                        System.out.println(toComp.getTitle() + ":" + toComp.getUrl());
                    }
                }
                /*for (Track track : MediaViewActivity.songs){
                    if (!Library.remoteSongLib.contains(track)){
                        System.out.println(track.getTitle());
                        updateTrack(track);
                    }
                }*/
                mainDB.getReference().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mainDB.getReference().child("tracks").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (Library.remoteSongLib != null){
                    System.out.println(dataSnapshot.getKey());
                    Track toAdd = new Track();
                    String[] trackInfo = dataSnapshot.getKey().split("_");
                    toAdd.setTitle(trackInfo[1]);
                    toAdd.setAlbum(trackInfo[0]);
                    toAdd.setUrl(dataSnapshot.child("Media").getValue(String.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void pullUser(String name){
        final DatabaseReference curName = userRef;
        final String thename = name;
        //final List<Name> thecontacts = contacts;
        curName.addValueEventListener(new ValueEventListener() {
            final ValueEventListener listener = this;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(thename)){
                    curName.child(thename).setValue("");
                }
                DatabaseReference newUser = curName.child(thename);
                DatabaseReference userFriend = newUser.child("friends");

                if (curUser == null){
                    curUser = new User(true, thename);
                    MediaViewActivity.vibemode = new VibeMode(curUser);
                    buildUserMap(dataSnapshot);
                }
                ArrayList<Track> tracks = MediaViewActivity.songs;
                for (Track track: tracks){
                    saveTrackRemote(track);
                }
                curName.removeEventListener(listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateContacts(){
        ArrayList<String> friendList = new ArrayList<>();
        if (contacts != null){
            for (Name name : contacts){
                friendList.add(name.getDisplayName());
            }
        }
        if(!friendList.isEmpty()) {
            System.out.println("User " + curUser.getName() + " has friends");
        }
        for (int key : curUser.allUsers.keySet()){
            User user = curUser.allUsers.get(key);
            if (friendList.contains(user.getName())){
                curUser.friends.put(user.getID(), user);
                System.out.println(user.getName());
            }
        }
    }

    public static void buildUserMap(DataSnapshot dataSnapshot){
        for (DataSnapshot user : dataSnapshot.getChildren()){
            if (!user.getKey().equals(acct.getDisplayName())){
                boolean anon;
                if (user.hasChild("anon")){
                    anon = user.child("anon").getValue(String.class).equals("true");
                }
                else{
                    anon = false;
                }
                final User friend = new User(anon, user.getKey());

                curUser.addUser(false, friend);
                DatabaseReference curFriend = userRef.child(user.getKey());
                curFriend.child("tracks").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friend.clearLog();
                        System.out.println(friend.getName());
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            String[] trackInfo = child.getKey().split("_");
                            Track toAdd = new Track();
                            toAdd.setAlbum(trackInfo[0]);
                            toAdd.setTitle(trackInfo[1]);
                            System.out.print(toAdd.getTitle());

                            String rawLog = child.getValue(String.class);
                            String[] fineLog = rawLog.split("@");
                            Location loc;
                            if (fineLog[0].equals("-200")){
                                loc = null;
                            }
                            else{
                                loc = new Location("");
                                Double lat = Double.parseDouble(fineLog[0]);
                                Double lon = Double.parseDouble(fineLog[1]);
                                loc.setLatitude(lat);
                                loc.setLongitude(lon);
                            }
                            System.out.print("\t\t" + loc);

                            LocalDateTime time;
                            if (fineLog.length <= 2 || fineLog[2].equals("null")){
                                time = null;
                            }
                            else{
                                time = LocalDateTime.parse(fineLog[2]);
                            }
                            System.out.println("\t\t" + time);

                            friend.logSong(toAdd, time, loc);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public static void saveTrackRemote(Track track){
        if (mainDB != null && userRef != null){
            if (track.getLog() != null){
                DatabaseReference userTrackRef = userRef.child(acct.getDisplayName()).child("tracks");
                Pair<Location, LocalDateTime> log = track.getLog();
                DatabaseReference curTrack = userTrackRef.
                        child(track.getAlbum() + "_" +track.getTitle());
                String toAdd = "";

                if (log.first != null){
                    toAdd += log.first.getLatitude() + "@" + log.first.getLongitude() + "@";
                }
                else{
                    toAdd += "-200@-200@";
                }

                if (log.second != null){
                    toAdd += log.second.toString();
                }
                else{
                    toAdd += "null";
                }
                curTrack.setValue(toAdd);
            }
        }
    }

    public static void updateTrack(Track track){
        if (mainDB != null && curUser != null && trackRef != null){

            DatabaseReference curTrackRef = trackRef.
                    child(track.getAlbum() + "_" + track.getTitle());
            curTrackRef.child("Artist").setValue(track.getArtist());
            if (track.hasUrl()){
                String url = track.getUrl();
                curTrackRef.child("Media").setValue(url);

            }

        }
    }

    public static void pullTrack(final String friendName, final String trackName){
        final DatabaseReference curName = userRef;
        final DatabaseReference user = userRef.child(friendName).child("tracks").child(trackName);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<String> getContacts() {
        return SignInActivity.getFriends();
    }

}
