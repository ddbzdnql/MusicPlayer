package JUnitTests;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.cse110.team28.flashbackmusicplayer.Album;
import com.cse110.team28.flashbackmusicplayer.Mode;
import com.cse110.team28.flashbackmusicplayer.SortService;
import com.cse110.team28.flashbackmusicplayer.Track;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Youyuan Lu on 3/4/2018.
 */


public class TestSortService {

    public class MockTrack extends Track {
        private String title;
        private String artist;
        public void setTitle(String newTitle) {
            this.title = newTitle;
        }
        public void setArtist(String newArtist) {
            this.artist = newArtist;
        }
        public String getTitle() {
            return this.title;
        }
        public String getArtist() {
            return this.artist;
        }
    }

    public class MockAlbum extends Album {
        MockAlbum(String album) {
            super(album, null);
        }
    }

    @Test
    public void testSortTracks() {
        int size = 40;
        TestSortService tester = new TestSortService();
        ArrayList<Track> trackList = new ArrayList<Track>();
        for (int i = 0; i < size; i++) {
            TestSortService.MockTrack mockTrack = tester.new MockTrack();
            mockTrack.setTitle(i + "t" + (size - i));
            mockTrack.setArtist((size - i) + "a" + i);
            mockTrack.setFavorite((i % 3) - 1);
            trackList.add(mockTrack);
        }

        SortService.sortTracks(trackList, Mode.TRACK);
        for (int i = 1; i < size; i ++) {
            String title_curr = trackList.get(i).getTitle();
            String title_prev = trackList.get(i - 1).getTitle();
            assertEquals(true, title_curr.compareTo(title_prev) >= 0);
        }

        SortService.sortTracks(trackList, Mode.ARTIST);
        for (int i = 1; i < size; i ++) {
            String artist_curr = trackList.get(i).getArtist();
            String artist_prev = trackList.get(i - 1).getArtist();
            assertEquals(true, artist_curr.compareTo(artist_prev) >= 0);
        }

        SortService.sortTracks(trackList, Mode.FAVORITE);
        for (int i = 1; i < size; i ++) {
            int favorite_curr = trackList.get(i).getFavorite();
            int favorite_prev = trackList.get(i - 1).getFavorite();
            assertEquals(true, favorite_curr <= favorite_prev);
        }
    }

    @Test
    public void testSortAlbums() {
        int size = 40;
        TestSortService tester = new TestSortService();
        ArrayList<Album> albumList = new ArrayList<Album>();
        for (int i = 0; i < size; i++) {
            TestSortService.MockAlbum mockAlbum = tester.new MockAlbum(i + "t" + (size - i));
            albumList.add(mockAlbum);
        }

        SortService.sortAlbums(albumList);
        for (int i = 1; i < size; i ++) {
            String title_curr = albumList.get(i).getName();
            String title_prev = albumList.get(i - 1).getName();
            assertEquals(true, title_curr.compareTo(title_prev) >= 0);
        }
    }
}
