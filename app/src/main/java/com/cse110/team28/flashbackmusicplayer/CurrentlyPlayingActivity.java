package com.cse110.team28.flashbackmusicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.cse110.team28.flashbackmusicplayer.MediaViewActivity.intent;

public class CurrentlyPlayingActivity extends AppCompatActivity {
    // different song states
    private static String[] signs = {"cross", "plus", "checkmark"};

    // song that is playing
    protected static Track toPlay;
    protected static ArrayList<Track> playlist;

    // timer for the progress bar
    private static Timer t = new Timer();

    // media player manages the playing of the song
    protected static MediaPlayer mp = new MediaPlayer();
    protected static boolean prepared = false;

    // favorites
    protected static int[] favorite_array = {0,1,-1};

    //Metadata of the activity
    protected static Location startLoc;
    protected static LocalDateTime startTime;
    protected static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentlyplaying);
        context = this;

        //This activity should always be launched by an intent so process the intent to get the
        //mode and other necessary information for setup.
        initFromIntent();
        // create layout
        loadLayout();

        // load panel
        loadCtrlPanel();

        // volume control
        enableVolumeCtrl();

        // progress control
        enableProgressCtrl();

    }

    public static void endCurrent(){
        if (context != null){
            ((AppCompatActivity)context).finish();
        }
    }

    //The method that loads the three controlling button onto the screen.
    public void loadCtrlPanel(){

        // load the button views
        final ImageView play_btn = findViewById(R.id.play);
        final ImageView pause_btn = findViewById(R.id.pause);
        final ImageView next_btn = findViewById(R.id.next);
        final ImageView prev_btn = findViewById(R.id.prev);

        //Set the behaviour of media player after it finished playing a song.
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // once the song completes, store the important metadata
                Pair<Location, LocalDateTime> startMeta = new Pair<>(startLoc, startTime);
                toPlay.setLog(startMeta);
                Library.save(MediaViewActivity.context, toPlay);
                DBService.saveTrackRemote(toPlay);
                //DBService.updateTrack(toPlay);

                //The go to the next song. This can be accomplished by clicking the next button.
                next_btn.performClick();
            }
        });


        // start the song
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prepared = true;
                mp.start();
                startLoc = MediaViewActivity.locationService.getCurrentLocation();
                startTime = MediaViewActivity.localTimeInfo.getCurrentTime();
                System.out.println("played at: " + startLoc + " : " + startTime);
                //toPlay.setLog(MediaViewActivity.locationService.getCurrentLocation(), LocalDateTime.now());
            }
        });

        // start song if played
        play_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                play_btn.setVisibility(View.GONE);
                pause_btn.setVisibility(View.VISIBLE);
                mp.start();
            }
        });

        // pause if pause button is clicked
        pause_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                play_btn.setVisibility(View.VISIBLE);
                pause_btn.setVisibility(View.GONE);
                mp.pause();
            }
        });

        // get the previous song and play it
        prev_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mp.reset();
                prepared = false;
                int index = playlist.indexOf(toPlay);

                // get previous song in the list
                index = ((index - 1) + playlist.size()) % playlist.size();
                toPlay = playlist.get(index);
                try{
                    // get the song from the internal data
                    if (toPlay.getIsLocal()){
                        String path = toPlay.getMedia();
                        if (!path.contains("resource")){
                            mp.setDataSource(toPlay.getMedia());
                        }
                        else{
                            mp.setDataSource(context, Uri.parse(path));
                        }
                    }
                    else{
                        mp.setDataSource(toPlay.getUrl());
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                mp.prepareAsync();
                loadLayout();
            }
        });

        // get the next song and play it
        next_btn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.reset();
                prepared = false;
                int index = playlist.indexOf(toPlay);

                // get the next song and prepare to play it
                index = ((index + 1) + playlist.size()) % playlist.size();
                toPlay = playlist.get(index);
                try{
                    // get the song from the internal data
                    if (toPlay.getIsLocal()){
                        String path = toPlay.getMedia();
                        if (!path.contains("resource")){
                            mp.setDataSource(toPlay.getMedia());
                        }
                        else{
                            mp.setDataSource(context, Uri.parse(path));
                        }
                    }
                    else{
                        mp.setDataSource(toPlay.getUrl());
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                mp.prepareAsync();
                loadLayout();
            }
        });


        // leave the view if back button is pressed
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                goBack();
            }
        });


        // implement logic for favoriting and unfavoriting songs
        for (int i = 0; i < 3; i++){
            ImageView iv = findViewById(getResources().getIdentifier(signs[i], "id", getPackageName()));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the state of the song and set it to the next state
                    int fav = toPlay.getFavorite();
                    toPlay.setFavorite(favorite_array[fav+1]);

                    // add the state to preferences
                    Library.save(context, toPlay);
                    reflectFavorite();
                }
            });
        }
    }

    public void initFromIntent(){
        Intent intent = getIntent();
        int toUse = intent.getIntExtra("index", -1);
        boolean isFlashback = intent.getBooleanExtra("isFB", true);

        // track has been found
        if (toUse != -1){
            playlist = new ArrayList<>();
            int[] arr = intent.getIntArrayExtra("playlist");
            // get the song from the passed playlist
            if (arr == null){
                if (isFlashback){
                    toPlay = MediaViewActivity.fb_songs.get(toUse);
                }
                else{
                    toPlay = MediaViewActivity.songs.get(toUse);
                }
            }
            else{
                toPlay = null;
            }
            // determine if track and if vibemode
            boolean isTrack = intent.getBooleanExtra("isTrack", true);


            // create a list of sings depending on vibemode mode or not
            ArrayList<Track> rawList;
            if (isFlashback){
                    rawList = MediaViewActivity.fb_songs;

            }
            else{
                if (isTrack){
                    rawList = MediaViewActivity.songs;

                }
                else{
                    // not a track, so get album and find the song
                    ArrayList<Track> albumList = toPlay.getParent().getTrackList();
                    rawList = new ArrayList<>();
                    for (int i = 0; i < albumList.size(); i++){
                        if (albumList.get(i).equals(toPlay) || albumList.get(i).getFavorite() != -1){
                            rawList.add(albumList.get(i));
                        }
                    }
                }
            }

            ArrayList<Track> before = new ArrayList<>();
            ArrayList<Track> after = new ArrayList<>();

            boolean found = false;

            for (Track cur : rawList){
                if (cur == toPlay){
                    found = true;
                }
                if (found){
                    after.add(cur);
                }
                else{
                    before.add(cur);
                }
            }

            // ad all songs after the track and before the track
            playlist.addAll(after);
            playlist.addAll(before);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void enableProgressCtrl() {
        final SeekBar song_progress = findViewById(R.id.progressBar);

        song_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(prepared){
                    mp.seekTo((long)((double)seekBar.getProgress() / seekBar.getMax() * mp.getDuration()), MediaPlayer.SEEK_CLOSEST_SYNC);
                }
            }
        });


    }
    @SuppressLint("ClickableViewAccessibility")
    public void enableVolumeCtrl(){

        // create progress bar and set volume control
        final float ratio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        final ImageView iv = findViewById(R.id.drag);
        final ProgressBar volume_bar = findViewById(R.id.volume_bar);
        final float[] init = {-1};
        final int[] initVol = {-1};
        float defaultVol = 0.5f;
        mp.setVolume(defaultVol, defaultVol);
        volume_bar.setProgress(50);
        System.out.println(iv.getLeft());
        iv.setX((100-13) * ratio);

        // set the volume control when user touches
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // get progress if user touches
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    init[0] = event.getX();
                    initVol[0] = volume_bar.getProgress();
                }

                // get the new position and set it on the bar
                float offset = event.getX() - init[0];
                if (Math.abs(offset) >= 1){
                    int curPos = (int)(iv.getX() + offset);
                    if (curPos <= 0){
                        curPos = 0;
                    }
                    if (curPos >= 200 * ratio){
                        curPos = (int)(200 * ratio);
                    }
                    iv.setX(curPos);
                    volume_bar.setProgress((int)(iv.getX() / ratio / 2));

                    // set volume
                    mp.setVolume(volume_bar.getProgress()/100f, volume_bar.getProgress()/100f);
                }
                //System.out.println(event.getX());
                return true;
            }
        });
    }

    public void loadLayout(){

        // get play and pause
        final ImageView play_btn = findViewById(R.id.play);
        final ImageView pause_btn = findViewById(R.id.pause);

        play_btn.setVisibility(View.GONE);
        pause_btn.setVisibility(View.VISIBLE);

        // get title and metadata, and set texts
        TextView title = findViewById(R.id.info_title);
        TextView meta = findViewById(R.id.info_meta);
        title.setText(toPlay.getTitle());
        meta.setText(toPlay.getArtist() + " - " + toPlay.getAlbum());


        // get the compressed picture and decode
        loadCover();

        // get the progress bar and set the progress to 0 initially
        final ProgressBar pb = findViewById(R.id.progressBar);
        pb.setProgress(0);
        pb.setBackgroundColor(Color.argb(0,250,250,250));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (prepared){
                    int cur = (int) (mp.getCurrentPosition() *
                            100 / mp.getDuration());
                    pb.setProgress(cur);
                }

            }
        };

        // set the timer to be 0
        t.cancel();
        t.purge();
        t = new Timer();
        t.scheduleAtFixedRate(task, 50, 1000);

        // reflect changes in favorites
        reflectFavorite();
    }

    public void loadCover(){
        Bitmap cover = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_close_icon);
        if (toPlay.getIsLocal()){
            byte[] raw = toPlay.getMd().getEmbeddedPicture();
            cover = BitmapFactory.decodeByteArray(raw, 0, raw.length);
        }
        //cover.setWidth(250);
        //cover.setHeight(250);
        ImageView iv_cover = findViewById(R.id.songPicture);
        iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prepared){
                    mp.seekTo(mp.getCurrentPosition() + 20000);
                }
            }
        });
        iv_cover.setImageBitmap(cover);
    }

    public void goBack(){
        finish();
    }

    public void reflectFavorite() {
        // when a user clicks the sign, reflect the change in the view
        int fav = toPlay.getFavorite();
        for (int i = 0; i < 3; i++){
            ImageView iv = findViewById(getResources().getIdentifier(signs[i], "id", getPackageName()));
            if (i == fav+1){
                iv.setVisibility(View.VISIBLE);
            }
            else{
                iv.setVisibility(View.GONE);
            }
        }
        //toPlay.setFavorite((fav + 1) % 3);
    }

    public static void play(Context context, Track track){

        // play the song and prpare intent
        MediaViewActivity.btn_current.setVisibility(View.VISIBLE);
        CurrentlyPlayingActivity.mp.reset();
        prepared = false;
        intent = new Intent(context, CurrentlyPlayingActivity.class);

        // pass values into the intent so it can be passed into the next activity
        int index;
        if (!MediaViewActivity.onVibe){
            index = MediaViewActivity.songs.indexOf(track);
        }
        else{
            index = MediaViewActivity.fb_songs.indexOf(track);
        }
        System.out.println(index);
        intent.putExtra("index", index);
        intent.putExtra("isTrack", MediaViewActivity.isTrack);
        intent.putExtra("isFB", MediaViewActivity.onVibe);
        context.startActivity(intent);

        //AssetFileDescriptor afd = context.getResources().openRawResourceFd(context.getResources().
        //       getIdentifier(track.getMedia(), "raw", context.getPackageName()));
    }

    public void onStart(){
        super.onStart();
        try {
            if (playlist != null && playlist.size() != 0){
                Track track = playlist.get(0);
                if (track.getIsLocal()){
                    String path = track.getMedia();
                    if (!path.contains("resource")){
                        CurrentlyPlayingActivity.mp.setDataSource(path);
                    }
                    else{
                        CurrentlyPlayingActivity.mp.setDataSource(this, Uri.parse(path));
                    }
                    CurrentlyPlayingActivity.mp.prepareAsync();
                    prepared = true;
                }
                else{

                    CurrentlyPlayingActivity.mp.setDataSource(track.getUrl());

                    prepared = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void resetPlaylist() {
        ArrayList<Track> currPlaylist = playlist;
        currPlaylist.add(0, toPlay);
        playlist = currPlaylist;
    }
}
