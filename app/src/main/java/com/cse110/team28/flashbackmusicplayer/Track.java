package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.util.Pair;

import java.time.LocalDateTime;

/**
 * Created by studying on 2/9/18.
 */
public class Track implements Comparable<Track> {

    // used to retrieve metadata
    private MediaMetadataRetriever md;

    // media string value
    private String media;

    // album in which the song is in
    private Album parent;

    // stores the locationServices and recent play time
    private Pair<Location, LocalDateTime> log;
    private int favorite = 0;
    private int weight = 0;
    private boolean isLocal = true;
    private boolean hasURL = false;
    private String album = "Unknown";
    private String artist = "Unknown";
    private String title = "Unknown";
    private String url;


    protected static String PROP_FAV = "fav";
    protected static String PROP_LOG = "log";

    public static int FAVORITE = 1;
    public static int NEUTRAL = 0;
    public static int UNFAVORITE = -1;

    public Track(){
    }

    public void setParent(Album parent) { this.parent = parent; }

    public void setWeight(int weight){ this.weight = weight; }

    public Album getParent(){ return parent; }

    public void setMd(MediaMetadataRetriever md){ this.md = md; }

    public void setFavorite(int favorite){
        this.favorite = favorite;
    }

    public boolean getIsLocal(){return isLocal;}

    public void setIsLocal(Boolean src){isLocal = src;}

    public MediaMetadataRetriever getMd(){return md;}

    public int getFavorite(){
        return favorite;
    }

    public void setUrl(String url){
        if (url != null){
            this.url = url;
            this.hasURL = true;
        }
    }

    public String getUrl(){return url;}

    public boolean hasUrl(){return hasURL;}


    public void resetWeight() { this.weight = 0; }
    public void incrementWeight() { this.weight++; }
    public int getWeight() { return this.weight; }

    public void setLog(Pair<Location, LocalDateTime> log) {
        this.log = log;
    }

    public void setLog(Location loc, LocalDateTime time) {
        log = new Pair<>(loc, time);
    }

    public Pair<Location, LocalDateTime> getLog(){return log;}

    public void setMedia(String media){this.media = media;}

    // uses retriever to get title
    public String getTitle(){
        if (md != null){
            String toRet = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            return toRet ==  null?"Unknown":toRet;
        }
        return title;
    }

    public void setTitle(String title){this.title = title;}

    // uses retriever to get artist
    public String getArtist(){
        if (md != null){
            String toRet = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            return toRet ==  null?"Unknown":toRet;
        }
        return artist;
    }

    public void setArtist(String artist){this.artist = artist;}

    // uses retriever to get album
    public String getAlbum(){
        if (md != null){
            String toRet = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            return toRet ==  null?"Unknown":toRet;
        }
        return album;
    }

    public void setAlbum(String album){this.album = album;}

    public String getMedia(){return media;}

    public MediaMetadataRetriever getMMR(){return md;}

    // compareTo method that is overriden to compare tracks
    @Override
    public int compareTo(Track track) {
        if(track == null) { return 1; }
        return this.getTitle().compareTo(track.getTitle());
    }

    // equals method is overrided determine if two songs are equal
    @Override
    public boolean equals(Object obj){
        if (obj.getClass().equals(Track.class)){
            Track toComp = (Track)obj;
            return this.getTitle().equals(toComp.getTitle()) &&
                    this.getAlbum().equals(toComp.getAlbum());
        }
        return false;
    }

    public Location getCurrLocation() {
        if (log != null ){
            return log.first;
        }
        return null;
    }

    public LocalDateTime getCurrTime() {
        if (log != null ){
            return log.second;
        }
        return null;
    }

    public void setCurrLocation(Location loc){
        if (log != null){
            log = new Pair<>(loc, log.second);
        }
        else{
            log = new Pair<>(loc, null);
        }
    }

    public void setCurrTime(LocalDateTime time){
        if (log != null){
            log = new Pair<>(log.first, time);
        }
        else{
            log = new Pair<>(null, time);
        }
    }

    public void updateLog(){}

}
