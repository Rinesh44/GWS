package com.example.android.gurkha;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.example.android.gurkha.helpers.MyLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Tracking extends FragmentActivity implements ResponseListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapLongClickListener,
        MyLocationListener
        {


    private static String base_url = "http://gws.pagodalabs.com.np/";
    private static final String url = "http://gws.pagodalabs.com.np/track/api/track?api_token=";
    private GoogleMap mMap;
    private ArrayList<LatLng> points; //added
    private ArrayList<String> pointsString;
    private ArrayList<String> markerValues;
    private ArrayList<LatLng> arrayedMarker;
    private ArrayList<LatLng> savedMarkers;
    private ArrayList<String> markerId;
    private ArrayList<String> base64Encoded;
    private ArrayList<String> descriptionText;
    String token, id;
    private ArrayList<String> userId;
    public File[] allFiles;
    Polyline line; //added
    private static final String TAG = "Tracking";
    private static final long INTERVAL = 1000 * 30; //half minute
    private static final long FASTEST_INTERVAL = 1000 * 30; // half minute
    private static final float SMALLEST_DISPLACEMENT = 0.25F; //quarter of a meter
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    String mLastUpdateTime;
    FloatingActionButton mSave;
    public static int RESULT_LOAD_IMAGE = 1;
    public static String cameraImagePath;
    private FloatingActionButton delete;

    public ArrayList<String> polylineValues;
    String savedText;
    private static final int REQUEST_PERMISSIONS = 124;
    String encodedImage, imageDir;
    EditText field;
    Context context;
    String imagePath;
    String currentMarker;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    private static final int CAMERA_REQUEST = 1888;
    public HashMap<String, String> infoWindowNote;
    public HashMap<String, String> infoWindowImageName;
    public JobManager mJobManager;
    private LocationManager mLocationManager;
    private SearchableSpinner routeName;
    protected com.example.android.gurkha.helpers.LocationListener myLocListener;
    private Marker pointer;
    private ProgressDialog progressDialog;
    retrofit2.Call<ResponseBody> call;
    private ArrayList<Object> list;
    private LinearLayout routeNameContainer;
    Typeface face;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActivityCompat.requestPermissions(Tracking.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);

        createLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        points = new ArrayList<LatLng>(); //added
        pointsString = new ArrayList<>();
        markerValues = new ArrayList<>();
        savedMarkers = new ArrayList<>();
        arrayedMarker = new ArrayList<>();
        markerId = new ArrayList<>();
        userId = new ArrayList<>();
        descriptionText = new ArrayList<>();
        base64Encoded = new ArrayList<>();
        infoWindowNote = new HashMap<>();
        infoWindowImageName = new HashMap<>();
        list = new ArrayList<>();

        routeNameContainer = findViewById(R.id.route_name_container);
        routeName = findViewById(R.id.route_name);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        googleApiClient.connect();

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        mSave = findViewById(R.id.save);
        mSave.setEnabled(false);
        delete = findViewById(R.id.delete);
        delete.setEnabled(false);

       /* final Dialog start = new Dialog(Tracking.this);
        start.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        start.requestWindowFeature(Window.FEATURE_NO_TITLE);
        start.setContentView(R.layout.pathname);
        start.setCancelable(false);
        start.show();*/

        Button ok = (Button) findViewById(R.id.btn);
        Button cancel = (Button) findViewById(R.id.cancel);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSave.setEnabled(true);
                delete.setEnabled(true);
                routeNameContainer.setVisibility(View.GONE);
//                imageDir = routeName.getText().toString() + "_onFoot" + getTimeStamp();
                imageDir = routeName.getSelectedItem().toString() + getTimeStamp();
                Toast.makeText(Tracking.this, "Route name saved as:" + imageDir, Toast.LENGTH_SHORT).show();
//                start.dismiss();
                Toast.makeText(Tracking.this, "Start moving to create path", Toast.LENGTH_LONG).show();
                Toast.makeText(Tracking.this, "Long press on map to add marker on pressed area", Toast.LENGTH_LONG).show();


                if (mMap.getMyLocation() != null)
                    animateCameraToCurrentLocation(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeNameContainer.setVisibility(View.GONE);
//                start.dismiss();
                finish();
            }
        });



/*
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Tracking.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.notes);
                dialog.show();

                field = (EditText) dialog.findViewById(R.id.noteField);
                Button saveNotes = (Button) dialog.findViewById(R.id.noteSave);

                saveNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savedText = field.getText().toString();
                        Toast.makeText(Tracking.this, "Notes Saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
*/


        mJobManager = GurkhaApplication.getInstance().getJobManager();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {

               /* if (markerValues.size() <= 0) {
                    Toast.makeText(Tracking.this, "Please add at least one marker. Long press on area to add marker", Toast.LENGTH_LONG).show();
                } else {*/
                        /*
                        final Dialog dialog = new Dialog(Tracking.this);
                        dialog.getWindow();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.routename);
                        dialog.show();

                        Button ok = (Button) dialog.findViewById(R.id.btn);
                        final EditText routeName = (EditText) dialog.findViewById(R.id.name);

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            */
                if (sessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                    id = user.get(SessionManager.KEY_ID);
                }

                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                    if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                    if (fbUser.get(SessionManager.KEY_ID) != null)
                        id = fbUser.get(SessionManager.KEY_ID);
                }


