package com.example.android.gurkha.activities.QnA;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

public class SocialPoll extends AppCompatActivity implements ResponseListener {
    SearchableSpinner name;
    private static String base_url = "http://gws.pagodalabs.com.np/";
    private ArrayList<Object> list;
    private static final String url = "http://gws.pagodalabs.com.np/form/api/form/2?api_token=";
    ProgressDialog progressDialog;
    RelativeLayout layout;
    String token;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    Toolbar toolbar;
    static String id, personalId;
    RadioGroup rgVillageLocation, rgVillageLocation1, rgWaterSupply, rgHealth, rgHealthService, rgCommunication, rgTransport, rgElectricity, rgAwcTravel;
    EditText distanceToSource, distanceToHealth, travelTime;
    Call<ResponseBody> call;
    Button save;
    RadioButton spring, stream;
    Object[] person_items;
    Typeface face;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        name = (SearchableSpinner) findViewById(R.id.textPerson);
        list = new ArrayList<>();
        save = findViewById(R.id.save);
        layout = findViewById(R.id.layout_distance);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        rgVillageLocation1 = findViewById(R.id.rg_location1);
        rgWaterSupply = findViewById(R.id.rg_water_supply1);
        rgHealth = findViewById(R.id.rg_health);
        rgHealthService = findViewById(R.id.rg_service_provided);
        rgCommunication = findViewById(R.id.rg_communication);
        rgTransport = findViewById(R.id.rg_transport);
        rgElectricity = findViewById(R.id.rg_electricity);
        rgAwcTravel = findViewById(R.id.rg_awc_travel);

        spring = findViewById(R.id.rg_spring);
        stream = findViewById(R.id.rg_stream);

        distanceToSource = findViewById(R.id.et_distance_to_source);
        distanceToHealth = findViewById(R.id.distanceToHealth);
        travelTime = findViewById(R.id.et_travel_time);

