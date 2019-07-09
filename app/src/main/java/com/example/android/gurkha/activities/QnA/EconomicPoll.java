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

public class EconomicPoll extends AppCompatActivity implements ResponseListener {
    Typeface face;
    SearchableSpinner name;
    ProgressDialog progressDialog;
    RadioButton cattleYes, noHouse, otherIncome;
    Map<String, String> vBuffalos, vGoats, vCows, vPigs, vOxen, vOthers;
    private ArrayList<Object> list;
    JSONObject jsonBuffalos, jsonCows, jsonOxen, jsonGoats, jsonPigs, jsonOthers;
    Call<ResponseBody> call;
    String token;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    Toolbar toolbar;
    String mHouseName, mIncomeSource, mIncomeValue;
    EditText landRopani, cornValue, riceValue, milletValue, wheatValue, barleyValue, mustardValue, othersValue, houseValue, buffalosValue, goatsValue, cowsValue, pigsValue, oxenValue, othersValueCattle, houseName, incomeSource, incomeValue;
    static String id, personalId;
    String mBuffalo, mGoats, mCows, mPigs, mOxen, mOtherCattle;
    Object[] person_items;
    RelativeLayout houseLayout, incomeLayout, layout;
    private static final String url = "http://gws.pagodalabs.com.np/form/api/form/3?api_token=";
    Button save;
    JobManager mJobManager;
    RadioGroup rg_dependent, rb2, rg_land, rg_cattle, rb_1, rb_2, rb_3, rg_cash_income;
    private static String base_url = "http://gws.pagodalabs.com.np/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_economic_poll);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        houseLayout = findViewById(R.id.house_layout);
        incomeLayout = findViewById(R.id.layout_other_income);
        layout = findViewById(R.id.layout);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        rg_dependent = findViewById(R.id.rg_dependent);
        rb2 = findViewById(R.id.rb2);
        rg_land = findViewById(R.id.rg_land);
        rg_cattle = findViewById(R.id.rg_cattle);
        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rg_cash_income = findViewById(R.id.rg_cash_income);

        cattleYes = findViewById(R.id.rb_cattle_yes);
        noHouse = findViewById(R.id.rb_no_house);
        otherIncome = findViewById(R.id.rb_other_income_yes);

        landRopani = findViewById(R.id.et_land_ropani);
        landRopani.requestFocus();
        cornValue = findViewById(R.id.corn_value);
        riceValue = findViewById(R.id.rice_value);
        milletValue = findViewById(R.id.millet_value);
        wheatValue = findViewById(R.id.wheat_value);
        barleyValue = findViewById(R.id.barley_value);
        mustardValue = findViewById(R.id.mustard_value);
        othersValue = findViewById(R.id.others_value);
        houseValue = findViewById(R.id.et_house_value);

