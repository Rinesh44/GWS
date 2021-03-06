package com.example.android.gurkha.activities.AddData;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.SpinnerAdapter;
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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


public class Add_payment extends AppCompatActivity implements ResponseListener {
    EditText paiddate, paid, unpaid_amount, itemsgiven, category, total_cost, granted_amount,
            date_granted_over, pvno, sponser_name;
    Button button;
    private static final String url = "http://gws.pagodalabs.com.np/payment_distribution/api/payment_distribution?api_token=";
    private ArrayList<Object> list;
    SearchableSpinner name;
    SearchableSpinner awc;
    private SpinnerAdapter adapter;
    private Calendar calendar;
    private ProgressDialog progressDialog;
    private static String base_url = "http://gws.pagodalabs.com.np/";
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
        setContentView(R.layout.activity_add_payment);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        final Spinner grant = (Spinner) findViewById(R.id.textgrant);
        String[] grant_items = new String[]{"GRANT", "ADF/EQRT", "ADF/HARDSHIP", "EQRT", "HARDSHIP", "ISOS", "ISOS/ADF"};
        ArrayAdapter<String> adapt_grant = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, grant_items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        grant.setAdapter(adapt_grant);
        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "Kaski", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        awc.setAdapter(adapt_awc);

        name = (SearchableSpinner) findViewById(R.id.textperson);
        /*
        ArrayAdapter<String> adapt_person = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        name.setAdapter(adapt_person);
*/

        paiddate = (EditText) findViewById(R.id.textpaiddate);
        paid = (EditText) findViewById(R.id.textpaid);
        unpaid_amount = (EditText) findViewById(R.id.textunpaidamount);
        category = (EditText) findViewById(R.id.textcategory);
        total_cost = (EditText) findViewById(R.id.texttotalcost);
        itemsgiven = (EditText) findViewById(R.id.textitemsgiven);
        granted_amount = (EditText) findViewById(R.id.textgrantedamount);
        date_granted_over = (EditText) findViewById(R.id.textdategrantedover);
        pvno = (EditText) findViewById(R.id.textpvno);
        sponser_name = (EditText) findViewById(R.id.textsponsername);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        list = new ArrayList<>();
        button = (Button) findViewById(R.id.btn);

        paid.requestFocus();


        total_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!paid.getText().toString().isEmpty() && !unpaid_amount.getText().toString().isEmpty()){
                    Double paidAmount = Double.valueOf(paid.getText().toString());
                    Double unpaidAmount = Double.valueOf(unpaid_amount.getText().toString());
                    Double cost = paidAmount + unpaidAmount;
                    total_cost.setText(String.valueOf(cost));
                }
            }
        });




        paiddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaidDate(v);
            }
        });

        date_granted_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateGrantedOver(v);
            }
        });

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new GetDataTask().execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
*/

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (InternetConnection.checkConnection(getApplicationContext())) {
                String mPerson = name.getSelectedItem().toString();
                String id = mPerson.substring(mPerson.lastIndexOf(":") + 1);
                String mpaiddate = paiddate.getText().toString();
                String mpaid = paid.getText().toString();
                String munpaidamount = unpaid_amount.getText().toString();
                String mcategory = category.getText().toString();
                String mtotalcost = total_cost.getText().toString();
                String mitemsgiven = itemsgiven.getText().toString();
                String mgranted_amount = granted_amount.getText().toString();
                String mdategrantedover = date_granted_over.getText().toString();
                String mpvno = pvno.getText().toString();
                String msponsername = sponser_name.getText().toString();
                String mgrant = grant.getSelectedItem().toString();
                String mAwc = awc.getSelectedItem().toString();

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                hiddenDate.setText(formattedDate);

                if (sessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                }

                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                    if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                }

                String mHiddenDate = hiddenDate.getText().toString().trim();

//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                Map<String, String> params = new HashMap<String, String>();
                params.put("personal_id", id);
                params.put("paid_date", mpaiddate);
                params.put("paid", mpaid);
                params.put("unpaid_amount", munpaidamount);
                params.put("items_given", mitemsgiven);
                params.put("category", mcategory);
                params.put("total_cost", mtotalcost);
                params.put("date_handed_over", mdategrantedover);
                params.put("granted_amount", mgranted_amount);
                params.put("sponsor_name", msponsername);
                params.put("pv_no", mpvno);
                params.put("grant", mgrant);
                params.put("awc", mAwc);
                params.put("created_at", mHiddenDate);
                params.put("api_token", token);

                if (mAwc.equals("Select Area Welfare Center")) {
                    Toast.makeText(Add_payment.this, "Please select Area welfare center", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mgrant.equals("GRANT")) {
                    Toast.makeText(Add_payment.this, "Please select grant", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject parameter = new JSONObject(params);

//                    OkHttpClient client = new OkHttpClient();

                Log.e("JSON:", parameter.toString());

                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), Add_payment.this));

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
                            Log.e("response", response.body().string());
                        }

                    });*/
                Toast.makeText(Add_payment.this, "Payment Details Added", Toast.LENGTH_SHORT).show();
                finish();
               /* } else {
                    Toast.makeText(Add_payment.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }*/
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    public void responseSuccess(okhttp3.Response response) {

    }

    @Override
    public void responseFail(String msg) {

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
            if (!InternetConnection.checkConnection(Add_payment.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(Add_payment.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Add_payment.ResponseCacheInterceptor())
                .addInterceptor(new Add_payment.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Add_payment.this
                        .getCacheDir(),
                        "addPaymentApiResponses"), 1024 * 1024))
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


        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Add_payment", t.toString());
                Toast.makeText(Add_payment.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (!(response.isSuccessful())) {
                        Toast.makeText(Add_payment.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
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

                Add_payment.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Add_payment.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
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
    public void setPaidDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setDateGrantedOver(View view) {
        showDialog(777);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myPaidDateListener, year, month, day);
        }
        if (id == 777) {
            return new DatePickerDialog(this,
                    myDateGrantedOverListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myPaidDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showPaidDate(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener myDateGrantedOverListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDateGrantedOver(arg3, arg2 + 1, arg1);
                }
            };

    private void showPaidDate(int day, int month, int year) {

        paiddate.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showDateGrantedOver(int day, int month, int year) {
        date_granted_over.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

}