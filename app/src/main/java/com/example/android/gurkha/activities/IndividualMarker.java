package com.example.android.gurkha.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.android.gurkha.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class IndividualMarker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_marker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void getIntentData() {
        Intent i = getIntent();
        String lat = ((Intent) i).getStringExtra("Lat");
        String lng = i.getStringExtra("Lng");
        String name = i.getStringExtra("Name");
        String surname = i.getStringExtra("Surname");
        String armyNo = i.getStringExtra("ArmyNo");

        plotCoordinatesOnMap(lat, lng, name, surname, armyNo);
    }

    private void plotCoordinatesOnMap(String lat, String lng, String name, String surname, String armyNo) {
        LatLng point = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(armyNo + ", " + name + " " + surname));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 13));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getIntentData();

    }
}
