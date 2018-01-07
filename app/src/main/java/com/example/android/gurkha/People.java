package com.example.android.gurkha;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import java.io.IOException;
import java.util.ArrayList;
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

public class People extends AppCompatActivity {
    private String TAG = People.class.getSimpleName();
    private TextView title;
    private Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    private EditText search;
    private TextView mEmptyStateTextView;
    // URL to get peoplelist JSON
    private static String base_url = "http://pagodalabs.com.np/";
    private static String url = "http://pagodalabs.com.np/gws/personal_detail/api/personal_detail/";
    ArrayList<HashMap<String, String>> peoplelist;
    Call<ResponseBody> call;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        peoplelist = new ArrayList<>();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        tv.setTypeface(face);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        adapter = new SimpleAdapter(People.this, peoplelist,
                R.layout.list_item, new String[]{"name", "surname", "army_no"}, new int[]{R.id.name, R.id.surname, R.id.army_no});
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
            //new People.GetNames().execute();
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
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     *
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

    void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        final String awc = SelectAwc.awc;

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            String id = user.get(SessionManager.KEY_ID);

            if (id != null){
                if (id.matches("1")) {
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

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
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

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
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

        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            String fbId = fbUser.get(SessionManager.KEY_ID);

            if (fbId != null){
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

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
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

                    call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
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
    }

        /**
         * Async task class to get json by making HTTP call
         */
        private class GetNames extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                progressDialog = new ProgressDialog(People.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                String awc = SelectAwc.awc;
                String checkAdmin = MainActivity.checkAdmin;
                // Making a request to url and getting response
                if (checkAdmin.matches("1")) {
                    String jsonStr = sh.makeServiceCall(url);

                    Log.e(TAG, "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            // Getting JSON Array node
                            JSONArray data = jsonObj.getJSONArray("personal");
                            // looping through All data
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

                                // adding contact to contact list
                                peoplelist.add(contact);
                                adapter.notifyDataSetChanged();

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
                } else {
                    String jsonStr = sh.makeServiceCall(url + awc);

                    Log.e(TAG, "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            // Getting JSON Array node
                            JSONArray data = jsonObj.getJSONArray("personal");
                            // looping through All data
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

                                // adding contact to contact list
                                peoplelist.add(contact);
                                adapter.notifyDataSetChanged();

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
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                /**
                 * Updating parsed JSON data into ListView
                 * */
            /*
            adapter = new SimpleAdapter(Village_List.this, villageList,
                    R.layout.list_item, new String[]{"v_id", "villages"}, new int[]{R.id.id, R.id.name}){
                public View getView (int position, View convertView, ViewGroup parent)
                {
                    View v = super.getView(position, convertView, parent);
                    if(position %2!=0)
                    {
                        v.setBackgroundColor(Color.BLACK);
                    }
                    else
                    {
                        v.setBackgroundColor(Color.GRAY);
                    }

                    return v;
                }

            };
             */

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

        }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void timerDelayRemoveDialog(long time, final ProgressDialog d){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

    }



