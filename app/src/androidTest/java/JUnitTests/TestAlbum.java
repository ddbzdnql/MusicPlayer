package JUnitTests;

import com.cse110.team28.flashbackmusicplayer.Album;
import com.cse110.team28.flashbackmusicplayer.Track;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by chengyou on 2/17/18.
 */
public class TestAlbum {

    String albumName1 = "album1";
    String albumName2 = "album2";

    String artistName1 = "artist1";
    String artistName2 = "artist2";

    ArrayList<Track> tracklist1 = new ArrayList<>();
    Track track1;
    Track track2;
    Track track3;

    Album album1 = new Album(albumName1, artistName1);
    Album album2 = new Album(albumName1, artistName1);
    Album album3 = new Album(albumName1, artistName2);
    Album album4 = new Album(albumName2, artistName1);
    Album album5 = new Album(albumName2, artistName2);

    @Test
    public void testAdd() {

        tracklist1.add(track1);
        assertEquals(tracklist1.size(), 1);

        tracklist1.add(track2);
        assertEquals(tracklist1.size(), 2);

        tracklist1.add(track3);
        assertEquals(tracklist1.size(), 3);
    }

    @Test
    public void testGetName() {
        assertEquals(album1.getName(), albumName1);
    }

    @Test
    public void testGetArtist() {
        assertEquals(album1.getArtist(), artistName1);
    }

    @Test
    public void testEquals() {
        assertEquals(album1.equals(album2), true); // same name, same artist
        assertEquals(album1.equals(album3), false); // same name, different artist
        assertEquals(album1.equals(album4), false); // different name, same artist
        assertEquals(album1.equals(album5), false); // different name, different artist
    }
}