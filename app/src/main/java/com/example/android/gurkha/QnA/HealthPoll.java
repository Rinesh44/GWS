package com.example.android.gurkha.QnA;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.sax.RootElement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.Add_investigation;
import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.Ca_servicetype;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SearchPerson;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HealthPoll extends AppCompatActivity {
    private SearchableSpinner name;
    private ArrayList<Object> list;
    private ProgressDialog progressDialog;
    EditText anyOther, totalFamilyMembers, totalSons, marriedSons, unmarriedSons, marriedDaughters, unmarriedDaughters, totalDaughters, relatives, nomineeName;
    Typeface face;
    TextView first, second, third, fourth, fifth, sixth, seventh;
    //    ColorStateList colorStateList;
    private static final String url = "http://pagodalabs.com.np/gws/form/api/form/1?api_token=";
    String token;
    RadioButton nominee;
    CheckBox parkinson, cancer, hypertension, diabetes, asthma, osteoarthritis, dementia, kidneydisease;
    LinearLayout layout;
    public String mLts;
    String mNomineeName;
    Map<String, String> vNominee;
    JSONObject jsonNominee;
    ArrayList<CheckBox> checkboxList;
    static String id, personalId;
    Toolbar toolbar;
    SessionManager sessionManager;
    JobManager mJobManager;
    FbSessionManager fbSessionManager;
    Button save;
    RadioGroup firstGrp, secondGrp, thirdGrp, fourthGrp, fifthGrp, sixthGrp, rg_lives_with, rg_first;
    private static String base_url = "http://pagodalabs.com.np/";
    Object[] person_items;
    Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_poll);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        layout = findViewById(R.id.nominee);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        name = (SearchableSpinner) findViewById(R.id.textPerson);
        first = findViewById(R.id.eye_sight);
        second = findViewById(R.id.hearing);
        third = findViewById(R.id.mobility);
        fourth = findViewById(R.id.mental_health);
        fifth = findViewById(R.id.movement);
        sixth = findViewById(R.id.urine_stool);
        seventh = findViewById(R.id.lts);

        anyOther = findViewById(R.id.other);

        firstGrp = findViewById(R.id.grp_eye_sight);
        secondGrp = findViewById(R.id.grp_hearing);
        thirdGrp = findViewById(R.id.grp_mobility);
        fourthGrp = findViewById(R.id.grp_mental_health);
        fifthGrp = findViewById(R.id.grp_movement);
        sixthGrp = findViewById(R.id.grp_urine_stool);

        rg_lives_with = findViewById(R.id.rg_lives_with);
        rg_first = findViewById(R.id.rg_first);

        parkinson = findViewById(R.id.cb_parkinson);
        cancer = findViewById(R.id.cb_cancer);
        hypertension = findViewById(R.id.cb_hypertension);
        diabetes = findViewById(R.id.cb_diabetes);
        asthma = findViewById(R.id.cb_asthma);
        osteoarthritis = findViewById(R.id.cb_osteoarthritis);
        dementia = findViewById(R.id.cb_dementia);
        kidneydisease = findViewById(R.id.cb_kidneydisease);

        nominee = findViewById(R.id.rb_nominee);
        checkboxList = new ArrayList<>();
        checkboxList.add(parkinson);
        checkboxList.add(cancer);
        checkboxList.add(hypertension);
        checkboxList.add(diabetes);
        checkboxList.add(asthma);
        checkboxList.add(osteoarthritis);
        checkboxList.add(dementia);
        checkboxList.add(kidneydisease);

        relatives = findViewById(R.id.et_relatives);
        totalFamilyMembers = findViewById(R.id.et_total_members);
        totalSons = findViewById(R.id.et_total_sons);
        marriedSons = findViewById(R.id.et_sons_married);
        unmarriedSons = findViewById(R.id.et_sons_unmarried);
        totalDaughters = findViewById(R.id.et_total_daughters);
        marriedDaughters = findViewById(R.id.et_married_daughters);
        unmarriedDaughters = findViewById(R.id.et_unmarried_daughters);


        save = findViewById(R.id.save);

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (InternetConnection.checkConnection(getApplicationContext())) {
                if ((firstGrp.getCheckedRadioButtonId() == -1) | (secondGrp.getCheckedRadioButtonId() == -1) | (thirdGrp.getCheckedRadioButtonId() == -1)
                        | (fourthGrp.getCheckedRadioButtonId() == -1) | (fifthGrp.getCheckedRadioButtonId() == -1) | (sixthGrp.getCheckedRadioButtonId() == -1)) {
                    Toast.makeText(HealthPoll.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                } else {

                    personalId = name.getSelectedItem().toString();
                    id = personalId.substring(personalId.lastIndexOf(":") + 1);


                    int eyeSightId = firstGrp.getCheckedRadioButtonId();
                    RadioButton selecetedEyeSight = (RadioButton) findViewById(eyeSightId);
                    String mEyeSight = selecetedEyeSight.getText().toString();

                    int hearingId = secondGrp.getCheckedRadioButtonId();
                    RadioButton selectedHearing = (RadioButton) findViewById(hearingId);
                    String mHearing = selectedHearing.getText().toString();

                    int mobilityId = thirdGrp.getCheckedRadioButtonId();
                    RadioButton selectedMobility = (RadioButton) findViewById(mobilityId);
                    String mMobility = selectedMobility.getText().toString();

                    int mentalHealthId = fourthGrp.getCheckedRadioButtonId();
                    RadioButton selectedMentalHealth = (RadioButton) findViewById(mentalHealthId);
                    String mMentalHealth = selectedMentalHealth.getText().toString();

                    int movement = fifthGrp.getCheckedRadioButtonId();
                    RadioButton selectedMovement = (RadioButton) findViewById(movement);
                    String mMovement = selectedMovement.getText().toString();

                    int urineStool = sixthGrp.getCheckedRadioButtonId();
                    RadioButton selectedUrine = (RadioButton) findViewById(urineStool);
                    String mUrine = selectedUrine.getText().toString();

                    for (int i = 0; i < checkboxList.size(); i++) {
                        if (checkboxList.get(i).isChecked()) {
                            mLts = checkboxList.get(i).getText().toString();
                        }
                    }

                    if (sessionManager.getUserDetails() != null) {
                        HashMap<String, String> user = sessionManager.getUserDetails();
                        token = user.get(SessionManager.KEY_TOKEN);
                    }

                    if (fbSessionManager.getUserDetails() != null) {
                        HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                        if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                            token = fbUser.get(SessionManager.KEY_TOKEN);
                    }

                      /*  int lts = seventhGrp.getCheckedRadioButtonId();
                        RadioButton selectedLts = (RadioButton) findViewById(lts);
                        String mLts = selectedLts.getText().toString();*/

                    String mOther = anyOther.getText().toString();

                    int livesWith = rg_lives_with.getCheckedRadioButtonId();
                    RadioButton selectedLivesWith = (RadioButton) findViewById(livesWith);
                    String mLivesWith = selectedLivesWith.getText().toString();

                    int drawnBy = rg_first.getCheckedRadioButtonId();
                    RadioButton selectedDrawnBy = (RadioButton) findViewById(drawnBy);
                    String mDrawnBy = selectedDrawnBy.getText().toString();

                    String mTotalMembers = totalFamilyMembers.getText().toString();

                    String mTotalSons = totalSons.getText().toString();
                    String mMarriedSons = marriedSons.getText().toString();
                    String mUnmarriedSons = unmarriedSons.getText().toString();

                    String mTotalDaughters = totalDaughters.getText().toString();
                    String mMarriedDaughters = marriedDaughters.getText().toString();
                    String mUnmarriedDaughters = unmarriedDaughters.getText().toString();

                    if ((Integer.valueOf(mMarriedSons) + Integer.valueOf(mUnmarriedSons) != Integer.valueOf(mTotalSons))) {
                        Toast.makeText(HealthPoll.this, "Sum of married and unmarried must equal total", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if ((Integer.valueOf(mMarriedDaughters) + Integer.valueOf(mUnmarriedDaughters) > Integer.valueOf(mTotalDaughters))) {
                        Toast.makeText(HealthPoll.this, "Sum of married and unmarried must equal total", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (Integer.valueOf(mTotalDaughters) + Integer.valueOf(mTotalSons) > Integer.valueOf(mTotalMembers)) {
                        Toast.makeText(HealthPoll.this, "Sum of total sons and daughters exceeded total members", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (layout.getVisibility() == View.VISIBLE) {
                        mNomineeName = nomineeName.getText().toString();
                    }

                    String mRelatives = relatives.getText().toString();


//                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                    Map<String, String> vEyeSight = new HashMap<String, String>();
                    vEyeSight.put("form_id", String.valueOf(1));
                    vEyeSight.put("field_name", "eye_sight");
                    vEyeSight.put("value", mEyeSight);
                    vEyeSight.put("personal_id", id);

                    Map<String, String> vHearing = new HashMap<String, String>();
                    vHearing.put("form_id", String.valueOf(1));
                    vHearing.put("field_name", "hearing");
                    vHearing.put("value", mHearing);
                    vHearing.put("personal_id", id);

                    Map<String, String> vMobility = new HashMap<String, String>();
                    vMobility.put("form_id", String.valueOf(1));
                    vMobility.put("field_name", "mobility");
                    vMobility.put("value", mMobility);
                    vMobility.put("personal_id", id);


                    Map<String, String> vMentalHealth = new HashMap<String, String>();
                    vMentalHealth.put("form_id", String.valueOf(1));
                    vMentalHealth.put("field_name", "mental_health");
                    vMentalHealth.put("value", mMentalHealth);
                    vMentalHealth.put("personal_id", id);

                    Map<String, String> vMovement = new HashMap<String, String>();
                    vMovement.put("form_id", String.valueOf(1));
                    vMovement.put("field_name", "movement");
                    vMovement.put("value", mMovement);
                    vMovement.put("personal_id", id);

                    Map<String, String> vUrine = new HashMap<String, String>();
                    vUrine.put("form_id", String.valueOf(1));
                    vUrine.put("field_name", "urine/stool");
                    vUrine.put("value", mUrine);
                    vUrine.put("personal_id", id);

                    Map<String, String> vLts = new HashMap<String, String>();
                    vLts.put("form_id", String.valueOf(1));
                    vLts.put("field_name", "long_term_sick(lts)");
                    vLts.put("value", mLts);
                    vLts.put("personal_id", id);

                    Map<String, String> vAnyOther = new HashMap<String, String>();
                    vAnyOther.put("form_id", String.valueOf(1));
                    vAnyOther.put("field_name", "state_if_any");
                    vAnyOther.put("value", mOther);
                    vAnyOther.put("personal_id", id);


                    Map<String, String> vLivesWith = new HashMap<String, String>();
                    vLivesWith.put("form_id", String.valueOf(1));
                    vLivesWith.put("field_name", "lives_with");
                    vLivesWith.put("value", mLivesWith);
                    vLivesWith.put("personal_id", id);

                    Map<String, String> vTotalMembers = new HashMap<String, String>();
                    vTotalMembers.put("form_id", String.valueOf(1));
                    vTotalMembers.put("field_name", "total_family_member");
                    vTotalMembers.put("value", mTotalMembers);
                    vTotalMembers.put("personal_id", id);

                    Map<String, String> vMarriedSons = new HashMap<String, String>();
                    vMarriedSons.put("form_id", String.valueOf(1));
                    vMarriedSons.put("field_name", "married_sons");
                    vMarriedSons.put("value", mMarriedSons);
                    vMarriedSons.put("personal_id", id);

                    Map<String, String> vUnmarriedSons = new HashMap<String, String>();
                    vUnmarriedSons.put("form_id", String.valueOf(1));
                    vUnmarriedSons.put("field_name", "unmarried_sons");
                    vUnmarriedSons.put("value", mUnmarriedSons);
                    vUnmarriedSons.put("personal_id", id);

                    Map<String, String> vTotalSons = new HashMap<String, String>();
                    vTotalSons.put("form_id", String.valueOf(1));
                    vTotalSons.put("field_name", "total_sons");
                    vTotalSons.put("value", mTotalSons);
                    vTotalSons.put("personal_id", id);

                    Map<String, String> vTotalDaughters = new HashMap<String, String>();
                    vTotalDaughters.put("form_id", String.valueOf(1));
                    vTotalDaughters.put("field_name", "total_daughters");
                    vTotalDaughters.put("value", mTotalDaughters);
                    vTotalDaughters.put("personal_id", id);

                    Map<String, String> vMarriedDaughters = new HashMap<String, String>();
                    vMarriedDaughters.put("form_id", String.valueOf(1));
                    vMarriedDaughters.put("field_name", "married_daughters");
                    vMarriedDaughters.put("value", mMarriedDaughters);
                    vMarriedDaughters.put("personal_id", id);

                    Map<String, String> vUnmarriedDaughters = new HashMap<String, String>();
                    vUnmarriedDaughters.put("form_id", String.valueOf(1));
                    vUnmarriedDaughters.put("field_name", "unmarried_daughters");
                    vUnmarriedDaughters.put("value", mUnmarriedDaughters);
                    vUnmarriedDaughters.put("personal_id", id);

                    Map<String, String> vRelatives = new HashMap<String, String>();
                    vRelatives.put("form_id", String.valueOf(1));
                    vRelatives.put("field_name", "relatives(if_any)");
                    vRelatives.put("value", mRelatives);
                    vRelatives.put("personal_id", id);

                    Map<String, String> vDrawnBy = new HashMap<String, String>();
                    vDrawnBy.put("form_id", String.valueOf(1));
                    vDrawnBy.put("field_name", "wp_drawn_by");
                    vDrawnBy.put("value", mDrawnBy);
                    vDrawnBy.put("personal_id", id);

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("api_token", token);

                    if (layout != null) {
                        vNominee = new HashMap<String, String>();
                        vNominee.put("form_id", String.valueOf(1));
                        vNominee.put("field_name", "name_of_nominee");
                        vNominee.put("value", mNomineeName);
                        vNominee.put("personal_id", id);
                    }


                    JSONObject jsonEyeSight = new JSONObject(vEyeSight);
                    JSONObject jsonHearing = new JSONObject(vHearing);
                    JSONObject jsonMobility = new JSONObject(vMobility);
                    JSONObject jsonMentalHealth = new JSONObject(vMentalHealth);
                    JSONObject jsonMovement = new JSONObject(vMovement);
                    JSONObject jsonUrine = new JSONObject(vUrine);
                    JSONObject jsonLts = new JSONObject(vLts);
                    JSONObject jsonOther = new JSONObject(vAnyOther);
                    JSONObject jsonLivesWith = new JSONObject(vLivesWith);
                    JSONObject jsonTotalMembers = new JSONObject(vTotalMembers);
                    JSONObject jsonTotalSons = new JSONObject(vTotalSons);
                    JSONObject jsonMarriedSons = new JSONObject(vMarriedSons);
                    JSONObject jsonUnmarriedSons = new JSONObject(vUnmarriedSons);
                    JSONObject jsonTotalDaughters = new JSONObject(vTotalDaughters);
                    JSONObject jsonMarriedDaughters = new JSONObject(vMarriedDaughters);
                    JSONObject jsonUnmarriedDaughters = new JSONObject(vUnmarriedDaughters);
                    JSONObject jsonRelatives = new JSONObject(vRelatives);
                    JSONObject jsonDrawnBy = new JSONObject(vDrawnBy);
                    if (vNominee != null) {
                        jsonNominee = new JSONObject(vNominee);
                    }

                    JSONObject parameter = new JSONObject();
                    try {
                        parameter.put("0", jsonEyeSight);
                        parameter.put("1", jsonHearing);
                        parameter.put("2", jsonMobility);
                        parameter.put("3", jsonMentalHealth);
                        parameter.put("4", jsonMovement);
                        parameter.put("5", jsonUrine);
                        parameter.put("6", jsonLts);
                        parameter.put("7", jsonOther);
                        parameter.put("8", jsonLivesWith);
                        parameter.put("9", jsonTotalMembers);
                        parameter.put("10", jsonTotalSons);
                        parameter.put("11", jsonMarriedSons);
                        parameter.put("12", jsonUnmarriedSons);
                        parameter.put("13", jsonTotalDaughters);
                        parameter.put("14", jsonMarriedDaughters);
                        parameter.put("15", jsonUnmarriedDaughters);
                        parameter.put("16", jsonRelatives);
                        parameter.put("17", jsonDrawnBy);
                        if (layout != null) {
                            parameter.put("18", jsonNominee);
                        }

                        parameter.put("api_token", token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*    JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("0", parameter);
                            jsonParams.put("api_token", token);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }*/

                    Log.e("JSON:", parameter.toString());

                    mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));



                      /*  OkHttpClient client = new OkHttpClient();
                        final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();

                        client.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                Log.e("healthResponse", call.request().body().toString());
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.e("healthResponse", response.body().string());
                                }
                            }

                        });
*/
                    Toast.makeText(HealthPoll.this, "Details Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
               /* } else {
                    Toast.makeText(HealthPoll.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        list = new ArrayList<>();

        /*    colorStateList = new ColorStateList(
                    new int[][]{

                            new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{

                            Color.BLACK //disabled
                            , Color.RED //enabled

                    }
            );*/

        try {
            run();
            //getDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rg_first.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_nominee) {
                    layout.setVisibility(View.VISIBLE);
                    nomineeName = findViewById(R.id.value_nominee);
                } else
                    layout.setVisibility(View.GONE);
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
            if (!InternetConnection.checkConnection(HealthPoll.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(HealthPoll.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new HealthPoll.ResponseCacheInterceptor())
                .addInterceptor(new HealthPoll.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(HealthPoll.this
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

        call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Health_Poll", t.toString());
                Toast.makeText(HealthPoll.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(HealthPoll.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
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

                HealthPoll.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(HealthPoll.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
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


/*    void getDetails() throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new HealthPoll.ResponseCacheInterceptor())
                .addInterceptor(new HealthPoll.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(HealthPoll.this
                        .getCacheDir(),
                        "addInvestigationApiResponses"), 1024 * 1024))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/form/api/form/1");

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Health_Poll", t.toString());
                Toast.makeText(HealthPoll.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    JSONArray array = new JSONArray(myResponse);

                        JSONObject object = array.getJSONObject(0);
                        String title = object.getString("column_label");

                        first.setText(title);

                        JSONArray data = object.getJSONArray("columnValue");
                        RadioButton rb;
                        for (int i = 0; i < data.length(); i++) {
                            // JSONObject values = data.getJSONObject(i);
                            String values = data.getString(i);
                            rb = new RadioButton(HealthPoll.this);
                            rb.setText(values);
                            rb.setTypeface(face);
                            firstGrp.addView(rb);
                        }

                    JSONObject object1 = array.getJSONObject(1);
                    String title1 = object1.getString("column_label");

                    second.setText(title1);

                    JSONArray data1 = object1.getJSONArray("columnValue");
                    RadioButton rb1;
                    for (int i = 0; i < data1.length(); i++) {
                        // JSONObject values = data.getJSONObject(i);
                        String values = data1.getString(i);
                        rb1 = new RadioButton(HealthPoll.this);
                        rb1.setText(values);
                        rb1.setTypeface(face);
                       *//* if (Build.VERSION.SDK_INT >= 21)
                            rb1.setButtonTintList(colorStateList);*//*
                        secondGrp.addView(rb1);
                    }

                    JSONObject object2 = array.getJSONObject(2);
                    String title2 = object2.getString("column_label");

                    third.setText(title2);

                    JSONArray data2 = object1.getJSONArray("columnValue");
                    RadioButton rb2;
                    for (int i = 0; i < data2.length(); i++) {
                        // JSONObject values = data.getJSONObject(i);
                        String values = data2.getString(i);
                        rb2 = new RadioButton(HealthPoll.this);
                        rb2.setText(values);
                        rb2.setTypeface(face);
//                        if (Build.VERSION.SDK_INT >= 21)
//                            rb2.setButtonTintList(colorStateList);
                        thirdGrp.addView(rb2);
                    }


                    JSONObject object3 = array.getJSONObject(3);
                    String title3 = object3.getString("column_label");

                    fourth.setText(title3);

                    JSONArray data3 = object3.getJSONArray("columnValue");
                    RadioButton rb3;
                    for (int i = 0; i < data3.length(); i++) {
                        // JSONObject values = data.getJSONObject(i);
                        String values = data3.getString(i);
                        rb3 = new RadioButton(HealthPoll.this);
                        rb3.setText(values);
                        rb3.setTypeface(face);
//                        if (Build.VERSION.SDK_INT >= 21)
//                            rb3.setButtonTintList(colorStateList);
                        fourthGrp.addView(rb3);
                    }

                    JSONObject object4 = array.getJSONObject(4);
                    String title4 = object1.getString("column_label");

                    fifth.setText(title4);

                    JSONArray data4 = object4.getJSONArray("columnValue");
                    RadioButton rb4;
                    for (int i = 0; i < data4.length(); i++) {
                        // JSONObject values = data.getJSONObject(i);
                        String values = data4.getString(i);
                        rb4 = new RadioButton(HealthPoll.this);
                        rb4.setText(values);
                        rb4.setTypeface(face);
//                        if (Build.VERSION.SDK_INT >= 21)
//                            rb4.setButtonTintList(colorStateList);
                        fifthGrp.addView(rb4);
                    }

                        JSONObject object5 = array.getJSONObject(5);
                        String title5 = object5.getString("column_label");

                        sixth.setText(title5);

                        JSONArray data5 = object5.getJSONArray("columnValue");
                        RadioButton rb5;
                        for (int i = 0; i < data5.length(); i++) {
                            // JSONObject values = data.getJSONObject(i);
                            String values = data5.getString(i);
                            rb5 = new RadioButton(HealthPoll.this);
                            rb5.setText(values);
                            rb5.setTypeface(face);
//                            if (Build.VERSION.SDK_INT >= 21)
//                                rb5.setButtonTintList(colorStateList);
                            sixthGrp.addView(rb5);
                        }

                    JSONObject object6 = array.getJSONObject(6);
                    String title6 = object6.getString("column_label");

                    seventh.setText(title6);

                    JSONArray data6 = object6.getJSONArray("columnValue");
                    RadioButton rb6;
                    for (int i = 0; i < data6.length(); i++) {
                        // JSONObject values = data.getJSONObject(i);
                        String values = data6.getString(i);
                        rb6 = new RadioButton(HealthPoll.this);
                        rb6.setText(values);
                        rb6.setTypeface(face);
//                        if (Build.VERSION.SDK_INT >= 21)
//                            rb6.setButtonTintList(colorStateList);
                        seventhGrp.addView(rb6);
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                call.cancel();


            }
        });
    }*/
}
