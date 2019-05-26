package com.cse110.team28.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by studying on 2/9/18.
 */

public class Album {
    private String name;
    private String artist;
    private ArrayList<Track> trackList;

    public Album(String name, String artist){
        this.name = (name==null?"Unknown":name);
        this.artist = (artist==null?"Unknown":artist);
        trackList = new ArrayList<>();
    }

    public void addTrack(Track track){
        trackList.add(track);
    }

    public String getName(){return name;}

    public String getArtist(){return artist;}

    public void setArtist(String artist) { this.artist = artist; }

    public void setName(String name){this.name=name;}

    public ArrayList<Track> getTrackList(){return trackList;}

    // determines equality of the album based on name and artist
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(Album.class)){
            return name.equals(((Album)obj).name) && artist.equals(((Album)obj).artist);
        }
        else{
            return false;
        }
    }
}
