package com.cse110.team28.flashbackmusicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by studying on 2/11/18.
 */

public class Library extends AppCompatActivity {
    protected static ArrayList<Album> lib;
    protected static ArrayList<Track> remoteSongLib = new ArrayList<>();
    private final static String ROOT = "raw";
    protected static ArrayList<Track> globalLib = null;

    // generate library
    public static void generateLibrary(Context context) {

        lib = new ArrayList<>();
        /*File rootDir = context.getFilesDir();
        File[] albums = rootDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (albums.length == 0){
            loadSamples(context, rootDir);
            albums = rootDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
        }
        for (File album : albums){
            File[] tracks = album.listFiles();
            if (tracks.length != 0){
                Album curAlbum = new Album(null, null);
                for (File track : tracks){
                    MediaMetadataRetriever md = new MediaMetadataRetriever();
                    md.setDataSource(track.getPath());
                    if (curAlbum.getName().equals("Unknown")){
                        String albumName = md.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_ALBUM);
                        curAlbum.setName(albumName);
                    }
                    if (curAlbum.getArtist().equals("Unknown")){
                        String albumArtist = md.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        curAlbum.setArtist(albumArtist);
                    }
                    Track curTrack = new Track();
                    curTrack.setMedia(track.getPath());
                    curTrack.setMd(md);
                    curTrack.setParent(curAlbum);
                    curAlbum.addTrack(curTrack);
                    retrieveLog(context, curTrack);
                }
                //if (curAlbum.getTrackList().size() != 0){
                    lib.add(curAlbum);
                //}
            }
        }*/
        File rootDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "vibe");
        loadSamples(context, rootDir);
        if (rootDir.isDirectory()){
            for (File file : rootDir.listFiles()){
                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(file.getPath());
                String title = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                if (title != null && artist != null && album != null){
                    System.out.println(title + ":" + artist + ":" + album);
                    Album curAlbum = new Album(album, artist);
                    if (lib.contains(curAlbum)){
                        curAlbum = lib.get(lib.indexOf(curAlbum));
                    }
                    else{
                        lib.add(curAlbum);
                    }
                    Track curTrack = new Track();
                    curTrack.setIsLocal(true);
                    curTrack.setParent(curAlbum);
                    curTrack.setMd(md);
                    curTrack.setMedia(file.getPath());
                    curAlbum.addTrack(curTrack);
                    retrieveLog(context, curTrack);
                }
            }
        }
        else {
            rootDir.mkdir();
        }
    }

    public static void retrieveLog(Context context, Track curTrack) {
        SharedPreferences sp = context.getSharedPreferences(curTrack.getAlbum() + "_" + curTrack.getTitle(), MODE_PRIVATE);
        if (sp.contains(Track.PROP_FAV)) {
            int fav = sp.getInt(Track.PROP_FAV, 0);
            curTrack.setFavorite(fav);
        }
        if (sp.contains(Track.PROP_LOG)) {
            String rawLog = sp.getString(Track.PROP_LOG, "");
            if (rawLog.equals("")){
                curTrack.setLog(null);
            }
            else {
                String[] fineLog = rawLog.split("@");
                double lat = Double.parseDouble(fineLog[0]);
                double lon = Double.parseDouble(fineLog[1]);

                Location trackLoc;
                LocalDateTime trackTS;

                if (lat == -200) {
                    trackLoc = null;
                }
                else {
                    trackLoc = new Location("");
                    trackLoc.setLatitude(lat);
                    trackLoc.setLongitude(lon);
                }

                if (fineLog[2].equals("null")) {
                    trackTS = null;
                }
                else{
                    trackTS = LocalDateTime.parse(fineLog[2]);
                }

                Pair<Location, LocalDateTime> log = new Pair<>(trackLoc, trackTS);
                curTrack.setLog(log);
            }
        }

        if (sp.contains("url")) {
            String url = sp.getString("url", null);
            curTrack.setUrl(url);
        }

    }

    // loads all the downloaded music
    //TODO: complete this method(formal params might need changing)
    public static void loadDownloads(Context contex, ContactsContract.Directory dir) {

    }
    public static void loadSamples(Context context, File rootDir){
        if (lib == null) {
            lib = new ArrayList<>();
        }
        Field[] fields = R.raw.class.getFields();
        ArrayList<String> mediaName = new ArrayList<>();
        ArrayList<Uri> mediaFile = new ArrayList<>();

        for (Field track : fields) {
            Uri trackUri = Uri.parse("android.resource://" + context.getPackageName() + "/" +
                    context.getResources().getIdentifier(track.getName(), "raw", context.getPackageName()));
            MediaMetadataRetriever md = new MediaMetadataRetriever();
            md.setDataSource(context, trackUri);
            String album = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (album != null && artist != null && title != null) {
                System.out.println(title + ":" + artist + ":" + album);
                Album curAlbum = new Album(album, artist);
                if (lib.contains(curAlbum)) {
                    curAlbum = lib.get(lib.indexOf(curAlbum));
                }
                else {
                    lib.add(curAlbum);
                }
                Track curTrack = new Track();
                curTrack.setIsLocal(true);
                curTrack.setParent(curAlbum);
                curTrack.setMd(md);
                curTrack.setMedia("android.resource://" + context.getPackageName() + "/" +
                        context.getResources().getIdentifier(track.getName(), "raw", context.getPackageName()));
                curAlbum.addTrack(curTrack);
                retrieveLog(context, curTrack);
            }
            /*
            final String albumName = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            File curAlbum;
            File[] thisAlbum = rootDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(albumName);
                }
            });
            if (thisAlbum.length != 0){
                curAlbum = thisAlbum[0];
            }
            else{
                curAlbum = new File(rootDir, albumName);
                curAlbum.mkdir();
            }
            try{
                InputStream fin = context.getResources().openRawResource(
                        context.getResources().getIdentifier(track.getName(), "raw", context.getPackageName()));
                FileOutputStream fout = new FileOutputStream(new File(curAlbum, track.getName()));
                byte[] all = new byte[fin.available()];
                fin.read(all);
                fout.write(all);
                fin.close();
                fout.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }*/
        }
    }

    public static void saveLog(Context context) {
        for (Album curA : lib) {
            for (Track curT : curA.getTrackList()) {
                save(context, curT);
            }
        }
    }


    public static void mock_file(Context context) {
        Random rand = new Random(LocalDateTime.now().toLocalTime().toSecondOfDay());
        Field[] fields = R.raw.class.getFields();
        for (Field cur : fields){
            double log = rand.nextDouble() * 360 - 180;
            double lat = rand.nextDouble() * 180 - 90;
            int seconds = rand.nextInt(86400);
            LocalDateTime time = LocalDateTime.now().minusSeconds(seconds);
            String toPut = lat + "@" + log + "@" + time.toString();
            SharedPreferences sp  = context.getSharedPreferences(cur.getName(), MODE_PRIVATE);
            SharedPreferences.Editor et = sp.edit();
            et.putString(Track.PROP_LOG, toPut);
            int fav = Math.abs(rand.nextInt() % 3);
            et.putInt(Track.PROP_FAV, fav);
            et.apply();
        }
    }

    public static ArrayList<Album> getAlbum() { return lib; }

    public static void save(Context context, Track track){
        SharedPreferences sp = context.getSharedPreferences(track.getAlbum() + "_" + track.getTitle(), MODE_PRIVATE);
        Log.d("Library", "Writing to " + track.getAlbum() + "_" + track.getTitle());
        SharedPreferences.Editor et = sp.edit();
        et.putInt(Track.PROP_FAV, track.getFavorite());
        Pair<Location, LocalDateTime> raw_log = track.getLog();
        String toAdd = "";
        if (raw_log != null && (raw_log.first != null || raw_log.second != null)){
            if (raw_log.first != null){
                toAdd += raw_log.first.getLatitude() + "@" + raw_log.first.getLongitude() + "@";
            }
            else{
                toAdd += "-200@-200@";
            }
            if (raw_log.second != null){
                toAdd += raw_log.second.toString();
            }
            else{
                toAdd += "null";
            }
            et.putString(Track.PROP_LOG, toAdd);
        }
        if (track.hasUrl()){
            et.putString("url", track.getUrl());
            System.out.println("url");
        }
        Log.d("Library", "Log is " + toAdd);
        et.apply();
    }

    public static void addDownload(Context context, Uri uri, boolean outside) {
        System.out.println(uri.toString());
        File trackfolder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "vibe");

        File track = trackfolder.listFiles()[trackfolder.listFiles().length-1];
        MediaMetadataRetriever md = new MediaMetadataRetriever();
        String title, album, artist;
        try{
            md.setDataSource(track.getPath());
            title = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            album = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (title==null || album==null|| artist==null){
                Toast.makeText(context, "Unable to read track info. Aborting.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        catch(Exception e){
            Toast.makeText(context, "Unable to read track info. Aborting.", Toast.LENGTH_LONG).show();
            return;
        }

        //Toast.makeText(context, title + " by " + artist + " downloaded.", Toast.LENGTH_LONG).show();

        File root = context.getFilesDir();
        File albumFolder;
        Album newAlbum = null;
        for (Album cur : lib){
            if (cur.getName().equals(album)){
                newAlbum = cur;
                albumFolder = new File(root, newAlbum.getName());
            }
        }
        if (newAlbum == null){
            newAlbum = new Album(album, artist);
        }
        Track newTrack = new Track();
        newTrack.setMd(md);
        newTrack.setParent(newAlbum);
        newTrack.setUrl(uri.toString());

        if (!MediaViewActivity.songs.contains(newTrack)){
            newAlbum.addTrack(newTrack);
            lib.add(newAlbum);
            albumFolder = new File(root, newAlbum.getName());
            albumFolder.mkdir();
            MediaViewActivity.songs.add(newTrack);
                newTrack.setMedia(track.getPath());
                newTrack.setIsLocal(true);
                System.out.println(newTrack.getMedia());
                if (!MediaViewActivity.onVibe)
                    ((MediaViewActivity)(MediaViewActivity.context)).setupTable(MediaViewActivity.songs);
                if (outside){
                    if (remoteSongLib == null || !remoteSongLib.contains(newTrack)){
                        System.out.println("Update into firebase");
                        DBService.updateTrack(newTrack);
                    }
                }
                else{
                    Track inLib = remoteSongLib.get(remoteSongLib.indexOf(newTrack));
                    inLib.setIsLocal(true);
                    inLib.setMedia(newTrack.getMedia());
                    inLib.setMd(newTrack.getMd());
                    if (inLib == CurrentlyPlayingActivity.toPlay){
                        ((CurrentlyPlayingActivity)(CurrentlyPlayingActivity.context)).loadCover();
                    }
                }

        }
        else{
            Toast.makeText(context, title + " already downloaded.", Toast.LENGTH_SHORT).show();
        }
        //track.delete();
    }
}