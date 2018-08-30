package com.example.android.gurkha;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

public class Current_Location extends FragmentActivity implements OnMapReadyCallback {


    private String url = "http://pagodalabs.com.np/gws/path/api/path?api_token=";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double lant, lont;
    private Toolbar toolbar;
    private List<Marker> originMarkers = new ArrayList<>();
    boolean isNetworkEnabled = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    GoogleApiClient mGoogleApiClient;
    private FloatingActionButton gps;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    public static String lastlat;
    public static String lastlong;
    String userId;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    String token;
    FloatingActionButton save;
    private static final int REQUEST_PERMISSIONS = 123;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    JobManager mJobManager;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
            userId = user.get(SessionManager.KEY_ID);

            Log.e("normalValues:", "token =" + token + " userId=" + userId);
        }

        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(FbSessionManager.KEY_ID) != null) {
                token = fbUser.get(FbSessionManager.KEY_TOKEN);
                userId = fbUser.get(FbSessionManager.KEY_ID);
                Log.e("fbValues:", "token =" + token + " userId=" + userId);
            }
        }

        mJobManager = GurkhaApplication.getInstance().getJobManager();
        save = (FloatingActionButton) findViewById(R.id.save);
        gps = findViewById(R.id.fab_gps);


        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Current_Location.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.show_lat_lng);
                dialog.show();


                TextView lat = dialog.findViewById(R.id.lat_value);
                TextView lng = dialog.findViewById(R.id.lng_value);
                Button ok = dialog.findViewById(R.id.ok);

                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(Current_Location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Current_Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Current_Location.this);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        lat.setText(String.valueOf(location.getLatitude()));
                        lng.setText(String.valueOf(location.getLongitude()));
                    }
                });

                lat.setText(String.valueOf(lastlat));
                lng.setText(String.valueOf(lastlong));


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_ACCESS_FINE_LOCATION);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the last know location from your location manager.
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(Current_Location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Current_Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Current_Location.this);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastlat = String.valueOf(location.getLatitude());
                        lastlong = String.valueOf(location.getLongitude());

                        postToServer(userId, lastlat, lastlong);

                    }
                });



               /* // now get the lat/lon from the location and do something with it.

                // Creating an instance of ContentValues
                ContentValues contentValues = new ContentValues();

                // Setting latitude in ContentValues
                contentValues.put(LocationsDB.FIELD_LAT, lastlat);

                // Setting longitude in ContentValues
                contentValues.put(LocationsDB.FIELD_LNG, lastlong);

                // Setting zoom in ContentValues
                contentValues.put(LocationsDB.FIELD_ZOOM, mMap.getCameraPosition().zoom);

                // Creating an instance of LocationInsertTask
                LocationInsertTask insertTask = new LocationInsertTask();

                // Storing the latitude, longitude and zoom level to SQLite database
                insertTask.execute(contentValues);
                Log.e("content_value:", String.valueOf(contentValues));*/

                Toast.makeText(Current_Location.this, "Location saved to Visited Place", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void postToServer(String id, String latitude, String longitude) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", id);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("api_token", token);

        JSONObject parameter = new JSONObject(params);
        Log.e("JSON:", parameter.toString());

        mJobManager.addJobInBackground(new PostJob(url + token, parameter.toString()));

    }


  /*  private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            *//** Setting up values to insert the clicked location into SQLite database *//*
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }*/

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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));

        }

    }

}

