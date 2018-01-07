package com.example.android.gurkha;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchPerson extends FragmentActivity implements OnMapReadyCallback {
    private String TAG = SearchPerson.class.getSimpleName();
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Double lant, lont;
    private Toolbar toolbar;
    private ArrayList<Marker> originMarkers = new ArrayList<>();
    private ProgressDialog progressDialog;
    boolean isNetworkEnabled = false;
    private static String base_url = "http://pagodalabs.com.np/";
    private static String url = "http://pagodalabs.com.np/gws/personal_detail/api/personal_detail/";
    public String lat, lon;
    private Marker locationMarker;
    private EditText search;
    ArrayList<HashMap<String, String>> positionList;
    private SimpleAdapter adapter;
    private Button btnSearch;
    Marker currentmarker, addedmarker;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public int size;
    Call<ResponseBody> call;
    String myResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_person);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        positionList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.select);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        adapter = new SimpleAdapter(SearchPerson.this, positionList,
                R.layout.list_item, new String[]{"name", "surname"}, new int[]{R.id.name, R.id.surname});

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        tv.setTypeface(face);

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location l = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < providers.size(); i++) {
                l = locationManager.getLastKnownLocation(providers.get(i));
                if (l != null) {
                    lant = l.getLatitude();
                    lont = l.getLongitude();
                    break;
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_ACCESS_FINE_LOCATION);
            for (int i = 0; i < providers.size(); i++) {
                l = locationManager.getLastKnownLocation(providers.get(i));
                if (l != null) {
                    lant = l.getLatitude();
                    lont = l.getLongitude();
                    break;
                }
            }
        }

        search = (EditText) findViewById(R.id.search);
        btnSearch = (Button) findViewById(R.id.btnsearch);

        //search.setTypeface(face);

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            //new GetPosition().execute();

            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Unable to load data. No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        */
    }

    /**
     * Interceptor to cache data and maintain it for a minute.
     *
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 900)
                    .build();
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     *
     * If the device is offline, stale (at most two weeks old)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(SearchPerson.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)
                        .build();
            }
            return chain.proceed(request);
        }
    }


    void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        final String awc = SelectAwcForMap.awc;
        if (sessionManager.getUserDetails() != null){
            HashMap<String, String> user = sessionManager.getUserDetails();
            String id = user.get(SessionManager.KEY_ID);

            if(id != null) {
                if (id.matches("1")) {

                    progressDialog = new ProgressDialog(SearchPerson.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(SearchPerson.this
                                    .getCacheDir(),
                                    "searchPersonApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(SearchPersonInterface.class).getResponse("gws/personal_detail/api/personal_detail/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(SearchPerson.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);


                                    lat = c.getString("latitude");
                                    lon = c.getString("longitude");
                                    String name = c.getString("name");

                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");

                                    String surname = c.getString("surname");
                                    String subunit = c.getString("subunit");
                                    String unit = c.getString("unit");
                                    String army = c.getString("army");
                                    String age = c.getString("age");
                                    String dob = c.getString("dob");
                                    String doe = c.getString("doe");
                                    String dod = c.getString("dod");
                                    String dc = c.getString("dc");
                                    String retain = c.getString("retain");
                                    String payee = c.getString("payee");
                                    String dependent_name = c.getString("dependent_name");

                                    String dependent_age = c.getString("dependent_age");
                                    String dependent_dob = c.getString("dependent_dob");
                                    String photo1 = c.getString("photo1");
                                    String photo2 = c.getString("photo2");
                                    String photo3 = c.getString("photo3");
                                    String nominee = c.getString("nominee");
                                    String relation = c.getString("relation");
                                    String paid_to = c.getString("paid_to");
                                    String health_state = c.getString("health_state");
                                    String type_of_welfare_pensioner = c.getString("type_of_welfare_pensioner");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("latitude", lat);
                                    contact.put("longitude", lon);
                                    contact.put("name", name);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("surname", surname);
                                    contact.put("subunit", subunit);
                                    contact.put("unit", unit);
                                    contact.put("army", army);
                                    contact.put("age", age);
                                    contact.put("dob", dob);
                                    contact.put("doe", doe);
                                    contact.put("dod", dod);
                                    contact.put("dc", dc);
                                    contact.put("retain", retain);
                                    contact.put("payee", payee);
                                    contact.put("dependent_name", dependent_name);
                                    contact.put("dependent_age", dependent_age);
                                    contact.put("dependent_dob", dependent_dob);
                                    contact.put("photo1", photo1);
                                    contact.put("photo2", photo2);
                                    contact.put("photo3", photo3);
                                    contact.put("nominee", nominee);
                                    contact.put("relation", relation);
                                    contact.put("paid_to", paid_to);
                                    contact.put("health_state", health_state);
                                    contact.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);

                                    // adding contact to contact list
                                    positionList.add(contact);

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            SearchPerson.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(myResponse);
                                        final JSONArray data = jsonObject.getJSONArray("personal");
                                        for (int i = 0; i < data.length(); i++) {
                                            if (i == 0) ////FOR ANIMATING THE CAMERA FOCUS FIRST TIME ON THE GOOGLE MAP
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(7).build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Normal")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Economical")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Health")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Social")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                            }

                                            btnSearch.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String text = search.getText().toString();
                                                    for (int i = 0; i < data.length(); i++) {
                                                        if ((text.equalsIgnoreCase(positionList.get(i).get("name") + " " + positionList.get(i).get("surname"))) || text.equals(positionList.get(i).get("army_no"))) {
                                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(16).build();
                                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                        }
                                                    }
                                                    if (text.equalsIgnoreCase(Menu.textName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude))).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }
                                                    if (text.equals("")) {
                                                        Toast.makeText(SearchPerson.this, "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (text.equalsIgnoreCase(Menu.onlyName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lant, lont)).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }


                                                }
                                            });

                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                                // Use default InfoWindow frame
                                                @Override
                                                public View getInfoWindow(Marker args) {
                                                    return null;
                                                }

                                                // Defines the contents of the InfoWindow
                                                @Override
                                                public View getInfoContents(Marker args) {

                                                    // Getting view from the layout file info_window_layout
                                                    View v = getLayoutInflater().inflate(R.layout.info, null);

                                                    // Getting the position from the marker
                                                    LatLng click = args.getPosition();

                                                    TextView title = (TextView) v.findViewById(R.id.content);
                                                    title.setText(args.getTitle());

                                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                        @Override
                                                        public void onInfoWindowClick(Marker marker) {
                                                            int i = Integer.parseInt(marker.getSnippet());
                                                            if (marker.getTitle().equals((positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).toString())) {

                                                                Intent inte = new Intent(SearchPerson.this, marker_details.class);
                                                                inte.putExtra("longitude", positionList.get(i).get("longitude").toString());
                                                                inte.putExtra("latitude", positionList.get(i).get("latitude").toString());
                                                                inte.putExtra("name", positionList.get(i).get("name").toString());

                                                                inte.putExtra("relation", positionList.get(i).get("relation").toString());
                                                                inte.putExtra("army_no", positionList.get(i).get("army_no").toString());
                                                                inte.putExtra("rank", positionList.get(i).get("rank").toString());

                                                                inte.putExtra("surname", positionList.get(i).get("surname").toString());
                                                                inte.putExtra("subunit", positionList.get(i).get("subunit").toString());
                                                                inte.putExtra("unit", positionList.get(i).get("unit").toString());
                                                                inte.putExtra("army", positionList.get(i).get("army").toString());
                                                                inte.putExtra("age", positionList.get(i).get("age").toString());
                                                                inte.putExtra("dob", positionList.get(i).get("dob").toString());
                                                                inte.putExtra("doe", positionList.get(i).get("doe").toString());
                                                                inte.putExtra("dod", positionList.get(i).get("dod").toString());
                                                                inte.putExtra("dc", positionList.get(i).get("dc").toString());
                                                                inte.putExtra("retain", positionList.get(i).get("retain").toString());
                                                                inte.putExtra("payee", positionList.get(i).get("payee").toString());
                                                                inte.putExtra("dependent_name", positionList.get(i).get("dependent_name").toString());
                                                                inte.putExtra("dependent_age", positionList.get(i).get("dependent_age").toString());
                                                                inte.putExtra("dependent_dob", positionList.get(i).get("dependent_dob").toString());
                                                                inte.putExtra("photo1", positionList.get(i).get("photo1").toString());
                                                                inte.putExtra("photo2", positionList.get(i).get("photo2").toString());
                                                                inte.putExtra("photo3", positionList.get(i).get("photo3").toString());
                                                                inte.putExtra("nominee", positionList.get(i).get("nominee").toString());
                                                                inte.putExtra("paid_to", positionList.get(i).get("paid_to").toString());
                                                                inte.putExtra("health_state", positionList.get(i).get("health_state").toString());
                                                                inte.putExtra("type_of_welfare_pensioner", positionList.get(i).get("type_of_welfare_pensioner").toString());
                                                                inte.putExtra("village", positionList.get(i).get("village").toString());
                                                                inte.putExtra("district", positionList.get(i).get("district").toString());
                                                                inte.putExtra("vdc", positionList.get(i).get("vdc").toString());
                                                                inte.putExtra("ward_no", positionList.get(i).get("ward_no").toString());
                                                                inte.putExtra("po", positionList.get(i).get("po").toString());

                                                                startActivity(inte);
                                                            }

                                                        }

                                                    });
                                                    return v;
                                                }

                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                    });
                } else {

                    progressDialog = new ProgressDialog(SearchPerson.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(SearchPerson.this
                                    .getCacheDir(),
                                    "userSearchPersonApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(SearchPersonInterface.class).getResponse("gws/personal_detail/api/personal_detail/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(SearchPerson.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);


                                    lat = c.getString("latitude");
                                    lon = c.getString("longitude");
                                    String name = c.getString("name");

                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");

                                    String surname = c.getString("surname");
                                    String subunit = c.getString("subunit");
                                    String unit = c.getString("unit");
                                    String army = c.getString("army");
                                    String age = c.getString("age");
                                    String dob = c.getString("dob");
                                    String doe = c.getString("doe");
                                    String dod = c.getString("dod");
                                    String dc = c.getString("dc");
                                    String retain = c.getString("retain");
                                    String payee = c.getString("payee");
                                    String dependent_name = c.getString("dependent_name");

                                    String dependent_age = c.getString("dependent_age");
                                    String dependent_dob = c.getString("dependent_dob");
                                    String photo1 = c.getString("photo1");
                                    String photo2 = c.getString("photo2");
                                    String photo3 = c.getString("photo3");
                                    String nominee = c.getString("nominee");
                                    String relation = c.getString("relation");
                                    String paid_to = c.getString("paid_to");
                                    String health_state = c.getString("health_state");
                                    String type_of_welfare_pensioner = c.getString("type_of_welfare_pensioner");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("latitude", lat);
                                    contact.put("longitude", lon);
                                    contact.put("name", name);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("surname", surname);
                                    contact.put("subunit", subunit);
                                    contact.put("unit", unit);
                                    contact.put("army", army);
                                    contact.put("age", age);
                                    contact.put("dob", dob);
                                    contact.put("doe", doe);
                                    contact.put("dod", dod);
                                    contact.put("dc", dc);
                                    contact.put("retain", retain);
                                    contact.put("payee", payee);
                                    contact.put("dependent_name", dependent_name);
                                    contact.put("dependent_age", dependent_age);
                                    contact.put("dependent_dob", dependent_dob);
                                    contact.put("photo1", photo1);
                                    contact.put("photo2", photo2);
                                    contact.put("photo3", photo3);
                                    contact.put("nominee", nominee);
                                    contact.put("relation", relation);
                                    contact.put("paid_to", paid_to);
                                    contact.put("health_state", health_state);
                                    contact.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);


                                    // adding contact to contact list
                                    positionList.add(contact);

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            SearchPerson.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(myResponse);
                                        final JSONArray data = jsonObject.getJSONArray("personal");
                                        for (int i = 0; i < data.length(); i++) {
                                            if (i == 0) ////FOR ANIMATING THE CAMERA FOCUS FIRST TIME ON THE GOOGLE MAP
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(7).build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Normal")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Economical")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Health")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Social")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                            }

                                            btnSearch.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String text = search.getText().toString();
                                                    for (int i = 0; i < data.length(); i++) {
                                                        if ((text.equalsIgnoreCase(positionList.get(i).get("name") + " " + positionList.get(i).get("surname"))) || text.equals(positionList.get(i).get("army_no"))) {
                                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(16).build();
                                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                        }
                                                    }
                                                    if (text.equalsIgnoreCase(Menu.textName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude))).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }
                                                    if (text.equals("")) {
                                                        Toast.makeText(SearchPerson.this, "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (text.equalsIgnoreCase(Menu.onlyName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lant, lont)).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }


                                                }
                                            });

                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                                // Use default InfoWindow frame
                                                @Override
                                                public View getInfoWindow(Marker args) {
                                                    return null;
                                                }

                                                // Defines the contents of the InfoWindow
                                                @Override
                                                public View getInfoContents(Marker args) {

                                                    // Getting view from the layout file info_window_layout
                                                    View v = getLayoutInflater().inflate(R.layout.info, null);

                                                    // Getting the position from the marker
                                                    LatLng click = args.getPosition();

                                                    TextView title = (TextView) v.findViewById(R.id.content);
                                                    title.setText(args.getTitle());

                                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                        @Override
                                                        public void onInfoWindowClick(Marker marker) {
                                                            int i = Integer.parseInt(marker.getSnippet());
                                                            if (marker.getTitle().equals((positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).toString())) {

                                                                Intent inte = new Intent(SearchPerson.this, marker_details.class);
                                                                inte.putExtra("longitude", positionList.get(i).get("longitude").toString());
                                                                inte.putExtra("latitude", positionList.get(i).get("latitude").toString());
                                                                inte.putExtra("name", positionList.get(i).get("name").toString());

                                                                inte.putExtra("relation", positionList.get(i).get("relation").toString());
                                                                inte.putExtra("army_no", positionList.get(i).get("army_no").toString());
                                                                inte.putExtra("rank", positionList.get(i).get("rank").toString());

                                                                inte.putExtra("surname", positionList.get(i).get("surname").toString());
                                                                inte.putExtra("subunit", positionList.get(i).get("subunit").toString());
                                                                inte.putExtra("unit", positionList.get(i).get("unit").toString());
                                                                inte.putExtra("army", positionList.get(i).get("army").toString());
                                                                inte.putExtra("age", positionList.get(i).get("age").toString());
                                                                inte.putExtra("dob", positionList.get(i).get("dob").toString());
                                                                inte.putExtra("doe", positionList.get(i).get("doe").toString());
                                                                inte.putExtra("dod", positionList.get(i).get("dod").toString());
                                                                inte.putExtra("dc", positionList.get(i).get("dc").toString());
                                                                inte.putExtra("retain", positionList.get(i).get("retain").toString());
                                                                inte.putExtra("payee", positionList.get(i).get("payee").toString());
                                                                inte.putExtra("dependent_name", positionList.get(i).get("dependent_name").toString());
                                                                inte.putExtra("dependent_age", positionList.get(i).get("dependent_age").toString());
                                                                inte.putExtra("dependent_dob", positionList.get(i).get("dependent_dob").toString());
                                                                inte.putExtra("photo1", positionList.get(i).get("photo1").toString());
                                                                inte.putExtra("photo2", positionList.get(i).get("photo2").toString());
                                                                inte.putExtra("photo3", positionList.get(i).get("photo3").toString());
                                                                inte.putExtra("nominee", positionList.get(i).get("nominee").toString());
                                                                inte.putExtra("paid_to", positionList.get(i).get("paid_to").toString());
                                                                inte.putExtra("health_state", positionList.get(i).get("health_state").toString());
                                                                inte.putExtra("type_of_welfare_pensioner", positionList.get(i).get("type_of_welfare_pensioner").toString());
                                                                inte.putExtra("village", positionList.get(i).get("village").toString());
                                                                inte.putExtra("district", positionList.get(i).get("district").toString());
                                                                inte.putExtra("vdc", positionList.get(i).get("vdc").toString());
                                                                inte.putExtra("ward_no", positionList.get(i).get("ward_no").toString());
                                                                inte.putExtra("po", positionList.get(i).get("po").toString());

                                                                startActivity(inte);
                                                            }

                                                        }

                                                    });
                                                    return v;
                                                }

                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                    });
                }
            }
    }

        if (fbSessionManager.getUserDetails() != null){
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            String fbId = fbUser.get(SessionManager.KEY_ID);

            if(fbId != null) {
                if (fbId.matches("1")) {

                    progressDialog = new ProgressDialog(SearchPerson.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(SearchPerson.this
                                    .getCacheDir(),
                                    "searchPersonApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(SearchPersonInterface.class).getResponse("gws/personal_detail/api/personal_detail/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(SearchPerson.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);


                                    lat = c.getString("latitude");
                                    lon = c.getString("longitude");
                                    String name = c.getString("name");

                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");

                                    String surname = c.getString("surname");
                                    String subunit = c.getString("subunit");
                                    String unit = c.getString("unit");
                                    String army = c.getString("army");
                                    String age = c.getString("age");
                                    String dob = c.getString("dob");
                                    String doe = c.getString("doe");
                                    String dod = c.getString("dod");
                                    String dc = c.getString("dc");
                                    String retain = c.getString("retain");
                                    String payee = c.getString("payee");
                                    String dependent_name = c.getString("dependent_name");

                                    String dependent_age = c.getString("dependent_age");
                                    String dependent_dob = c.getString("dependent_dob");
                                    String photo1 = c.getString("photo1");
                                    String photo2 = c.getString("photo2");
                                    String photo3 = c.getString("photo3");
                                    String nominee = c.getString("nominee");
                                    String relation = c.getString("relation");
                                    String paid_to = c.getString("paid_to");
                                    String health_state = c.getString("health_state");
                                    String type_of_welfare_pensioner = c.getString("type_of_welfare_pensioner");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("latitude", lat);
                                    contact.put("longitude", lon);
                                    contact.put("name", name);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("surname", surname);
                                    contact.put("subunit", subunit);
                                    contact.put("unit", unit);
                                    contact.put("army", army);
                                    contact.put("age", age);
                                    contact.put("dob", dob);
                                    contact.put("doe", doe);
                                    contact.put("dod", dod);
                                    contact.put("dc", dc);
                                    contact.put("retain", retain);
                                    contact.put("payee", payee);
                                    contact.put("dependent_name", dependent_name);
                                    contact.put("dependent_age", dependent_age);
                                    contact.put("dependent_dob", dependent_dob);
                                    contact.put("photo1", photo1);
                                    contact.put("photo2", photo2);
                                    contact.put("photo3", photo3);
                                    contact.put("nominee", nominee);
                                    contact.put("relation", relation);
                                    contact.put("paid_to", paid_to);
                                    contact.put("health_state", health_state);
                                    contact.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);

                                    // adding contact to contact list
                                    positionList.add(contact);

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            SearchPerson.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(myResponse);
                                        final JSONArray data = jsonObject.getJSONArray("personal");
                                        for (int i = 0; i < data.length(); i++) {
                                            if (i == 0) ////FOR ANIMATING THE CAMERA FOCUS FIRST TIME ON THE GOOGLE MAP
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(7).build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Normal")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Economical")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Health")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Social")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                            }

                                            btnSearch.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String text = search.getText().toString();
                                                    for (int i = 0; i < data.length(); i++) {
                                                        if ((text.equalsIgnoreCase(positionList.get(i).get("name") + " " + positionList.get(i).get("surname"))) || text.equals(positionList.get(i).get("army_no"))) {
                                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(16).build();
                                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                        }
                                                    }
                                                    if (text.equalsIgnoreCase(Menu.textName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude))).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }
                                                    if (text.equals("")) {
                                                        Toast.makeText(SearchPerson.this, "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (text.equalsIgnoreCase(Menu.onlyName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lant, lont)).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }


                                                }
                                            });

                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                                // Use default InfoWindow frame
                                                @Override
                                                public View getInfoWindow(Marker args) {
                                                    return null;
                                                }

                                                // Defines the contents of the InfoWindow
                                                @Override
                                                public View getInfoContents(Marker args) {

                                                    // Getting view from the layout file info_window_layout
                                                    View v = getLayoutInflater().inflate(R.layout.info, null);

                                                    // Getting the position from the marker
                                                    LatLng click = args.getPosition();

                                                    TextView title = (TextView) v.findViewById(R.id.content);
                                                    title.setText(args.getTitle());

                                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                        @Override
                                                        public void onInfoWindowClick(Marker marker) {
                                                            int i = Integer.parseInt(marker.getSnippet());
                                                            if (marker.getTitle().equals((positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).toString())) {

                                                                Intent inte = new Intent(SearchPerson.this, marker_details.class);
                                                                inte.putExtra("longitude", positionList.get(i).get("longitude").toString());
                                                                inte.putExtra("latitude", positionList.get(i).get("latitude").toString());
                                                                inte.putExtra("name", positionList.get(i).get("name").toString());

                                                                inte.putExtra("relation", positionList.get(i).get("relation").toString());
                                                                inte.putExtra("army_no", positionList.get(i).get("army_no").toString());
                                                                inte.putExtra("rank", positionList.get(i).get("rank").toString());

                                                                inte.putExtra("surname", positionList.get(i).get("surname").toString());
                                                                inte.putExtra("subunit", positionList.get(i).get("subunit").toString());
                                                                inte.putExtra("unit", positionList.get(i).get("unit").toString());
                                                                inte.putExtra("army", positionList.get(i).get("army").toString());
                                                                inte.putExtra("age", positionList.get(i).get("age").toString());
                                                                inte.putExtra("dob", positionList.get(i).get("dob").toString());
                                                                inte.putExtra("doe", positionList.get(i).get("doe").toString());
                                                                inte.putExtra("dod", positionList.get(i).get("dod").toString());
                                                                inte.putExtra("dc", positionList.get(i).get("dc").toString());
                                                                inte.putExtra("retain", positionList.get(i).get("retain").toString());
                                                                inte.putExtra("payee", positionList.get(i).get("payee").toString());
                                                                inte.putExtra("dependent_name", positionList.get(i).get("dependent_name").toString());
                                                                inte.putExtra("dependent_age", positionList.get(i).get("dependent_age").toString());
                                                                inte.putExtra("dependent_dob", positionList.get(i).get("dependent_dob").toString());
                                                                inte.putExtra("photo1", positionList.get(i).get("photo1").toString());
                                                                inte.putExtra("photo2", positionList.get(i).get("photo2").toString());
                                                                inte.putExtra("photo3", positionList.get(i).get("photo3").toString());
                                                                inte.putExtra("nominee", positionList.get(i).get("nominee").toString());
                                                                inte.putExtra("paid_to", positionList.get(i).get("paid_to").toString());
                                                                inte.putExtra("health_state", positionList.get(i).get("health_state").toString());
                                                                inte.putExtra("type_of_welfare_pensioner", positionList.get(i).get("type_of_welfare_pensioner").toString());
                                                                inte.putExtra("village", positionList.get(i).get("village").toString());
                                                                inte.putExtra("district", positionList.get(i).get("district").toString());
                                                                inte.putExtra("vdc", positionList.get(i).get("vdc").toString());
                                                                inte.putExtra("ward_no", positionList.get(i).get("ward_no").toString());
                                                                inte.putExtra("po", positionList.get(i).get("po").toString());

                                                                startActivity(inte);
                                                            }

                                                        }

                                                    });
                                                    return v;
                                                }

                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                    });
                } else {

                    progressDialog = new ProgressDialog(SearchPerson.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(SearchPerson.this
                                    .getCacheDir(),
                                    "userSearchPersonApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(SearchPersonInterface.class).getResponse("gws/personal_detail/api/personal_detail/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(SearchPerson.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);


                                    lat = c.getString("latitude");
                                    lon = c.getString("longitude");
                                    String name = c.getString("name");

                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");

                                    String surname = c.getString("surname");
                                    String subunit = c.getString("subunit");
                                    String unit = c.getString("unit");
                                    String army = c.getString("army");
                                    String age = c.getString("age");
                                    String dob = c.getString("dob");
                                    String doe = c.getString("doe");
                                    String dod = c.getString("dod");
                                    String dc = c.getString("dc");
                                    String retain = c.getString("retain");
                                    String payee = c.getString("payee");
                                    String dependent_name = c.getString("dependent_name");

                                    String dependent_age = c.getString("dependent_age");
                                    String dependent_dob = c.getString("dependent_dob");
                                    String photo1 = c.getString("photo1");
                                    String photo2 = c.getString("photo2");
                                    String photo3 = c.getString("photo3");
                                    String nominee = c.getString("nominee");
                                    String relation = c.getString("relation");
                                    String paid_to = c.getString("paid_to");
                                    String health_state = c.getString("health_state");
                                    String type_of_welfare_pensioner = c.getString("type_of_welfare_pensioner");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("latitude", lat);
                                    contact.put("longitude", lon);
                                    contact.put("name", name);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("surname", surname);
                                    contact.put("subunit", subunit);
                                    contact.put("unit", unit);
                                    contact.put("army", army);
                                    contact.put("age", age);
                                    contact.put("dob", dob);
                                    contact.put("doe", doe);
                                    contact.put("dod", dod);
                                    contact.put("dc", dc);
                                    contact.put("retain", retain);
                                    contact.put("payee", payee);
                                    contact.put("dependent_name", dependent_name);
                                    contact.put("dependent_age", dependent_age);
                                    contact.put("dependent_dob", dependent_dob);
                                    contact.put("photo1", photo1);
                                    contact.put("photo2", photo2);
                                    contact.put("photo3", photo3);
                                    contact.put("nominee", nominee);
                                    contact.put("relation", relation);
                                    contact.put("paid_to", paid_to);
                                    contact.put("health_state", health_state);
                                    contact.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);


                                    // adding contact to contact list
                                    positionList.add(contact);

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            SearchPerson.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(myResponse);
                                        final JSONArray data = jsonObject.getJSONArray("personal");
                                        for (int i = 0; i < data.length(); i++) {
                                            if (i == 0) ////FOR ANIMATING THE CAMERA FOCUS FIRST TIME ON THE GOOGLE MAP
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(7).build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Normal")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Economical")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Health")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("Social")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            }

                                            if ((positionList.get(i).get("background")).matches("")) {
                                                locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                            }

                                            btnSearch.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String text = search.getText().toString();
                                                    for (int i = 0; i < data.length(); i++) {
                                                        if ((text.equalsIgnoreCase(positionList.get(i).get("name") + " " + positionList.get(i).get("surname"))) || text.equals(positionList.get(i).get("army_no"))) {
                                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(16).build();
                                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                        }
                                                    }
                                                    if (text.equalsIgnoreCase(Menu.textName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude))).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }
                                                    if (text.equals("")) {
                                                        Toast.makeText(SearchPerson.this, "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (text.equalsIgnoreCase(Menu.onlyName)) {
                                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lant, lont)).zoom(14).build();
                                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    }


                                                }
                                            });

                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                                // Use default InfoWindow frame
                                                @Override
                                                public View getInfoWindow(Marker args) {
                                                    return null;
                                                }

                                                // Defines the contents of the InfoWindow
                                                @Override
                                                public View getInfoContents(Marker args) {

                                                    // Getting view from the layout file info_window_layout
                                                    View v = getLayoutInflater().inflate(R.layout.info, null);

                                                    // Getting the position from the marker
                                                    LatLng click = args.getPosition();

                                                    TextView title = (TextView) v.findViewById(R.id.content);
                                                    title.setText(args.getTitle());

                                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                        @Override
                                                        public void onInfoWindowClick(Marker marker) {
                                                            int i = Integer.parseInt(marker.getSnippet());
                                                            if (marker.getTitle().equals((positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).toString())) {

                                                                Intent inte = new Intent(SearchPerson.this, marker_details.class);
                                                                inte.putExtra("longitude", positionList.get(i).get("longitude").toString());
                                                                inte.putExtra("latitude", positionList.get(i).get("latitude").toString());
                                                                inte.putExtra("name", positionList.get(i).get("name").toString());

                                                                inte.putExtra("relation", positionList.get(i).get("relation").toString());
                                                                inte.putExtra("army_no", positionList.get(i).get("army_no").toString());
                                                                inte.putExtra("rank", positionList.get(i).get("rank").toString());

                                                                inte.putExtra("surname", positionList.get(i).get("surname").toString());
                                                                inte.putExtra("subunit", positionList.get(i).get("subunit").toString());
                                                                inte.putExtra("unit", positionList.get(i).get("unit").toString());
                                                                inte.putExtra("army", positionList.get(i).get("army").toString());
                                                                inte.putExtra("age", positionList.get(i).get("age").toString());
                                                                inte.putExtra("dob", positionList.get(i).get("dob").toString());
                                                                inte.putExtra("doe", positionList.get(i).get("doe").toString());
                                                                inte.putExtra("dod", positionList.get(i).get("dod").toString());
                                                                inte.putExtra("dc", positionList.get(i).get("dc").toString());
                                                                inte.putExtra("retain", positionList.get(i).get("retain").toString());
                                                                inte.putExtra("payee", positionList.get(i).get("payee").toString());
                                                                inte.putExtra("dependent_name", positionList.get(i).get("dependent_name").toString());
                                                                inte.putExtra("dependent_age", positionList.get(i).get("dependent_age").toString());
                                                                inte.putExtra("dependent_dob", positionList.get(i).get("dependent_dob").toString());
                                                                inte.putExtra("photo1", positionList.get(i).get("photo1").toString());
                                                                inte.putExtra("photo2", positionList.get(i).get("photo2").toString());
                                                                inte.putExtra("photo3", positionList.get(i).get("photo3").toString());
                                                                inte.putExtra("nominee", positionList.get(i).get("nominee").toString());
                                                                inte.putExtra("paid_to", positionList.get(i).get("paid_to").toString());
                                                                inte.putExtra("health_state", positionList.get(i).get("health_state").toString());
                                                                inte.putExtra("type_of_welfare_pensioner", positionList.get(i).get("type_of_welfare_pensioner").toString());
                                                                inte.putExtra("village", positionList.get(i).get("village").toString());
                                                                inte.putExtra("district", positionList.get(i).get("district").toString());
                                                                inte.putExtra("vdc", positionList.get(i).get("vdc").toString());
                                                                inte.putExtra("ward_no", positionList.get(i).get("ward_no").toString());
                                                                inte.putExtra("po", positionList.get(i).get("po").toString());

                                                                startActivity(inte);
                                                            }

                                                        }

                                                    });
                                                    return v;
                                                }

                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                    });
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


        if ((Menu.numlatitude != null) && (Menu.numlongitude != null)) {
            LatLng mark = new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude));
            addedmarker = (mMap.addMarker(new MarkerOptions()
                    .title(Menu.textName)
                    .position(mark)));
            addedmarker.showInfoWindow();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals(Menu.textName)) {
                        Intent ints = new Intent(SearchPerson.this, Addeddesc.class);
                        ints.putExtra("name", Menu.textName);
                        ints.putExtra("age", Menu.numAge);
                        ints.putExtra("address", Menu.textAddress);
                        ints.putExtra("latitude", Menu.numlatitude);
                        ints.putExtra("longitude", Menu.numlongitude);
                        startActivity(ints);
                        return true;
                    }
                    if (marker.getTitle().equals("you")) {
                        currentmarker.hideInfoWindow();
                        return true;
                    }

                    return false;

                }
            });

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude)), 16));
        }
        if (Menu.marker == true) {
            LatLng current = new LatLng(lant, lont);
            currentmarker = (mMap.addMarker(new MarkerOptions()
                    .title("You")
                    .position(current)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lant, lont), 16));
            currentmarker.showInfoWindow();

        }


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


    }

    private class GetPosition extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(SearchPerson.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray data = jsonObj.getJSONArray("personal");
                    Log.e("total number:", String.valueOf(size));
                    size = data.length();
                    // looping through All data
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);


                        lat = c.getString("latitude");
                        lon = c.getString("longitude");
                        String name = c.getString("name");

                        String army_no = c.getString("army_no");
                        String rank = c.getString("rank");

                        String surname = c.getString("surname");
                        String subunit = c.getString("subunit");
                        String unit = c.getString("unit");
                        String army = c.getString("army");
                        String age = c.getString("age");
                        String dob = c.getString("dob");
                        String doe = c.getString("doe");
                        String dod = c.getString("dod");
                        String dc = c.getString("dc");
                        String retain = c.getString("retain");
                        String payee = c.getString("payee");
                        String dependent_name = c.getString("dependent_name");

                        String dependent_age = c.getString("dependent_age");
                        String dependent_dob = c.getString("dependent_dob");
                        String photo1 = c.getString("photo1");
                        String photo2 = c.getString("photo2");
                        String photo3 = c.getString("photo3");
                        String nominee = c.getString("nominee");
                        String relation = c.getString("relation");
                        String paid_to = c.getString("paid_to");
                        String health_state = c.getString("health_state");
                        String type_of_welfare_pensioner = c.getString("type_of_welfare_pensioner");
                        String village = c.getString("village");
                        String district = c.getString("district");
                        String vdc = c.getString("vdc");
                        String ward_no = c.getString("ward_no");
                        String po = c.getString("po");
                        String background = c.getString("background");


                        // tmp hash map for single data
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value

                        contact.put("latitude", lat);
                        contact.put("longitude", lon);
                        contact.put("name", name);
                        contact.put("army_no", army_no);
                        contact.put("rank", rank);
                        contact.put("surname", surname);
                        contact.put("subunit", subunit);
                        contact.put("unit", unit);
                        contact.put("army", army);
                        contact.put("age", age);
                        contact.put("dob", dob);
                        contact.put("doe", doe);
                        contact.put("dod", dod);
                        contact.put("dc", dc);
                        contact.put("retain", retain);
                        contact.put("payee", payee);
                        contact.put("dependent_name", dependent_name);
                        contact.put("dependent_age", dependent_age);
                        contact.put("dependent_dob", dependent_dob);
                        contact.put("photo1", photo1);
                        contact.put("photo2", photo2);
                        contact.put("photo3", photo3);
                        contact.put("nominee", nominee);
                        contact.put("relation", relation);
                        contact.put("paid_to", paid_to);
                        contact.put("health_state", health_state);
                        contact.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                        contact.put("village", village);
                        contact.put("district", district);
                        contact.put("vdc", vdc);
                        contact.put("ward_no", ward_no);
                        contact.put("po", po);
                        contact.put("background", background);


                        // adding contact to contact list
                        positionList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Connection Timeout! Please check internet connection",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            for (int i = 0; i < size; i++) {
                if (i == 0) ////FOR ANIMATING THE CAMERA FOCUS FIRST TIME ON THE GOOGLE MAP
                {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(7).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if ((positionList.get(i).get("background")).matches("Normal")) {
                    locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }

                if ((positionList.get(i).get("background")).matches("Economical")) {
                    locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }

                if ((positionList.get(i).get("background")).matches("Health")) {
                    locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i)));
                }

                if ((positionList.get(i).get("background")).matches("Social")) {
                    locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }

                if ((positionList.get(i).get("background")).matches("")) {
                    locationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).title(positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).snippet(Integer.toString(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }

                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = search.getText().toString();
                        for (int i = 0; i < size; i++) {
                            if (text.equalsIgnoreCase(positionList.get(i).get("name") + " " + positionList.get(i).get("surname"))) {
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(positionList.get(i).get("latitude")), Double.parseDouble(positionList.get(i).get("longitude")))).zoom(14).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                        if (text.equalsIgnoreCase(Menu.textName)) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Float.parseFloat(Menu.numlatitude), Float.parseFloat(Menu.numlongitude))).zoom(14).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        if (text.equals("")) {
                            Toast.makeText(SearchPerson.this, "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                        }

                        if (text.equalsIgnoreCase(Menu.onlyName)) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lant, lont)).zoom(14).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }


                    }
                });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker args) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker args) {

                        // Getting view from the layout file info_window_layout
                        View v = getLayoutInflater().inflate(R.layout.info, null);

                        // Getting the position from the marker
                        LatLng click = args.getPosition();

                        TextView title = (TextView) v.findViewById(R.id.content);
                        title.setText(args.getTitle());

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                int i = Integer.parseInt(marker.getSnippet());
                                if (marker.getTitle().equals((positionList.get(i).get("name") + " " + positionList.get(i).get("surname")).toString())) {

                                    Intent inte = new Intent(SearchPerson.this, marker_details.class);
                                    inte.putExtra("longitude", positionList.get(i).get("longitude").toString());
                                    inte.putExtra("latitude", positionList.get(i).get("latitude").toString());
                                    inte.putExtra("name", positionList.get(i).get("name").toString());

                                    inte.putExtra("relation", positionList.get(i).get("relation").toString());
                                    inte.putExtra("army_no", positionList.get(i).get("army_no").toString());
                                    inte.putExtra("rank", positionList.get(i).get("rank").toString());

                                    inte.putExtra("surname", positionList.get(i).get("surname").toString());
                                    inte.putExtra("subunit", positionList.get(i).get("subunit").toString());
                                    inte.putExtra("unit", positionList.get(i).get("unit").toString());
                                    inte.putExtra("army", positionList.get(i).get("army").toString());
                                    inte.putExtra("age", positionList.get(i).get("age").toString());
                                    inte.putExtra("dob", positionList.get(i).get("dob").toString());
                                    inte.putExtra("doe", positionList.get(i).get("doe").toString());
                                    inte.putExtra("dod", positionList.get(i).get("dod").toString());
                                    inte.putExtra("dc", positionList.get(i).get("dc").toString());
                                    inte.putExtra("retain", positionList.get(i).get("retain").toString());
                                    inte.putExtra("payee", positionList.get(i).get("payee").toString());
                                    inte.putExtra("dependent_name", positionList.get(i).get("dependent_name").toString());
                                    inte.putExtra("dependent_age", positionList.get(i).get("dependent_age").toString());
                                    inte.putExtra("dependent_dob", positionList.get(i).get("dependent_dob").toString());
                                    inte.putExtra("photo1", positionList.get(i).get("photo1").toString());
                                    inte.putExtra("photo2", positionList.get(i).get("photo2").toString());
                                    inte.putExtra("photo3", positionList.get(i).get("photo3").toString());
                                    inte.putExtra("nominee", positionList.get(i).get("nominee").toString());
                                    inte.putExtra("paid_to", positionList.get(i).get("paid_to").toString());
                                    inte.putExtra("health_state", positionList.get(i).get("health_state").toString());
                                    inte.putExtra("type_of_welfare_pensioner", positionList.get(i).get("type_of_welfare_pensioner").toString());
                                    inte.putExtra("village", positionList.get(i).get("village").toString());
                                    inte.putExtra("district", positionList.get(i).get("district").toString());
                                    inte.putExtra("vdc", positionList.get(i).get("vdc").toString());
                                    inte.putExtra("ward_no", positionList.get(i).get("ward_no").toString());
                                    inte.putExtra("po", positionList.get(i).get("po").toString());

                                    startActivity(inte);
                                }

                            }

                        });
                        return v;
                    }

                });

            }


        }

    }

}
