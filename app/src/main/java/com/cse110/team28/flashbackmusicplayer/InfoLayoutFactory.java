package com.cse110.team28.flashbackmusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDateTime;

/**
 * Created by studying on 3/9/18.
 */

public class InfoLayoutFactory implements OnMapReadyCallback{

    public static Track curTrack;

    public static void buildInfo(Context context, Track track){

        curTrack = track;
        InfoLayoutFactory mapFrag = new InfoLayoutFactory();

        AppCompatActivity activity = (AppCompatActivity)context;
        System.out.println("Info -- \t" + track.getTitle());
        final LinearLayout ll = activity.findViewById(R.id.info_window);
        ll.setVisibility(View.VISIBLE);
        final TextView title = activity.findViewById(R.id.title);
        title.setText("Title:\n" + track.getTitle());
        final TextView artist = activity.findViewById(R.id.artist);
        artist.setText("Artist:\n" + track.getArtist());
        final TextView album = activity.findViewById(R.id.album);
        album.setText("Album:\n" + track.getAlbum());
        ImageView iv = activity.findViewById(R.id.cover_art);

        // retrivew picture representing the track

        // get log of locationServices and time
        Pair<Location, LocalDateTime> arr = track.getLog();
        TextView comment = activity.findViewById(R.id.comment);
        comment.setText("");

        User bestUser = getRecentUser(track);
        if (bestUser != null){
            Log.d("Username", "Username is " + bestUser);
            String name = bestUser.getName();
            if (!User.friends.containsKey(bestUser.getID())){
                name = "#" +name.hashCode() % 10000;
            }
            comment.setText(comment.getText() + "User " + name + " listened to this song.");
        }

        if (arr != null && track.getIsLocal()) {
            Pair<Location, LocalDateTime> recent = arr;
            comment.setText(comment.getText() + "\nYou listened to this track at " +  recent.second.toString());
            Location curTrackLoc = recent.first;
            if (curTrackLoc != null){
                // shows users the map with locationServices
                MapFragment frag = (MapFragment)(activity.getFragmentManager().findFragmentById(R.id.frag));
                frag.getMapAsync(mapFrag);
                LinearLayout wrapper = activity.findViewById(R.id.wrapper);
                wrapper.setVisibility(View.VISIBLE);
            }
        }
        else {
            // song has not been listened to before, so information cannot be displayed
            comment.setText(comment.getText() + "\nYou haven't listened to this track before.");
            LinearLayout wrapper = activity.findViewById(R.id.wrapper);
            wrapper.setVisibility(View.GONE);
        }

        // decode compressed resources so that they can be used
        Bitmap toUse;
        if (!track.getIsLocal()){
            toUse = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_cover);
        }
        else{
            byte[] raw = track.getMd().getEmbeddedPicture();
            toUse = BitmapFactory.decodeByteArray(raw, 0, raw.length);
        }
        iv.setImageBitmap(toUse);
    }

    public void onMapReady(GoogleMap googleMap){
        if (curTrack != null){
            googleMap.clear();
            Location curLoc = curTrack.getCurrLocation();
            // retrieve current location
            float lat = (float)curLoc.getLatitude();
            float lon = (float)curLoc.getLongitude();
            LatLng ll = new LatLng(lat, lon);

            // adds a marker to the current locationServices and moves camera to it
            googleMap.addMarker(new MarkerOptions().position(ll));
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.
                    newCameraPosition(CameraPosition.fromLatLngZoom(ll, 10)));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            Log.i(InfoLayoutFactory.class.getSimpleName(),"Latitude is " + lat + "." + "Longitude is " + lon +".");
        }
    }

    public static User getRecentUser(Track track){
        if (DBService.curUser != null){
            User bestUser=null;
            LocalDateTime bestTime = null;
            for (int key : User.allUsers.keySet()){
                User cur = User.allUsers.get(key);

                for (Track trackKey : cur.getUserSongs().keySet()) {
                    Pair<LocalDateTime, Location> curPair = cur.getUserSongs().get(trackKey);
                    if (curPair != null) {
                        LocalDateTime curTime = curPair.first;
                        System.out.println(curTime);
                        if (curTime != null) {
                            if (bestTime != null) {
                                if (!bestTime.isAfter(curTime)) {
                                    bestTime = curTime;
                                    bestUser = cur;
                                }
                            } else {
                                bestUser = cur;
                                bestTime = curTime;
                            }
                        }
                    }
                }
            }
            return bestUser;
        }
        return null;
    }
}
