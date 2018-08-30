package com.example.android.gurkha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import okhttp3.Cache;
import okhttp3.CacheControl;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Add_investigation extends AppCompatActivity {
    public static EditText person, investigator, paymentbase, date, startdate, reviewdate;
    private static final String url = "http://pagodalabs.com.np/gws/investigate/api/investigate?api_token=";
    Button btn;
    private ArrayList<Object> list;
    private SpinnerAdapter adapt;
    private SearchableSpinner name;
    SearchableSpinner awc;
    private static String base_url = "http://pagodalabs.com.np/";
    private Calendar calendar;
    private ProgressDialog progressDialog;
    private int year, month, day;
    Object[] person_items;
    String token;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    Toolbar toolbar;
    Typeface face;
    Call<ResponseBody> call;
    TextView hiddenDate;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_investigation);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        //   person = (EditText) findViewById(R.id.textperson);
        investigator = (EditText) findViewById(R.id.textinvestigator);
        paymentbase = (EditText) findViewById(R.id.textpaymentbase);
        date = (EditText) findViewById(R.id.textdate);
        startdate = (EditText) findViewById(R.id.textstartdate);
        reviewdate = (EditText) findViewById(R.id.textreviewdate);
        list = new ArrayList<>();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        name = (SearchableSpinner) findViewById(R.id.textperson);
        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "Kaski", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        awc.setAdapter(adapt_awc);


        //ArrayAdapter<String> adapt_person = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        //adapt_person.notifyDataSetChanged();

        btn = (Button) findViewById(R.id.btn);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartDate(v);
            }
        });

        reviewdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReviewDate(v);
            }
        });
/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new GetDataTask().execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
*/

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mJobManager = GurkhaApplication.getInstance().getJobManager();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mPerson = name.getSelectedItem().toString();
                    String id = mPerson.substring(mPerson.lastIndexOf(":") + 1);
                    String mInvestigator = investigator.getText().toString().trim();
                    String mPaymentbase = paymentbase.getText().toString().trim();
                    String mDate = date.getText().toString().trim();
                    String mStartdate = startdate.getText().toString().trim();
                    String mReviewdate = reviewdate.getText().toString().trim();
                    String mAwc = awc.getSelectedItem().toString().trim();
                    Log.i("start Date:",mStartdate);

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();

                    if(sessionManager.getUserDetails() != null) {
                        HashMap<String, String> user = sessionManager.getUserDetails();
                        token = user.get(SessionManager.KEY_TOKEN);
                    }
                    if(fbSessionManager.getUserDetails() != null) {
                        HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                        if(fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                    }


                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("date", mDate);
                    params.put("start_date", mStartdate);
                    params.put("personal_id", id);
                    params.put("investigator", mInvestigator);
                    params.put("payment_base", mPaymentbase);
                    params.put("review_date", mReviewdate);
                    params.put("awc", mAwc);
                    params.put("created_at", mHiddenDate);
                    params.put("api_token", token);

                if(mAwc.equals("Select Area Welfare Center")){
                    Toast.makeText(Add_investigation.this, "Please select area welfare center", Toast.LENGTH_SHORT).show();
                    return;
                }

                    JSONObject parameter = new JSONObject(params);
                    Log.e("JSON:", parameter.toString());

//                    OkHttpClient client = new OkHttpClient();

                    mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));

                   /* final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("content-type", "application/json; charset=utf-8")
                            .build();

                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.e("response", call.request().body().toString());
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.e("response", response.body().string());
                            }
                        }

                    });
*/
                    Toast.makeText(Add_investigation.this, "Investigation Details Added", Toast.LENGTH_SHORT).show();
                    finish();
                /*} else {
                    Toast.makeText(Add_investigation.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }*/
            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
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
            if (!InternetConnection.checkConnection(Add_investigation.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(Add_investigation.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Add_investigation.ResponseCacheInterceptor())
                .addInterceptor(new Add_investigation.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Add_investigation.this
                        .getCacheDir(),
                        "addInvestigationApiResponses"), 1024 * 1024))
                .build();


        if(sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if(fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if(fbUser.get(SessionManager.KEY_TOKEN) != null)
            token = fbUser.get(SessionManager.KEY_TOKEN);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Add_investigation", t.toString());
                Toast.makeText(Add_investigation.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())){
                        Toast.makeText(Add_investigation.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
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
                        String personal_id = innerObject.getString("personal_id");

                        list.add(name + " " + surname + " " + army_no + "," + " " + "id:" + personal_id);
                        person_items = list.toArray();

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                Add_investigation.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Add_investigation.this, R.layout.support_simple_spinner_dropdown_item, person_items){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        name.setAdapter(adapt_person);
                    }

                });
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setStartDate(View view) {
        showDialog(777);
    }

    @SuppressWarnings("deprecation")
    public void setReviewDate(View view) {
        showDialog(555);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    dateListener, year, month, day);
        }
        if (id == 777) {
            return new DatePickerDialog(this,
                    startDateListener, year, month, day);
        }

        if (id == 555) {
            return new DatePickerDialog(this,
                    reviewDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener startDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showStartDate(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener reviewDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showReviewDate(arg3, arg2 + 1, arg1);
                }
            };

    private void showDate(int day, int month, int year) {

        date.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showStartDate(int day, int month, int year) {
        startdate.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showReviewDate(int day, int month, int year) {
        reviewdate.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

}