        rgVillageLocation = findViewById(R.id.rg1);
        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rgWaterSupply.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if ((i == R.id.rg_spring) | i == R.id.rg_stream) {
                    layout.setVisibility(View.VISIBLE);
                } else
                    layout.setVisibility(View.GONE);
            }
        });


        mJobManager = GurkhaApplication.getInstance().getJobManager();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                  /*  if ((rgVillageLocation.getCheckedRadioButtonId() == -1) | (rgVillageLocation1.getCheckedRadioButtonId() == -1) | (rgWaterSupply.getCheckedRadioButtonId() == -1)
                            | (rgHealth.getCheckedRadioButtonId() == -1) | (rgHealthService.getCheckedRadioButtonId() == -1)
                            | (rgCommunication.getCheckedRadioButtonId() == -1) | (rgTransport.getCheckedRadioButtonId() == -1) | (rgElectricity.getCheckedRadioButtonId() == -1) | (rgAwcTravel.getCheckedRadioButtonId() == -1)) {
                        Toast.makeText(SocialPoll.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                    } else {*/

                personalId = name.getSelectedItem().toString();
                id = personalId.substring(personalId.lastIndexOf(":") + 1);

                int villageLocationId = rgVillageLocation.getCheckedRadioButtonId();
                RadioButton selectedVillageLocatonButton = (RadioButton) findViewById(villageLocationId);
                String mVillageLocation = selectedVillageLocatonButton.getText().toString();

                int villageLocationId1 = rgVillageLocation1.getCheckedRadioButtonId();
                RadioButton selectedVillageLocatonButton1 = (RadioButton) findViewById(villageLocationId1);
                String mVillageLocation1 = selectedVillageLocatonButton1.getText().toString();

                int waterSupplyId = rgWaterSupply.getCheckedRadioButtonId();
                RadioButton selectedWaterSupply = (RadioButton) findViewById(waterSupplyId);
                String mWaterSupply = selectedWaterSupply.getText().toString();

                if (layout.getVisibility() == View.VISIBLE) {
                    String mDistanceToSource = distanceToSource.getText().toString();
                }

                String mDistanceToSource = distanceToSource.getText().toString();

                int healthPostId = rgHealth.getCheckedRadioButtonId();
                RadioButton selectedHealth = (RadioButton) findViewById(healthPostId);
                String mHealthPost = selectedHealth.getText().toString();

                String mDistanceToPost = distanceToHealth.getText().toString();

                int healthServiceId = rgHealthService.getCheckedRadioButtonId();
                RadioButton selectedHealthServ = (RadioButton) findViewById(healthServiceId);
                String mHealthServ = selectedHealthServ.getText().toString();

                int communicationId = rgCommunication.getCheckedRadioButtonId();
                RadioButton selectedCommunication = (RadioButton) findViewById(communicationId);
                String mCommunication = selectedCommunication.getText().toString();

                int transportId = rgTransport.getCheckedRadioButtonId();
                RadioButton selectedTransport = (RadioButton) findViewById(transportId);
                String mTransport = selectedTransport.getText().toString();

                int electricityId = rgElectricity.getCheckedRadioButtonId();
                RadioButton selectedElectricity = (RadioButton) findViewById(electricityId);
                String mElectricity = selectedElectricity.getText().toString();

                int homeToAwcId = rgAwcTravel.getCheckedRadioButtonId();
                RadioButton selectedAwcTravel = (RadioButton) findViewById(homeToAwcId);
                String mHomeToAwc = selectedAwcTravel.getText().toString();

                String mTravelTime = travelTime.getText().toString();

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                Map<String, String> vLocation = new HashMap<String, String>();
                vLocation.put("form_id", String.valueOf(2));
                vLocation.put("field_name", "village_location");
                vLocation.put("value", mVillageLocation);
                vLocation.put("personal_id", id);

                Map<String, String> vLocation1 = new HashMap<String, String>();
                vLocation1.put("form_id", String.valueOf(2));
                vLocation1.put("field_name", "area");
                vLocation1.put("value", mVillageLocation1);
                vLocation1.put("personal_id", id);

                Map<String, String> dToSource = new HashMap<String, String>();
                dToSource.put("form_id", String.valueOf(2));
                dToSource.put("field_name", "if_spring_or_stram_how_far?");
                if (layout.getVisibility() == View.VISIBLE) {
                    dToSource.put("value", mDistanceToSource);
                } else
                    dToSource.put("value", "");
                vLocation.put("personal_id", id);

                Map<String, String> pHealth = new HashMap<String, String>();
                pHealth.put("form_id", String.valueOf(2));
                pHealth.put("field_name", "health_facilities");
                pHealth.put("value", mHealthPost);
                pHealth.put("personal_id", id);

                Map<String, String> dHealth = new HashMap<String, String>();
                dHealth.put("form_id", String.valueOf(2));
                dHealth.put("field_name", "distance_");
                dHealth.put("value", mDistanceToPost);
                dHealth.put("personal_id", id);

                Map<String, String> sHealth = new HashMap<String, String>();
                sHealth.put("form_id", String.valueOf(2));
                sHealth.put("field_name", "health_service_provided");
                sHealth.put("value", mHealthServ);
                sHealth.put("personal_id", id);

                Map<String, String> wSupply = new HashMap<String, String>();
                wSupply.put("form_id", String.valueOf(2));
                wSupply.put("field_name", "water_supply");
                wSupply.put("value", mWaterSupply);
                wSupply.put("personal_id", id);

                Map<String, String> communication = new HashMap<String, String>();
                communication.put("form_id", String.valueOf(2));
                communication.put("field_name", "communication(tel_access)");
                communication.put("value", mCommunication);
                communication.put("personal_id", id);

                Map<String, String> transport = new HashMap<String, String>();
                transport.put("form_id", String.valueOf(2));
                transport.put("field_name", "road_transport");
                transport.put("value", mTransport);
                transport.put("personal_id", id);

                Map<String, String> electricity = new HashMap<String, String>();
                electricity.put("form_id", String.valueOf(2));
                electricity.put("field_name", "electricity");
                electricity.put("value", mElectricity);
                electricity.put("personal_id", id);

                Map<String, String> dTravelAwc = new HashMap<String, String>();
                dTravelAwc.put("form_id", String.valueOf(2));
                dTravelAwc.put("field_name", "home_to_awc_travel");
                dTravelAwc.put("value", mHomeToAwc);
                dTravelAwc.put("personal_id", id);

                Map<String, String> tTravel = new HashMap<String, String>();
                tTravel.put("form_id", String.valueOf(2));
                tTravel.put("field_name", "specify_walking_and_bus/jeep_travel_time");
                tTravel.put("value", mTravelTime);
                tTravel.put("personal_id", id);

                if (sessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                }
                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = fbSessionManager.getUserDetails();
                    if (user.get(SessionManager.KEY_TOKEN) != null)
                        token = user.get(SessionManager.KEY_TOKEN);
                }

                JSONObject jsonLocation1 = new JSONObject(vLocation);
                JSONObject jsonLocation2 = new JSONObject(vLocation1);
                JSONObject jsonDToSource = new JSONObject(dToSource);
                JSONObject jsonPHealth = new JSONObject(pHealth);
                JSONObject jsonDHealth = new JSONObject(dHealth);
                JSONObject jsonSHealth = new JSONObject(sHealth);
                JSONObject jsonWSupply = new JSONObject(wSupply);
                JSONObject jsonCommunication = new JSONObject(communication);
                JSONObject jsonTransport = new JSONObject(transport);
                JSONObject jsonElectricity = new JSONObject(electricity);
                JSONObject jsonTravelAwc = new JSONObject(dTravelAwc);
                JSONObject jsonTravel = new JSONObject(tTravel);

                JSONObject parameter = new JSONObject();
                try {
                    parameter.put("0", jsonLocation1);
                    parameter.put("1", jsonLocation2);
                    parameter.put("2", jsonDToSource);
                    parameter.put("3", jsonPHealth);
                    parameter.put("4", jsonDHealth);
                    parameter.put("5", jsonSHealth);
                    parameter.put("6", jsonWSupply);
                    parameter.put("7", jsonCommunication);
                    parameter.put("8", jsonTransport);
                    parameter.put("9", jsonElectricity);
                    parameter.put("10", jsonTravelAwc);
                    parameter.put("11", jsonTravel);
                    parameter.put("api_token", token);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e("JSON:", parameter.toString());

                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), SocialPoll.this));


                       /* OkHttpClient client = new OkHttpClient();
                        final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();

                        client.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                Log.e("socialResponse", call.request().body().toString());
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.e("socialResponse", response.body().string());
                                }
                            }

                        });*/

                Toast.makeText(SocialPoll.this, "Details Added", Toast.LENGTH_SHORT).show();
                finish();
//                    }
                /*} else {
                    Toast.makeText(SocialPoll.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
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
            if (!InternetConnection.checkConnection(SocialPoll.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(SocialPoll.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new SocialPoll.ResponseCacheInterceptor())
                .addInterceptor(new SocialPoll.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(SocialPoll.this
                        .getCacheDir(),
                        "addInvestigationApiResponses"), 1024 * 1024))
                .build();

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> user = fbSessionManager.getUserDetails();
            if (user.get(SessionManager.KEY_TOKEN) != null)
                token = user.get(SessionManager.KEY_TOKEN);
        }

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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("SocialPoll", t.toString());
                Toast.makeText(SocialPoll.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(SocialPoll.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
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

                SocialPoll.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(SocialPoll.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
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

}