//                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                userId.add(id);
                JSONObject parameter = new JSONObject();
                JSONObject name = new JSONObject();
                //JSONObject id = new JSONObject();
                try {
                    JSONArray list = new JSONArray(pointsString);
                    JSONArray markerlist = new JSONArray(markerValues);
                    JSONArray imageList = new JSONArray(base64Encoded);
                    JSONArray markerIdList = new JSONArray(markerId);
                    JSONArray noteList = new JSONArray(descriptionText);
                    JSONArray idList = new JSONArray(userId);
                    name.put("polyline", list);
                    name.put("marker", markerlist);
                    name.put("marker_id", markerIdList);
                    name.put("user_id", idList);
                    name.put("images", imageList);
                    name.put("notes", noteList);
                    name.put("api_token", token);
                    parameter.put(imageDir, name);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("parameterJSON:", parameter.toString());
                Log.e("imageSIze:", String.valueOf(base64Encoded.size()));

                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), Tracking.this));

                Toast.makeText(getBaseContext(), "The path has been saved", Toast.LENGTH_SHORT).show();
                finish();
            }

        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Removing all markers from the Google Map
                mMap.clear();
                Toast.makeText(getBaseContext(), "All removed", Toast.LENGTH_LONG).show();
            }
        });


       run();


    }

            @Override
            public void responseSuccess(Response response) {

            }

            @Override
            public void responseFail(String msg) {

            }
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Tracking.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }
    }

