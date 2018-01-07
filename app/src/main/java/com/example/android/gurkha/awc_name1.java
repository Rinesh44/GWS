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

public class awc_name1 extends AppCompatActivity {
    private String TAG = Village_List.class.getSimpleName();
    private TextView title;
    private Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    private EditText search;

    private static String base_url = "http://pagodalabs.com.np/";
    // URL to get villagelist JSON
    private static String url = "http://pagodalabs.com.np/gws/awc/api/awc/";
    ArrayList<HashMap<String, String>> names1;
    Call<ResponseBody> call;
    private TextView mEmptyStateTextView;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awc_name1);
        names1 = new ArrayList<>();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        tv.setTypeface(face);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        adapter = new SimpleAdapter(awc_name1.this, names1,
                R.layout.list_item, new String[]{"name", "army_no"}, new int[]{R.id.name, R.id.army_no});

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
           // new GetNames().execute();
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
            if (!InternetConnection.checkConnection(awc_name1.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600) //2 weeks
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

                    progressDialog = new ProgressDialog(awc_name1.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(awc_name1.this
                                    .getCacheDir(),
                                    "awcApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(AwcInterface.class).getResponse("gws/awc/api/awc/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Awc", t.toString());
                            Toast.makeText(awc_name1.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("awc");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String name = c.getString("name");
                                    final String wp_sp = c.getString("wp_sp");
                                    final String address = c.getString("address");
                                    final String approved_grant = c.getString("approved_grant");
                                    final String construction_cost = c.getString("construction_cost");
                                    final String project_supervisor = c.getString("supervisor");
                                    final String remarks = c.getString("remarks");
                                    final String status = c.getString("status");
                                    final String location = c.getString("location");
                                    final String category = c.getString("category");
                                    final String army_no = c.getString("army_no");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("name", name);
                                    contact.put("wp_sp", wp_sp);
                                    contact.put("address", address);
                                    contact.put("approved_grant", approved_grant);
                                    contact.put("construction_cost", construction_cost);
                                    contact.put("supervisor", project_supervisor);
                                    contact.put("remarks", remarks);
                                    contact.put("status", status);
                                    contact.put("location", location);
                                    contact.put("category", category);
                                    contact.put("army_no", army_no);

                                    // adding contact to contact list
                                    names1.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            awc_name1.this.runOnUiThread(new Runnable() {
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

                                            final String name = (String) obj.get("name");
                                            final String wp_sp = (String) obj.get("wp_sp");
                                            final String address = (String) obj.get("address");
                                            final String approved_grant = (String) obj.get("approved_grant");
                                            final String construction_cost = (String) obj.get("construction_cost");
                                            final String project_supervisor = (String) obj.get("supervisor");
                                            final String remarks = (String) obj.get("remarks");
                                            final String status = (String) obj.get("status");
                                            final String location = (String) obj.get("location");
                                            final String category = (String) obj.get("category");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(awc_name1.this, awc_details.class);
                                            in.putExtra("name", name);
                                            in.putExtra("wp_sp", wp_sp);
                                            in.putExtra("address", address);
                                            in.putExtra("approved_grant", approved_grant);
                                            in.putExtra("construction_cost", construction_cost);
                                            in.putExtra("supervisor", project_supervisor);
                                            in.putExtra("remarks", remarks);
                                            in.putExtra("status", status);
                                            in.putExtra("location", location);
                                            in.putExtra("category", category);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            awc_name1.this.adapter.getFilter().filter(cs);
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
                    progressDialog = new ProgressDialog(awc_name1.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(awc_name1.this
                                    .getCacheDir(),
                                    "userAwcApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(AwcInterface.class).getResponse("gws/awc/api/awc/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Awc", t.toString());
                            Toast.makeText(awc_name1.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("awc");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String name = c.getString("name");
                                    final String wp_sp = c.getString("wp_sp");
                                    final String address = c.getString("address");
                                    final String approved_grant = c.getString("approved_grant");
                                    final String construction_cost = c.getString("construction_cost");
                                    final String project_supervisor = c.getString("supervisor");
                                    final String remarks = c.getString("remarks");
                                    final String status = c.getString("status");
                                    final String location = c.getString("location");
                                    final String category = c.getString("category");
                                    final String army_no = c.getString("army_no");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("name", name);
                                    contact.put("wp_sp", wp_sp);
                                    contact.put("address", address);
                                    contact.put("approved_grant", approved_grant);
                                    contact.put("construction_cost", construction_cost);
                                    contact.put("supervisor", project_supervisor);
                                    contact.put("remarks", remarks);
                                    contact.put("status", status);
                                    contact.put("location", location);
                                    contact.put("category", category);
                                    contact.put("army_no", army_no);

                                    // adding contact to contact list
                                    names1.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            awc_name1.this.runOnUiThread(new Runnable() {
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

                                            final String name = (String) obj.get("name");
                                            final String wp_sp = (String) obj.get("wp_sp");
                                            final String address = (String) obj.get("address");
                                            final String approved_grant = (String) obj.get("approved_grant");
                                            final String construction_cost = (String) obj.get("construction_cost");
                                            final String project_supervisor = (String) obj.get("supervisor");
                                            final String remarks = (String) obj.get("remarks");
                                            final String status = (String) obj.get("status");
                                            final String location = (String) obj.get("location");
                                            final String category = (String) obj.get("category");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(awc_name1.this, awc_details.class);
                                            in.putExtra("name", name);
                                            in.putExtra("wp_sp", wp_sp);
                                            in.putExtra("address", address);
                                            in.putExtra("approved_grant", approved_grant);
                                            in.putExtra("construction_cost", construction_cost);
                                            in.putExtra("supervisor", project_supervisor);
                                            in.putExtra("remarks", remarks);
                                            in.putExtra("status", status);
                                            in.putExtra("location", location);
                                            in.putExtra("category", category);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            awc_name1.this.adapter.getFilter().filter(cs);
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

            if (fbId != null){
                if (fbId.matches("1")) {

                    progressDialog = new ProgressDialog(awc_name1.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(awc_name1.this
                                    .getCacheDir(),
                                    "awcApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(AwcInterface.class).getResponse("gws/awc/api/awc/");

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Awc", t.toString());
                            Toast.makeText(awc_name1.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("awc");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String name = c.getString("name");
                                    final String wp_sp = c.getString("wp_sp");
                                    final String address = c.getString("address");
                                    final String approved_grant = c.getString("approved_grant");
                                    final String construction_cost = c.getString("construction_cost");
                                    final String project_supervisor = c.getString("supervisor");
                                    final String remarks = c.getString("remarks");
                                    final String status = c.getString("status");
                                    final String location = c.getString("location");
                                    final String category = c.getString("category");
                                    final String army_no = c.getString("army_no");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("name", name);
                                    contact.put("wp_sp", wp_sp);
                                    contact.put("address", address);
                                    contact.put("approved_grant", approved_grant);
                                    contact.put("construction_cost", construction_cost);
                                    contact.put("supervisor", project_supervisor);
                                    contact.put("remarks", remarks);
                                    contact.put("status", status);
                                    contact.put("location", location);
                                    contact.put("category", category);
                                    contact.put("army_no", army_no);

                                    // adding contact to contact list
                                    names1.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            awc_name1.this.runOnUiThread(new Runnable() {
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

                                            final String name = (String) obj.get("name");
                                            final String wp_sp = (String) obj.get("wp_sp");
                                            final String address = (String) obj.get("address");
                                            final String approved_grant = (String) obj.get("approved_grant");
                                            final String construction_cost = (String) obj.get("construction_cost");
                                            final String project_supervisor = (String) obj.get("supervisor");
                                            final String remarks = (String) obj.get("remarks");
                                            final String status = (String) obj.get("status");
                                            final String location = (String) obj.get("location");
                                            final String category = (String) obj.get("category");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(awc_name1.this, awc_details.class);
                                            in.putExtra("name", name);
                                            in.putExtra("wp_sp", wp_sp);
                                            in.putExtra("address", address);
                                            in.putExtra("approved_grant", approved_grant);
                                            in.putExtra("construction_cost", construction_cost);
                                            in.putExtra("supervisor", project_supervisor);
                                            in.putExtra("remarks", remarks);
                                            in.putExtra("status", status);
                                            in.putExtra("location", location);
                                            in.putExtra("category", category);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            awc_name1.this.adapter.getFilter().filter(cs);
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
                    progressDialog = new ProgressDialog(awc_name1.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(awc_name1.this
                                    .getCacheDir(),
                                    "userAwcApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(AwcInterface.class).getResponse("gws/awc/api/awc/" + awc);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Awc", t.toString());
                            Toast.makeText(awc_name1.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("awc");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String name = c.getString("name");
                                    final String wp_sp = c.getString("wp_sp");
                                    final String address = c.getString("address");
                                    final String approved_grant = c.getString("approved_grant");
                                    final String construction_cost = c.getString("construction_cost");
                                    final String project_supervisor = c.getString("supervisor");
                                    final String remarks = c.getString("remarks");
                                    final String status = c.getString("status");
                                    final String location = c.getString("location");
                                    final String category = c.getString("category");
                                    final String army_no = c.getString("army_no");

                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("name", name);
                                    contact.put("wp_sp", wp_sp);
                                    contact.put("address", address);
                                    contact.put("approved_grant", approved_grant);
                                    contact.put("construction_cost", construction_cost);
                                    contact.put("supervisor", project_supervisor);
                                    contact.put("remarks", remarks);
                                    contact.put("status", status);
                                    contact.put("location", location);
                                    contact.put("category", category);
                                    contact.put("army_no", army_no);

                                    // adding contact to contact list
                                    names1.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            awc_name1.this.runOnUiThread(new Runnable() {
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

                                            final String name = (String) obj.get("name");
                                            final String wp_sp = (String) obj.get("wp_sp");
                                            final String address = (String) obj.get("address");
                                            final String approved_grant = (String) obj.get("approved_grant");
                                            final String construction_cost = (String) obj.get("construction_cost");
                                            final String project_supervisor = (String) obj.get("supervisor");
                                            final String remarks = (String) obj.get("remarks");
                                            final String status = (String) obj.get("status");
                                            final String location = (String) obj.get("location");
                                            final String category = (String) obj.get("category");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(awc_name1.this, awc_details.class);
                                            in.putExtra("name", name);
                                            in.putExtra("wp_sp", wp_sp);
                                            in.putExtra("address", address);
                                            in.putExtra("approved_grant", approved_grant);
                                            in.putExtra("construction_cost", construction_cost);
                                            in.putExtra("supervisor", project_supervisor);
                                            in.putExtra("remarks", remarks);
                                            in.putExtra("status", status);
                                            in.putExtra("location", location);
                                            in.putExtra("category", category);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            awc_name1.this.adapter.getFilter().filter(cs);
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
            progressDialog = new ProgressDialog(awc_name1.this);
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
                        JSONArray data = jsonObj.getJSONArray("awc");

                        // looping through All data
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            final String name = c.getString("name");
                            final String wp_sp = c.getString("wp_sp");
                            final String address = c.getString("address");
                            final String approved_grant = c.getString("approved_grant");
                            final String construction_cost = c.getString("construction_cost");
                            final String project_supervisor = c.getString("supervisor");
                            final String remarks = c.getString("remarks");
                            final String status = c.getString("status");
                            final String location = c.getString("location");
                            final String category = c.getString("category");
                            final String army_no = c.getString("army_no");

                            // tmp hash map for single data
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value

                            contact.put("name", name);
                            contact.put("wp_sp", wp_sp);
                            contact.put("address", address);
                            contact.put("approved_grant", approved_grant);
                            contact.put("construction_cost", construction_cost);
                            contact.put("supervisor", project_supervisor);
                            contact.put("remarks", remarks);
                            contact.put("status", status);
                            contact.put("location", location);
                            contact.put("category", category);
                            contact.put("army_no", army_no);

                            // adding contact to contact list
                            names1.add(contact);
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
                        JSONArray data = jsonObj.getJSONArray("awc");

                        // looping through All data
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            final String name = c.getString("name");
                            final String wp_sp = c.getString("wp_sp");
                            final String address = c.getString("address");
                            final String approved_grant = c.getString("approved_grant");
                            final String construction_cost = c.getString("construction_cost");
                            final String project_supervisor = c.getString("supervisor");
                            final String remarks = c.getString("remarks");
                            final String status = c.getString("status");
                            final String location = c.getString("location");
                            final String category = c.getString("category");
                            final String army_no = c.getString("army_no");

                            // tmp hash map for single data
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value

                            contact.put("name", name);
                            contact.put("wp_sp", wp_sp);
                            contact.put("address", address);
                            contact.put("approved_grant", approved_grant);
                            contact.put("construction_cost", construction_cost);
                            contact.put("supervisor", project_supervisor);
                            contact.put("remarks", remarks);
                            contact.put("status", status);
                            contact.put("location", location);
                            contact.put("category", category);
                            contact.put("army_no", army_no);

                            // adding contact to contact list
                            names1.add(contact);
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
                    final String name = (String) obj.get("name");
                    final String wp_sp = (String) obj.get("wp_sp");
                    final String address = (String) obj.get("address");
                    final String approved_grant = (String) obj.get("approved_grant");
                    final String construction_cost = (String) obj.get("construction_cost");
                    final String project_supervisor = (String) obj.get("supervisor");
                    final String remarks = (String) obj.get("remarks");
                    final String status = (String) obj.get("status");
                    final String location = (String) obj.get("location");
                    final String category = (String) obj.get("category");
                    final String army_no = (String) obj.get("army_no");

                    Intent in = new Intent(awc_name1.this, awc_details.class);
                    in.putExtra("name", name);
                    in.putExtra("wp_sp", wp_sp);
                    in.putExtra("address", address);
                    in.putExtra("approved_grant", approved_grant);
                    in.putExtra("construction_cost", construction_cost);
                    in.putExtra("supervisor", project_supervisor);
                    in.putExtra("remarks", remarks);
                    in.putExtra("status", status);
                    in.putExtra("location", location);
                    in.putExtra("category", category);
                    in.putExtra("army_no", army_no);
                    startActivity(in);
                }
            });
            search.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    awc_name1.this.adapter.getFilter().filter(cs);
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

    public void timerDelayRemoveDialog(long time, final ProgressDialog d){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }


}


