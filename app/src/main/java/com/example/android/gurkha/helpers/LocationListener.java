package com.example.android.gurkha.helpers;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

public class LocationListener implements android.location.LocationListener {

    private static final String TAG = LocationListener.class.getSimpleName();
    private Context context;
    private MyLocationListener myLocationListener;

    public LocationListener(Context context) {
        this.context = context;
        myLocationListener = (MyLocationListener) context;
    }

    @Override
    public void onLocationChanged(Location location) {

            myLocationListener.getChangedLocation(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
