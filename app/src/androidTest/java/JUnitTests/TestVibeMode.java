package JUnitTests;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.util.Pair;


import com.cse110.team28.flashbackmusicplayer.Track;
import com.cse110.team28.flashbackmusicplayer.User;
import com.cse110.team28.flashbackmusicplayer.VibeMode;
import com.cse110.team28.flashbackmusicplayer.VibeTrack;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.PriorityQueue;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ankurgupta on 2/18/18.
 */

public class TestVibeMode {

    private Context context = InstrumentationRegistry.getTargetContext();
    public static User mainUser;


    public Location currLocation;
    public LocalDateTime currTime;
    public static VibeMode vb;

    private static boolean runOnce = false;

    @Before
    public void setup() {

        if(runOnce) {
            return;
        }

        runOnce = true;

        User.clearMaps();

        mainUser = new User(false, "Ankur");
        User.addUser(true, false, "Abi");
        User.addUser(true, false, "Aditya");
        User.addUser(true, false, "Abhi");
        User.addUser(true, false, "Raman");
        User.addUser(false, false, "Ben");
        User.addUser(false, false, "Max");
        User.addUser(false, false, "Tom");
        User.addUser(false, false, "Chad");


        User user2 = User.getAllUsers().get(1001);
        User user3 = User.getAllUsers().get(1002);
        User user4 = User.getAllUsers().get(1003);
        User user5 = User.getAllUsers().get(1004);
        User user6 = User.getAllUsers().get(1005);
        User user7 = User.getAllUsers().get(1006);
        User user8 = User.getAllUsers().get(1007);
        User user9 = User.getAllUsers().get(1008);



        //test locationServices
        currLocation = new Location("");
        currLocation.setLatitude(32.8801);
        currLocation.setLongitude(117.2340);


        //test time
        currTime = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 18), LocalTime.of(12, 00));


        Location tempLoc;


        // track with same week, diff location
        Track temp1 = new Track();
        Location location = new Location("");
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 18), LocalTime.of(12, 00));
        location.setLatitude(1.23);
        location.setLongitude(4.56);
        temp1.setCurrTime(dateTime);
        temp1.setLog(location, dateTime);
        temp1.updateLog();
        user2.logSong(temp1, dateTime, location);
        user6.logSong(temp1, dateTime, location);

         //track with diff week and location
        Track temp2 = new Track();
        LocalDateTime dateTime2 = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 1), LocalTime.of(12, 00));
        location = new Location("");
        location.setLatitude(1.23);
        location.setLongitude(4.56);
        temp2.setCurrTime(dateTime2);
        temp2.setLog(location, dateTime2);
        temp2.updateLog();
        user3.logSong(temp2, dateTime2, location);
        user7.logSong(temp2, dateTime2, location);


        // track with matching locationServices, different week
        Track temp3 = new Track();
        tempLoc = new Location("");
        tempLoc.setLatitude(32.8801);
        tempLoc.setLongitude(117.2340);
        LocalDateTime dateTime3 = LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 1), LocalTime.of(12, 00));
        temp3.setCurrLocation(tempLoc);
        temp3.setCurrTime(dateTime3);
        temp3.updateLog();
        user4.logSong(temp3, dateTime3, tempLoc);
        user8.logSong(temp3, dateTime3, tempLoc);


        // track with matching week and locationServices
        Track temp4 = new Track();
        LocalDateTime dateTime4 = (LocalDateTime.of(LocalDate.of(2018, Month.FEBRUARY, 18), LocalTime.of(12, 00)));
        tempLoc = new Location("");
        tempLoc.setLatitude(32.8801);
        tempLoc.setLongitude(117.2340);
        temp4.setCurrLocation(tempLoc);
        temp4.setCurrTime(dateTime4);
        temp4.updateLog();
        user5.logSong(temp4, dateTime4, tempLoc);
        user9.logSong(temp4, dateTime4, tempLoc);

        vb = new VibeMode(mainUser);
        vb.refreshVibeMode(currTime, currLocation);

    }


    //test that refresh correctly calls all 3 submethods and makes queue correctly
    @Test
    public void testRefreshComparator() {


        assertEquals(vb.getQueue().size(), 8);

        PriorityQueue<VibeTrack> queue = vb.getQueue();
        assertEquals(queue.peek().getWeight(), 3);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 2);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 2);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 2);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 1);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 1);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 1);
        queue.poll();
        assertEquals(queue.peek().getWeight(), 0);



    }

    //tests that weightLocation works for each vibetrack
    @Test
    public void testWeighLocation() {

        assertEquals(vb.getTracks().size(), 8);


        for(VibeTrack track:  vb.getTracks()) {

            //Log.d("WEIGHT LOC", " " + track.getUser().getName() + " " + track.gettiebreaker()[0]);
            Log.d("WEIGHT TOTAL?", " " + track.getWeight() + " " + track.getUser().getName());
             if(track.getUser().getName().equals("Abi")) {
                assertEquals(track.gettiebreaker()[0], 0);
            }
            else if(track.getUser().getName().equals("Aditya")) {
                assertEquals(track.gettiebreaker()[0], 0);
            }
            else if(track.getUser().getName().equals("Abhi")) {
                assertEquals(track.gettiebreaker()[0], 1);
            }
            else if(track.getUser().getName().equals("Raman")) {
                assertEquals(track.gettiebreaker()[0], 1);
            }
            else if(track.getUser().getName().equals("Ben")) {
                assertEquals(track.gettiebreaker()[0], 0);
            }
            else if(track.getUser().getName().equals("Max")) {
                assertEquals(track.gettiebreaker()[0], 0);
            }
            else if(track.getUser().getName().equals("Tom")) {
                assertEquals(track.gettiebreaker()[0], 1);
            }
            else if(track.getUser().getName().equals("Chad")) {
                assertEquals(track.gettiebreaker()[0], 1);
            }


        }

    }

    //tests that weightWeek works for each vibetrack
    @Test
    public void testWeightWeek() {

        assertEquals(vb.getTracks().size(), 8);


        for(VibeTrack track:  vb.getTracks()) {

            //Log.d("WEIGHT LOC", " " + track.getUser().getName() + " " + track.gettiebreaker()[1]);
            //Log.d("WEIGHT TOTAL?", " " + track.getWeight() + " " + track.getUser().getName());
            if(track.getUser().getName().equals("Abi")) {
                assertEquals(track.gettiebreaker()[1], 1);
            }
            else if(track.getUser().getName().equals("Aditya")) {
                assertEquals(track.gettiebreaker()[1], 0);
            }
            else if(track.getUser().getName().equals("Abhi")) {
                assertEquals(track.gettiebreaker()[1], 0);
            }
            else if(track.getUser().getName().equals("Raman")) {
                assertEquals(track.gettiebreaker()[1], 1);
            }
            else if(track.getUser().getName().equals("Ben")) {
                assertEquals(track.gettiebreaker()[1], 1);
            }
            else if(track.getUser().getName().equals("Max")) {
                assertEquals(track.gettiebreaker()[1], 0);
            }
            else if(track.getUser().getName().equals("Tom")) {
                assertEquals(track.gettiebreaker()[1], 0);
            }
            else if(track.getUser().getName().equals("Chad")) {
                assertEquals(track.gettiebreaker()[1], 1);
            }


        }

    }

    //tests that weightFriend works for each vibetrack
    @Test
    public void testWeightFriend() {

        assertEquals(vb.getTracks().size(), 8);


        for(VibeTrack track:  vb.getTracks()) {

            //Log.d("WEIGHT LOC", " " + track.getUser().getName() + " " + track.gettiebreaker()[2]);
            //Log.d("WEIGHT TOTAL?", " " + track.getWeight() + " " + track.getUser().getName());

            if(track.getUser().getName().equals("Abi")) {
                assertEquals(track.gettiebreaker()[2], 1);
            }
            else if(track.getUser().getName().equals("Aditya")) {
                assertEquals(track.gettiebreaker()[2], 1);
            }
            else if(track.getUser().getName().equals("Abhi")) {
                assertEquals(track.gettiebreaker()[2], 1);
            }
            else if(track.getUser().getName().equals("Raman")) {
                assertEquals(track.gettiebreaker()[2], 1);
            }
            else if(track.getUser().getName().equals("Ben")) {
                assertEquals(track.gettiebreaker()[2], 0);
            }
            else if(track.getUser().getName().equals("Max")) {
                assertEquals(track.gettiebreaker()[2], 0);
            }
            else if(track.getUser().getName().equals("Tom")) {
                assertEquals(track.gettiebreaker()[2], 0);
            }
            else if(track.getUser().getName().equals("Chad")) {
                assertEquals(track.gettiebreaker()[2], 0);
            }


        }
    }

}