        rg_cattle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_cattle_yes) {
                    layout.setVisibility(View.VISIBLE);
                    buffalosValue = findViewById(R.id.buffalos_value);
                    goatsValue = findViewById(R.id.goats_value);
                    cowsValue = findViewById(R.id.cows_value);
                    pigsValue = findViewById(R.id.pigs_value);
                    oxenValue = findViewById(R.id.oxen_value);
                    othersValueCattle = findViewById(R.id.other_cattle_value);
                } else
                    layout.setVisibility(View.GONE);
            }
        });

        rb_3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_no_house) {
                    houseLayout.setVisibility(View.VISIBLE);
                    houseName = findViewById(R.id.et_house_name);
                } else
                    houseLayout.setVisibility(View.GONE);
            }
        });

        rg_cash_income.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_other_income_yes) {
                    incomeLayout.setVisibility(View.VISIBLE);
                    incomeSource = findViewById(R.id.et_income_source);
                    incomeValue = findViewById(R.id.rs_value);
                } else
                    incomeLayout.setVisibility(View.GONE);
            }
        });


        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        list = new ArrayList<>();

        name = (SearchableSpinner) findViewById(R.id.textPerson);
        save = findViewById(R.id.save);

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mJobManager = GurkhaApplication.getInstance().getJobManager();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
               /* if ((rb_1.getCheckedRadioButtonId() == -1) | (rb_2.getCheckedRadioButtonId() == -1) | (rb_3.getCheckedRadioButtonId() == -1)
                        | (rg_cash_income.getCheckedRadioButtonId() == -1) | (rg_cattle.getCheckedRadioButtonId() == -1) | (rg_land.getCheckedRadioButtonId() == -1)
                        | (rb2.getCheckedRadioButtonId() == -1) | (rg_dependent.getCheckedRadioButtonId() == -1)) {
                    Toast.makeText(EconomicPoll.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                } else {*/

                    personalId = name.getSelectedItem().toString();
                    id = personalId.substring(personalId.lastIndexOf(":") + 1);


                    int dependentWp = rg_dependent.getCheckedRadioButtonId();
                    RadioButton selectedDependentWp = (RadioButton) findViewById(dependentWp);
                    String mDependentWp = selectedDependentWp.getText().toString();

                    int typeId = rg_land.getCheckedRadioButtonId();
                    RadioButton selectedType = (RadioButton) findViewById(typeId);
                    String mType = selectedType.getText().toString();

                    int regionId = rb2.getCheckedRadioButtonId();
                    RadioButton selectedRegion = (RadioButton) findViewById(regionId);
                    String mRegion = selectedRegion.getText().toString();

                    String mLandRopani = landRopani.getText().toString();

                    String mCornValue = cornValue.getText().toString();
                    String mRiceValue = riceValue.getText().toString();
                    String mMilletValue = milletValue.getText().toString();
                    String mWheatValue = wheatValue.getText().toString();
                    String mBarleyValue = barleyValue.getText().toString();
                    String mMustardValue = mustardValue.getText().toString();
                    String mOtherValue = othersValue.getText().toString();

                    int cattleId = rg_cattle.getCheckedRadioButtonId();
                    RadioButton selectedCattle = (RadioButton) findViewById(cattleId);
                    String mCattle = selectedCattle.getText().toString();

                    if (layout.getVisibility() == View.VISIBLE) {
                        mBuffalo = buffalosValue.getText().toString();
                        mGoats = goatsValue.getText().toString();
                        mCows = cowsValue.getText().toString();
                        mPigs = pigsValue.getText().toString();
                        mOxen = oxenValue.getText().toString();
                        mOtherCattle = othersValueCattle.getText().toString();
                    }

                    String mHouseValue = houseValue.getText().toString();

                    int houseMaterialId = rb_1.getCheckedRadioButtonId();
                    RadioButton selectedMaterial = (RadioButton) findViewById(houseMaterialId);
                    String mHouseMaterial = selectedMaterial.getText().toString();


                    int houseRoofId = rb_2.getCheckedRadioButtonId();
                    RadioButton selectedRoof = (RadioButton) findViewById(houseRoofId);
                    String mHouseRoof = selectedRoof.getText().toString();


                    int roofCondition = rb_3.getCheckedRadioButtonId();
                    RadioButton selectedCondition = (RadioButton) findViewById(roofCondition);
                    String mRoofCondition = selectedCondition.getText().toString();

                    int cashIncome = rg_cash_income.getCheckedRadioButtonId();
                    RadioButton selectedCashIncome = (RadioButton) findViewById(cashIncome);
                    String mCashIncome = selectedCashIncome.getText().toString();

                    if (houseLayout.getVisibility() == View.VISIBLE) {
                        mHouseName = houseName.getText().toString();
                    }

                    if (incomeLayout.getVisibility() == View.VISIBLE) {
                        mIncomeSource = incomeSource.getText().toString();
                        mIncomeValue = incomeValue.getText().toString();
                    }

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                    Map<String, String> vDependent = new HashMap<String, String>();
                    vDependent.put("form_id", String.valueOf(3));
                    vDependent.put("field_name", "dependent_on_wp");
                    vDependent.put("value", mDependentWp);
                    vDependent.put("personal_id", id);

                    Map<String, String> vLandType = new HashMap<String, String>();
                    vLandType.put("form_id", String.valueOf(3));
                    vLandType.put("field_name", "land_type");
                    vLandType.put("value", mType);
                    vLandType.put("personal_id", id);

                    Map<String, String> vRopani = new HashMap<String, String>();
                    vRopani.put("form_id", String.valueOf(3));
                    vRopani.put("field_name", "ropani");
                    vRopani.put("value", mLandRopani);
                    vRopani.put("personal_id", id);

                    Map<String, String> vRegion = new HashMap<String, String>();
                    vRegion.put("form_id", String.valueOf(3));
                    vRegion.put("field_name", "cultivating_land");
                    vRegion.put("value", mRegion);
                    vRegion.put("personal_id", id);

                    Map<String, String> vCorn = new HashMap<String, String>();
                    vCorn.put("form_id", String.valueOf(3));
                    vCorn.put("field_name", "corn");
                    vCorn.put("value", mCornValue);
                    vCorn.put("personal_id", id);

                    Map<String, String> vRice = new HashMap<String, String>();
                    vRice.put("form_id", String.valueOf(3));
                    vRice.put("field_name", "rice");
                    vRice.put("value", mRiceValue);
                    vRice.put("personal_id", id);

                    Map<String, String> vMillet = new HashMap<String, String>();
                    vMillet.put("form_id", String.valueOf(3));
                    vMillet.put("field_name", "millet");
                    vMillet.put("value", mMilletValue);
                    vMillet.put("personal_id", id);

                    Map<String, String> vWheat = new HashMap<String, String>();
                    vWheat.put("form_id", String.valueOf(3));
                    vWheat.put("field_name", "wheat");
                    vWheat.put("value", mWheatValue);
                    vWheat.put("personal_id", id);

                    Map<String, String> vBarley = new HashMap<String, String>();
                    vBarley.put("form_id", String.valueOf(3));
                    vBarley.put("field_name", "barly");
                    vBarley.put("value", mBarleyValue);
                    vBarley.put("personal_id", id);

                    Map<String, String> vMustard = new HashMap<String, String>();
                    vMustard.put("form_id", String.valueOf(3));
                    vMustard.put("field_name", "mustard");
                    vMustard.put("value", mMustardValue);
                    vMustard.put("personal_id", id);

                    Map<String, String> vOther = new HashMap<String, String>();
                    vOther.put("form_id", String.valueOf(3));
                    vOther.put("field_name", "others");
                    vOther.put("value", mOtherValue);
                    vOther.put("personal_id", id);

                    Map<String, String> vCattle = new HashMap<String, String>();
                    vCattle.put("form_id", String.valueOf(3));
                    vCattle.put("field_name", "cattle");
                    vCattle.put("value", mCattle);
                    vCattle.put("personal_id", id);

                    if (layout != null) {
                        vBuffalos = new HashMap<String, String>();
                        vBuffalos.put("form_id", String.valueOf(3));
                        vBuffalos.put("field_name", "buffalos");
                        vBuffalos.put("value", mBuffalo);
                        vBuffalos.put("personal_id", id);

                        vGoats = new HashMap<String, String>();
                        vGoats.put("form_id", String.valueOf(3));
                        vGoats.put("field_name", "goats");
                        vGoats.put("value", mGoats);
                        vGoats.put("personal_id", id);

                        vCows = new HashMap<String, String>();
                        vCows.put("form_id", String.valueOf(3));
                        vCows.put("field_name", "cows");
                        vCows.put("value", mCows);
                        vCows.put("personal_id", id);

                        vPigs = new HashMap<String, String>();
                        vPigs.put("form_id", String.valueOf(3));
                        vPigs.put("field_name", "pigs");
                        vPigs.put("value", mPigs);
                        vPigs.put("personal_id", id);

                        vOxen = new HashMap<String, String>();
                        vOxen.put("form_id", String.valueOf(3));
                        vOxen.put("field_name", "oxen");
                        vOxen.put("value", mOxen);
                        vOxen.put("personal_id", id);

                        vOthers = new HashMap<String, String>();
                        vOthers.put("form_id", String.valueOf(3));
                        vOthers.put("field_name", "others");
                        vOthers.put("value", mOtherCattle);
                        vOthers.put("personal_id", id);
                    }


                    Map<String, String> vHouse = new HashMap<String, String>();
                    vHouse.put("form_id", String.valueOf(3));
                    vHouse.put("field_name", "house_value_rs");
                    vHouse.put("value", mHouseValue);
                    vHouse.put("personal_id", id);

                    Map<String, String> vHouseType = new HashMap<String, String>();
                    vHouseType.put("form_id", String.valueOf(3));
                    vHouseType.put("field_name", "house_type");
                    vHouseType.put("value", mHouseMaterial);
                    vHouseType.put("personal_id", id);

                    Map<String, String> vHouseRoof = new HashMap<String, String>();
                    vHouseRoof.put("form_id", String.valueOf(3));
                    vHouseRoof.put("field_name", "house_roof");
                    vHouseRoof.put("value", mHouseRoof);
                    vHouseRoof.put("personal_id", id);

                    Map<String, String> vHouseName = new HashMap<String, String>();
                    vHouseName.put("form_id", String.valueOf(3));
                    vHouseName.put("field_name", "where_is_he_or_she_living");
                    vHouseName.put("value", mHouseName);
                    vHouseName.put("personal_id", id);

                    Map<String, String> vIncome = new HashMap<String, String>();
                    vIncome.put("form_id", String.valueOf(3));
                    vIncome.put("field_name", "where_is_he_or_she_living");
                    vIncome.put("value", mCashIncome);
                    vIncome.put("personal_id", id);

                    Map<String, String> vIncomeSource = new HashMap<String, String>();
                    vIncomeSource.put("form_id", String.valueOf(3));
                    vIncomeSource.put("field_name", "name_of_source");
                    vIncomeSource.put("value", mIncomeSource);
                    vIncomeSource.put("personal_id", id);

                    Map<String, String> vIncomeValue = new HashMap<String, String>();
                    vIncomeValue.put("form_id", String.valueOf(3));
                    vIncomeValue.put("field_name", "how_much_per_month_or_year");
                    vIncomeValue.put("value", mIncomeValue);
                    vIncomeValue.put("personal_id", id);

                    if (sessionManager.getUserDetails() != null) {
                        HashMap<String, String> user = sessionManager.getUserDetails();
                        token = user.get(SessionManager.KEY_TOKEN);
                    }
                    if (fbSessionManager.getUserDetails() != null) {
                        HashMap<String, String> user = fbSessionManager.getUserDetails();
                        if (user.get(SessionManager.KEY_TOKEN) != null)
                            token = user.get(SessionManager.KEY_TOKEN);
                    }

                    JSONObject jsonHouse = new JSONObject(vHouse);
                    JSONObject jsonHouseType = new JSONObject(vHouseType);
                    JSONObject jsonHouseRoof = new JSONObject(vHouseRoof);
                    JSONObject jsonHouseName = new JSONObject(vHouseName);
                    JSONObject jsonIncome = new JSONObject(vIncome);
                    JSONObject jsonIncomeSource = new JSONObject(vIncomeSource);
                    JSONObject jsonIncomeValue = new JSONObject(vIncomeValue);
                    JSONObject jsonDependent = new JSONObject(vDependent);
                    JSONObject jsonLandRopani = new JSONObject(vRopani);
                    JSONObject jsonRegion = new JSONObject(vRegion);
                    JSONObject jsonLandType = new JSONObject(vLandType);

                    JSONObject jsonCorn = new JSONObject(vCorn);
                    JSONObject jsonRice = new JSONObject(vRice);
                    JSONObject jsonMillet = new JSONObject(vMillet);
                    JSONObject jsonWheat = new JSONObject(vWheat);
                    JSONObject jsonBarley = new JSONObject(vBarley);
                    JSONObject jsonMustard = new JSONObject(vMustard);
                    JSONObject jsonOther = new JSONObject(vOther);

                    JSONObject jsonCattle = new JSONObject(vCattle);
                    if (layout != null) {
                        jsonBuffalos = new JSONObject(vBuffalos);
                        jsonCows = new JSONObject(vCows);
                        jsonOxen = new JSONObject(vOxen);
                        jsonGoats = new JSONObject(vGoats);
                        jsonPigs = new JSONObject(vPigs);
                        jsonOthers = new JSONObject(vOthers);
                    }


                    JSONObject parameter = new JSONObject();
                    try {
                        parameter.put("0", jsonDependent);
                        parameter.put("1", jsonLandRopani);
                        parameter.put("2", jsonRegion);
                        parameter.put("3", jsonLandType);
                        parameter.put("4", jsonCorn);
                        parameter.put("5", jsonRice);
                        parameter.put("6", jsonMillet);
                        parameter.put("7", jsonWheat);
                        parameter.put("8", jsonBarley);
                        parameter.put("9", jsonMustard);
                        parameter.put("10", jsonOther);
                        parameter.put("11", jsonCattle);

                        if (layout != null) {
                            parameter.put("12", jsonBuffalos);
                            parameter.put("13", jsonCows);
                            parameter.put("14", jsonOxen);
                            parameter.put("15", jsonGoats);
                            parameter.put("16", jsonPigs);
                            parameter.put("17", jsonOthers);
                        }

                        parameter.put("18", jsonHouse);
                        parameter.put("19", jsonHouseType);
                        parameter.put("20", jsonHouseRoof);

                        if (houseLayout != null) {
                            parameter.put("21", jsonHouseName);
                        }

                        if (incomeLayout != null) {
                            parameter.put("22", jsonIncomeSource);
                            parameter.put("23", jsonIncomeValue);
                        }

                        parameter.put("api_token", token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("JSON:", parameter.toString());

                    mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), EconomicPoll.this));


                       /* OkHttpClient client = new OkHttpClient();
                        final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                        Request request = new Request.Builder()
                                .url(url + token)
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();

                        client.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                Log.e("economicResponse", call.request().body().toString());
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.e("economicResponse", response.body().string());
                                }
                            }

                        });*/

                    Toast.makeText(EconomicPoll.this, "Details Added", Toast.LENGTH_SHORT).show();
                    finish();
//                }
              /*  } else {
                    Toast.makeText(EconomicPoll.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
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
            if (!InternetConnection.checkConnection(EconomicPoll.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(EconomicPoll.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new EconomicPoll.ResponseCacheInterceptor())
                .addInterceptor(new EconomicPoll.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(EconomicPoll.this
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
                Log.e("Add_investigation", t.toString());
                Toast.makeText(EconomicPoll.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(EconomicPoll.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
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

                EconomicPoll.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(EconomicPoll.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
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
