package JUnitTests;

/**
 * Created by ankurgupta on 2/15/18.
 */

import android.content.Context;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.util.Pair;

import com.cse110.team28.flashbackmusicplayer.R;
import com.cse110.team28.flashbackmusicplayer.Track;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestTrack {

    public Track track;
    public Context context;
  
    ArrayList<String> mediaName;
    ArrayList<Uri> mediaFile;

    @Before
    public void setup() {
        track = new Track();

        context = InstrumentationRegistry.getTargetContext();
        Field[] fields = R.raw.class.getFields();
        mediaName = new ArrayList<>();
        mediaFile = new ArrayList<>();
        int i = 0;
        try{
            for (Field cur : fields){
                mediaName.add(cur.getName());
                mediaFile.add(Uri.parse("android.resource://" + context.getPackageName() + "/" +
                        context.getResources().getIdentifier(cur.getName(), "raw", context.getPackageName())));

                i++;
                if (i == 2) {break;}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(context, mediaFile.get(0));
        track.setMd(md);
        track.setMedia(mediaName.get(0));

    }

    @Test
    public void testIncWeight() {

        track.incrementWeight();
        assertEquals(1, track.getWeight());
        track.incrementWeight();
        assertEquals(2, track.getWeight());
        track.incrementWeight();
        assertEquals(3, track.getWeight());

    }

    @Test
    public void testResetWeight() {
        assertEquals(track.getWeight(), 0);
        track.incrementWeight();
        assertEquals(1, track.getWeight());
        track.resetWeight();
        assertEquals(0, track.getWeight());
    }

    @Test
    public void testFavorite() {

        track.setFavorite(0);
        assertEquals(0, track.getFavorite());

        track.setFavorite(1);
        assertEquals(1, track.getFavorite());

        track.setFavorite(-1);
        assertEquals(-1, track.getFavorite());
    }
    @Test
    public void testLocation() {
        Location testLoc = new Location("");
        testLoc.setLatitude(32.8801);
        testLoc.setLongitude(117.2340);

        LocalDateTime testTime = LocalDateTime.now();


        track.setLog(testLoc, testTime);
        assertEquals(testLoc, track.getCurrLocation());
        assertEquals(testTime, track.getCurrTime());
        assertEquals(new Pair<Location,LocalDateTime>(testLoc,testTime), track.getLog());
        track.updateLog();
        assertEquals(new Pair<Location,LocalDateTime>(testLoc,testTime), track.getLog());
        assertEquals(testLoc, track.getCurrLocation());
        assertEquals(testTime, track.getCurrTime());
    }

    @Test
    public void testPopulateData() {
        assert track.getMd() != null;
        assertEquals(track.getMedia(), "_1");
    }

    @Test
    public void testCompare() {
        Track track2 = new Track();
        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(context, mediaFile.get(1));
        track2.setMd(md);
        track2.setMedia(mediaName.get(1));
        assert track.compareTo(track2) != 0;
    }

    @Test
    public void testEquals() {
        Track track2 = new Track();
        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(context, mediaFile.get(1));
        track2.setMd(md);
        track2.setMedia(mediaName.get(1));
        assert track.equals(track2) == false;
    }
}
