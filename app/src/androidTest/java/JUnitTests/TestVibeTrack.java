package JUnitTests;

/**
 * Created by ankurgupta on 3/14/18.
 */


import android.location.Location;

import android.util.Pair;


import com.cse110.team28.flashbackmusicplayer.Track;
import com.cse110.team28.flashbackmusicplayer.User;
import com.cse110.team28.flashbackmusicplayer.VibeMode;
import com.cse110.team28.flashbackmusicplayer.VibeTrack;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.LocalDate;
import java.time.Month;

import static junit.framework.Assert.assertEquals;


public class TestVibeTrack {

    public static User mainUser;
    public User user2;
    public User user3;


    public Location loc1;
    public LocalDateTime time1;

    public Location loc2;
    public LocalDateTime time2;

    Track origTrack1;
    Track origTrack2;

    VibeTrack track1;
    VibeTrack track2;

    public static VibeMode vb;

    private static boolean runOnce = true;

    @Before
    public void setup() {

        if(runOnce) {
            User.clearMaps();

            mainUser = new User(false, "Ankur");
            User.addUser(true, false, "Abi");
            User.addUser(true, false, "Aditya");

        }

        runOnce = false;

        origTrack1 = new Track();
        origTrack2 = new Track();


        user2 = User.getAllUsers().get(1001);
        user3 = User.getAllUsers().get(1002);


        loc1 = new Location("");
        loc1.setLatitude(32.8801);
        loc1.setLongitude(117.2340);
        time1 = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 18), LocalTime.of(12, 00));

        loc2 = new Location("");
        loc2.setLatitude(1.23);
        loc2.setLongitude(4.56);

        time2 = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 1), LocalTime.of(11, 00));

        track1 = new VibeTrack(user2, new Pair<LocalDateTime, Location>(time1, loc1), origTrack1);
        track2 = new VibeTrack(user3, new Pair<LocalDateTime, Location>(time2, loc2), origTrack2);

    }

    @Test
    public void testInfo() {
        assertEquals(track1.getDate(), time1);

        assertEquals(track1.getLocationPlayed(), loc1);

        assertEquals(track1.getUser(), user2);

        assertEquals(track1.getTrack(), origTrack1);


        assertEquals(track2.getDate(), time2);

        assertEquals(track2.getLocationPlayed(), loc2);

        assertEquals(track2.getUser(), user3);

        assertEquals(track2.getTrack(), origTrack2);
    }

    @Test
    public void testIncWeight() {

        assertEquals(track1.getWeight(), 0);
        assertEquals(track2.getWeight(), 0);

        track1.incWeight();
        track2.incWeight();

        assertEquals(track1.getWeight(), 1);
        assertEquals(track2.getWeight(), 1);

        track1.incWeight();
        track2.incWeight();

        assertEquals(track1.getWeight(), 2);
        assertEquals(track2.getWeight(), 2);

        track1.incWeight();
        track2.incWeight();

        assertEquals(track1.getWeight(), 3);
        assertEquals(track2.getWeight(), 3);
    }

    @Test
    public void testResetWeight() {

        assertEquals(track1.getWeight(), 0);
        assertEquals(track2.getWeight(), 0);

        track1.incWeight();
        track2.incWeight();

        assertEquals(track1.getWeight(), 1);
        assertEquals(track2.getWeight(), 1);

        track1.resetWeight();
        track2.resetWeight();

        assertEquals(track1.getWeight(), 0);
        assertEquals(track2.getWeight(), 0);

    }

    @Test
    public void testNearby() {

        assertEquals(track1.gettiebreaker()[0], 0);
        assertEquals(track2.gettiebreaker()[0], 0);

        track1.nearby();
        track2.nearby();

        assertEquals(track1.gettiebreaker()[0], 1);
        assertEquals(track2.gettiebreaker()[0], 1);
    }

    @Test
    public void testLastWeek() {

        assertEquals(track1.gettiebreaker()[1], 0);
        assertEquals(track2.gettiebreaker()[1], 0);

        track1.lastweek();
        track2.lastweek();

        assertEquals(track1.gettiebreaker()[1], 1);
        assertEquals(track2.gettiebreaker()[1], 1);
    }

    @Test
    public void testFriend() {

        assertEquals(track1.gettiebreaker()[2], 0);
        assertEquals(track2.gettiebreaker()[2], 0);

        track1.friend();
        track2.friend();

        assertEquals(track1.gettiebreaker()[2], 1);
        assertEquals(track2.gettiebreaker()[2], 1);
    }

}
