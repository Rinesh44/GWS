package com.example.android.gurkha;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LastVisit extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = LastVisit.class.getSimpleName();
    private GoogleMap mMap;
    private static String base_url = "http://gws.pagodalabs.com.np";
    private LocationManager locationManager;
    public static double lant, lont;
    private List<Marker> originMarkers = new ArrayList<>();
    boolean isNetworkEnabled = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private SessionManager sessionManager;
    private FbSessionManager fbSessionManager;
    private ProgressDialog dialog;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private String token;
    private FloatingActionButton remover;
    Call<ResponseBody> call;

    private FusedLocationProviderClient mFusedLocationClient;
    private Button mSatelliteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
//        getSupportLoaderManager().initLoader(0, null, this);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_ACCESS_FINE_LOCATION);
        }

        mSatelliteView = findViewById(R.id.btn_satellite_view);
        mSatelliteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });


        getVisitedLocations();
        remover = (FloatingActionButton) findViewById(R.id.remove);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Removing all markers from the Google Map
                mMap.clear();

                // Creating an instance of LocationDeleteTask
//                LocationDeleteTask deleteTask = new LocationDeleteTask();

                // Deleting all the rows from SQLite database table
//                deleteTask.execute();

                Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_LONG).show();
            }
        });

    }


    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }


    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(LastVisit.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)//2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }


    void getVisitedLocations() {
        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);

            dialog = new ProgressDialog(LastVisit.this);
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();


//            timerDelayRemoveDialog(5000, dialog);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // Enable response caching
                    .addNetworkInterceptor(new LastVisit.ResponseCacheInterceptor())
                    .addInterceptor(new LastVisit.OfflineResponseCacheInterceptor())
                    // Set the cache location and size (1 MB)
                    .cache(new Cache(new File(LastVisit.this
                            .getCacheDir(),
                            "apiResponses"), 2 * 1024))
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url) // that means base url + the left url in interface
                    .client(okHttpClient)//adding okhttp3 object that we have created
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            call = retrofit.create(ApiInterface.class).getResponse("/path/api/path?api_token=" + token);

            if (call.isExecuted())
                call = call.clone();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (dialog.isShowing()) dialog.dismiss();
                        if (!(response.isSuccessful())) {
                            Toast.makeText(LastVisit.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        final String getResponse = response.body().string();
                        Log.e(TAG, getResponse);
                        JSONObject jsonObject = new JSONObject(getResponse);
                        JSONArray data = jsonObject.getJSONArray("path");
                        for (int i = 0; i <= data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            String lat = obj.getString("latitude");
                            String lng = obj.getString("longitude");
                            String username = obj.getString("username");
                            String id = obj.getString("user_id");

                            plotMarker(lat, lng, username, id);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(LastVisit.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void plotMarker(String lat, String lng, String username, String id) {
        StringBuilder title = new StringBuilder(username).append(", ").append(id);
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                .title(title.toString()));
    }


   /* private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            */

    /**
     * Deleting all the locations stored in SQLite database
     *//*
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }*/
    public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (d.isShowing()) {
                    d.dismiss();
                }
            }
        }, time);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.setMyLocationEnabled(true);
        if (location != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 7));
    }


  /*  @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);
    }*/

 /*   @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;

        // Number of locations available in the SQLite database table
        locationCount = data.getCount();

        // Move the current record pointer to the first row of the table
        data.moveToFirst();

        for (int i = 0; i < locationCount; i++) {

            // Get the latitude
            lat = data.getDouble(data.getColumnIndex(LocationsDB.FIELD_LAT));
            Log.e("Retrievedlat:", String.valueOf(lat));
            // Get the longitude
            lng = data.getDouble(data.getColumnIndex(LocationsDB.FIELD_LNG));
            Log.e("Retrievedlat:", String.valueOf(lng));
            // Get the zoom level
            zoom = data.getFloat(data.getColumnIndex(LocationsDB.FIELD_ZOOM));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            drawMarker(location);

            // Traverse the pointer to the next row
            data.moveToNext();
        }

        if (locationCount > 0) {
            // Moving CameraPosition to last clicked position
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));

            // Setting the zoom level in the map on last position  is clicked
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }*/

}
