package com.cse110.team28.flashbackmusicplayer;

import java.io.FileInputStream;

/**
 * Created by studying on 2/10/18.
 */

public class MetaData {

    // metadata fields
    private final static String TAG_ARTIST_3 = "TPE1";
    private final static String TAG_ALBUM_3 = "TALB";
    private final static String TAG_TITLE_3 = "TIT2";
    private final static String TAG_ARTIST_2 = "TP1";
    private final static String TAG_ALBUM_2 = "TAL";
    private final static String TAG_TITLE_2 = "TT2";
    private final static int    ENCODING_UNI = 1;
    private final static String SPECIAL_CHARSET = "Unicode";
    private final static int    PROP_NUM = 3;
    private final static int    HEADER_SIZE = 10;
    private final static int    TAG_OFFSET = 4;
    private final static int    TAG_OFFSET_2 = 3;
    private final static int    SIZE_OFFSET = 4;
    private final static int    SIZE_OFFSET_2 = 3;
    private final static int    VERSION_OFFSET = 3;
    private final static int    VERSION_3 = 3;
    private final static int    FRAME_HEADER_2 = 6;

    private String album;
    private String artist;
    private String title;

    public String getAlbum(){return album==null?"Unknown":album;}

    public String getTitle(){return title==null?"Unknown":title;}

    public String getArtist(){return artist==null?"Unknown":artist;}
}
