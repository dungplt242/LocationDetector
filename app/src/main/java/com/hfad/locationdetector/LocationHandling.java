package com.hfad.locationdetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class LocationHandling {

    private Activity context;

    public LocationHandling(Activity context) {
        this.context = context;
    }

    public Location getCurrentLocation() {
        // TODO: Fix this method. Problem in NETWORK_PROVIDER service
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        checkAndRequestLocationPermission();
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) { loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); }
        return loc;
    }

    private void checkAndRequestLocationPermission() {
        while (!accessPermissionGranted()) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
        }
    }

    private boolean accessPermissionGranted() {
        return ActivityCompat.
                checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
