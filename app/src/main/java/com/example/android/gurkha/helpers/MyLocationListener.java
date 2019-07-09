package com.example.android.gurkha.helpers;

import android.location.Location;

public interface MyLocationListener {

    /**
     * listen when location is changed
     *
     * @param location changed location
     */
    void getChangedLocation(Location location);

}