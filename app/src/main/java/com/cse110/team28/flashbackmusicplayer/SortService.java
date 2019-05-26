package com.cse110.team28.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chengyou on 3/3/18.
 */

public class SortService {

    private Mode mode = Mode.TRACK;

    public SortService(Mode mode) {
        this.mode = mode;
    }

    public class TrackSorter implements Comparator<Track> {
        public int compare(Track track1, Track track2) {
            int result = 0;
            switch (mode) {
                case TRACK:
                    result = track1.getTitle().compareTo(track2.getTitle());
                    break;
                case ARTIST:
                    result = track1.getArtist().compareTo(track2.getArtist());
                    break;
                case FAVORITE:
                    if (track1.getFavorite() == track2.getFavorite()) {
                        result = 0;
                    }
                    else if (track1.getFavorite() < track2.getFavorite()) {
                        result = 1;
                    }
                    else {
                        result = -1;
                    }
            }
            return result;
        }
    }

    public class AlbumSorter implements Comparator<Album> {
        public int compare(Album album1, Album album2) {
            return album1.getName().compareTo(album2.getName());
        }
    }

    public static void sortTracks(ArrayList<Track> trackList, Mode mode) {
        SortService service = new SortService(mode);
        SortService.TrackSorter trackSorter = service.new TrackSorter();
        Collections.sort(trackList, trackSorter);
    }

    public static void sortAlbums(ArrayList<Album> albumList) {
        SortService service = new SortService(Mode.ALBUM);
        SortService.AlbumSorter albumSorter = service.new AlbumSorter();
        Collections.sort(albumList, albumSorter);
    }
}