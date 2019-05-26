package com.cse110.team28.flashbackmusicplayer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by abipalli on 3/5/18.
 */

public class LocationService implements LocationListener {
    private boolean isGPSEnabled = false, isNetworkEnabled = false;
    protected LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE = 0;
    private static final long MIN_TIME_CHANGE = 5000;


    protected Location currLoc;

    private ArrayList<LocationServiceListener> listeners = new ArrayList<>();

    private final static String permissions = Manifest.permission.ACCESS_FINE_LOCATION;

    // debug
    private static final String TAG = "LocationServices";

    LocationService(Context context) {
        // initialize the locationServices update service so that other methods can access current locationServices
        if (locationManager == null) {
            initLocationService(context);
            Log.d(TAG, "LocationManager created");
        }
    }

    public void initLocationService(Context context) {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return ;
            } else {

                if (Build.FINGERPRINT.contains("generic")) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_CHANGE,
                            MIN_DISTANCE_CHANGE, this);
                }

                else{
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_CHANGE,
                            MIN_DISTANCE_CHANGE, this);
                }
                if(locationManager != null) {
                    currLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } catch(Exception ex) {
            Log.e( TAG, "Error creating location service: " + ex.getMessage());
        }
    }

    public void registerListener(LocationServiceListener listener) {
        // store the deelegate that will be used for callback functionality
        listeners.add(listener);
        checkPermissions(listener);
    }
    public void unregisterListener(LocationServiceListener listener) {
        int i = listeners.indexOf(listener);
        if(i >= 0) { listeners.remove(i); }
    }
    public void notifyListeners() {
        for(LocationServiceListener listener : listeners) {
            listener.onLocationChanged(currLoc);
        }
    }

    public void checkPermissions(LocationServiceListener delegate) {
        delegate.checkPermissions();
    }

    public Location getCurrentLocation() {
        return currLoc;
    }

    @Override
    public void onLocationChanged(Location location) {
        currLoc = location;
        notifyListeners();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
