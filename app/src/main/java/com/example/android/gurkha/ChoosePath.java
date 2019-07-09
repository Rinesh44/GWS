package com.example.android.gurkha;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

public class ChoosePath extends AppCompatActivity {
    private String TAG = ChoosePath.class.getSimpleName();

    TextView title;
    Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    Typeface face;
    private EditText search;
    ArrayList<HashMap<String, Object>> pathlist;
    private static final int REQUEST_PERMISSIONS = 1246;
    private static String base_url = "http://gws.pagodalabs.com.np/";
    // URL to get peoplelist JSON
    private static String url = "http://gws.pagodalabs.com.np/track/api/track?api_token=";
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    static JSONArray images;
    static String name;
    String token;
    SessionManager sessionManager;
    String userId;
    JSONArray imagePathArray;
    private TextView mEmptyStateTextView;
    Call<ResponseBody> call;
    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_path);
        if (Build.VERSION.SDK_INT > 22) {
            ActivityCompat.requestPermissions(ChoosePath.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);
        }
        pathlist = new ArrayList<>();


        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        tv.setTypeface(face);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        sessionManager = new SessionManager(getApplicationContext());

        adapter = new SimpleAdapter(ChoosePath.this, pathlist, R.layout.list_item, new String[]{"name"}, new int[]{R.id.name}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView surname = (TextView) view.findViewById(R.id.surname);
                TextView army_no = (TextView) view.findViewById(R.id.army_no);
                TextView id = (TextView) view.findViewById(R.id.id);
                name.setTypeface(face);
                surname.setTypeface(face);
                army_no.setTypeface(face);
                id.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(ChoosePath.this, (position > lastPosition) ? R.anim.item_animation_fall_down : R.anim.item_animation_fall_down);
                view.startAnimation(animation);
                lastPosition = position;

                return view;
            }
        };
        adapter.notifyDataSetChanged();

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        listView = (ListView) findViewById(R.id.list_people);
        search = (EditText) findViewById(R.id.inputSearch);
        search.setTypeface(face);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);


        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            //new GetPath().execute();
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        */

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.load_all_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all:
                try {
                    getAllRoutes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.action_my_routes:
                try {
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        // case blocks for other MenuItems (if any)
        return false;
    }

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     * <p>
     * If the device is offline, stale (at most 2 weeks)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(ChoosePath.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)//  2 weeeks
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        pathlist.clear();
        progressDialog = new ProgressDialog(ChoosePath.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        timerDelayRemoveDialog(7000, progressDialog);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new ResponseCacheInterceptor())
                .addInterceptor(new OfflineResponseCacheInterceptor())
                // Set the cache location and size (5 MB)
                .cache(new Cache(new File(ChoosePath.this
                        .getCacheDir(),
                        "choosePathApiResponses"), 5 * 1024 * 1024))
                .build();

        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);
        userId = user.get(SessionManager.KEY_ID);
        Log.e("UserId:", userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ChoosePathInterface.class).getResponse("track/api/track?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("ChoosePath", t.toString());
                Toast.makeText(ChoosePath.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                mEmptyStateTextView.setText(R.string.no_data);
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(ChoosePath.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    final String myResponse = response.body().string();
                    Log.e("ResponsePath:", myResponse);
                    if (myResponse == null) {
                        Toast.makeText(ChoosePath.this, "Cache not found. Please connect to internet and try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("Info");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);

                        String name = c.getString("name");
                        String gps_id = c.getString("gps_id");
                        JSONArray marker_id = c.getJSONArray("marker_id");
                        images = c.getJSONArray("encoded_images");
                        JSONArray marker = c.getJSONArray("marker");
                        Log.e("markerExtrated:", marker.toString());
                        JSONArray polyline = c.getJSONArray("polyline");
                        JSONArray notes = c.getJSONArray("notes");

                        // tmp hash map for single data
                        HashMap<String, Object> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("marker", marker);
                        contact.put("polyline", polyline);
                        contact.put("marker_id", marker_id);
                        contact.put("notes", notes);
                        contact.put("images", images);
                        contact.put("gps_id", gps_id);

                        pathlist.add(contact);

                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();


                ChoosePath.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        mEmptyStateTextView.setText(R.string.no_data);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);
                                JSONArray marker = (JSONArray) obj.get("marker");
                                JSONArray polyline = (JSONArray) obj.get("polyline");
                                JSONArray notes = (JSONArray) obj.get("notes");
                                name = (String) obj.get("name");
                                String gps_id = (String) obj.get("gps_id");
                                JSONArray marker_id = (JSONArray) obj.get("marker_id");
                                JSONArray images = (JSONArray) obj.get("images");
                                imagePathArray = new JSONArray();
                                try {
                                    for (int j = 0; j < images.length(); j++) {
                                        String base64 = images.get(j).toString();
                                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                                        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        //File folderPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWS/" + "MarkerImages/" + name);
                                        File image = new File(appFolderCheckandCreate(), "img" + getTimeStamp() + ".jpg");
                                        if (image.exists())
                                            image.delete();
                                        String imagePath = image.getAbsolutePath();
                                        imagePathArray.put(imagePath);
                                        try {
                                            FileOutputStream out = new FileOutputStream(image);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                                            out.flush();
                                            out.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        image.deleteOnExit();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent parse = new Intent(ChoosePath.this, Loader.class);
                                parse.putExtra("marker", marker.toString());
                                parse.putExtra("polyline", polyline.toString());
                                parse.putExtra("notes", notes.toString());
                                parse.putExtra("name", name);
                                parse.putExtra("marker_id", marker_id.toString());
                                parse.putExtra("image_path", imagePathArray.toString());
                                parse.putExtra("gps_id", gps_id);
                                startActivity(parse);
                            }
                        });

                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                ChoosePath.this.adapter.getFilter().filter(cs);
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                          int arg3) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }

                });
            }
        });
    }

    void getAllRoutes() throws IOException {
        pathlist.clear();
        progressDialog = new ProgressDialog(ChoosePath.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        timerDelayRemoveDialog(7000, progressDialog);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new ResponseCacheInterceptor())
                .addInterceptor(new OfflineResponseCacheInterceptor())
                // Set the cache location and size (5 MB)
                .cache(new Cache(new File(ChoosePath.this
                        .getCacheDir(),
                        "choosePathApiResponses"), 5 * 1024 * 1024))
                .build();

        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);
        userId = user.get(SessionManager.KEY_ID);
        Log.e("UserId:", userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ChoosePathInterface.class).getResponse("track/api/getAll/" + userId + "?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("ChoosePath", t.toString());
                Toast.makeText(ChoosePath.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                mEmptyStateTextView.setText(R.string.no_data);
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(ChoosePath.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    final String myResponse = response.body().string();
                    Log.e("ResponsePath:", myResponse);
                    if (myResponse == null) {
                        Toast.makeText(ChoosePath.this, "Cache not found. Please connect to internet and try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("Info");
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject c = data.getJSONObject(i);

                        String name = c.getString("name");
                        String gps_id = c.getString("gps_id");
                        JSONArray marker_id = c.getJSONArray("marker_id");
                        images = c.getJSONArray("encoded_images");
                        JSONArray marker = c.getJSONArray("marker");
                        Log.e("markerExtrated:", marker.toString());
                        JSONArray polyline = c.getJSONArray("polyline");
                        JSONArray notes = c.getJSONArray("notes");

                        // tmp hash map for single data
                        HashMap<String, Object> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("marker", marker);
                        contact.put("polyline", polyline);
                        contact.put("marker_id", marker_id);
                        contact.put("notes", notes);
                        contact.put("images", images);
                        contact.put("gps_id", gps_id);

                        pathlist.add(contact);

                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();


                ChoosePath.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        mEmptyStateTextView.setText(R.string.no_data);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);
                                JSONArray marker = (JSONArray) obj.get("marker");
                                JSONArray polyline = (JSONArray) obj.get("polyline");
                                JSONArray notes = (JSONArray) obj.get("notes");
                                name = (String) obj.get("name");
                                String gps_id = (String) obj.get("gps_id");
                                JSONArray marker_id = (JSONArray) obj.get("marker_id");
                                JSONArray images = (JSONArray) obj.get("images");
                                imagePathArray = new JSONArray();
                                try {
                                    for (int j = 0; j < images.length(); j++) {
                                        String base64 = images.get(j).toString();
                                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                                        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        //File folderPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWS/" + "MarkerImages/" + name);
                                        File image = new File(appFolderCheckandCreate(), "img" + getTimeStamp() + ".jpg");
                                        if (image.exists())
                                            image.delete();
                                        String imagePath = image.getAbsolutePath();
                                        imagePathArray.put(imagePath);
                                        try {
                                            FileOutputStream out = new FileOutputStream(image);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                                            out.flush();
                                            out.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        image.deleteOnExit();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent parse = new Intent(ChoosePath.this, Loader.class);
                                parse.putExtra("marker", marker.toString());
                                parse.putExtra("polyline", polyline.toString());
                                parse.putExtra("notes", notes.toString());
                                parse.putExtra("name", name);
                                parse.putExtra("marker_id", marker_id.toString());
                                parse.putExtra("image_path", imagePathArray.toString());
                                parse.putExtra("gps_id", gps_id);
                                startActivity(parse);
                            }
                        });

                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                ChoosePath.this.adapter.getFilter().filter(cs);
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                          int arg3) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }

                });
            }
        });
    }


    private String appFolderCheckandCreate() {

        String appFolderPath = "";
        File externalStorage = Environment.getExternalStorageDirectory();

        if (externalStorage.canWrite()) {
            appFolderPath = externalStorage.getAbsolutePath() + "/GWS/" + "MarkerImages/" + name;
            File dir = new File(appFolderPath);

            if (!dir.exists()) {
                dir.mkdirs();
            } else
                dir.delete();

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

    public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

}




