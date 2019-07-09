package com.example.android.gurkha;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.view.Menu;
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

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Investigation extends AppCompatActivity implements ResponseListener {
    private String TAG = Investigation.class.getSimpleName();
    private TextView title;
    private Toolbar toolbar;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    Typeface face;
    String token;
    Menu menu;
    private EditText search;
    // URL to get villagelist JSON
    private static String base_url = "http://gws.pagodalabs.com.np/";
    private static String url = "http://gws.pagodalabs.com.np/investigate/api/investigate?api_token=";
    private static String delete_url = "http://gws.pagodalabs.com.np/investigate/api/deleteInvestigate?api_token=";
    ArrayList<HashMap<String, String>> personlist;
    Call<ResponseBody> call;
    private TextView mEmptyStateTextView;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    private int lastPosition = -1;
    JobManager mJobManager;
    MaterialProgressBar progressBar;
    private boolean searchByAwc;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation);
        personlist = new ArrayList<>();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        tv.setTypeface(face);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        progressBar = findViewById(R.id.progressBar);

        checkIfSearchByAwc();

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        adapter = new SimpleAdapter(Investigation.this, personlist,
                R.layout.list_item, new String[]{"name", "army_no"}, new int[]{R.id.name, R.id.army_no}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(R.id.name);
//                TextView surname = (TextView) view.findViewById(R.id.surname);
                TextView army_no = (TextView) view.findViewById(R.id.army_no);
                name.setTypeface(face);
//                surname.setTypeface(face);
                army_no.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(Investigation.this, (position > lastPosition) ? R.anim.item_animation_fall_down : R.anim.item_animation_fall_down);
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


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Investigation.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(Investigation.this);
                }
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    HashMap<String, String> obj = (HashMap<String, String>) parent.getItemAtPosition(position);
                                    String id = obj.get("i_id");
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

        //new GetNames().execute();
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

        Map<String, String> params = new HashMap<String, String>();
        params.put("i_id", id);
        params.put("api_token", token);

        Log.e(TAG, "ID:" + id + "token:" + token);

        JSONObject parameter = new JSONObject(params);

        mJobManager.addJobInBackground(new PostJob(delete_url, parameter.toString(), Investigation.this));

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
                    Toast.makeText(Investigation.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Investigation.this, msg, Toast.LENGTH_SHORT).show();
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
            if (!InternetConnection.checkConnection(Investigation.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog();
                break;
        }
        return false;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Investigation.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(Investigation.this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkIfSearchByAwc() {
        Intent i = getIntent();
        searchByAwc = i.getBooleanExtra("search_by_awc", false);
    }

    void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        final String awc = preferences.getString("awc", null);
        String awc = SelectAwc.awc;
        Log.d(TAG, "run: getAwc" + awc);

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
            String id = user.get(SessionManager.KEY_ID);

            Log.e(TAG, "TOKEN:" + token);

            if (id != null) {
                if (!searchByAwc) {
                    progressDialog = new ProgressDialog(Investigation.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    timerDelayRemoveDialog(7000, progressDialog);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Investigation.this
                                    .getCacheDir(),
                                    "investigationApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                    call = retrofit.create(InvestigationInterface.class).getResponse("investigate/api/investigate?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();
                    call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(Investigation.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(Investigation.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("investigate");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String i_id = c.getString("i_id");
                                    final String investigator = c.getString("investigator");
                                    final String name = c.getString("name");
                                    final String personal_id = c.getString("personal_id");
                                    final String surname = c.getString("surname");
                                    final String payment_base = c.getString("payment_base");
                                    final String date = c.getString("date");
                                    final String start_date = c.getString("start_date");
                                    final String review_date = c.getString("review_date");
                                    final String army_no = c.getString("army_no");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("i_id", i_id);
                                    contact.put("investigator", investigator);
                                    contact.put("name", name + " " + surname);
                                    contact.put("personal_id", personal_id);
//                                    contact.put("surname", surname);
                                    contact.put("payment_base", payment_base);
                                    contact.put("date", date);
                                    contact.put("start_date", start_date);
                                    contact.put("review_date", review_date);
                                    contact.put("army_no", army_no);


                                    // adding contact to contact list
                                    personlist.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Investigation.this.runOnUiThread(new Runnable() {
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

                                            final String i_id = (String) obj.get("i_id");
                                            final String investigator = (String) obj.get("investigator");
                                            final String personal_id = (String) obj.get("personal_id");
                                            final String name = (String) obj.get("name");
                                            final String surname = (String) obj.get("surname");
                                            final String payment_base = (String) obj.get("payment_base");
                                            final String date = (String) obj.get("date");
                                            final String start_date = (String) obj.get("start_date");
                                            final String review_date = (String) obj.get("review_date");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(Investigation.this, Investigation_details.class);
                                            in.putExtra("i_id", i_id);
                                            in.putExtra("investigator", investigator);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("payment_base", payment_base);
                                            in.putExtra("date", date);
                                            in.putExtra("start_date", start_date);
                                            in.putExtra("review_date", review_date);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Investigation.this.adapter.getFilter().filter(cs);
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
                    progressDialog = new ProgressDialog(Investigation.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Investigation.this
                                    .getCacheDir(),
                                    "UserInvestigationApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(InvestigationInterface.class).getResponse("investigate/api/investigate/" + awc + "?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(Investigation.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(Investigation.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("investigate");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String i_id = c.getString("i_id");
                                    final String investigator = c.getString("investigator");
                                    final String personal_id = c.getString("personal_id");
                                    final String name = c.getString("name");
                                    final String surname = c.getString("surname");
                                    final String payment_base = c.getString("payment_base");
                                    final String date = c.getString("date");
                                    final String start_date = c.getString("start_date");
                                    final String review_date = c.getString("review_date");
                                    final String army_no = c.getString("army_no");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("i_id", i_id);
                                    contact.put("investigator", investigator);
                                    contact.put("personal_id", personal_id);
                                    contact.put("name", name + " " + surname);
//                                    contact.put("surname", surname);
                                    contact.put("payment_base", payment_base);
                                    contact.put("date", date);
                                    contact.put("start_date", start_date);
                                    contact.put("review_date", review_date);
                                    contact.put("army_no", army_no);


                                    // adding contact to contact list
                                    personlist.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Investigation.this.runOnUiThread(new Runnable() {
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

                                            final String i_id = (String) obj.get("i_id");
                                            final String investigator = (String) obj.get("investigator");
                                            final String personal_id = (String) obj.get("personal_id");
                                            final String name = (String) obj.get("name");
                                            final String surname = (String) obj.get("surname");
                                            final String payment_base = (String) obj.get("payment_base");
                                            final String date = (String) obj.get("date");
                                            final String start_date = (String) obj.get("start_date");
                                            final String review_date = (String) obj.get("review_date");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(Investigation.this, Investigation_details.class);
                                            in.putExtra("i_id", i_id);
                                            in.putExtra("investigator", investigator);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("payment_base", payment_base);
                                            in.putExtra("date", date);
                                            in.putExtra("start_date", start_date);
                                            in.putExtra("review_date", review_date);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Investigation.this.adapter.getFilter().filter(cs);
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

       /* if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            //HashMap<String, String> user = sessionManager.getUserDetails();
            String fbId = fbUser.get(SessionManager.KEY_ID);
            token = fbUser.get(SessionManager.KEY_TOKEN);

            if (fbId != null) {
                if (fbId.matches("1")) {
                    progressDialog = new ProgressDialog(Investigation.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Investigation.this
                                    .getCacheDir(),
                                    "investigationApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(InvestigationInterface.class).getResponse("gws/investigate/api/investigate?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();
                    call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(Investigation.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(Investigation.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("investigate");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String i_id = c.getString("i_id");
                                    final String investigator = c.getString("investigator");
                                    final String personal_id = c.getString("personal_id");
                                    final String name = c.getString("name");
                                    final String surname = c.getString("surname");
                                    final String payment_base = c.getString("payment_base");
                                    final String date = c.getString("date");
                                    final String start_date = c.getString("start_date");
                                    final String review_date = c.getString("review_date");
                                    final String army_no = c.getString("army_no");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("i_id", i_id);
                                    contact.put("investigator", investigator);
                                    contact.put("personal_id", personal_id);
                                    contact.put("name", name);
                                    contact.put("surname", surname);
                                    contact.put("payment_base", payment_base);
                                    contact.put("date", date);
                                    contact.put("start_date", start_date);
                                    contact.put("review_date", review_date);
                                    contact.put("army_no", army_no);


                                    // adding contact to contact list
                                    personlist.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Investigation.this.runOnUiThread(new Runnable() {
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

                                            final String i_id = (String) obj.get("i_id");
                                            final String investigator = (String) obj.get("investigator");
                                            final String personal_id = (String) obj.get("personal_id");
                                            final String name = (String) obj.get("name");
                                            final String surname = (String) obj.get("surname");
                                            final String payment_base = (String) obj.get("payment_base");
                                            final String date = (String) obj.get("date");
                                            final String start_date = (String) obj.get("start_date");
                                            final String review_date = (String) obj.get("review_date");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(Investigation.this, Investigation_details.class);
                                            in.putExtra("i_id", i_id);
                                            in.putExtra("investigator", investigator);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("payment_base", payment_base);
                                            in.putExtra("date", date);
                                            in.putExtra("start_date", start_date);
                                            in.putExtra("review_date", review_date);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Investigation.this.adapter.getFilter().filter(cs);
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
                    progressDialog = new ProgressDialog(Investigation.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            // Enable response caching
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .addInterceptor(new OfflineResponseCacheInterceptor())
                            // Set the cache location and size (1 MB)
                            .cache(new Cache(new File(Investigation.this
                                    .getCacheDir(),
                                    "UserInvestigationApiResponses"), 1024 * 1024))
                            .build();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(base_url) // that means base url + the left url in interface
                            .client(okHttpClient)//adding okhttp3 object that we have created
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    call = retrofit.create(InvestigationInterface.class).getResponse("gws/investigate/api/investigate/" + awc + "?api_token=" + token);

                    if (call.isExecuted())
                        call = call.clone();

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Log error here since request failed
                            Log.e("Investigation", t.toString());
                            Toast.makeText(Investigation.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mEmptyStateTextView.setText(R.string.no_data);
                        }

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            try {
                                if (String.valueOf(response.code()).equals("504")) {
                                    Toast.makeText(Investigation.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String myResponse = response.body().string();
                                Log.e("getResponse:", myResponse);
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONArray data = jsonObject.getJSONArray("investigate");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject c = data.getJSONObject(i);

                                    final String i_id = c.getString("i_id");
                                    final String personal_id = c.getString("personal_id");
                                    final String investigator = c.getString("investigator");
                                    final String name = c.getString("name");
                                    final String surname = c.getString("surname");
                                    final String payment_base = c.getString("payment_base");
                                    final String date = c.getString("date");
                                    final String start_date = c.getString("start_date");
                                    final String review_date = c.getString("review_date");
                                    final String army_no = c.getString("army_no");


                                    // tmp hash map for single data
                                    HashMap<String, String> contact = new HashMap<>();

                                    // adding each child node to HashMap key => value

                                    contact.put("i_id", i_id);
                                    contact.put("investigator", investigator);
                                    contact.put("personal_id", personal_id);
                                    contact.put("name", name);
                                    contact.put("surname", surname);
                                    contact.put("payment_base", payment_base);
                                    contact.put("date", date);
                                    contact.put("start_date", start_date);
                                    contact.put("review_date", review_date);
                                    contact.put("army_no", army_no);


                                    // adding contact to contact list
                                    personlist.add(contact);
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            call.cancel();

                            Investigation.this.runOnUiThread(new Runnable() {
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

                                            final String i_id = (String) obj.get("i_id");
                                            final String investigator = (String) obj.get("investigator");
                                            final String personal_id = (String) obj.get("personal_id");
                                            final String name = (String) obj.get("name");
                                            final String surname = (String) obj.get("surname");
                                            final String payment_base = (String) obj.get("payment_base");
                                            final String date = (String) obj.get("date");
                                            final String start_date = (String) obj.get("start_date");
                                            final String review_date = (String) obj.get("review_date");
                                            final String army_no = (String) obj.get("army_no");

                                            Intent in = new Intent(Investigation.this, Investigation_details.class);
                                            in.putExtra("i_id", i_id);
                                            in.putExtra("investigator", investigator);
                                            in.putExtra("personal_id", personal_id);
                                            in.putExtra("name", name);
                                            in.putExtra("surname", surname);
                                            in.putExtra("payment_base", payment_base);
                                            in.putExtra("date", date);
                                            in.putExtra("start_date", start_date);
                                            in.putExtra("review_date", review_date);
                                            in.putExtra("army_no", army_no);
                                            startActivity(in);
                                        }
                                    });
                                    search.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text
                                            Investigation.this.adapter.getFilter().filter(cs);
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
        }*/

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


