package com.cse110.team28.flashbackmusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by studying on 2/11/18.
 */

public class AlbumView extends ViewGroup {
    private final static String TAG = "AlbumView";
    private Album album;

    public AlbumView(final Context context, Album album){
        super(context);
        int height = 0;
        this.album = album;

        // get parameters for the linear layout
        LinearLayout.LayoutParams child_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        child_params.setMargins(0,5,0,5);

        // set the title of the album
        TextView title = new TextView(context);
        title.setTextSize(21);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.LEFT);
        title.setText(album.getName());
        title.setLayoutParams(child_params);
        title.measure(0, 0);
        height += title.getMeasuredHeight() + 10;

        // set the artist of the album
        TextView artist = new TextView(context);
        artist.setTextSize(16);
        artist.setTextColor(Color.WHITE);
        artist.setGravity(Gravity.LEFT);
        artist.setText(album.getArtist());
        artist.setLayoutParams(child_params);
        artist.measure(0,0);
        height += artist.getMeasuredHeight() + 10;

        // add to view
        this.addView(title);

        // for each track in the list of tracks, add the track to the album view
        for (Track cur : album.getTrackList()){
            TextView track = new TrackView(context, cur);
            track.setTextSize(18);
            track.setTextColor(Color.WHITE);
            track.setGravity(Gravity.RIGHT);
            track.setText(cur.getTitle());
            track.setLayoutParams(child_params);
            track.measure(0, 0);
            height += track.getMeasuredHeight() + 10;
            this.addView(track);
        }

        // set the parameters for the entire linearlayout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(0,10,0,15);
        this.setLayoutParams(params);

        final Album curAlbum = album;

        // if clicked, play track
        setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (curAlbum.getTrackList().size() != 0){
                    for (int i = 0; i < curAlbum.getTrackList().size(); i++){
                        Track track = curAlbum.getTrackList().get(i);
                        TrackView toPlay = new TrackView(context, track);
                        if (track.getFavorite() != -1){
                            toPlay.performClick();
                            break;
                        }
                    }
                }
            }
        });
    }

    public void onLayout(boolean changed, int l, int t, int r, int b){
        int curTop = this.getPaddingTop();
        int curLeft = this.getPaddingLeft();

        Log.d(TAG, "AlbumView " + this + " layout: " + l + ", " + t + ", " + r + ", " + b + "\n");

        // set the margins of the child views
        int height = 0;
        for (int i = 0; i < getChildCount(); i++){
            View cur = getChildAt(i);
            try{
                ((TextView)cur).setWidth(r-l);
                cur.measure(0,0);
                height += cur.getMeasuredHeight();
                height += ((LinearLayout.LayoutParams)(cur.getLayoutParams())).topMargin;
                height += ((LinearLayout.LayoutParams)(cur.getLayoutParams())).bottomMargin;
            }
            catch(ClassCastException e){

            }
        }

        System.out.println(height);

        // set the linear layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(0,10,0,15);
        this.setLayoutParams(params);

        // set the dimensions
        for (int i = 0; i < getChildCount(); i++){
            View cur = getChildAt(i);
            cur.measure(0, 0);
            int curWidth = cur.getMeasuredWidth();
            int curHeight = cur.getMeasuredHeight();
            cur.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            curTop += curHeight;
        }
    }
}