package com.example.android.gurkha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

public class Ca_servicetype extends AppCompatActivity {
    private String TAG = Village_List.class.getSimpleName();
    private TextView title;
    private Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    private EditText search;
    String token;
    Typeface face;
    private TextView mEmptyStateTextView;
    private static String base_url = "http://pagodalabs.com.np/";
    private static String url = "http://pagodalabs.com.np/gws/ca/api/ca?api_token=";
    ArrayList<HashMap<String, String>> servicetype;
    Call<ResponseBody> call;
    private int lastPosition = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    SessionManager sessionManager;
    FbSessionManager fbSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_servicetype);
        servicetype = new ArrayList<>();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        tv.setTypeface(face);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        adapter = new SimpleAdapter(Ca_servicetype.this, servicetype,
                R.layout.list_item, new String[]{"service_type"}, new int[]{R.id.name}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(Ca_servicetype.this, (position > lastPosition) ? R.anim.item_animation_fall_down : R.anim.item_animation_fall_down);
                view.startAnimation(animation);
                lastPosition = position;
                return view;
            }
        };

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        listView = (ListView) findViewById(R.id.list);
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
            //new GetNames().execute();
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
     * If the device is offline, stale (at most 2 weeks old)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(Ca_servicetype.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
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
            token = user.get(SessionManager.KEY_TOKEN);
            if (id != null) {
                if (id.matches("1")) {

                    progressDialog = new ProgressDialog(Ca_servicetype.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Ca_servicetype.this
                                    .getCacheDir(),
                                    "caApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(CaInterface.class).getResponse("gws/ca/api/ca?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Ca", t.toString());
                            Toast.makeText(Ca_servicetype.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (!(response.isSuccessful())) {
                                    Toast.makeText(Ca_servicetype.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();

                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("CA");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String scheme_no = c.getString("scheme_no");
                                    final String location = c.getString("location");
                                    final String village = c.getString("village");
                                    final String vdc = c.getString("vdc");
                                    final String ward_no = c.getString("ward_no");
                                    final String service_type = c.getString("service_type");
                                    final String hh_total = c.getString("hh_total");
                                    final String population_total = c.getString("population_total");
                                    final String tenta_tap_no = c.getString("tenta_tap_no");
                                    final String level = c.getString("level");
                                    final String school_popu_total = c.getString("school_popu_total");
                                    final String gws_kaaa = c.getString("gws_kaa");
                                    final String community = c.getString("community");
                                    final String total = c.getString("total");
                                    final String funder = c.getString("funder");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("scheme_no", scheme_no);
                                    contact.put("location", location);
                                    contact.put("village", village);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("service_type", service_type);
                                    contact.put("hh_total", hh_total);
                                    contact.put("population_total", population_total);
                                    contact.put("tenta_tap_no", tenta_tap_no);
                                    contact.put("level", level);
                                    contact.put("school_popu_total", school_popu_total);
                                    contact.put("gws_kaa", gws_kaaa);
                                    contact.put("community", community);
                                    contact.put("total", total);
                                    contact.put("funder", funder);

                                    // adding contact to contact list
                                    servicetype.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Ca_servicetype.this.runOnUiThread(new Runnable() {
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

                                            final String scheme_no = (String) obj.get("scheme_no");
                                            final String location = (String) obj.get("location");
                                            final String village = (String) obj.get("village");
                                            final String vdc = (String) obj.get("vdc");
                                            final String ward_no = (String) obj.get("ward_no");
                                            final String service_type = (String) obj.get("service_type");
                                            final String hh_total = (String) obj.get("hh_total");
                                            final String population_total = (String) obj.get("population_total");
                                            final String tenta_tap_no = (String) obj.get("tenta_tap_no");
                                            final String level = (String) obj.get("level");
                                            final String school_popu_total = (String) obj.get("school_popu_total");
                                            final String gws_kaaa = (String) obj.get("gws_kaa");
                                            final String community = (String) obj.get("community");
                                            final String total = (String) obj.get("total");
                                            final String funder = (String) obj.get("funder");

                                            Intent in = new Intent(Ca_servicetype.this, Ca_details.class);
                                            in.putExtra("scheme_no", scheme_no);
                                            in.putExtra("location", location);
                                            in.putExtra("village", village);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("service_type", service_type);
                                            in.putExtra("hh_total", hh_total);
                                            in.putExtra("population_total", population_total);
                                            in.putExtra("tenta_tap_no", tenta_tap_no);
                                            in.putExtra("level", level);
                                            in.putExtra("school_popu_total", school_popu_total);
                                            in.putExtra("gws_kaa", gws_kaaa);
                                            in.putExtra("community", community);
                                            in.putExtra("total", total);
                                            in.putExtra("funder", funder);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Ca_servicetype.this.adapter.getFilter().filter(cs);
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
                } else {
                    progressDialog = new ProgressDialog(Ca_servicetype.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Ca_servicetype.this
                                    .getCacheDir(),
                                    "userCaApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(CaInterface.class).getResponse("gws/ca/api/ca/" + awc + "?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Ca", t.toString());
                            Toast.makeText(Ca_servicetype.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {

                                if (!(response.isSuccessful())) {
                                    Toast.makeText(Ca_servicetype.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("CA");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String scheme_no = c.getString("scheme_no");
                                    final String location = c.getString("location");
                                    final String village = c.getString("village");
                                    final String vdc = c.getString("vdc");
                                    final String ward_no = c.getString("ward_no");
                                    final String service_type = c.getString("service_type");
                                    final String hh_total = c.getString("hh_total");
                                    final String population_total = c.getString("population_total");
                                    final String tenta_tap_no = c.getString("tenta_tap_no");
                                    final String level = c.getString("level");
                                    final String school_popu_total = c.getString("school_popu_total");
                                    final String gws_kaaa = c.getString("gws_kaa");
                                    final String community = c.getString("community");
                                    final String total = c.getString("total");
                                    final String funder = c.getString("funder");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("scheme_no", scheme_no);
                                    contact.put("location", location);
                                    contact.put("village", village);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("service_type", service_type);
                                    contact.put("hh_total", hh_total);
                                    contact.put("population_total", population_total);
                                    contact.put("tenta_tap_no", tenta_tap_no);
                                    contact.put("level", level);
                                    contact.put("school_popu_total", school_popu_total);
                                    contact.put("gws_kaa", gws_kaaa);
                                    contact.put("community", community);
                                    contact.put("total", total);
                                    contact.put("funder", funder);

                                    // adding contact to contact list
                                    servicetype.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Ca_servicetype.this.runOnUiThread(new Runnable() {
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

                                            final String scheme_no = (String) obj.get("scheme_no");
                                            final String location = (String) obj.get("location");
                                            final String village = (String) obj.get("village");
                                            final String vdc = (String) obj.get("vdc");
                                            final String ward_no = (String) obj.get("ward_no");
                                            final String service_type = (String) obj.get("service_type");
                                            final String hh_total = (String) obj.get("hh_total");
                                            final String population_total = (String) obj.get("population_total");
                                            final String tenta_tap_no = (String) obj.get("tenta_tap_no");
                                            final String level = (String) obj.get("level");
                                            final String school_popu_total = (String) obj.get("school_popu_total");
                                            final String gws_kaaa = (String) obj.get("gws_kaa");
                                            final String community = (String) obj.get("community");
                                            final String total = (String) obj.get("total");
                                            final String funder = (String) obj.get("funder");

                                            Intent in = new Intent(Ca_servicetype.this, Ca_details.class);
                                            in.putExtra("scheme_no", scheme_no);
                                            in.putExtra("location", location);
                                            in.putExtra("village", village);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("service_type", service_type);
                                            in.putExtra("hh_total", hh_total);
                                            in.putExtra("population_total", population_total);
                                            in.putExtra("tenta_tap_no", tenta_tap_no);
                                            in.putExtra("level", level);
                                            in.putExtra("school_popu_total", school_popu_total);
                                            in.putExtra("gws_kaa", gws_kaaa);
                                            in.putExtra("community", community);
                                            in.putExtra("total", total);
                                            in.putExtra("funder", funder);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Ca_servicetype.this.adapter.getFilter().filter(cs);
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
            }
        }

        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            String fbId = fbUser.get(SessionManager.KEY_ID);
            //HashMap<String, String> user = sessionManager.getUserDetails();
            token = fbUser.get(SessionManager.KEY_TOKEN);

            if (fbId != null) {
                if (fbId.matches("1")) {

                    progressDialog = new ProgressDialog(Ca_servicetype.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Ca_servicetype.this
                                    .getCacheDir(),
                                    "caApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(CaInterface.class).getResponse("gws/ca/api/ca?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Ca", t.toString());
                            Toast.makeText(Ca_servicetype.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (!(response.isSuccessful())) {
                                    Toast.makeText(Ca_servicetype.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("CA");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String scheme_no = c.getString("scheme_no");
                                    final String location = c.getString("location");
                                    final String village = c.getString("village");
                                    final String vdc = c.getString("vdc");
                                    final String ward_no = c.getString("ward_no");
                                    final String service_type = c.getString("service_type");
                                    final String hh_total = c.getString("hh_total");
                                    final String population_total = c.getString("population_total");
                                    final String tenta_tap_no = c.getString("tenta_tap_no");
                                    final String level = c.getString("level");
                                    final String school_popu_total = c.getString("school_popu_total");
                                    final String gws_kaaa = c.getString("gws_kaa");
                                    final String community = c.getString("community");
                                    final String total = c.getString("total");
                                    final String funder = c.getString("funder");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("scheme_no", scheme_no);
                                    contact.put("location", location);
                                    contact.put("village", village);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("service_type", service_type);
                                    contact.put("hh_total", hh_total);
                                    contact.put("population_total", population_total);
                                    contact.put("tenta_tap_no", tenta_tap_no);
                                    contact.put("level", level);
                                    contact.put("school_popu_total", school_popu_total);
                                    contact.put("gws_kaa", gws_kaaa);
                                    contact.put("community", community);
                                    contact.put("total", total);
                                    contact.put("funder", funder);

                                    // adding contact to contact list
                                    servicetype.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Ca_servicetype.this.runOnUiThread(new Runnable() {
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

                                            final String scheme_no = (String) obj.get("scheme_no");
                                            final String location = (String) obj.get("location");
                                            final String village = (String) obj.get("village");
                                            final String vdc = (String) obj.get("vdc");
                                            final String ward_no = (String) obj.get("ward_no");
                                            final String service_type = (String) obj.get("service_type");
                                            final String hh_total = (String) obj.get("hh_total");
                                            final String population_total = (String) obj.get("population_total");
                                            final String tenta_tap_no = (String) obj.get("tenta_tap_no");
                                            final String level = (String) obj.get("level");
                                            final String school_popu_total = (String) obj.get("school_popu_total");
                                            final String gws_kaaa = (String) obj.get("gws_kaa");
                                            final String community = (String) obj.get("community");
                                            final String total = (String) obj.get("total");
                                            final String funder = (String) obj.get("funder");

                                            Intent in = new Intent(Ca_servicetype.this, Ca_details.class);
                                            in.putExtra("scheme_no", scheme_no);
                                            in.putExtra("location", location);
                                            in.putExtra("village", village);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("service_type", service_type);
                                            in.putExtra("hh_total", hh_total);
                                            in.putExtra("population_total", population_total);
                                            in.putExtra("tenta_tap_no", tenta_tap_no);
                                            in.putExtra("level", level);
                                            in.putExtra("school_popu_total", school_popu_total);
                                            in.putExtra("gws_kaa", gws_kaaa);
                                            in.putExtra("community", community);
                                            in.putExtra("total", total);
                                            in.putExtra("funder", funder);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Ca_servicetype.this.adapter.getFilter().filter(cs);
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
                } else {
                    progressDialog = new ProgressDialog(Ca_servicetype.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Ca_servicetype.this
                                    .getCacheDir(),
                                    "userCaApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(CaInterface.class).getResponse("gws/ca/api/ca/" + awc + "?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Ca", t.toString());
                            Toast.makeText(Ca_servicetype.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {

                                if (!(response.isSuccessful())) {
                                    Toast.makeText(Ca_servicetype.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("CA");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String scheme_no = c.getString("scheme_no");
                                    final String location = c.getString("location");
                                    final String village = c.getString("village");
                                    final String vdc = c.getString("vdc");
                                    final String ward_no = c.getString("ward_no");
                                    final String service_type = c.getString("service_type");
                                    final String hh_total = c.getString("hh_total");
                                    final String population_total = c.getString("population_total");
                                    final String tenta_tap_no = c.getString("tenta_tap_no");
                                    final String level = c.getString("level");
                                    final String school_popu_total = c.getString("school_popu_total");
                                    final String gws_kaaa = c.getString("gws_kaa");
                                    final String community = c.getString("community");
                                    final String total = c.getString("total");
                                    final String funder = c.getString("funder");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("scheme_no", scheme_no);
                                    contact.put("location", location);
                                    contact.put("village", village);
                                    contact.put("vdc", vdc);
                                    contact.put("ward_no", ward_no);
                                    contact.put("service_type", service_type);
                                    contact.put("hh_total", hh_total);
                                    contact.put("population_total", population_total);
                                    contact.put("tenta_tap_no", tenta_tap_no);
                                    contact.put("level", level);
                                    contact.put("school_popu_total", school_popu_total);
                                    contact.put("gws_kaa", gws_kaaa);
                                    contact.put("community", community);
                                    contact.put("total", total);
                                    contact.put("funder", funder);

                                    // adding contact to contact list
                                    servicetype.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Ca_servicetype.this.runOnUiThread(new Runnable() {
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

                                            final String scheme_no = (String) obj.get("scheme_no");
                                            final String location = (String) obj.get("location");
                                            final String village = (String) obj.get("village");
                                            final String vdc = (String) obj.get("vdc");
                                            final String ward_no = (String) obj.get("ward_no");
                                            final String service_type = (String) obj.get("service_type");
                                            final String hh_total = (String) obj.get("hh_total");
                                            final String population_total = (String) obj.get("population_total");
                                            final String tenta_tap_no = (String) obj.get("tenta_tap_no");
                                            final String level = (String) obj.get("level");
                                            final String school_popu_total = (String) obj.get("school_popu_total");
                                            final String gws_kaaa = (String) obj.get("gws_kaa");
                                            final String community = (String) obj.get("community");
                                            final String total = (String) obj.get("total");
                                            final String funder = (String) obj.get("funder");

                                            Intent in = new Intent(Ca_servicetype.this, Ca_details.class);
                                            in.putExtra("scheme_no", scheme_no);
                                            in.putExtra("location", location);
                                            in.putExtra("village", village);
                                            in.putExtra("vdc", vdc);
                                            in.putExtra("ward_no", ward_no);
                                            in.putExtra("service_type", service_type);
                                            in.putExtra("hh_total", hh_total);
                                            in.putExtra("population_total", population_total);
                                            in.putExtra("tenta_tap_no", tenta_tap_no);
                                            in.putExtra("level", level);
                                            in.putExtra("school_popu_total", school_popu_total);
                                            in.putExtra("gws_kaa", gws_kaaa);
                                            in.putExtra("community", community);
                                            in.putExtra("total", total);
                                            in.putExtra("funder", funder);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Ca_servicetype.this.adapter.getFilter().filter(cs);
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
            progressDialog = new ProgressDialog(Ca_servicetype.this);
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
                        JSONObject jsonObj = JSONParser.getCAData();

                        // Getting JSON Array node
                        JSONArray data = jsonObj.getJSONArray("CA");

                        // looping through All data
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);


                            final String scheme_no = c.getString("scheme_no");
                            final String location = c.getString("location");
                            final String village = c.getString("village");
                            final String vdc = c.getString("vdc");
                            final String ward_no = c.getString("ward_no");
                            final String service_type = c.getString("service_type");
                            final String hh_total = c.getString("hh_total");
                            final String population_total = c.getString("population_total");
                            final String tenta_tap_no = c.getString("tenta_tap_no");
                            final String level = c.getString("level");
                            final String school_popu_total = c.getString("school_popu_total");
                            final String gws_kaaa = c.getString("gws_kaa");
                            final String community = c.getString("community");
                            final String total = c.getString("total");
                            final String funder = c.getString("funder");

                            // tmp hash map for single data
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value

                            contact.put("scheme_no", scheme_no);
                            contact.put("location", location);
                            contact.put("village", village);
                            contact.put("vdc", vdc);
                            contact.put("ward_no", ward_no);
                            contact.put("service_type", service_type);
                            contact.put("hh_total", hh_total);
                            contact.put("population_total", population_total);
                            contact.put("tenta_tap_no", tenta_tap_no);
                            contact.put("level", level);
                            contact.put("school_popu_total", school_popu_total);
                            contact.put("gws_kaa", gws_kaaa);
                            contact.put("community", community);
                            contact.put("total", total);
                            contact.put("funder", funder);

                            // adding contact to contact list
                            servicetype.add(contact);
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
                        JSONObject jsonObj = JSONParser.getCAData();

                        // Getting JSON Array node
                        JSONArray data = jsonObj.getJSONArray("CA");

                        // looping through All data
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);


                            final String scheme_no = c.getString("scheme_no");
                            final String location = c.getString("location");
                            final String village = c.getString("village");
                            final String vdc = c.getString("vdc");
                            final String ward_no = c.getString("ward_no");
                            final String service_type = c.getString("service_type");
                            final String hh_total = c.getString("hh_total");
                            final String population_total = c.getString("population_total");
                            final String tenta_tap_no = c.getString("tenta_tap_no");
                            final String level = c.getString("level");
                            final String school_popu_total = c.getString("school_popu_total");
                            final String gws_kaaa = c.getString("gws_kaa");
                            final String community = c.getString("community");
                            final String total = c.getString("total");
                            final String funder = c.getString("funder");

                            // tmp hash map for single data
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value

                            contact.put("scheme_no", scheme_no);
                            contact.put("location", location);
                            contact.put("village", village);
                            contact.put("vdc", vdc);
                            contact.put("ward_no", ward_no);
                            contact.put("service_type", service_type);
                            contact.put("hh_total", hh_total);
                            contact.put("population_total", population_total);
                            contact.put("tenta_tap_no", tenta_tap_no);
                            contact.put("level", level);
                            contact.put("school_popu_total", school_popu_total);
                            contact.put("gws_kaa", gws_kaaa);
                            contact.put("community", community);
                            contact.put("total", total);
                            contact.put("funder", funder);

                            // adding contact to contact list
                            servicetype.add(contact);
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
                    if(position %2==0)
                    {
                        v.setBackgroundColor(Color.DKGRAY);
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
                    final String scheme_no = (String) obj.get("scheme_no");
                    final String location = (String) obj.get("location");
                    final String village = (String) obj.get("village");
                    final String vdc = (String) obj.get("vdc");
                    final String ward_no = (String) obj.get("ward_no");
                    final String service_type = (String) obj.get("service_type");
                    final String hh_total = (String) obj.get("hh_total");
                    final String population_total = (String) obj.get("population_total");
                    final String tenta_tap_no = (String) obj.get("tenta_tap_no");
                    final String level = (String) obj.get("level");
                    final String school_popu_total = (String) obj.get("school_popu_total");
                    final String gws_kaaa = (String) obj.get("gws_kaa");
                    final String community = (String) obj.get("community");
                    final String total = (String) obj.get("total");
                    final String funder = (String) obj.get("funder");

                    Intent in = new Intent(Ca_servicetype.this, Ca_details.class);
                    in.putExtra("scheme_no", scheme_no);
                    in.putExtra("location", location);
                    in.putExtra("village", village);
                    in.putExtra("vdc", vdc);
                    in.putExtra("ward_no", ward_no);
                    in.putExtra("service_type", service_type);
                    in.putExtra("hh_total", hh_total);
                    in.putExtra("population_total", population_total);
                    in.putExtra("tenta_tap_no", tenta_tap_no);
                    in.putExtra("level", level);
                    in.putExtra("school_popu_total", school_popu_total);
                    in.putExtra("gws_kaa", gws_kaaa);
                    in.putExtra("community", community);
                    in.putExtra("total", total);
                    in.putExtra("funder", funder);
                    startActivity(in);
                }
            });
            search.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    Ca_servicetype.this.adapter.getFilter().filter(cs);
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

    public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }
}


