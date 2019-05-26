package com.cse110.team28.flashbackmusicplayer;

import android.location.Location;

/**
 * Created by abipalli on 3/7/18.
 */

interface LocationServiceListener {
    void checkPermissions();

    void onLocationChanged(Location location);
}
