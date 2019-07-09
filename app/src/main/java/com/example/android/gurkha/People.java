package com.example.android.gurkha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.facebook.appevents.internal.AppEventUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class People extends AppCompatActivity implements ResponseListener {
    private String TAG = People.class.getSimpleName();
    private TextView title;
    private Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    private EditText search;
    private TextView mEmptyStateTextView;
    String token, fbToken;
    // URL to get peoplelist JSON
    private static String base_url = "http://gws.pagodalabs.com.np/";
    private static String url = "http://gws.pagodalabs.com.np/personal_detail/api/personal_detail?api_token=";
    private static String urlDelete = "http://gws.pagodalabs.com.np/personal_detail/api/deletePersonalDetails?api_token=";
    ArrayList<HashMap<String, String>> peoplelist;
    Call<ResponseBody> call;
    Typeface face;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    private int lastPosition = -1;
    MaterialProgressBar progressBar;
    JobManager mJobManager;
    private boolean searchByAwc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        peoplelist = new ArrayList<>();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        tv.setTypeface(face);

        checkIfSearchByAwc();


        toolbar = (Toolbar) findViewById(R.id.app_bar);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);
        mJobManager = GurkhaApplication.getInstance().getJobManager();

        adapter = new SimpleAdapter(People.this, peoplelist,
                R.layout.list_item, new String[]{"name", "surname", "army_no"}, new int[]{R.id.name, R.id.surname, R.id.army_no}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView surname = (TextView) view.findViewById(R.id.surname);
                TextView army_no = (TextView) view.findViewById(R.id.army_no);
                name.setTypeface(face);
                surname.setTypeface(face);
                army_no.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(People.this, (position > lastPosition) ? R.anim.item_animation_fall_down : R.anim.item_animation_fall_down);
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

      /*  if (InternetConnection.checkConnection(getApplicationContext())) {
            //new People.GetNames().execute();
            try {

                run();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            finish();
        }*/

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(People.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(People.this);
                }
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    HashMap<String, String> obj = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                                    String id = obj.get("personal_id");
                                    deleteItem(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });

    }

    private void checkIfSearchByAwc() {
        Intent i = getIntent();
        searchByAwc = i.getBooleanExtra("search_by_awc", false);
    }

    private void deleteItem(String id) {
        progressBar.setVisibility(View.VISIBLE);
        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                token = fbUser.get(SessionManager.KEY_TOKEN);
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("personal_id", id);
        params.put("api_token", token);

        Log.e(TAG, "ID:" + id + "token:" + token);

        JSONObject parameter = new JSONObject(params);

        mJobManager.addJobInBackground(new PostJob(urlDelete, parameter.toString(), People.this));
    }

    @Override
    public void responseSuccess(okhttp3.Response response) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                try {
//
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.INVISIBLE);
                    String responseString = response.body().string();
                    JSONObject responseObj = new JSONObject(responseString);
                    boolean responseBoolean = responseObj.getBoolean("success");
                    String msg = responseObj.getString("msg");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(People.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void responseFail(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                Toast.makeText(People.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
            if (!InternetConnection.checkConnection(People.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)//2week
                        .build();
            }
            return chain.proceed(request);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    void run() throws IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        final String awc = preferences.getString("awc", null);
        String awc = SelectAwc.awc;
        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            String id = user.get(SessionManager.KEY_ID);
            token = user.get(SessionManager.KEY_TOKEN);

            if (id != null) {
                if (!searchByAwc) {
                    progressDialog = new ProgressDialog(People.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(People.this
                                    .getCacheDir(),
                                    "apiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(ApiInterface.class).getResponse("personal_detail/api/personal_detail?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                Log.e(TAG, "responseCode:" + String.valueOf(response.code()));
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(People.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }

                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    String personal_id = c.getString("personal_id");
                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");
                                    String name = c.getString("name");
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
                                    String longitude = c.getString("longitude");
                                    String latitude = c.getString("latitude");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");
                                    String child_name = c.getString("child_name");
                                    String child_dob = c.getString("child_dob");
                                    String child_age = c.getString("child_age");

                                    // tmp hash map for single data
                                    HashMap<String, String> details = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    details.put("personal_id", personal_id);
                                    details.put("army_no", army_no);
                                    details.put("rank", rank);
                                    details.put("name", name);
                                    details.put("surname", surname);
                                    details.put("subunit", subunit);
                                    details.put("unit", unit);
                                    details.put("army", army);
                                    details.put("age", age);
                                    details.put("dob", dob);
                                    details.put("doe", doe);
                                    details.put("dod", dod);
                                    details.put("dc", dc);
                                    details.put("retain", retain);
                                    details.put("payee", payee);
                                    details.put("dependent_name", dependent_name);
                                    details.put("dependent_age", dependent_age);
                                    details.put("dependent_dob", dependent_dob);
                                    details.put("photo1", photo1);
                                    details.put("photo2", photo2);
                                    details.put("photo3", photo3);
                                    details.put("nominee", nominee);
                                    details.put("relation", relation);
                                    details.put("paid_to", paid_to);
                                    details.put("health_state", health_state);
                                    details.put("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                    details.put("longitude", longitude);
                                    details.put("latitude", latitude);
                                    details.put("village", village);
                                    details.put("district", district);
                                    details.put("vdc", vdc);
                                    details.put("ward_no", ward_no);
                                    details.put("po", po);
                                    details.put("background", background);
                                    details.put("child_name", child_name);
                                    details.put("child_dob", child_dob);
                                    details.put("child_age", child_age);


                                    // adding contact to contact list
                                    peoplelist.add(details);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            call.cancel();

                            People.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    mEmptyStateTextView.setText(R.string.no_data);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            HashMap<String, Object> detailsHash = (HashMap<String, Object>) adapter.getItem(position);

                                            String personal_id = (String) detailsHash.get("personal_id");
                                            String army_no = (String) detailsHash.get("army_no");
                                            String rank = (String) detailsHash.get("rank");
                                            String name = (String) detailsHash.get("name");
                                            String surname = (String) detailsHash.get("surname");
                                            String subunit = (String) detailsHash.get("subunit");
                                            String unit = (String) detailsHash.get("unit");
                                            String army = (String) detailsHash.get("army");
                                            String age = (String) detailsHash.get("age");
                                            String dob = (String) detailsHash.get("dob");
                                            String doe = (String) detailsHash.get("doe");
                                            String dod = (String) detailsHash.get("dod");
                                            String dc = (String) detailsHash.get("dc");
                                            String retain = (String) detailsHash.get("retain");
                                            String payee = (String) detailsHash.get("payee");
                                            String dependent_name = (String) detailsHash.get("dependent_name");
                                            String dependent_age = (String) detailsHash.get("dependent_age");
                                            String dependent_dob = (String) detailsHash.get("dependent_dob");
                                            String photo1 = (String) detailsHash.get("photo1");
                                            String photo2 = (String) detailsHash.get("photo2");
                                            String photo3 = (String) detailsHash.get("photo3");
                                            String nominee = (String) detailsHash.get("nominee");
                                            String relation = (String) detailsHash.get("relation");
                                            String paid_to = (String) detailsHash.get("paid_to");
                                            String health_state = (String) detailsHash.get("health_state");
                                            String type_of_welfare_pensioner = (String) detailsHash.get("type_of_welfare_pensioner");
                                            String longitude = (String) detailsHash.get("longitude");
                                            String latitude = (String) detailsHash.get("latitude");
                                            String village = (String) detailsHash.get("village");
                                            String district = (String) detailsHash.get("district");
                                            String vdc = (String) detailsHash.get("vdc");
                                            String ward_no = (String) detailsHash.get("ward_no");
                                            String po = (String) detailsHash.get("po");
                                            String background = (String) detailsHash.get("background");
                                            String child_age = (String) detailsHash.get("child_age");
                                            String child_name = (String) detailsHash.get("child_name");
                                            String child_dob = (String) detailsHash.get("child_dob");


                                            Intent in = new Intent(People.this, Person_Details.class);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("army_no", army_no);
                                            in.putExtra("rank", rank);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("subunit", subunit);
                                            in.putExtra("unit", unit);
                                            in.putExtra("army", army);
                                            in.putExtra("age", age);
                                            in.putExtra("dob", dob);
                                            in.putExtra("doe", doe);
                                            in.putExtra("dod", dod);
                                            in.putExtra("dc", dc);
                                            in.putExtra("retain", retain);
                                            in.putExtra("payee", payee);
                                            in.putExtra("dependent_name", dependent_name);
                                            in.putExtra("dependent_age", dependent_age);
                                            in.putExtra("dependent_dob", dependent_dob);
                                            in.putExtra("photo1", photo1);
                                            in.putExtra("photo2", photo2);
                                            in.putExtra("photo3", photo3);
                                            in.putExtra("nominee", nominee);
                                            in.putExtra("relation", relation);
                                            in.putExtra("paid_to", paid_to);
                                            in.putExtra("health_state", health_state);
                                            in.putExtra("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                            in.putExtra("longitude", longitude);
                                            in.putExtra("latitude", latitude);
                                            in.putExtra("village", village);
                                            in.putExtra("district", district);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("po", po);
                                            in.putExtra("background", background);
                                            in.putExtra("child_age", child_age);
                                            in.putExtra("child_name", child_name);
                                            in.putExtra("child_dob", child_dob);


                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            People.this.adapter.getFilter().filter(cs);
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

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("MainActivity", t.toString());
                            Toast.makeText(People.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }
                    });
                } else {
                    Log.d(TAG, "run: id not 1");
                    progressDialog = new ProgressDialog(People.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(People.this
                                    .getCacheDir(),
                                    "userApiResponses"), 1024 * 1024))
                            .build();

                    Log.d(TAG, "run: no internet");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    call = retrofit.create(ApiInterface.class).getResponse("personal_detail/api/personal_detail/" + awc + "?api_token=" + token);


                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(People.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    String personal_id = c.getString("personal_id");
                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");
                                    String name = c.getString("name");
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
                                    String longitude = c.getString("longitude");
                                    String latitude = c.getString("latitude");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");
                                    String child_name = c.getString("child_name");
                                    String child_dob = c.getString("child_dob");
                                    String child_age = c.getString("child_age");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("personal_id", personal_id);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("name", name);
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
                                    contact.put("longitude", longitude);
                                    contact.put("latitude", latitude);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);
                                    contact.put("child_name", child_name);
                                    contact.put("child_dob", child_dob);
                                    contact.put("child_age", child_age);

                                    // adding contact to contact list
                                    peoplelist.add(contact);
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            call.cancel();

                            People.this.runOnUiThread(new Runnable() {
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

                                            String personal_id = (String) obj.get("personal_id");
                                            String army_no = (String) obj.get("army_no");
                                            String rank = (String) obj.get("rank");
                                            String name = (String) obj.get("name");
                                            String surname = (String) obj.get("surname");
                                            String subunit = (String) obj.get("subunit");
                                            String unit = (String) obj.get("unit");
                                            String army = (String) obj.get("army");
                                            String age = (String) obj.get("age");
                                            String dob = (String) obj.get("dob");
                                            String doe = (String) obj.get("doe");
                                            String dod = (String) obj.get("dod");
                                            String dc = (String) obj.get("dc");
                                            String retain = (String) obj.get("retain");
                                            String payee = (String) obj.get("payee");
                                            String dependent_name = (String) obj.get("dependent_name");
                                            String dependent_age = (String) obj.get("dependent_age");
                                            String dependent_dob = (String) obj.get("dependent_dob");
                                            String photo1 = (String) obj.get("photo1");
                                            String photo2 = (String) obj.get("photo2");
                                            String photo3 = (String) obj.get("photo3");
                                            String nominee = (String) obj.get("nominee");
                                            String relation = (String) obj.get("relation");
                                            String paid_to = (String) obj.get("paid_to");
                                            String health_state = (String) obj.get("health_state");
                                            String type_of_welfare_pensioner = (String) obj.get("type_of_welfare_pensioner");
                                            String longitude = (String) obj.get("longitude");
                                            String latitude = (String) obj.get("latitude");
                                            String village = (String) obj.get("village");
                                            String district = (String) obj.get("district");
                                            String vdc = (String) obj.get("vdc");
                                            String ward_no = (String) obj.get("ward_no");
                                            String po = (String) obj.get("po");
                                            String background = (String) obj.get("background");
                                            String child_name = (String) obj.get("child_name");
                                            String child_dob = (String) obj.get("child_dob");
                                            String child_age = (String) obj.get("child_age");


                                            Intent in = new Intent(People.this, Person_Details.class);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("army_no", army_no);
                                            in.putExtra("rank", rank);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("subunit", subunit);
                                            in.putExtra("unit", unit);
                                            in.putExtra("army", army);
                                            in.putExtra("age", age);
                                            in.putExtra("dob", dob);
                                            in.putExtra("doe", doe);
                                            in.putExtra("dod", dod);
                                            in.putExtra("dc", dc);
                                            in.putExtra("retain", retain);
                                            in.putExtra("payee", payee);
                                            in.putExtra("dependent_name", dependent_name);
                                            in.putExtra("dependent_age", dependent_age);
                                            in.putExtra("dependent_dob", dependent_dob);
                                            in.putExtra("photo1", photo1);
                                            in.putExtra("photo2", photo2);
                                            in.putExtra("photo3", photo3);
                                            in.putExtra("nominee", nominee);
                                            in.putExtra("relation", relation);
                                            in.putExtra("paid_to", paid_to);
                                            in.putExtra("health_state", health_state);
                                            in.putExtra("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                            in.putExtra("longitude", longitude);
                                            in.putExtra("latitude", latitude);
                                            in.putExtra("village", village);
                                            in.putExtra("district", district);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("po", po);
                                            in.putExtra("background", background);
                                            in.putExtra("child_name", child_name);
                                            in.putExtra("child_dob", child_dob);
                                            in.putExtra("child_age", child_age);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            People.this.adapter.getFilter().filter(cs);
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

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("MainActivity", t.toString());
                            Toast.makeText(People.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }
                    });
                }
            }
        }

    /*    if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            String fbId = fbUser.get(FbSessionManager.KEY_ID);

            HashMap<String, String> user = sessionManager.getUserDetails();
            fbToken = fbUser.get(FbSessionManager.KEY_TOKEN);
            //token = user.get(SessionManager.KEY_TOKEN);

            if (fbId != null) {
                if (fbId.matches("1")) {
                    progressDialog = new ProgressDialog(People.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(People.this
                                    .getCacheDir(),
                                    "apiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail?api_token=" + fbToken);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(People.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    String personal_id = c.getString("personal_id");
                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");
                                    String name = c.getString("name");
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
                                    String longitude = c.getString("longitude");
                                    String latitude = c.getString("latitude");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");
                                    String child_name = c.getString("child_name");
                                    String child_dob = c.getString("child_dob");
                                    String child_age = c.getString("child_age");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("personal_id", personal_id);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("name", name);
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
                                    contact.put("longitude", longitude);
                                    contact.put("latitude", latitude);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);
                                    contact.put("child_name", child_name);
                                    contact.put("child_dob", child_dob);
                                    contact.put("child_age", child_age);


                                    // adding contact to contact list
                                    peoplelist.add(contact);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            call.cancel();

                            People.this.runOnUiThread(new Runnable() {
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

                                            String personal_id = (String) obj.get("personal_id");
                                            String army_no = (String) obj.get("army_no");
                                            String rank = (String) obj.get("rank");
                                            String name = (String) obj.get("name");
                                            String surname = (String) obj.get("surname");
                                            String subunit = (String) obj.get("subunit");
                                            String unit = (String) obj.get("unit");
                                            String army = (String) obj.get("army");
                                            String age = (String) obj.get("age");
                                            String dob = (String) obj.get("dob");
                                            String doe = (String) obj.get("doe");
                                            String dod = (String) obj.get("dod");
                                            String dc = (String) obj.get("dc");
                                            String retain = (String) obj.get("retain");
                                            String payee = (String) obj.get("payee");
                                            String dependent_name = (String) obj.get("dependent_name");
                                            String dependent_age = (String) obj.get("dependent_age");
                                            String dependent_dob = (String) obj.get("dependent_dob");
                                            String photo1 = (String) obj.get("photo1");
                                            String photo2 = (String) obj.get("photo2");
                                            String photo3 = (String) obj.get("photo3");
                                            String nominee = (String) obj.get("nominee");
                                            String relation = (String) obj.get("relation");
                                            String paid_to = (String) obj.get("paid_to");
                                            String health_state = (String) obj.get("health_state");
                                            String type_of_welfare_pensioner = (String) obj.get("type_of_welfare_pensioner");
                                            String longitude = (String) obj.get("longitude");
                                            String latitude = (String) obj.get("latitude");
                                            String village = (String) obj.get("village");
                                            String district = (String) obj.get("district");
                                            String vdc = (String) obj.get("vdc");
                                            String ward_no = (String) obj.get("ward_no");
                                            String po = (String) obj.get("po");
                                            String background = (String) obj.get("background");
                                            String child_name = (String) obj.get("child_name");
                                            String child_dob = (String) obj.get("child_dob");
                                            String child_age = (String) obj.get("child_age");


                                            Intent in = new Intent(People.this, Person_Details.class);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("army_no", army_no);
                                            in.putExtra("rank", rank);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("subunit", subunit);
                                            in.putExtra("unit", unit);
                                            in.putExtra("army", army);
                                            in.putExtra("age", age);
                                            in.putExtra("dob", dob);
                                            in.putExtra("doe", doe);
                                            in.putExtra("dod", dod);
                                            in.putExtra("dc", dc);
                                            in.putExtra("retain", retain);
                                            in.putExtra("payee", payee);
                                            in.putExtra("dependent_name", dependent_name);
                                            in.putExtra("dependent_age", dependent_age);
                                            in.putExtra("dependent_dob", dependent_dob);
                                            in.putExtra("photo1", photo1);
                                            in.putExtra("photo2", photo2);
                                            in.putExtra("photo3", photo3);
                                            in.putExtra("nominee", nominee);
                                            in.putExtra("relation", relation);
                                            in.putExtra("paid_to", paid_to);
                                            in.putExtra("health_state", health_state);
                                            in.putExtra("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                            in.putExtra("longitude", longitude);
                                            in.putExtra("latitude", latitude);
                                            in.putExtra("village", village);
                                            in.putExtra("district", district);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("po", po);
                                            in.putExtra("background", background);
                                            in.putExtra("child_name", child_name);
                                            in.putExtra("child_dob", child_dob);
                                            in.putExtra("child_age", child_age);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            People.this.adapter.getFilter().filter(cs);
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

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("MainActivity", t.toString());
                            Toast.makeText(People.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }
                    });
                } else {
                    progressDialog = new ProgressDialog(People.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(People.this
                                    .getCacheDir(),
                                    "userApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail/" + awc + "?api_token=" + fbToken);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(People.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("personal");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    String personal_id = c.getString("personal_id");
                                    String army_no = c.getString("army_no");
                                    String rank = c.getString("rank");
                                    String name = c.getString("name");
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
                                    String longitude = c.getString("longitude");
                                    String latitude = c.getString("latitude");
                                    String village = c.getString("village");
                                    String district = c.getString("district");
                                    String vdc = c.getString("vdc");
                                    String ward_no = c.getString("ward_no");
                                    String po = c.getString("po");
                                    String background = c.getString("background");
                                    String child_name = c.getString("child_name");
                                    String child_dob = c.getString("child_dob");
                                    String child_age = c.getString("child_age");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("personal_id", personal_id);
                                    contact.put("army_no", army_no);
                                    contact.put("rank", rank);
                                    contact.put("name", name);
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
                                    contact.put("longitude", longitude);
                                    contact.put("latitude", latitude);
                                    contact.put("village", village);
                                    contact.put("district", district);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("po", po);
                                    contact.put("background", background);
                                    contact.put("child_name", child_name);
                                    contact.put("child_dob", child_dob);
                                    contact.put("child_age", child_age);
                                    // adding contact to contact list
                                    peoplelist.add(contact);
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            call.cancel();

                            People.this.runOnUiThread(new Runnable() {
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

                                            String personal_id = (String) obj.get("personal_id");
                                            String army_no = (String) obj.get("army_no");
                                            String rank = (String) obj.get("rank");
                                            String name = (String) obj.get("name");
                                            String surname = (String) obj.get("surname");
                                            String subunit = (String) obj.get("subunit");
                                            String unit = (String) obj.get("unit");
                                            String army = (String) obj.get("army");
                                            String age = (String) obj.get("age");
                                            String dob = (String) obj.get("dob");
                                            String doe = (String) obj.get("doe");
                                            String dod = (String) obj.get("dod");
                                            String dc = (String) obj.get("dc");
                                            String retain = (String) obj.get("retain");
                                            String payee = (String) obj.get("payee");
                                            String dependent_name = (String) obj.get("dependent_name");
                                            String dependent_age = (String) obj.get("dependent_age");
                                            String dependent_dob = (String) obj.get("dependent_dob");
                                            String photo1 = (String) obj.get("photo1");
                                            String photo2 = (String) obj.get("photo2");
                                            String photo3 = (String) obj.get("photo3");
                                            String nominee = (String) obj.get("nominee");
                                            String relation = (String) obj.get("relation");
                                            String paid_to = (String) obj.get("paid_to");
                                            String health_state = (String) obj.get("health_state");
                                            String type_of_welfare_pensioner = (String) obj.get("type_of_welfare_pensioner");
                                            String longitude = (String) obj.get("longitude");
                                            String latitude = (String) obj.get("latitude");
                                            String village = (String) obj.get("village");
                                            String district = (String) obj.get("district");
                                            String vdc = (String) obj.get("vdc");
                                            String ward_no = (String) obj.get("ward_no");
                                            String po = (String) obj.get("po");
                                            String background = (String) obj.get("background");
                                            String child_name = (String) obj.get("child_name");
                                            String child_dob = (String) obj.get("child_dob");
                                            String child_age = (String) obj.get("child_age");


                                            Intent in = new Intent(People.this, Person_Details.class);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("army_no", army_no);
                                            in.putExtra("rank", rank);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("subunit", subunit);
                                            in.putExtra("unit", unit);
                                            in.putExtra("army", army);
                                            in.putExtra("age", age);
                                            in.putExtra("dob", dob);
                                            in.putExtra("doe", doe);
                                            in.putExtra("dod", dod);
                                            in.putExtra("dc", dc);
                                            in.putExtra("retain", retain);
                                            in.putExtra("payee", payee);
                                            in.putExtra("dependent_name", dependent_name);
                                            in.putExtra("dependent_age", dependent_age);
                                            in.putExtra("dependent_dob", dependent_dob);
                                            in.putExtra("photo1", photo1);
                                            in.putExtra("photo2", photo2);
                                            in.putExtra("photo3", photo3);
                                            in.putExtra("nominee", nominee);
                                            in.putExtra("relation", relation);
                                            in.putExtra("paid_to", paid_to);
                                            in.putExtra("health_state", health_state);
                                            in.putExtra("type_of_welfare_pensioner", type_of_welfare_pensioner);
                                            in.putExtra("longitude", longitude);
                                            in.putExtra("latitude", latitude);
                                            in.putExtra("village", village);
                                            in.putExtra("district", district);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("po", po);
                                            in.putExtra("background", background);
                                            in.putExtra("child_name", child_name);
                                            in.putExtra("child_dob", child_dob);
                                            in.putExtra("child_age", child_age);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            People.this.adapter.getFilter().filter(cs);
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

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("MainActivity", t.toString());
                            Toast.makeText(People.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }
                    });
                }
            }
        }*/
    }


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

}



