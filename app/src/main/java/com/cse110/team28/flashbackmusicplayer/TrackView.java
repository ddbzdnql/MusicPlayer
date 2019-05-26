package com.cse110.team28.flashbackmusicplayer;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

/**
 * Created by studying on 2/11/18.
 */

public class TrackView extends AppCompatTextView {
    public TrackView(Context context, Track track){
        super(context);
        final Track currTrack = track;
        setOnClickListener(new View.OnClickListener(){

            // implement onClick listener
            public void onClick(View v){
            // only play if not in vibemode mode the view is a track
            if(!MediaViewActivity.onVibe || !MediaViewActivity.isTrack) {
                if (MediaViewActivity.onVibe){
                    ((MediaViewActivity)v.getContext()).resetFromAlbum();

                }
                // begin the new activity
                Context c = v.getContext();
                CurrentlyPlayingActivity.play(c, currTrack);
            }
            }
        });
    }
}