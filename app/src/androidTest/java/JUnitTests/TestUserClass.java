package JUnitTests;

import android.location.Location;
import android.util.Pair;

import com.cse110.team28.flashbackmusicplayer.Track;
import com.cse110.team28.flashbackmusicplayer.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static junit.framework.Assert.assertEquals;


/**
 * Created by ankurgupta on 3/9/18.
 */

public class TestUserClass {

    public static User mainUser;

    private static boolean runOnce = false;

    @Before
    public void setup () {

        if(runOnce) {
            return;
        }
        User.clearMaps();
        runOnce = true;
        mainUser = new User(false,"Ankur");
        User.addUser(true, false, "Abi");
        User.addUser(true, false, "Aditya");
        User.addUser(false, true, "Random");



    }

    @Test
    public void testID() {

        assertEquals(1004, User.getIDCounter());



    }

    @Test
    public void testAddUser() {

        assertEquals(User.getAllUsers().size(), 4);
        assertEquals(User.getAllFriends().size(), 2);
    }

    @Test
    public void testInfo() {

        assertEquals(mainUser.getID(), 1000);
        assertEquals(mainUser.getName(), "Ankur");
        assertEquals(mainUser.isAnonymous(), false);


        User user2 = User.getAllUsers().get(1001);
        User user3 = User.getAllUsers().get(1002);
        User user4 = User.getAllUsers().get(1003);

        assertEquals(user2.getID(), 1001);
        assertEquals(user2.getName(), "Abi");
        assertEquals(user2.isAnonymous(), false);


        assertEquals(user3.getID(), 1002);
        assertEquals(user3.getName(), "Aditya");
        assertEquals(user3.isAnonymous(), false);


        assertEquals(user4.getID(), 1003);
        assertEquals(user4.getName(), "Random");
        assertEquals(user4.isAnonymous(), true);


    }

    @Test
    public void testlogSong() {
        Track track1 = new Track();
        Track track2 = new Track();
        Track track3 = new Track();

        Location currLocation = new Location("");
        currLocation.setLatitude(32.8801);
        currLocation.setLongitude(117.2340);

        LocalDateTime currTime = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 18), LocalTime.of(12, 00));
        LocalDateTime currTime2 = LocalDateTime.of(LocalDate.of(2017, Month.FEBRUARY, 17), LocalTime.of(11, 00));

        User user2 = User.getAllUsers().get(1001);
        User user3 = User.getAllUsers().get(1002);
        User user4 = User.getAllUsers().get(1003);

        //should not add dup song
        mainUser.logSong(track1, currTime, currLocation);
        mainUser.logSong(track1, currTime, currLocation);

        assertEquals(mainUser.getUserSongs().size(), 1);

        //check new time is stored
        mainUser.logSong(track1, currTime2, currLocation);

        assertEquals(mainUser.getUserSongs().size(), 1);
        assertEquals(mainUser.getUserSongs().get(track1).first, currTime2);

        user2.logSong(track1, currTime, currLocation);

        user3.logSong(track1, currTime, currLocation);
        user3.logSong(track2, currTime, currLocation);

        user4.logSong(track1, currTime, currLocation);
        user4.logSong(track2, currTime, currLocation);
        user4.logSong(track3, currTime, currLocation);


        assertEquals(user2.getUserSongs().size(), 1);

        assertEquals(user3.getUserSongs().size(), 2);

        assertEquals(user4.getUserSongs().size(), 3);

    }


}