*/


    /**
     * Interceptor to cache data and maintain it for a minute.
     * <p>
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     * <p>
     * If the device is offline, stale (at most eight hours old)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(Tracking.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }


    void run() {
        progressDialog = new ProgressDialog(Tracking.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Tracking.ResponseCacheInterceptor())
                .addInterceptor(new Tracking.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Tracking.this
                        .getCacheDir(),
                        "trackingApiResponses"), 5 * 1024))
                .build();


        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }


        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(FbSessionManager.KEY_TOKEN) != null)
                token = fbUser.get(FbSessionManager.KEY_TOKEN);
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("personal_detail/api/personal_detail?api_token=" + token);
        Log.e("tokenValue:", token);


        if (call.isExecuted()) call = call.clone();

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Tracking", t.toString());
                Toast.makeText(Tracking.this, "Something went wrong: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                try {

                    if (!(response.isSuccessful())) {
                        Toast.makeText(Tracking.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("personal");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);

                        String name = innerObject.getString("name");
                        String surname = innerObject.getString("surname");
                        String army_no = innerObject.getString("army_no");
//                        String personal_id = innerObject.getString("personal_id");

                        list.add(name + " " + surname + " " + army_no);

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                Tracking.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Tracking.this, R.layout.support_simple_spinner_dropdown_item, list.toArray()) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        routeName.setAdapter(adapt_person);
                    }

                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/GWS/" + imageDir + "/");
            if (folder.exists()) {
                allFiles = folder.listFiles();
                base64Encoded.clear();
                for (int j = 0; j < allFiles.length; j++) {
                    new Tracking.SingleMediaScanner(Tracking.this, allFiles[j]);
                    imagePath = allFiles[j].getAbsolutePath();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 2;  //you can also calculate your inSampleSize
                    options.inJustDecodeBounds = false;
                    options.inTempStorage = new byte[16 * 1024];

                    Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    Log.i("encodedImageString:", encodedImage);
                    base64Encoded.add(encodedImage);

                }
            }
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(Tracking.this, "Current Location", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMapLongClickListener(this);
        //mMap.setOnMapClickListener(this);


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout

                final View v = getLayoutInflater().inflate(R.layout.map_info, null);
                // Getting the position from the marker
                LatLng click = marker.getPosition();


                if (ContextCompat.checkSelfPermission(Tracking.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Tracking.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }

                if (ContextCompat.checkSelfPermission(Tracking.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Tracking.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 179);
                }

                final TextView imageName = (TextView) v.findViewById(R.id.imageName);
                final TextView imageDesc = (TextView) v.findViewById(R.id.imageDesc);
                String getNote = infoWindowNote.get(marker.getId());
                String getName = infoWindowImageName.get(marker.getId());
                if (marker.getSnippet() == null) {
                    if (getName == null) {
                        imageName.setText("Click to add data");
                    } else
                        imageName.setText(getName);
                } else {
                    imageName.setText(mLastUpdateTime);
                    imageDesc.setVisibility(View.GONE);
                }
                imageDesc.setText(getNote);

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        if (Build.VERSION.SDK_INT > 22) {
                            if (ContextCompat.checkSelfPermission(Tracking.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(Tracking.this,
                                        new String[]{Manifest.permission.CAMERA,},
                                        6);
                            }
                        }

                        if (marker.getSnippet() == null) {

                            final Dialog dialog = new Dialog(Tracking.this);
                            dialog.getWindow();
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.info_window_dialog);
                            dialog.show();

                            Button addImage = (Button) dialog.findViewById(R.id.addImage);
                            Button addText = (Button) dialog.findViewById(R.id.addText);

                            addImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File image = new File(appFolderCheckandCreate(), "img" + getTimeStamp() + ".jpg");
                                    // Uri uriSavedImage = Uri.fromFile(image);
                                    Uri uriSavedImage = FileProvider.getUriForFile(Tracking.this, BuildConfig.APPLICATION_ID + ".provider", image);
                                    String imagePathName = image.getName();
                                    infoWindowImageName.put(marker.getId(), imagePathName);

                                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                    i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                                    i.putExtra("return-data", true);
                                    startActivityForResult(i, CAMERA_REQUEST);
                                    marker.hideInfoWindow();

                                }
                            });

                            addText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Dialog dialogNotes = new Dialog(Tracking.this);
                                    dialogNotes.getWindow();
                                    dialogNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialogNotes.setContentView(R.layout.notes);
                                    dialogNotes.show();

                                    field = (EditText) dialogNotes.findViewById(R.id.noteField);
                                    Button saveNotes = (Button) dialogNotes.findViewById(R.id.noteSave);

                                    saveNotes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            savedText = field.getText().toString();
                                            descriptionText.add(savedText);
                                            Toast.makeText(Tracking.this, "Notes Saved", Toast.LENGTH_SHORT).show();
                                            Log.i("textArray", descriptionText.toString());
                                            infoWindowNote.put(marker.getId(), savedText);
                                            Log.i("checkinfo", infoWindowNote.toString());
                                            markerId.add(marker.getId());
                                            dialogNotes.dismiss();
                                            dialog.dismiss();
                                            marker.hideInfoWindow();
                                        }
                                    });
                                }

                            });
                        }
                    }
                });


                return v;
            }
        });

    }


    private void redrawLine() {

        //mMap.clear();  //clears all Markers and Polylines
        if (pointer != null) {
            pointer.remove();
        }

        pointer = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));

        PolylineOptions options = new PolylineOptions().width(7).color(getResources().getColor(android.R.color.holo_blue_light)).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
//        addMarker(); //add Marker in current position
        if (line != null) {
            line.remove();
        }

        line = mMap.addPolyline(options); //add Polyline

    }

    private void addMarker() {

        MarkerOptions options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        // options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime + requiredArea + city)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        options.position(currentLatLng);
        Marker mapMarker = mMap.addMarker(options);
        long atTime = currentLocation.getTime();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        String title = mLastUpdateTime;
        mapMarker.setTitle(title);


        Log.d(TAG, "Marker added.............................");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                13));
        Log.d(TAG, "Zoom done.............................");
    }

    private void createLocationRequest() {
       /* mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); //added
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        myLocListener = new com.example.android.gurkha.helpers.LocationListener(Tracking.this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        String bestProvider = mLocationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(bestProvider, 1000, 5, myLocListener);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        startLocationUpdates();
    }

  /*  protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest, this);
            Log.d(TAG, "Location update started ..............: ");
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        savedMarkers.add(latLng);
        markerValues.add(String.valueOf(latLng.latitude) + ":" + String.valueOf(latLng.longitude));
        arrayedMarker.add(latLng);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Add Description").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
        Log.e("markerValues:", markerValues.toString());
    }
/*
    @Override
    public void onMapClick(LatLng latLng) {
        for (int i = 0; i < savedMarkers.size(); i++) {
            LatLng putMarker = new LatLng(savedMarkers.get(i).latitude, savedMarkers.get(i).longitude);
            MarkerOptions add = new MarkerOptions().position(putMarker);
            mMap.addMarker(add);
        }

    }
*/
    /*
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
*/


    private String appFolderCheckandCreate() {

        String appFolderPath = "";
        File externalStorage = Environment.getExternalStorageDirectory();

        if (externalStorage.canWrite()) {
            appFolderPath = externalStorage.getAbsolutePath() + "/GWS/" + imageDir;
            File dir = new File(appFolderPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

        } else {

        }

        return appFolderPath;
    }


    private String getTimeStamp() {

        final long timestamp = new Date().getTime();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        final String timeString = new SimpleDateFormat("HH_mm_ss_SSS").format(cal.getTime());

        return timeString;
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    @Override
    public void getChangedLocation(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
           /* mMap.addMarker(new MarkerOptions()
                .position(latLng).snippet("polyline").title("Initial Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));*/
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        currentLocation = location;

        points.add(latLng); //added
        pointsString.add(String.valueOf(latitude) + ":" + String.valueOf(longitude));

        redrawLine(); //added


        animateToIncludeAllLatLng(latLng);


    }

    private void animateToIncludeAllLatLng(LatLng latLng) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        LatLngBounds bounds = builder.build();
        mMap.setLatLngBoundsForCameraTarget(bounds);
    }


    private void animateCameraToCurrentLocation(Double Lat, Double Lng) {
        LatLng latLng = new LatLng(Lat, Lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mMap.animateCamera(cameraUpdate);
    }


    private class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        private SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        public void onScanCompleted(String path, Uri uri) {
            /*
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            */
            mMs.disconnect();
        }

    }


}
