package com.cse110.team28.flashbackmusicplayer;

import android.Manifest;
import java.time.LocalDate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;


public class MediaViewActivity extends AppCompatActivity implements LocationServiceListener
        , ActivityCompat.OnRequestPermissionsResultCallback {
    // table layout
    TableLayout tableLayout;

    // object that manages vibemode related data
    protected static VibeMode vibemode;

    // the current url being entered for download
    protected String currentUrl;

    // booleans to determine state of app
    protected static boolean onVibe = false;
    protected static boolean isTrack = true;

    // stores songs for track mode
    protected static ArrayList<Track> songs;
    // stores songs for vibemode mode
    protected static ArrayList<Track> fb_songs;
    // stores songs for album mode
    protected static ArrayList<Album> albums;

    protected static final int SIGN_IN_ATTEMPT = 0;

    // Location services
    protected static LocationService locationService;

    // LocalDateTime service
    protected static LocalTimeInfo localTimeInfo;
    protected static boolean isLiveTime = true;

    // objects used for the activity
    protected static Intent intent;
    public static RelativeLayout trackLayout;
    public static Button btn_current;
    public static Context context;

    // permissions for locationServices access
    private final static String permissions = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String TAG = "MediaViewActivity";



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationService = new LocationService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Library.saveLog(this);
        Log.i(TAG, "Destroy Successful");
        SharedPreferences sp = getSharedPreferences("mode", MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean("isTrack", isTrack);
        et.apply();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaview);

        //Save the context for calling other methods.
        context = this;
        checkPermissions();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null){
            startActivityForResult(new Intent(this, SignInActivity.class), SIGN_IN_ATTEMPT);
        }
        else{
            Toast.makeText(this, "Couldn't login", Toast.LENGTH_LONG).show();
        }

        //Retrieve mode info from shared preferences.
        SharedPreferences sp = getSharedPreferences("mode", MODE_PRIVATE);
        isTrack = sp.getBoolean("isTrack", true);
        onVibe = false;

        // Initialize the locationServices update service so that other methods can access current locationServices.
        locationService = new LocationService(MediaViewActivity.this);

        // Initialize the localTimeInfo service to allow for custom time metadata settings
        localTimeInfo = new LocalTimeInfo(context);

        trackLayout = findViewById(R.id.trackLayout);

        //Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Set the prepare action for media player.
        /*CurrentlyPlayingActivity.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });*/

        // load all library data
        songs = new ArrayList<>();
        loadLibrary();
        DownloadService.setup(this);
        // setup UI
        setupUI();

        // hide the currently playing button until a song is played
        btn_current = findViewById(R.id.btn_currentSong);
        if (CurrentlyPlayingActivity.toPlay == null) {
            btn_current.setVisibility(View.GONE);
        }
        btn_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentlyPlayingActivity();
            }
        });

        //Set the action for the close button of the info window.
        ImageView close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout info_window = findViewById(R.id.info_window);
                info_window.setVisibility(View.GONE);
            }
        });

        Button downloadButton = (Button)findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadService.download(Uri.parse(currentUrl), true);
            }
        });

        Button mockTimeButton = (Button) findViewById(R.id.bttn_mockTime);
        mockTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.setting_dialog).setVisibility(View.GONE);

                localTimeInfo.setCustomTime();
            }
        });

        final EditText urlText = (EditText)findViewById(R.id.url);
        urlText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentUrl = charSequence.toString();
                Log.d("onTextChanged: ", currentUrl);

            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // show content
        setupTable(songs);
        /*if (!isTrack){
            ViewSwitcher switcher =  findViewById(R.id.viewSwitcher);
            switcher.setDisplayedChild(1);
        }*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (DBService.acct != null){
            Toast.makeText(this, "Hi! " + DBService.acct.getDisplayName() + "!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Couldn't login.", Toast.LENGTH_LONG).show();
        }

    }

    //When loading CurrentlyPlayingActivity from the button, set the index to -1.
    public void showCurrentlyPlayingActivity() {
        intent.putExtra("index", -1);
        startActivity(intent);

    }

    public void resetFromAlbum(){
        findViewById(R.id.changeSwitch).performClick();
        ViewSwitcher switcher = findViewById(R.id.viewSwitcher);
        switcher.setDisplayedChild(1);
        isTrack = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // creates the menu to traverse between album and track mode
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void switchListener(View v) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null){
            swapMode();

            // checks to see if there are available tracks for vibemode mode
            if (onVibe && fb_songs.size() != 0 && fb_songs.get(0).getWeight() != 0){
                CurrentlyPlayingActivity.play(context, fb_songs.get(0));
                ViewSwitcher switcher = findViewById(R.id.viewSwitcher);
                switcher.setDisplayedChild(0);
            }

            if (onVibe && (fb_songs.size() == 0 || fb_songs.get(0).getWeight() == 0) ){
                Toast toast = Toast.makeText(this, "No song to vibemode", Toast.LENGTH_LONG);
                toast.show();
                ((Checkable)v).setChecked(false);
                onVibe = false;
            }

            if (!onVibe){
                // switches to first view
                ViewSwitcher switcher = findViewById(R.id.viewSwitcher);
                if (isTrack){
                    switcher.setDisplayedChild(0);
                }
                else{
                    switcher.setDisplayedChild(1);
                }
            }
        }
        else{
            Toast.makeText(this, "No connection", Toast.LENGTH_LONG);
            ((Checkable)v).setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ViewSwitcher switcher =  findViewById(R.id.viewSwitcher);

        if (!onVibe){
            // checks options
            switch(item.getItemId()) {

                case R.id.songView:
                    // show track view again
                    SortService.sortTracks(songs, Mode.TRACK);
                    tableLayout.removeAllViewsInLayout();
                    setupTable(songs);
                    switcher.setDisplayedChild(0);
                    isTrack = true;
                    return true;

                case R.id.albumView:
                    // show album view
                    SortService.sortAlbums(albums);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.tbl_albumview);
                    linearLayout.removeAllViewsInLayout();
                    populateAlbumView();
                    switcher.setDisplayedChild(1);
                    isTrack = false;
                    return true;

                case R.id.artistView:
                    // sort tracks by artist
                    SortService.sortTracks(songs, Mode.ARTIST);
                    tableLayout.removeAllViewsInLayout();
                    setupTable(songs);
                    switcher.setDisplayedChild(0);
                    isTrack = true;
                    return true;

                case R.id.favoriteView:
                    // sort tracks by favorite
                    SortService.sortTracks(songs, Mode.FAVORITE);
                    tableLayout.removeAllViewsInLayout();
                    setupTable(songs);
                    switcher.setDisplayedChild(0);
                    isTrack = true;
                    return true;

                default:
                    return super.onOptionsItemSelected(item);

            }
        }
        else{
            Toast.makeText(this, "Can't change view in VibeMode.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void swapMode() {
        // remove views from layout and recreate them
        if(onVibe) {
            tableLayout.removeAllViewsInLayout();
        }
        else {
            tableLayout.removeAllViewsInLayout();
            // vibemode mode
            refreshVibe();
        }
        onVibe = !onVibe;
        // create table
        setupTable(onVibe ? fb_songs:songs);
    }

    private void setupUI() {
        // scroll view layout (wraps around table layout)
        Button btn_currentSong = findViewById(R.id.btn_currentSong);
        if(btn_currentSong.getVisibility() == View.VISIBLE) {
            ScrollView scroll_songtable = findViewById(R.id.scroll_songtable);
            RelativeLayout.LayoutParams scrollViewLayout = (RelativeLayout.LayoutParams) scroll_songtable.getLayoutParams();
            scroll_songtable.setLayoutParams(scrollViewLayout);
        }

        // table layout
        tableLayout = findViewById(R.id.tbl_songview);
        tableLayout.setPadding(20,0,20,0);
    }

    //Call the methods in Library class to load songs from local resources.
    public void loadLibrary() {
        Library.generateLibrary(this);
        albums = Library.getAlbum();
        // add songs from the albums into the track list
        for(Album alb : albums) {
            songs.addAll(alb.getTrackList());
        }

        // sort the container
        Collections.sort(songs);
    }

    @Override
    public void checkPermissions() {
        if (checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationServices", "Called");
            requestPermissions(new String[]{permissions, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (onVibe) {
            refreshVibe();

            CurrentlyPlayingActivity.resetPlaylist();

//            ArrayList<Track> newList = new ArrayList<>();
//            newList.add(CurrentlyPlayingActivity.toPlay);
//            newList.addAll(CurrentlyPlayingActivity.playlist);
//            CurrentlyPlayingActivity.playlist = newList;
        }
    }

    public void showSettingDialog(View v){
        LinearLayout ll = findViewById(R.id.setting_dialog);
        ll.setVisibility(View.VISIBLE);
    }

    public void hideSettingDialog(View v){
        LinearLayout ll = findViewById(R.id.setting_dialog);
        ll.setVisibility(View.GONE);
    }

    final private class SongRowButtonClickListener implements View.OnClickListener {
        private final Track track;

        SongRowButtonClickListener(Context context, Track track) {
            this.track = track;
        }

        //Populate the info window with info from the given track.
        @Override
        public void onClick(View view) {

            InfoLayoutFactory.buildInfo(context, track);
        }
    }

    //The method that gets called when the outside of the info window is clicked.
    final public void toInvisible(View v){
        final LinearLayout ll = findViewById(R.id.info_window);
        ll.setVisibility(View.GONE);
    }


    protected void refreshVibe() {
        // refresh vibemode object so it updates the weights and priority queue
        vibemode.refreshVibeMode(localTimeInfo.getCurrentTime(), locationService.getCurrentLocation());
        PriorityQueue<VibeTrack> trackQueue = vibemode.getQueue();

        // refresh GUI table
        fb_songs = new ArrayList<>();
        while(!trackQueue.isEmpty()) {
            VibeTrack currentSong = trackQueue.poll();
            Log.d(currentSong.getTrack().getTitle(), currentSong.getWeight()+"");
            Track toAdd = currentSong.getTrack();
            if (!fb_songs.contains(toAdd) ){
                if (songs.contains(toAdd)){
                    toAdd = songs.get(songs.indexOf(toAdd));
                    if (toAdd.getFavorite() != -1){
                        toAdd.setWeight(currentSong.getWeight());
                        fb_songs.add(toAdd);
                    }
                }
                else{
                    if (Library.remoteSongLib.contains(toAdd)){
                        toAdd = Library.remoteSongLib.get(Library.remoteSongLib.indexOf(toAdd));
                        DownloadService.download(Uri.parse(toAdd.getUrl()), false);
                        toAdd.setWeight(currentSong.getWeight());
                        fb_songs.add(toAdd);
                    }
                }
            }
        }
    }

    protected void setupTable(ArrayList<Track> songs) {
        tableLayout.removeAllViewsInLayout();
        // add table row for each song
        for (int i = 0; i < songs.size(); i++) {
            addTableRow(songs.get(i), i);
        }

        //dummy row behind currently playing button
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        //add blank textview
        TextView view_songTitle = new TextView(this);
        view_songTitle.setTextAppearance(R.style.SongRowTextStyle);
        row.addView(view_songTitle);
        tableLayout.addView(row, songs.size());

        //Populate album view
        populateAlbumView();
    }

    private void addTableRow(Track song, int songNum) {
        // create row
        TableRow row = new TableRow(this);
        row.setClickable(true);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        //  set the song title
        TrackView view_songTitle = new TrackView(this, song);
        view_songTitle.setTextAppearance(R.style.SongRowTextStyle);
        view_songTitle.setText(Html.fromHtml("<b>" + song.getTitle() + "</b>" +  "<br />" +
                "<small>" + song.getArtist() + "</small>" + "<br />"));
        view_songTitle.setTextColor(Color.WHITE);

        // create button to go to song information
        Button btn_songInfo = new Button(this);
        btn_songInfo.getBackground().setAlpha(250);
        btn_songInfo.setMaxWidth(10);
        btn_songInfo.setMaxHeight(10);
        btn_songInfo.setOnClickListener(new SongRowButtonClickListener(this, song));

        // add the row to the layout
        row.addView(view_songTitle);
        row.addView(btn_songInfo);
        tableLayout.addView(row,songNum);
    }

    private void populateAlbumView() {
        // go through the entire list of albums and add the view to a linear layout to present
        final LinearLayout album_list = findViewById(R.id.tbl_albumview);
        for (int i = 0; i < albums.size(); i++){
            Album cur = albums.get(i);
            AlbumView curView = new AlbumView(this, cur);
            album_list.addView(curView);
        }
    }
}