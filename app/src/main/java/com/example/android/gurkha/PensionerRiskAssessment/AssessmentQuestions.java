package com.example.android.gurkha.PensionerRiskAssessment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.Add_payment;
import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.QnA.SocialPoll;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.application.GurkhaApplication;
import com.example.android.gurkha.utils.SetDate;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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


public class AssessmentQuestions extends AppCompatActivity {

    public static String url = "http://pagodalabs.com.np/gws/pensioner_risk_assessment/api/pensioner_risk_assessment";
    private static String base_url = "http://pagodalabs.com.np/";

    String token;
    TextView totalScore;
    TextView locQ1ac, locQ2ac, locQ3ac, locQ4ac, locQ5ac;
    TextView houseQ1ac, houseQ2ac, houseQ3ac, houseQ4ac;
    TextView homeQ1ac, homeQ2ac, homeQ3ac, homeQ4ac, homeQ5ac, homeQ6ac, homeQ7ac, homeQ8ac;
    TextView comQ1ac, comQ2ac, comQ3ac, comQ4ac, comQ5ac;
    Toolbar toolbar;
    TextView shopQ1ac, shopQ2ac;
    TextView foodQ1ac, foodQ2ac, foodQ3ac, foodQ4ac;
    TextView laundryQ1ac;
    TextView aboutQ1ac, aboutQ2ac;
    TextView medicineQ1ac;
    TextView supportQ1ac, supportQ2ac, supportQ3ac, supportQ4ac;
    TextView ecoQ1ac, ecoQ2ac, ecoQ3ac;
    TextView safeQ1ac, safeQ2ac, safeQ3ac, safeQ4ac;
    TextView medicalQ1ac, medicalQ2ac, medicalQ3ac, medicalQ4ac, medicalQ5ac, medicalQ6ac;
    TextView loaAc, assessedByAc, riskPositionAc;
    private ArrayList<Object> list;
    Object[] actionPlanItems;
    JobManager mJobManager;
    JSONArray[] houseArray;

    Spinner assessmentLocation, assessedByScore, riskPosition;
    Spinner assessmentLocationActionPlan, assessedByScoreActionPlan, riskPositionActionPlan;
    EditText assessmentLocationDueDate, assessedByScoreDueDate, riskPositionDueDate;
    EditText assessmentLocationRemarks, assessedByScoreRemarks, riskPositionRemarks;
    ExpandableLayout elLocation, elHouse, elInTheHome, elCommunication, elShopping, elFoodCooking, elLaundry, elGettingAbout, elMedicine, elHomeSupport, elEconomic, elSafeguarding, elMedical, elLoa, elAccessedBy, elRiskPosition;

    Button done;
    Button locationDropdown, houseDropdown, inTheHomeDropdown, communicationDropdown, shoppingDropdown, foodCookingDropdown, laundryDropdown, gettingAboutDropDown, medicineDropdown, homeSupportDropdown, economicDropdown, safeguardingDropdown, medicalDropdown, loaDropdown, accessedByDropdown, riskPositionDropdown;

    Spinner locQ1ActionPlan, locQ2ActionPlan, locQ3ActionPlan, locQ4ActionPlan, locQ5ActionPlan, houseQ1ActionPlan, houseQ2ActionPlan, houseQ3ActionPlan, houseQ4ActionPlan, homeQ1ActionPlan, homeQ2ActionPlan, homeQ3ActionPlan, homeQ4ActionPlan, homeQ5ActionPlan, homeQ6ActionPlan, homeQ7ActionPlan, homeQ8ActionPlan, comQ1ActionPlan, comQ2ActionPlan, comQ3ActionPlan, comQ4ActionPlan, comQ5ActionPlan, shopQ1ActionPlan, shopQ2ActionPlan, foodQ1ActionPlan, foodQ2ActionPlan, foodQ3ActionPlan, foodQ4ActionPlan, laundryQ1ActionPlan,
            aboutQ1ActionPlan, aboutQ2ActionPlan, medicineQ1ActionPlan, supportQ1ActionPlan, supportQ2ActionPlan, supportQ3ActionPlan, supportQ4ActionPlan, ecoQ1ActionPlan, ecoQ2ActionPlan, ecoQ3ActionPlan, safeQ1ActionPlan, safeQ2ActionPlan, safeQ3ActionPlan, safeQ4ActionPlan, medicalQ1ActionPlan, medicalQ2ActionPlan, medicalQ3ActionPlan, medicalQ4ActionPlan, medicalQ6ActionPlan, medicalQ5ActionPlan;

    TextView assessment, locQ1, locQ2, locQ3, locQ4, locQ5;
    Spinner locQ1Spinner, locQ2Spinner, locQ3Spinner, locQ4Spinner, locQ5Spinner;
    EditText locQ1DueDate, locQ1Remarks, locQ2DueDate, locQ2Remarks,
            locQ3DueDate, locQ3Remarks, locQ4DueDate, locQ4Remarks, locQ5DueDate, locQ5Remarks;

    TextView houseQ1, houseQ2, houseq3, houseq4;
    Spinner houseQ1Spinner, houseQ2Spinner, houseQ3Spinner, houseQ4Spinner;
    EditText houseQ1DueDate, houseQ1Remarks, houseQ2DueDate, houseQ2Remarks, houseQ3DueDate, houseQ3Remarks, houseQ4DueDate, houseQ4Remarks;

    TextView homeQ1, homeQ2, homeq3, homeq4, homeq5, homeQ6, homeQ7, homeQ8;
    Spinner homeQ1Spinner, homeQ2Spinner, homeQ3Spinner, homeQ4Spinner, homeQ5Spinner, homeQ6Spinner, homeQ7Spinner, homeQ8Spinner;
    EditText homeQ1DueDate, homeQ1Remarks, homeQ2DueDate, homeQ2Remarks, homeQ3DueDate, homeQ3Remarks, homeQ4DueDate, homeQ4Remarks, homeQ5DueDate, homeQ5Remarks, homeQ6DueDate, homeQ6Remarks, homeQ7DueDate, homeQ7Remarks, homeQ8DueDate, homeQ8Remarks;

    TextView comQ1, comQ2, comQ3, comQ4, comQ5;
    Spinner comQ1Spinner, comQ2Spinner, comQ3Spinner, comQ4Spinner, comQ5Spinner;
    EditText comQ1DueDate, comQ1Remarks, comQ2DueDate, comQ2Remarks,
            comQ3DueDate, comQ3Remarks, comQ4DueDate, comQ4Remarks, comQ5DueDate, comQ5Remarks;

    TextView shopQ1, shopQ2;
    Spinner shopQ1Spinner, shopQ2Spinner;
    EditText shopQ1DueDate, shopQ1Remarks, shopQ2DueDate, shopQ2Remarks;

    TextView foodQ1, foodQ2, foodQ3, foodQ4;
    Spinner foodQ1Spinner, foodQ2Spinner, foodQ3Spinner, foodQ4Spinner;
    EditText foodQ1DueDate, foodQ1Remarks, foodQ2DueDate, foodQ2Remarks, foodQ3DueDate, foodQ3Remarks, foodQ4DueDate, foodQ4Remarks;

    TextView laundryQ1;
    Spinner laundryQ1Spinner;
    EditText laundryQ1DueDate, laundryQ1Remarks;

    TextView aboutQ1, aboutQ2;
    Spinner aboutQ1Spinner, aboutQ2Spinner;
    EditText aboutQ1DueDate, aboutQ1Remarks, aboutQ2DueDate, aboutQ2Remarks;

    TextView medicineQ1;
    Spinner medicineQ1Spinner;
    EditText medicineQ1DueDate, medicineQ1Remarks;

    TextView supportQ1, supportQ2, supportQ3, supportQ4;
    Spinner supportQ1Spinner, supportQ2Spinner, supportQ3Spinner, supportQ4Spinner;
    EditText supportQ1DueDate, supportQ1Remarks, supportQ2DueDate, supportQ2Remarks, supportQ3DueDate, supportQ3Remarks, supportQ4DueDate, supportQ4Remarks;

    TextView ecoQ1, ecoQ2, ecoQ3;
    Spinner ecoQ1Spinner, ecoQ2Spinner, ecoQ3Spinner;
    EditText ecoQ1DueDate, ecoQ1Remarks, ecoQ2DueDate, ecoQ2Remarks, ecoQ3DueDate, ecoQ3Remarks;

    TextView safeQ1, safeQ2, safeQ3, safeQ4;
    Spinner safeQ1Spinner, safeQ2Spinner, safeQ3Spinner, safeQ4Spinner;
    EditText safeQ1DueDate, safeQ1Remarks, safeQ2DueDate, safeQ2Remarks, safeQ3DueDate, safeQ3Remarks, safeQ4DueDate, safeQ4Remarks;

    TextView medicalQ1, medicalQ2, medicalQ3, medicalQ4, medicalQ5, medicalQ6;
    Spinner medicalQ1Spinner, medicalQ2Spinner, medicalQ3Spinner, medicalQ4Spinner, medicalQ5Spinner, medicalQ6Spinner;
    EditText medicalQ1DueDate, medicalQ1Remarks, medicalQ2DueDate, medicalQ2Remarks, medicalQ3DueDate, medicalQ3Remarks, medicalQ4DueDate, medicalQ4Remarks, medicalQ5DueDate, medicalQ5Remarks, medicalQ6DueDate, medicalQ6Remarks;

    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    ProgressDialog progressDialog;
    Typeface face;
    Call<ResponseBody> call;
    String criteriaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_questions);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initializeView();

        pickDate();

        String getAssessment = getIntent().getStringExtra("assessment_type");
        assessment.setText(getAssessment);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        String[] spinner_items = new String[]{"0", "1"};
        String[] spinner_items1 = new String[]{"0", "3", "5"};
        String[] spinner_items2 = new String[]{"0", "3"};
        String[] spinner_items3 = new String[]{"0", "5"};
        String[] spinner_items4 = new String[]{"0", "7"};
        String[] spinner_items5 = new String[]{"0", "2"};
        String[] spinner_items_location = new String[]{"In Pensioner's home", "By Phone", "In AWC"};
        String[] spinner_items_assessscore = new String[]{"Top", "Middle", "Botton"};
        String[] spinner_items_risk = new String[]{"High risk", "Medium risk", "Low risk"};
        String[] spinner_items_actionplan = new String[]{"Repair a roof", "Build a toilet", "Build a house"
                , "Install a tap", "Other construction required", "Build a house", "Relocation not appropriate or pensioner unwilling -re-assess" +
                "in 6 months", "Appoint and train the carer", "re-assess in 6 months", "Seek appropriate professional help"
                , "Supply walking stick or frame", "Supply wheelchair", "Supply other mobility equipment"
                , "Supply specialist bed", "Supply special mattress", "Supply other specialist medical equipment"
                , "Supply a stove", "Free Text", "Pension assessment", "Specialist medical assessment (OT, Hearing, etc)", "CML review"
                , "Other Dr review", "No further Action"};


        ArrayAdapter<String> adapt_items_actionplan = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items2) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items3) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items4 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items4) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_items5 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items5) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_assessmentloc_items = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_location) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_assessscore_items = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_assessscore) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_risk_items = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_risk) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        /*locQ1ActionPlan.setAdapter(adapt_items_actionplan);
        locQ2ActionPlan.setAdapter(adapt_items_actionplan);
        locQ3ActionPlan.setAdapter(adapt_items_actionplan);
        locQ4ActionPlan.setAdapter(adapt_items_actionplan);
        locQ5ActionPlan.setAdapter(adapt_items_actionplan);*/
        locQ1Spinner.setAdapter(adapt_items);
        locQ2Spinner.setAdapter(adapt_items);
        locQ3Spinner.setAdapter(adapt_items);
        locQ4Spinner.setAdapter(adapt_items);
        locQ5Spinner.setAdapter(adapt_items);


   /*     houseQ1ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ2ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ3ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ4ActionPlan.setAdapter(adapt_items_actionplan);*/
        houseQ1Spinner.setAdapter(adapt_items5);
        houseQ2Spinner.setAdapter(adapt_items);
        houseQ3Spinner.setAdapter(adapt_items);
        houseQ4Spinner.setAdapter(adapt_items);

        homeQ1ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ2ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ3ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ4ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ5ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ6ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ7ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ8ActionPlan.setAdapter(adapt_items_actionplan);
        homeQ1Spinner.setAdapter(adapt_items);
        homeQ2Spinner.setAdapter(adapt_items);
        homeQ3Spinner.setAdapter(adapt_items);
        homeQ4Spinner.setAdapter(adapt_items);
        homeQ5Spinner.setAdapter(adapt_items);
        homeQ6Spinner.setAdapter(adapt_items);
        homeQ7Spinner.setAdapter(adapt_items);
        homeQ8Spinner.setAdapter(adapt_items);

        comQ1ActionPlan.setAdapter(adapt_items_actionplan);
        comQ2ActionPlan.setAdapter(adapt_items_actionplan);
        comQ3ActionPlan.setAdapter(adapt_items_actionplan);
        comQ4ActionPlan.setAdapter(adapt_items_actionplan);
        comQ5ActionPlan.setAdapter(adapt_items_actionplan);
        comQ1Spinner.setAdapter(adapt_items);
        comQ2Spinner.setAdapter(adapt_items);
        comQ3Spinner.setAdapter(adapt_items);
        comQ4Spinner.setAdapter(adapt_items5);
        comQ5Spinner.setAdapter(adapt_items);

        shopQ1ActionPlan.setAdapter(adapt_items_actionplan);
        shopQ2ActionPlan.setAdapter(adapt_items_actionplan);
        shopQ1Spinner.setAdapter(adapt_items5);
        shopQ2Spinner.setAdapter(adapt_items);

        foodQ1ActionPlan.setAdapter(adapt_items_actionplan);
        foodQ2ActionPlan.setAdapter(adapt_items_actionplan);
        foodQ3ActionPlan.setAdapter(adapt_items_actionplan);
        foodQ4ActionPlan.setAdapter(adapt_items_actionplan);
        foodQ1Spinner.setAdapter(adapt_items);
        foodQ2Spinner.setAdapter(adapt_items);
        foodQ3Spinner.setAdapter(adapt_items);
        foodQ4Spinner.setAdapter(adapt_items);

        laundryQ1ActionPlan.setAdapter(adapt_items_actionplan);
        laundryQ1Spinner.setAdapter(adapt_items);

        aboutQ1ActionPlan.setAdapter(adapt_items_actionplan);
        aboutQ2ActionPlan.setAdapter(adapt_items_actionplan);
        aboutQ1Spinner.setAdapter(adapt_items);
        aboutQ2Spinner.setAdapter(adapt_items);

        medicineQ1ActionPlan.setAdapter(adapt_items_actionplan);
        medicineQ1Spinner.setAdapter(adapt_items5);

        supportQ1ActionPlan.setAdapter(adapt_items_actionplan);
        supportQ2ActionPlan.setAdapter(adapt_items_actionplan);
        supportQ3ActionPlan.setAdapter(adapt_items_actionplan);
        supportQ4ActionPlan.setAdapter(adapt_items_actionplan);
        supportQ1Spinner.setAdapter(adapt_items4);
        supportQ2Spinner.setAdapter(adapt_items3);
        supportQ3Spinner.setAdapter(adapt_items2);
        supportQ4Spinner.setAdapter(adapt_items);

        ecoQ1ActionPlan.setAdapter(adapt_items_actionplan);
        ecoQ2ActionPlan.setAdapter(adapt_items_actionplan);
        ecoQ3ActionPlan.setAdapter(adapt_items_actionplan);
        ecoQ1Spinner.setAdapter(adapt_items);
        ecoQ2Spinner.setAdapter(adapt_items);
        ecoQ3Spinner.setAdapter(adapt_items);

        safeQ1ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ2ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ3ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ4ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ1Spinner.setAdapter(adapt_items);
        safeQ2Spinner.setAdapter(adapt_items);
        safeQ3Spinner.setAdapter(adapt_items);
        safeQ4Spinner.setAdapter(adapt_items);

        medicalQ1ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ2ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ3ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ4ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ5ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ6ActionPlan.setAdapter(adapt_items_actionplan);
        medicalQ1Spinner.setAdapter(adapt_items1);
        medicalQ2Spinner.setAdapter(adapt_items);
        medicalQ3Spinner.setAdapter(adapt_items);
        medicalQ4Spinner.setAdapter(adapt_items);
        medicalQ5Spinner.setAdapter(adapt_items);
        medicalQ6Spinner.setAdapter(adapt_items2);

        assessmentLocation.setAdapter(adapt_assessmentloc_items);
        assessedByScore.setAdapter(adapt_assessscore_items);
        riskPosition.setAdapter(adapt_risk_items);
        assessmentLocationActionPlan.setAdapter(adapt_items_actionplan);
        assessedByScoreActionPlan.setAdapter(adapt_items_actionplan);
        riskPositionActionPlan.setAdapter(adapt_items_actionplan);


        locQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locQ1Spinner.getSelectedItem().toString().equals("0")) {
                    locQ1ActionPlan.setVisibility(View.VISIBLE);
                    locQ1ac.setVisibility(View.VISIBLE);
                } else {
                    locQ1ActionPlan.setVisibility(View.GONE);
                    locQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locQ2Spinner.getSelectedItem().toString().equals("0")) {
                    locQ2ActionPlan.setVisibility(View.VISIBLE);
                    locQ2ac.setVisibility(View.VISIBLE);
                } else {
                    locQ2ActionPlan.setVisibility(View.GONE);
                    locQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        locQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locQ3Spinner.getSelectedItem().toString().equals("0")) {
                    locQ3ActionPlan.setVisibility(View.VISIBLE);
                    locQ3ac.setVisibility(View.VISIBLE);
                } else {
                    locQ3ActionPlan.setVisibility(View.GONE);
                    locQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locQ4Spinner.getSelectedItem().toString().equals("0")) {
                    locQ4ActionPlan.setVisibility(View.VISIBLE);
                    locQ4ac.setVisibility(View.VISIBLE);
                } else {
                    locQ4ActionPlan.setVisibility(View.GONE);
                    locQ4ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locQ5Spinner.getSelectedItem().toString().equals("0")) {
                    locQ5ActionPlan.setVisibility(View.VISIBLE);
                    locQ5ac.setVisibility(View.VISIBLE);
                } else {
                    locQ5ActionPlan.setVisibility(View.GONE);
                    locQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        houseQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!houseQ1Spinner.getSelectedItem().toString().equals("0")) {
                    houseQ1ActionPlan.setVisibility(View.VISIBLE);
                    houseQ1ac.setVisibility(View.VISIBLE);
                } else {
                    houseQ1ActionPlan.setVisibility(View.GONE);
                    houseQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        houseQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!houseQ2Spinner.getSelectedItem().toString().equals("0")) {
                    houseQ2ActionPlan.setVisibility(View.VISIBLE);
                    houseQ2ac.setVisibility(View.VISIBLE);
                } else {
                    houseQ2ActionPlan.setVisibility(View.GONE);
                    houseQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        houseQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!houseQ3Spinner.getSelectedItem().toString().equals("0")) {
                    houseQ3ActionPlan.setVisibility(View.VISIBLE);
                    houseQ3ac.setVisibility(View.VISIBLE);
                } else {
                    houseQ3ActionPlan.setVisibility(View.GONE);
                    houseQ3ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        houseQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!houseQ4Spinner.getSelectedItem().toString().equals("0")) {
                    houseQ4ActionPlan.setVisibility(View.VISIBLE);
                    houseQ4ac.setVisibility(View.VISIBLE);
                } else {
                    houseQ4ActionPlan.setVisibility(View.GONE);
                    houseQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ1Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ1ActionPlan.setVisibility(View.VISIBLE);
                    homeQ1ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ1ActionPlan.setVisibility(View.GONE);
                    homeQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ2Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ2ActionPlan.setVisibility(View.VISIBLE);
                    homeQ2ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ2ActionPlan.setVisibility(View.GONE);
                    homeQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ3Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ3ActionPlan.setVisibility(View.VISIBLE);
                    homeQ3ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ3ActionPlan.setVisibility(View.GONE);
                    homeQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ4Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ4ActionPlan.setVisibility(View.VISIBLE);
                    homeQ4ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ4ActionPlan.setVisibility(View.GONE);
                    homeQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ5Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ5ActionPlan.setVisibility(View.VISIBLE);
                    homeQ5ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ5ActionPlan.setVisibility(View.GONE);
                    homeQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ6Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ6ActionPlan.setVisibility(View.VISIBLE);
                    homeQ6ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ6ActionPlan.setVisibility(View.GONE);
                    homeQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ7Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ7Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ7ActionPlan.setVisibility(View.VISIBLE);
                    homeQ7ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ7ActionPlan.setVisibility(View.GONE);
                    homeQ7ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeQ8Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!homeQ8Spinner.getSelectedItem().toString().equals("0")) {
                    homeQ8ActionPlan.setVisibility(View.VISIBLE);
                    homeQ8ac.setVisibility(View.VISIBLE);
                } else {
                    homeQ8ActionPlan.setVisibility(View.GONE);
                    homeQ8ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        comQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!comQ1Spinner.getSelectedItem().toString().equals("0")) {
                    comQ1ActionPlan.setVisibility(View.VISIBLE);
                    comQ1ac.setVisibility(View.VISIBLE);
                } else {
                    comQ1ActionPlan.setVisibility(View.GONE);
                    comQ1ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        comQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!comQ2Spinner.getSelectedItem().toString().equals("0")) {
                    comQ2ActionPlan.setVisibility(View.VISIBLE);
                    comQ2ac.setVisibility(View.VISIBLE);
                } else {
                    comQ2ActionPlan.setVisibility(View.GONE);
                    comQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        comQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!comQ3Spinner.getSelectedItem().toString().equals("0")) {
                    comQ3ActionPlan.setVisibility(View.VISIBLE);
                    comQ3ac.setVisibility(View.VISIBLE);
                } else {
                    comQ3ActionPlan.setVisibility(View.GONE);
                    comQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        comQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!comQ4Spinner.getSelectedItem().toString().equals("0")) {
                    comQ4ActionPlan.setVisibility(View.VISIBLE);
                    comQ4ac.setVisibility(View.VISIBLE);
                } else {
                    comQ4ActionPlan.setVisibility(View.GONE);
                    comQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        comQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!comQ5Spinner.getSelectedItem().toString().equals("0")) {
                    comQ5ActionPlan.setVisibility(View.VISIBLE);
                    comQ5ac.setVisibility(View.VISIBLE);
                } else {
                    comQ5ActionPlan.setVisibility(View.GONE);
                    comQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shopQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!shopQ1Spinner.getSelectedItem().toString().equals("0")) {
                    shopQ1ActionPlan.setVisibility(View.VISIBLE);
                    shopQ1ac.setVisibility(View.VISIBLE);
                } else {
                    shopQ1ActionPlan.setVisibility(View.GONE);
                    shopQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shopQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!shopQ2Spinner.getSelectedItem().toString().equals("0")) {
                    shopQ2ActionPlan.setVisibility(View.VISIBLE);
                    shopQ2ac.setVisibility(View.VISIBLE);
                } else {
                    shopQ2ActionPlan.setVisibility(View.GONE);
                    shopQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!foodQ1Spinner.getSelectedItem().toString().equals("0")) {
                    foodQ1ActionPlan.setVisibility(View.VISIBLE);
                    foodQ1ac.setVisibility(View.VISIBLE);
                } else {
                    foodQ1ActionPlan.setVisibility(View.GONE);
                    foodQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!foodQ2Spinner.getSelectedItem().toString().equals("0")) {
                    foodQ2ActionPlan.setVisibility(View.VISIBLE);
                    foodQ2ac.setVisibility(View.VISIBLE);
                } else {
                    foodQ2ActionPlan.setVisibility(View.GONE);
                    foodQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!foodQ3Spinner.getSelectedItem().toString().equals("0")) {
                    foodQ3ActionPlan.setVisibility(View.VISIBLE);
                    foodQ3ac.setVisibility(View.VISIBLE);
                } else {
                    foodQ3ActionPlan.setVisibility(View.GONE);
                    foodQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!foodQ4Spinner.getSelectedItem().toString().equals("0")) {
                    foodQ4ActionPlan.setVisibility(View.VISIBLE);
                    foodQ4ac.setVisibility(View.VISIBLE);
                } else {
                    foodQ4ActionPlan.setVisibility(View.GONE);
                    foodQ4ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        laundryQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!laundryQ1Spinner.getSelectedItem().toString().equals("0")) {
                    laundryQ1ActionPlan.setVisibility(View.VISIBLE);
                    laundryQ1ac.setVisibility(View.VISIBLE);
                } else {
                    laundryQ1ActionPlan.setVisibility(View.GONE);
                    laundryQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aboutQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!aboutQ1Spinner.getSelectedItem().toString().equals("0")) {
                    aboutQ1ActionPlan.setVisibility(View.VISIBLE);
                    aboutQ1ac.setVisibility(View.VISIBLE);
                } else {
                    aboutQ1ActionPlan.setVisibility(View.GONE);
                    aboutQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aboutQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!aboutQ2Spinner.getSelectedItem().toString().equals("0")) {
                    aboutQ2ActionPlan.setVisibility(View.VISIBLE);
                    aboutQ2ac.setVisibility(View.VISIBLE);
                } else {
                    aboutQ2ActionPlan.setVisibility(View.GONE);
                    aboutQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicineQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicineQ1Spinner.getSelectedItem().toString().equals("0")) {
                    medicineQ1ActionPlan.setVisibility(View.VISIBLE);
                    medicineQ1ac.setVisibility(View.VISIBLE);
                } else {
                    medicineQ1ActionPlan.setVisibility(View.GONE);
                    medicineQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supportQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!supportQ1Spinner.getSelectedItem().toString().equals("0")) {
                    supportQ1ActionPlan.setVisibility(View.VISIBLE);
                    supportQ1ac.setVisibility(View.VISIBLE);
                } else {
                    supportQ1ActionPlan.setVisibility(View.GONE);
                    supportQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supportQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!supportQ2Spinner.getSelectedItem().toString().equals("0")) {
                    supportQ2ActionPlan.setVisibility(View.VISIBLE);
                    supportQ2ac.setVisibility(View.VISIBLE);
                } else {
                    supportQ2ActionPlan.setVisibility(View.GONE);
                    supportQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supportQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!supportQ3Spinner.getSelectedItem().toString().equals("0")) {
                    supportQ3ActionPlan.setVisibility(View.VISIBLE);
                    supportQ3ac.setVisibility(View.VISIBLE);
                } else {
                    supportQ3ActionPlan.setVisibility(View.GONE);
                    supportQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supportQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!supportQ4Spinner.getSelectedItem().toString().equals("0")) {
                    supportQ4ActionPlan.setVisibility(View.VISIBLE);
                    supportQ4ac.setVisibility(View.VISIBLE);
                } else {
                    supportQ4ActionPlan.setVisibility(View.GONE);
                    supportQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ecoQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ecoQ1Spinner.getSelectedItem().toString().equals("0")) {
                    ecoQ1ActionPlan.setVisibility(View.VISIBLE);
                    ecoQ1ac.setVisibility(View.VISIBLE);
                } else {
                    ecoQ1ActionPlan.setVisibility(View.GONE);
                    ecoQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ecoQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ecoQ2Spinner.getSelectedItem().toString().equals("0")) {
                    ecoQ2ActionPlan.setVisibility(View.VISIBLE);
                    ecoQ2ac.setVisibility(View.VISIBLE);
                } else {
                    ecoQ2ActionPlan.setVisibility(View.GONE);
                    ecoQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ecoQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ecoQ3Spinner.getSelectedItem().toString().equals("0")) {
                    ecoQ3ActionPlan.setVisibility(View.VISIBLE);
                    ecoQ3ac.setVisibility(View.VISIBLE);
                } else {
                    ecoQ3ActionPlan.setVisibility(View.GONE);
                    ecoQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        safeQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!safeQ1Spinner.getSelectedItem().toString().equals("0")) {
                    safeQ1ActionPlan.setVisibility(View.VISIBLE);
                    safeQ1ac.setVisibility(View.VISIBLE);
                } else {
                    safeQ1ActionPlan.setVisibility(View.GONE);
                    safeQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        safeQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!safeQ2Spinner.getSelectedItem().toString().equals("0")) {
                    safeQ2ActionPlan.setVisibility(View.VISIBLE);
                    safeQ2ac.setVisibility(View.VISIBLE);
                } else {
                    safeQ2ActionPlan.setVisibility(View.GONE);
                    safeQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        safeQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!safeQ3Spinner.getSelectedItem().toString().equals("0")) {
                    safeQ3ActionPlan.setVisibility(View.VISIBLE);
                    safeQ3ac.setVisibility(View.VISIBLE);
                } else {
                    safeQ3ActionPlan.setVisibility(View.GONE);
                    safeQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        safeQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!safeQ4Spinner.getSelectedItem().toString().equals("0")) {
                    safeQ4ActionPlan.setVisibility(View.VISIBLE);
                    safeQ4ac.setVisibility(View.VISIBLE);
                } else {
                    safeQ4ActionPlan.setVisibility(View.GONE);
                    safeQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        medicalQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ1Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ1ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ1ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ1ActionPlan.setVisibility(View.GONE);
                    medicalQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicalQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ2Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ2ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ2ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ2ActionPlan.setVisibility(View.GONE);
                    medicalQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicalQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ3Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ3ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ3ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ3ActionPlan.setVisibility(View.GONE);
                    medicalQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicalQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ4Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ4ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ4ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ4ActionPlan.setVisibility(View.GONE);
                    medicalQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicalQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ5Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ5ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ5ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ5ActionPlan.setVisibility(View.GONE);
                    medicalQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        medicalQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!medicalQ6Spinner.getSelectedItem().toString().equals("0")) {
                    medicalQ6ActionPlan.setVisibility(View.VISIBLE);
                    medicalQ6ac.setVisibility(View.VISIBLE);
                } else {
                    medicalQ6ActionPlan.setVisibility(View.GONE);
                    medicalQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assessmentLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!assessmentLocation.getSelectedItem().toString().equals("0")) {
                    assessmentLocationActionPlan.setVisibility(View.VISIBLE);
                    loaAc.setVisibility(View.VISIBLE);
                } else {
                    assessmentLocationActionPlan.setVisibility(View.GONE);
                    loaAc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assessedByScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!assessedByScore.getSelectedItem().toString().equals("0")) {
                    assessedByScoreActionPlan.setVisibility(View.VISIBLE);
                    assessedByAc.setVisibility(View.VISIBLE);
                } else {
                    assessedByScoreActionPlan.setVisibility(View.GONE);
                    assessedByAc.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        riskPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!riskPosition.getSelectedItem().toString().equals("0")) {
                    riskPositionActionPlan.setVisibility(View.VISIBLE);
                    riskPositionAc.setVisibility(View.VISIBLE);
                } else {
                    riskPositionActionPlan.setVisibility(View.GONE);
                    riskPositionAc.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        locationDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elLocation.isExpanded()) {
                    elLocation.collapse();
                } else {
                    criteriaId = "1";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elLocation.expand();
                }
            }
        });

        houseDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elHouse.isExpanded()) {
                    elHouse.collapse();
                } else {
                    criteriaId = "2";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elHouse.expand();
                }
            }
        });

        inTheHomeDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elInTheHome.isExpanded()) {
                    elInTheHome.collapse();
                } else {
                    criteriaId = "3";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elInTheHome.expand();
                }
            }
        });

        communicationDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elCommunication.isExpanded()) {
                    elCommunication.collapse();
                } else {
                    criteriaId = "4";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elCommunication.expand();
                }
            }
        });

        shoppingDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elShopping.isExpanded()) {
                    elShopping.collapse();
                } else {
                    criteriaId = "5";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elShopping.expand();
                }
            }
        });

        foodCookingDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elFoodCooking.isExpanded()) {
                    elFoodCooking.collapse();
                } else {
                    criteriaId = "6";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elFoodCooking.expand();
                }
            }
        });

        laundryDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elLaundry.isExpanded()) {
                    elLaundry.collapse();
                } else {
                    criteriaId = "7";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elLaundry.expand();
                }
            }
        });

        gettingAboutDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elGettingAbout.isExpanded()) {
                    elGettingAbout.collapse();
                } else {
                    criteriaId = "8";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elGettingAbout.expand();
                }
            }
        });

        medicineDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elMedicine.isExpanded()) {
                    elMedicine.collapse();
                } else {
                    criteriaId = "9";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elMedicine.expand();
                }
            }
        });

        homeSupportDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elHomeSupport.isExpanded()) {
                    elHomeSupport.collapse();
                } else {
                    criteriaId = "10";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elHomeSupport.expand();
                }
            }
        });

        economicDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elEconomic.isExpanded()) {
                    elEconomic.collapse();
                } else {
                    criteriaId = "11";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elEconomic.expand();
                }
            }
        });

        safeguardingDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elSafeguarding.isExpanded()) {
                    elSafeguarding.collapse();
                } else {
                    criteriaId = "12";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elSafeguarding.expand();
                }
            }
        });

        medicalDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elMedical.isExpanded()) {
                    elMedical.collapse();
                } else {
                    criteriaId = "13";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elMedical.expand();
                }
            }
        });

        loaDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elLoa.isExpanded()) {
                    elLoa.collapse();
                } else {
                    criteriaId = "14";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elLoa.expand();
                }
            }
        });

        accessedByDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elAccessedBy.isExpanded()) {
                    elAccessedBy.collapse();
                } else {
                    criteriaId = "15";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elAccessedBy.expand();
                }
            }
        });


        riskPositionDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elRiskPosition.isExpanded()) {
                    elRiskPosition.collapse();
                } else {
                    criteriaId = "16";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elRiskPosition.expand();
                }
            }
        });

        mJobManager = GurkhaApplication.getInstance().getJobManager();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                String mPersonalId = getIntent().getStringExtra("personal_id");

                String mLocationQ1Value = locQ1Spinner.getSelectedItem().toString();
                String mLocationQ2Value = locQ2Spinner.getSelectedItem().toString();
                String mLocationQ3Value = locQ3Spinner.getSelectedItem().toString();
                String mLocationQ4Value = locQ4Spinner.getSelectedItem().toString();
                String mLocationQ5Value = locQ5Spinner.getSelectedItem().toString();
                String mLocationQ1ActionPlan = locQ1ActionPlan.getSelectedItem().toString().trim();
                String mLocationQ2ActionPlan = locQ2ActionPlan.getSelectedItem().toString().trim();
                String mLocationQ3ActionPlan = locQ3ActionPlan.getSelectedItem().toString().trim();
                String mLocationQ4ActionPlan = locQ4ActionPlan.getSelectedItem().toString().trim();
                String mLocationQ5ActionPlan = locQ5ActionPlan.getSelectedItem().toString().trim();
                String mLocationQ1DueDate = locQ1DueDate.getText().toString().trim();
                String mLocationQ2DueDate = locQ2DueDate.getText().toString().trim();
                String mLocationQ3DueDate = locQ3DueDate.getText().toString().trim();
                String mLocationQ4DueDate = locQ4DueDate.getText().toString().trim();
                String mLocationQ5DueDate = locQ5DueDate.getText().toString().trim();
                String mLocationQ1Remarks = locQ1Remarks.getText().toString().trim();
                String mLocationQ2Remarks = locQ2Remarks.getText().toString().trim();
                String mLocationQ3Remarks = locQ3Remarks.getText().toString().trim();
                String mLocationQ4Remarks = locQ4Remarks.getText().toString().trim();
                String mLocationQ5Remarks = locQ5Remarks.getText().toString().trim();

                String mHouseNFacilitiesQ1Value = houseQ1Spinner.getSelectedItem().toString();
                String mHouseNFacilitiesQ2Value = houseQ2Spinner.getSelectedItem().toString();
                String mHouseNFacilitiesQ3Value = houseQ3Spinner.getSelectedItem().toString();
                String mHouseNFacilitiesQ4Value = houseQ4Spinner.getSelectedItem().toString();
                String mHouseNFacilitiesQ1ActionPlan = houseQ1ActionPlan.getSelectedItem().toString().trim();
                String mHouseNFacilitiesQ2ActionPlan = houseQ2ActionPlan.getSelectedItem().toString().trim();
                String mHouseNFacilitiesQ3ActionPlan = houseQ3ActionPlan.getSelectedItem().toString().trim();
                String mHouseNFacilitiesQ4ActionPlan = houseQ4ActionPlan.getSelectedItem().toString().trim();
                String mHouseNFacilitiesQ1DueDate = houseQ1DueDate.getText().toString().trim();
                String mHouseNFacilitiesQ2DueDate = houseQ2DueDate.getText().toString().trim();
                String mHouseNFacilitiesQ3DueDate = houseQ3DueDate.getText().toString().trim();
                String mHouseNFacilitiesQ4DueDate = houseQ4DueDate.getText().toString().trim();
                String mHouseNFacilitiesQ1Remarks = houseQ1Remarks.getText().toString().trim();
                String mHouseNFacilitiesQ2Remarks = houseQ2Remarks.getText().toString().trim();
                String mHouseNFacilitiesQ3Remarks = houseQ3Remarks.getText().toString().trim();
                String mHouseNFacilitiesQ4Remarks = houseQ4Remarks.getText().toString().trim();

                String mInHomeQ1Value = homeQ1Spinner.getSelectedItem().toString();
                String mInHomeQ2Value = homeQ2Spinner.getSelectedItem().toString();
                String mInHomeQ3Value = homeQ3Spinner.getSelectedItem().toString();
                String mInHomeQ4Value = homeQ4Spinner.getSelectedItem().toString();
                String mInHomeQ5Value = homeQ5Spinner.getSelectedItem().toString();
                String mInHomeQ6Value = homeQ6Spinner.getSelectedItem().toString();
                String mInHomeQ7Value = homeQ7Spinner.getSelectedItem().toString();
                String mInHomeQ8Value = homeQ8Spinner.getSelectedItem().toString();
                String mInHomeQ1ActionPlan = homeQ1ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ2ActionPlan = homeQ2ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ3ActionPlan = homeQ3ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ4ActionPlan = homeQ4ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ5ActionPlan = homeQ5ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ6ActionPlan = homeQ6ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ7ActionPlan = homeQ7ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ8ActionPlan = homeQ8ActionPlan.getSelectedItem().toString().trim();
                String mInHomeQ1DueDate = homeQ1DueDate.getText().toString().trim();
                String mInHomeQ2DueDate = homeQ2DueDate.getText().toString().trim();
                String mInHomeQ3DueDate = homeQ3DueDate.getText().toString().trim();
                String mInHomeQ4DueDate = homeQ4DueDate.getText().toString().trim();
                String mInHomeQ5DueDate = homeQ5DueDate.getText().toString().trim();
                String mInHomeQ6DueDate = homeQ6DueDate.getText().toString().trim();
                String mInHomeQ7DueDate = homeQ7DueDate.getText().toString().trim();
                String mInHomeQ8DueDate = homeQ8DueDate.getText().toString().trim();
                String mInHomeQ1Remarks = homeQ1Remarks.getText().toString().trim();
                String mInHomeQ2Remarks = homeQ2Remarks.getText().toString().trim();
                String mInHomeQ3Remarks = homeQ3Remarks.getText().toString().trim();
                String mInHomeQ4Remarks = homeQ4Remarks.getText().toString().trim();
                String mInHomeQ5Remarks = homeQ5Remarks.getText().toString().trim();
                String mInHomeQ6Remarks = homeQ6Remarks.getText().toString().trim();
                String mInHomeQ7Remarks = homeQ7Remarks.getText().toString().trim();
                String mInHomeQ8Remarks = homeQ8Remarks.getText().toString().trim();

                String mCommunicationQ1Value = comQ1Spinner.getSelectedItem().toString();
                String mCommunicationQ2Value = comQ2Spinner.getSelectedItem().toString();
                String mCommunicationQ3Value = comQ3Spinner.getSelectedItem().toString();
                String mCommunicationQ4Value = comQ4Spinner.getSelectedItem().toString();
                String mCommunicationQ5Value = comQ5Spinner.getSelectedItem().toString();
                String mCommunicationQ1ActionPlan = comQ1ActionPlan.getSelectedItem().toString().trim();
                String mCommunicationQ2ActionPlan = comQ2ActionPlan.getSelectedItem().toString().trim();
                String mCommunicationQ3ActionPlan = comQ3ActionPlan.getSelectedItem().toString().trim();
                String mCommunicationQ4ActionPlan = comQ4ActionPlan.getSelectedItem().toString().trim();
                String mCommunicationQ5ActionPlan = comQ5ActionPlan.getSelectedItem().toString().trim();
                String mCommunicationQ1DueDate = comQ1DueDate.getText().toString().trim();
                String mCommunicationQ2DueDate = comQ2DueDate.getText().toString().trim();
                String mCommunicationQ3DueDate = comQ3DueDate.getText().toString().trim();
                String mCommunicationQ4DueDate = comQ4DueDate.getText().toString().trim();
                String mCommunicationQ5DueDate = comQ5DueDate.getText().toString().trim();
                String mCommunicationQ1Remarks = comQ1Remarks.getText().toString().trim();
                String mCommunicationQ2Remarks = comQ2Remarks.getText().toString().trim();
                String mCommunicationQ3Remarks = comQ3Remarks.getText().toString().trim();
                String mCommunicationQ4Remarks = comQ4Remarks.getText().toString().trim();
                String mCommunicationQ5Remarks = comQ5Remarks.getText().toString().trim();

                String mShoppingQ1Value = shopQ1Spinner.getSelectedItem().toString();
                String mShoppingQ2Value = shopQ2Spinner.getSelectedItem().toString();
                String mShoppingQ1ActionPlan = shopQ1ActionPlan.getSelectedItem().toString().trim();
                String mShoppingQ2ActionPlan = shopQ2ActionPlan.getSelectedItem().toString().trim();
                String mShoppingQ1DueDate = shopQ1DueDate.getText().toString().trim();
                String mShoppingQ2DueDate = shopQ2DueDate.getText().toString().trim();
                String mShoppingQ1Remarks = shopQ1Remarks.getText().toString().trim();
                String mShoppingQ2Remarks = shopQ2Remarks.getText().toString().trim();

                String mFoodPrepNCookingQ1Value = foodQ1Spinner.getSelectedItem().toString();
                String mFoodPrepNCookingQ2Value = foodQ2Spinner.getSelectedItem().toString();
                String mFoodPrepNCookingQ3Value = foodQ3Spinner.getSelectedItem().toString();
                String mFoodPrepNCookingQ4Value = foodQ4Spinner.getSelectedItem().toString();
                String mFoodPrepNCookingQ1ActionPlan = foodQ1ActionPlan.getSelectedItem().toString().trim();
                String mFoodPrepNCookingQ2ActionPlan = foodQ2ActionPlan.getSelectedItem().toString().trim();
                String mFoodPrepNCookingQ3ActionPlan = foodQ3ActionPlan.getSelectedItem().toString().trim();
                String mFoodPrepNCookingQ4ActionPlan = foodQ4ActionPlan.getSelectedItem().toString().trim();
                String mFoodPrepNCookingQ1DueDate = foodQ1DueDate.getText().toString().trim();
                String mFoodPrepNCookingQ2DueDate = foodQ2DueDate.getText().toString().trim();
                String mFoodPrepNCookingQ3DueDate = foodQ3DueDate.getText().toString().trim();
                String mFoodPrepNCookingQ4DueDate = foodQ4DueDate.getText().toString().trim();
                String mFoodPrepNCookingQ1Remarks = foodQ1Remarks.getText().toString().trim();
                String mFoodPrepNCookingQ2Remarks = foodQ2Remarks.getText().toString().trim();
                String mFoodPrepNCookingQ3Remarks = foodQ3Remarks.getText().toString().trim();
                String mFoodPrepNCookingQ4Remarks = foodQ4Remarks.getText().toString().trim();

                String mLaundryQ1Value = laundryQ1Spinner.getSelectedItem().toString();
                String mLaundryQ1ActionPlan = laundryQ1ActionPlan.getSelectedItem().toString().trim();
                String mLaundryQ1DueDate = laundryQ1DueDate.getText().toString().trim();
                String mLaundryQ1Remarks = laundryQ1Remarks.getText().toString().trim();

                String mGettingAboutQ1Value = aboutQ1Spinner.getSelectedItem().toString();
                String mGettingAboutQ2Value = aboutQ2Spinner.getSelectedItem().toString();
                String mGettingAboutQ1ActionPlan = aboutQ1ActionPlan.getSelectedItem().toString().trim();
                String mGettingAboutQ2ActionPlan = aboutQ2ActionPlan.getSelectedItem().toString().trim();
                String mGettingAboutQ1DueDate = aboutQ1DueDate.getText().toString().trim();
                String mGettingAboutQ2DueDate = aboutQ2DueDate.getText().toString().trim();
                String mGettingAboutQ1Remarks = aboutQ1Remarks.getText().toString().trim();
                String mGettingAboutQ2Remarks = aboutQ2Remarks.getText().toString().trim();

                String mMedicineQ1Value = medicineQ1Spinner.getSelectedItem().toString();
                String mMedicineQ1ActionPlan = medicineQ1ActionPlan.getSelectedItem().toString().trim();
                String mMedicineQ1DueDate = medicineQ1DueDate.getText().toString().trim();
                String mMedicineQ1Remarks = medicineQ1Remarks.getText().toString().trim();

                String mHomeSupportQ1Value = supportQ1Spinner.getSelectedItem().toString();
                String mHomeSupportQ2Value = supportQ2Spinner.getSelectedItem().toString();
                String mHomeSupportQ3Value = supportQ3Spinner.getSelectedItem().toString();
                String mHomeSupportQ4Value = supportQ4Spinner.getSelectedItem().toString();
                String mHomeSupportQ1ActionPlan = supportQ1ActionPlan.getSelectedItem().toString().trim();
                String mHomeSupportQ2ActionPlan = supportQ2ActionPlan.getSelectedItem().toString().trim();
                String mHomeSupportQ3ActionPlan = supportQ3ActionPlan.getSelectedItem().toString().trim();
                String mHomeSupportQ4ActionPlan = supportQ4ActionPlan.getSelectedItem().toString().trim();
                String mHomeSupportQ1DueDate = supportQ1DueDate.getText().toString().trim();
                String mHomeSupportQ2DueDate = supportQ2DueDate.getText().toString().trim();
                String mHomeSupportQ3DueDate = supportQ3DueDate.getText().toString().trim();
                String mHomeSupportQ4DueDate = supportQ4DueDate.getText().toString().trim();
                String mHomeSupportQ1Remarks = supportQ1Remarks.getText().toString().trim();
                String mHomeSupportQ2Remarks = supportQ2Remarks.getText().toString().trim();
                String mHomeSupportQ3Remarks = supportQ3Remarks.getText().toString().trim();
                String mHomeSupportQ4Remarks = supportQ4Remarks.getText().toString().trim();

                String mEconomicQ1Value = ecoQ1Spinner.getSelectedItem().toString();
                String mEconomicQ2Value = ecoQ2Spinner.getSelectedItem().toString();
                String mEconomicQ3Value = ecoQ3Spinner.getSelectedItem().toString();
                String mEconomicQ1ActionPlan = ecoQ1ActionPlan.getSelectedItem().toString().trim();
                String mEconomicQ2ActionPlan = ecoQ2ActionPlan.getSelectedItem().toString().trim();
                String mEconomicQ3ActionPlan = ecoQ3ActionPlan.getSelectedItem().toString().trim();
                String mEconomicQ1DueDate = ecoQ1DueDate.getText().toString().trim();
                String mEconomicQ2DueDate = ecoQ2DueDate.getText().toString().trim();
                String mEconomicQ3DueDate = ecoQ3DueDate.getText().toString().trim();
                String mEconomicQ1Remarks = ecoQ1Remarks.getText().toString().trim();
                String mEconomicQ2Remarks = ecoQ2Remarks.getText().toString().trim();
                String mEconomicQ3Remarks = ecoQ3Remarks.getText().toString().trim();


                String mSafeguardingQ1Value = safeQ1Spinner.getSelectedItem().toString();
                String mSafeguardingQ2Value = safeQ2Spinner.getSelectedItem().toString();
                String mSafeguardingQ3Value = safeQ3Spinner.getSelectedItem().toString();
                String mSafeguardingQ4Value = safeQ4Spinner.getSelectedItem().toString();
                String mSafeguardingQ1ActionPlan = safeQ1ActionPlan.getSelectedItem().toString().trim();
                String mSafeguardingQ2ActionPlan = safeQ2ActionPlan.getSelectedItem().toString().trim();
                String mSafeguardingQ3ActionPlan = safeQ3ActionPlan.getSelectedItem().toString().trim();
                String mSafeguardingQ4ActionPlan = safeQ4ActionPlan.getSelectedItem().toString().trim();
                String mSafeguardingQ1DueDate = safeQ1DueDate.getText().toString().trim();
                String mSafeguardingQ2DueDate = safeQ2DueDate.getText().toString().trim();
                String mSafeguardingQ3DueDate = safeQ3DueDate.getText().toString().trim();
                String mSafeguardingQ4DueDate = safeQ4DueDate.getText().toString().trim();
                String mSafeguardingQ1Remarks = safeQ1Remarks.getText().toString().trim();
                String mSafeguardingQ2Remarks = safeQ2Remarks.getText().toString().trim();
                String mSafeguardingQ3Remarks = safeQ3Remarks.getText().toString().trim();
                String mSafeguardingQ4Remarks = safeQ4Remarks.getText().toString().trim();


                String mMedicalQ1Value = medicalQ1Spinner.getSelectedItem().toString();
                String mMedicalQ2Value = medicalQ2Spinner.getSelectedItem().toString();
                String mMedicalQ3Value = medicalQ3Spinner.getSelectedItem().toString();
                String mMedicalQ4Value = medicalQ4Spinner.getSelectedItem().toString();
                String mMedicalQ5Value = medicalQ5Spinner.getSelectedItem().toString();
                String mMedicalQ6Value = medicalQ6Spinner.getSelectedItem().toString();
                String mMedicalQ1ActionPlan = medicalQ1ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ2ActionPlan = medicalQ2ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ3ActionPlan = medicalQ3ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ4ActionPlan = medicalQ4ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ5ActionPlan = medicalQ5ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ6ActionPlan = medicalQ6ActionPlan.getSelectedItem().toString().trim();
                String mMedicalQ1DueDate = medicalQ1DueDate.getText().toString().trim();
                String mMedicalQ2DueDate = medicalQ2DueDate.getText().toString().trim();
                String mMedicalQ3DueDate = medicalQ3DueDate.getText().toString().trim();
                String mMedicalQ4DueDate = medicalQ4DueDate.getText().toString().trim();
                String mMedicalQ5DueDate = medicalQ5DueDate.getText().toString().trim();
                String mMedicalQ6DueDate = medicalQ6DueDate.getText().toString().trim();
                String mMedicalQ1Remarks = medicalQ1Remarks.getText().toString().trim();
                String mMedicalQ2Remarks = medicalQ2Remarks.getText().toString().trim();
                String mMedicalQ3Remarks = medicalQ3Remarks.getText().toString().trim();
                String mMedicalQ4Remarks = medicalQ4Remarks.getText().toString().trim();
                String mMedicalQ5Remarks = medicalQ5Remarks.getText().toString().trim();
                String mMedicalQ6Remarks = medicalQ6Remarks.getText().toString().trim();

                String mLocationOfAssessmentValue = assessmentLocation.getSelectedItem().toString();
                String mLocationOfAssessmentActionPlan = assessmentLocationActionPlan.getSelectedItem().toString().trim();
                String mLocationOfAssessmentDueDate = assessmentLocationDueDate.getText().toString().trim();
                String mLocationOfAssessmentRemarks = assessmentLocationRemarks.getText().toString().trim();

                String mAssessedByScoreValue = assessedByScore.getSelectedItem().toString();
                String mAssessedByScoreActionPlan = assessedByScoreActionPlan.getSelectedItem().toString().trim();
                String mAssessedByScoreDueDate = assessedByScoreDueDate.getText().toString().trim();
                String mAssessedByScoreRemarks = assessedByScoreRemarks.getText().toString().trim();

                String mRiskAssociatedValue = riskPosition.getSelectedItem().toString();
                String mRiskAssociatedActionPlan = riskPositionActionPlan.getSelectedItem().toString().trim();
                String mRiskAssociatedDueDate = riskPositionDueDate.getText().toString().trim();
                String mRiskAssociatedRemarks = riskPositionRemarks.getText().toString().trim();

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
                params.put("personal_id", mPersonalId);
                params.put("location_q1_value", mLocationQ1Value);
                params.put("location_q2_value", mLocationQ2Value);
                params.put("location_q3_value", mLocationQ3Value);
                params.put("location_q4_value", mLocationQ4Value);
                params.put("location_q5_value", mLocationQ5Value);
                params.put("location_q1_action_plan", mLocationQ1ActionPlan);
                params.put("location_q2_action_plan", mLocationQ2ActionPlan);
                params.put("location_q3_action_plan", mLocationQ3ActionPlan);
                params.put("location_q4_action_plan", mLocationQ4ActionPlan);
                params.put("location_q5_action_plan", mLocationQ5ActionPlan);
                params.put("location_q1_due_date", mLocationQ1DueDate);
                params.put("location_q2_due_date", mLocationQ2DueDate);
                params.put("location_q3_due_date", mLocationQ3DueDate);
                params.put("location_q4_due_date", mLocationQ4DueDate);
                params.put("location_q5_due_date", mLocationQ5DueDate);
                params.put("location_q1_remarks", mLocationQ1Remarks);
                params.put("location_q2_remarks", mLocationQ2Remarks);
                params.put("location_q3_remarks", mLocationQ3Remarks);
                params.put("location_q4_remarks", mLocationQ4Remarks);
                params.put("location_q5_remarks", mLocationQ5Remarks);

                params.put("house_n_facilities_q1_value", mHouseNFacilitiesQ1Value);
                params.put("house_n_facilities_q2_value", mHouseNFacilitiesQ2Value);
                params.put("house_n_facilities_q3_value", mHouseNFacilitiesQ3Value);
                params.put("house_n_facilities_q4_value", mHouseNFacilitiesQ4Value);
                params.put("house_n_facilities_q1_action_plan", mHouseNFacilitiesQ1ActionPlan);
                params.put("house_n_facilities_q2_action_plan", mHouseNFacilitiesQ2ActionPlan);
                params.put("house_n_facilities_q3_action_plan", mHouseNFacilitiesQ3ActionPlan);
                params.put("house_n_facilities_q4_action_plan", mHouseNFacilitiesQ4ActionPlan);
                params.put("house_n_facilities_q1_due_date", mHouseNFacilitiesQ1DueDate);
                params.put("house_n_facilities_q2_due_date", mHouseNFacilitiesQ2DueDate);
                params.put("house_n_facilities_q3_due_date", mHouseNFacilitiesQ3DueDate);
                params.put("house_n_facilities_q4_due_date", mHouseNFacilitiesQ4DueDate);
                params.put("house_n_facilities_q1_remarks", mHouseNFacilitiesQ1Remarks);
                params.put("house_n_facilities_q2_remarks", mHouseNFacilitiesQ2Remarks);
                params.put("house_n_facilities_q3_remarks", mHouseNFacilitiesQ3Remarks);
                params.put("house_n_facilities_q4_remarks", mHouseNFacilitiesQ4Remarks);

                params.put("in_the_home_q1_value", mInHomeQ1Value);
                params.put("in_the_home_q2_value", mInHomeQ2Value);
                params.put("in_the_home_q3_value", mInHomeQ3Value);
                params.put("in_the_home_q4_value", mInHomeQ4Value);
                params.put("in_the_home_q5_value", mInHomeQ5Value);
                params.put("in_the_home_q6_value", mInHomeQ6Value);
                params.put("in_the_home_q7_value", mInHomeQ7Value);
                params.put("in_the_home_q8_value", mInHomeQ8Value);
                params.put("in_the_home_q1_action_plan", mInHomeQ1ActionPlan);
                params.put("in_the_home_q2_action_plan", mInHomeQ2ActionPlan);
                params.put("in_the_home_q3_action_plan", mInHomeQ3ActionPlan);
                params.put("in_the_home_q4_action_plan", mInHomeQ4ActionPlan);
                params.put("in_the_home_q5_action_plan", mInHomeQ5ActionPlan);
                params.put("in_the_home_q6_action_plan", mInHomeQ6ActionPlan);
                params.put("in_the_home_q7_action_plan", mInHomeQ7ActionPlan);
                params.put("in_the_home_q7_action_plan", mInHomeQ8ActionPlan);
                params.put("in_the_home_q1_due_date", mInHomeQ1DueDate);
                params.put("in_the_home_q2_due_date", mInHomeQ2DueDate);
                params.put("in_the_home_q3_due_date", mInHomeQ3DueDate);
                params.put("in_the_home_q4_due_date", mInHomeQ4DueDate);
                params.put("in_the_home_q5_due_date", mInHomeQ5DueDate);
                params.put("in_the_home_q6_due_date", mInHomeQ6DueDate);
                params.put("in_the_home_q7_due_date", mInHomeQ7DueDate);
                params.put("in_the_home_q8_due_date", mInHomeQ8DueDate);
                params.put("in_the_home_q1_remarks", mInHomeQ1Remarks);
                params.put("in_the_home_q2_remarks", mInHomeQ2Remarks);
                params.put("in_the_home_q3_remarks", mInHomeQ3Remarks);
                params.put("in_the_home_q4_remarks", mInHomeQ4Remarks);
                params.put("in_the_home_q5_remarks", mInHomeQ5Remarks);
                params.put("in_the_home_q6_remarks", mInHomeQ6Remarks);
                params.put("in_the_home_q7_remarks", mInHomeQ7Remarks);
                params.put("in_the_home_q8_remarks", mInHomeQ8Remarks);

                params.put("communication_q1_value", mCommunicationQ1Value);
                params.put("communication_q2_value", mCommunicationQ2Value);
                params.put("communication_q3_value", mCommunicationQ3Value);
                params.put("communication_q4_value", mCommunicationQ4Value);
                params.put("communication_q5_value", mCommunicationQ5Value);
                params.put("communication_q1_action_plan", mCommunicationQ1ActionPlan);
                params.put("communication_q2_action_plan", mCommunicationQ2ActionPlan);
                params.put("communication_q3_action_plan", mCommunicationQ3ActionPlan);
                params.put("communication_q4_action_plan", mCommunicationQ4ActionPlan);
                params.put("communication_q5_action_plan", mCommunicationQ5ActionPlan);
                params.put("communication_q1_due_date", mCommunicationQ1DueDate);
                params.put("communication_q2_due_date", mCommunicationQ2DueDate);
                params.put("communication_q3_due_date", mCommunicationQ3DueDate);
                params.put("communication_q4_due_date", mCommunicationQ4DueDate);
                params.put("communication_q5_due_date", mCommunicationQ5DueDate);
                params.put("communication_q1_remarks", mCommunicationQ1Remarks);
                params.put("communication_q2_remarks", mCommunicationQ2Remarks);
                params.put("communication_q3_remarks", mCommunicationQ3Remarks);
                params.put("communication_q4_remarks", mCommunicationQ4Remarks);
                params.put("communication_q5_remarks", mCommunicationQ5Remarks);


                params.put("shopping_q1_value", mShoppingQ1Value);
                params.put("shopping_q2_value", mShoppingQ2Value);
                params.put("shopping_q1_action_plan", mShoppingQ1ActionPlan);
                params.put("shopping_q2_action_plan", mShoppingQ2ActionPlan);
                params.put("shopping_q1_due_date", mShoppingQ1DueDate);
                params.put("shopping_q2_due_date", mShoppingQ2DueDate);
                params.put("shopping_q1_remarks", mShoppingQ1Remarks);
                params.put("shopping_q2_remarks", mShoppingQ2Remarks);

                params.put("food_prep_n_cooking_q1_value", mFoodPrepNCookingQ1Value);
                params.put("food_prep_n_cooking_q2_value", mFoodPrepNCookingQ2Value);
                params.put("food_prep_n_cooking_q3_value", mFoodPrepNCookingQ3Value);
                params.put("food_prep_n_cooking_q4_value", mFoodPrepNCookingQ4Value);
                params.put("food_prep_n_cooking_q1_action_plan", mFoodPrepNCookingQ1ActionPlan);
                params.put("food_prep_n_cooking_q2_action_plan", mFoodPrepNCookingQ2ActionPlan);
                params.put("food_prep_n_cooking_q3_action_plan", mFoodPrepNCookingQ3ActionPlan);
                params.put("food_prep_n_cooking_q4_action_plan", mFoodPrepNCookingQ4ActionPlan);
                params.put("food_prep_n_cooking_q1_due_date", mFoodPrepNCookingQ1DueDate);
                params.put("food_prep_n_cooking_q2_due_date", mFoodPrepNCookingQ2DueDate);
                params.put("food_prep_n_cooking_q3_due_date", mFoodPrepNCookingQ3DueDate);
                params.put("food_prep_n_cooking_q4_due_date", mFoodPrepNCookingQ4DueDate);
                params.put("food_prep_n_cooking_q1_remarks", mFoodPrepNCookingQ1Remarks);
                params.put("food_prep_n_cooking_q2_remarks", mFoodPrepNCookingQ2Remarks);
                params.put("food_prep_n_cooking_q3_remarks", mFoodPrepNCookingQ3Remarks);
                params.put("food_prep_n_cooking_q4_remarks", mFoodPrepNCookingQ4Remarks);

                params.put("laundry_n_washing_q1_value", mLaundryQ1Value);
                params.put("laundry_n_washing_q1_action_plan", mLaundryQ1ActionPlan);
                params.put("laundry_n_washing_q1_due_date", mLaundryQ1DueDate);
                params.put("laundry_n_washing_q1_remarks", mLaundryQ1Remarks);


                params.put("getting_about_q1_value", mGettingAboutQ1Value);
                params.put("getting_about_q2_value", mGettingAboutQ2Value);
                params.put("getting_about_q1_action_plan", mGettingAboutQ1ActionPlan);
                params.put("getting_about_q2_action_plan", mGettingAboutQ2ActionPlan);
                params.put("getting_about_q1_due_date", mGettingAboutQ1DueDate);
                params.put("getting_about_q2_due_date", mGettingAboutQ2DueDate);
                params.put("getting_about_q1_remarks", mGettingAboutQ1Remarks);
                params.put("getting_about_q2_remarks", mGettingAboutQ2Remarks);

                params.put("medicine_q1_value", mMedicineQ1Value);
                params.put("medicine_q1_action_plan", mMedicineQ1ActionPlan);
                params.put("medicine_q1_due_date", mMedicineQ1DueDate);
                params.put("medicine_q1_remarks", mMedicineQ1Remarks);

                params.put("home_support_q1_value", mHomeSupportQ1Value);
                params.put("home_support_q2_value", mHomeSupportQ2Value);
                params.put("home_support_q3_value", mHomeSupportQ3Value);
                params.put("home_support_q4_value", mHomeSupportQ4Value);
                params.put("home_support_q1_action_plan", mHomeSupportQ1ActionPlan);
                params.put("home_support_q2_action_plan", mHomeSupportQ2ActionPlan);
                params.put("home_support_q3_action_plan", mHomeSupportQ3ActionPlan);
                params.put("home_support_q4_action_plan", mHomeSupportQ4ActionPlan);
                params.put("home_support_q1_due_date", mHomeSupportQ1DueDate);
                params.put("home_support_q2_due_date", mHomeSupportQ2DueDate);
                params.put("home_support_q3_due_date", mHomeSupportQ3DueDate);
                params.put("home_support_q4_due_date", mHomeSupportQ4DueDate);
                params.put("home_support_q1_remarks", mHomeSupportQ1Remarks);
                params.put("home_support_q2_remarks", mHomeSupportQ2Remarks);
                params.put("home_support_q3_remarks", mHomeSupportQ3Remarks);
                params.put("home_support_q4_remarks", mHomeSupportQ4Remarks);

                params.put("economic_q1_value", mEconomicQ1Value);
                params.put("economic_q2_value", mEconomicQ2Value);
                params.put("economic_q3_value", mEconomicQ3Value);
                params.put("economic_q1_action_plan", mEconomicQ1ActionPlan);
                params.put("economic_q2_action_plan", mEconomicQ2ActionPlan);
                params.put("economic_q3_action_plan", mEconomicQ3ActionPlan);
                params.put("economic_q1_due_date", mEconomicQ1DueDate);
                params.put("economic_q2_due_date", mEconomicQ2DueDate);
                params.put("economic_q3_due_date", mEconomicQ3DueDate);
                params.put("economic_q1_remarks", mEconomicQ1Remarks);
                params.put("economic_q2_remarks", mEconomicQ2Remarks);
                params.put("economic_q3_remarks", mEconomicQ3Remarks);

                params.put("safeguarding_q1_value", mSafeguardingQ1Value);
                params.put("safeguarding_q2_value", mSafeguardingQ2Value);
                params.put("safeguarding_q3_value", mSafeguardingQ3Value);
                params.put("safeguarding_q4_value", mSafeguardingQ4Value);
                params.put("safeguarding_q1_action_plan", mSafeguardingQ1ActionPlan);
                params.put("safeguarding_q2_action_plan", mSafeguardingQ2ActionPlan);
                params.put("safeguarding_q3_action_plan", mSafeguardingQ3ActionPlan);
                params.put("safeguarding_q4_action_plan", mSafeguardingQ4ActionPlan);
                params.put("safeguarding_q1_due_date", mSafeguardingQ1DueDate);
                params.put("safeguarding_q2_due_date", mSafeguardingQ2DueDate);
                params.put("safeguarding_q3_due_date", mSafeguardingQ3DueDate);
                params.put("safeguarding_q4_due_date", mSafeguardingQ4DueDate);
                params.put("safeguarding_q1_remarks", mSafeguardingQ1Remarks);
                params.put("safeguarding_q2_remarks", mSafeguardingQ2Remarks);
                params.put("safeguarding_q3_remarks", mSafeguardingQ3Remarks);
                params.put("safeguarding_q4_remarks", mSafeguardingQ4Remarks);

                params.put("medical_q1_value", mMedicalQ1Value);
                params.put("medical_q2_value", mMedicalQ2Value);
                params.put("medical_q3_value", mMedicalQ3Value);
                params.put("medical_q4_value", mMedicalQ4Value);
                params.put("medical_q5_value", mMedicalQ5Value);
                params.put("medical_q6value", mMedicalQ6Value);
                params.put("medical_q1_action_plan", mMedicalQ1ActionPlan);
                params.put("medical_q2_action_plan", mMedicalQ2ActionPlan);
                params.put("medical_q3_action_plan", mMedicalQ3ActionPlan);
                params.put("medical_q4_action_plan", mMedicalQ4ActionPlan);
                params.put("medical_q5_action_plan", mMedicalQ5ActionPlan);
                params.put("medical_q6_action_plan", mMedicalQ6ActionPlan);
                params.put("medical_q1_due_date", mMedicalQ1DueDate);
                params.put("medical_q2_due_date", mMedicalQ2DueDate);
                params.put("medical_q3_due_date", mMedicalQ3DueDate);
                params.put("medical_q4_due_date", mMedicalQ4DueDate);
                params.put("medical_q5_due_date", mMedicalQ5DueDate);
                params.put("medical_q6_due_date", mMedicalQ6DueDate);
                params.put("medical_q1_remarks", mMedicalQ1Remarks);
                params.put("medical_q2_remarks", mMedicalQ2Remarks);
                params.put("medical_q3_remarks", mMedicalQ3Remarks);
                params.put("medical_q4_remarks", mMedicalQ4Remarks);
                params.put("medical_q5_remarks", mMedicalQ5Remarks);
                params.put("medical_q6_remarks", mMedicalQ6Remarks);

                params.put("location_of_assessment_value", mLocationOfAssessmentValue);
                params.put("location_of_assessment_action_plan", mLocationOfAssessmentActionPlan);
                params.put("location_of_assessment_due_date", mLocationOfAssessmentDueDate);
                params.put("location_of_assessment_remarks", mLocationOfAssessmentRemarks);

                params.put("assessed_by_score_value", mAssessedByScoreValue);
                params.put("assessed_by_score_action_plan", mAssessedByScoreActionPlan);
                params.put("assessed_by_score_due_date", mAssessedByScoreDueDate);
                params.put("assessed_by_score_remarks", mAssessedByScoreRemarks);

                params.put("risk_associated_value", mRiskAssociatedValue);
                params.put("risk_associated_action_plan", mRiskAssociatedActionPlan);
                params.put("risk_associated_due_date", mRiskAssociatedDueDate);
                params.put("risk_associated_remarks", mRiskAssociatedRemarks);

                params.put("api_token", token);


                int total = Integer.valueOf(mLocationQ1Value) + Integer.valueOf(mLocationQ2Value) + Integer.valueOf(mLocationQ3Value) +
                        Integer.valueOf(mLocationQ4Value) + Integer.valueOf(mLocationQ5Value) + Integer.valueOf(mHouseNFacilitiesQ1Value) +
                        Integer.valueOf(mHouseNFacilitiesQ2Value) + Integer.valueOf(mHouseNFacilitiesQ3Value) + Integer.valueOf(mHouseNFacilitiesQ4Value) +
                        Integer.valueOf(mInHomeQ1Value) + Integer.valueOf(mInHomeQ2Value) + Integer.valueOf(mInHomeQ3Value) + Integer.valueOf(mInHomeQ4Value) +
                        Integer.valueOf(mInHomeQ5Value) + Integer.valueOf(mInHomeQ6Value) + Integer.valueOf(mInHomeQ7Value) + Integer.valueOf(mInHomeQ8Value) +
                        Integer.valueOf(mCommunicationQ1Value) + Integer.valueOf(mCommunicationQ2Value) + Integer.valueOf(mCommunicationQ3Value) +
                        Integer.valueOf(mCommunicationQ4Value) + Integer.valueOf(mCommunicationQ5Value) + Integer.valueOf(mShoppingQ1Value) +
                        Integer.valueOf(mShoppingQ2Value) + Integer.valueOf(mFoodPrepNCookingQ1Value) + Integer.valueOf(mFoodPrepNCookingQ2Value) +
                        Integer.valueOf(mFoodPrepNCookingQ3Value) + Integer.valueOf(mFoodPrepNCookingQ4Value) + Integer.valueOf(mLaundryQ1Value) +
                        Integer.valueOf(mGettingAboutQ1Value) + Integer.valueOf(mGettingAboutQ2Value) + Integer.valueOf(mMedicalQ1Value) +
                        Integer.valueOf(mHomeSupportQ1Value) + Integer.valueOf(mHomeSupportQ2Value) + Integer.valueOf(mHomeSupportQ3Value) +
                        Integer.valueOf(mHomeSupportQ4Value) + Integer.valueOf(mEconomicQ1Value) + Integer.valueOf(mEconomicQ2Value) +
                        Integer.valueOf(mEconomicQ3Value) + Integer.valueOf(mSafeguardingQ1Value) + Integer.valueOf(mSafeguardingQ2Value) +
                        Integer.valueOf(mSafeguardingQ3Value) + Integer.valueOf(mSafeguardingQ4Value) + Integer.valueOf(mMedicalQ1Value) +
                        Integer.valueOf(mMedicalQ2Value) + Integer.valueOf(mMedicalQ3Value) + Integer.valueOf(mMedicalQ4Value) + Integer.valueOf(mMedicalQ5Value) +
                        Integer.valueOf(mMedicalQ6Value);

//                    totalScore.setText(String.valueOf(total));


                if (mSafeguardingQ1Value.equals("0")) {
                    if (mSafeguardingQ1ActionPlan.isEmpty()) {
                        Toast.makeText(AssessmentQuestions.this, "Please fill up safeguarding action plan 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mSafeguardingQ2Value.equals("0")) {
                    if (mSafeguardingQ2ActionPlan.isEmpty()) {
                        Toast.makeText(AssessmentQuestions.this, "Please fill up safeguarding action plan 2", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mSafeguardingQ3Value.equals("0")) {
                    if (mSafeguardingQ1ActionPlan.isEmpty()) {
                        Toast.makeText(AssessmentQuestions.this, "Please fill up safeguarding action plan 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mSafeguardingQ4Value.equals("0")) {
                    if (mSafeguardingQ4ActionPlan.isEmpty()) {
                        Toast.makeText(AssessmentQuestions.this, "Please fill up safeguarding action plan 4", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                JSONObject parameter = new JSONObject(params);

//                    OkHttpClient client = new OkHttpClient();

                Log.e("JSON:", parameter.toString());
                mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));


                    /*final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
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
                Toast.makeText(AssessmentQuestions.this, "Total Score = " + String.valueOf(total), Toast.LENGTH_SHORT).show();
                finish();

               /* } else {
                    Toast.makeText(AssessmentQuestions.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

    private void pickDate() {
        SetDate locQ1DatePicker = new SetDate(this, locQ1DueDate);
        SetDate locQ2DatePicker = new SetDate(this, locQ2DueDate);
        SetDate locQ3DatePicker = new SetDate(this, locQ3DueDate);
        SetDate locQ4DatePicker = new SetDate(this, locQ4DueDate);
        SetDate locQ5DatePicker = new SetDate(this, locQ5DueDate);

        SetDate houseQ1DatePicker = new SetDate(this, houseQ1DueDate);
        SetDate houseQ2DatePicker = new SetDate(this, houseQ2DueDate);
        SetDate houseQ3DatePicker = new SetDate(this, houseQ3DueDate);
        SetDate houseQ4DatePicker = new SetDate(this, houseQ4DueDate);

        SetDate homeQ1DatePicker = new SetDate(this, homeQ1DueDate);
        SetDate homeQ2DatePicker = new SetDate(this, homeQ2DueDate);
        SetDate homeQ3DatePicker = new SetDate(this, homeQ3DueDate);
        SetDate homeQ4DatePicker = new SetDate(this, homeQ4DueDate);
        SetDate homeQ5DatePicker = new SetDate(this, homeQ5DueDate);
        SetDate homeQ6DatePicker = new SetDate(this, homeQ6DueDate);
        SetDate homeQ7DatePicker = new SetDate(this, homeQ7DueDate);
        SetDate homeQ8DatePicker = new SetDate(this, homeQ8DueDate);

        SetDate comQ1DatePicker = new SetDate(this, comQ1DueDate);
        SetDate comQ2DatePicker = new SetDate(this, comQ2DueDate);
        SetDate comQ3DatePicker = new SetDate(this, comQ3DueDate);
        SetDate comQ4DatePicker = new SetDate(this, comQ4DueDate);
        SetDate comQ5DatePicker = new SetDate(this, comQ5DueDate);

        SetDate shopQ1DatePicker = new SetDate(this, shopQ1DueDate);
        SetDate shopQ2DatePicker = new SetDate(this, shopQ2DueDate);

        SetDate foodQ1DatePicker = new SetDate(this, foodQ1DueDate);
        SetDate foodQ2DatePicker = new SetDate(this, foodQ2DueDate);
        SetDate foodQ3DatePicker = new SetDate(this, foodQ3DueDate);
        SetDate foodQ4DatePicker = new SetDate(this, foodQ4DueDate);

        SetDate laundryQ1DatePicker = new SetDate(this, laundryQ1DueDate);

        SetDate aboutQ1DatePicker = new SetDate(this, aboutQ1DueDate);
        SetDate aboutQ2DatePicker = new SetDate(this, aboutQ2DueDate);

        SetDate medicineQ1DatePicker = new SetDate(this, medicineQ1DueDate);

        SetDate supportQ1DatePicker = new SetDate(this, supportQ1DueDate);
        SetDate supportQ2DatePicker = new SetDate(this, supportQ2DueDate);
        SetDate supportQ3DatePicker = new SetDate(this, supportQ3DueDate);
        SetDate supportQ4DatePicker = new SetDate(this, supportQ4DueDate);

        SetDate ecoQ1DatePicker = new SetDate(this, ecoQ1DueDate);
        SetDate ecoQ2DatePicker = new SetDate(this, ecoQ2DueDate);
        SetDate ecoQ3DatePicker = new SetDate(this, ecoQ3DueDate);

        SetDate safeQ1DatePicker = new SetDate(this, safeQ1DueDate);
        SetDate safeQ2DatePicker = new SetDate(this, safeQ2DueDate);
        SetDate safeQ3DatePicker = new SetDate(this, safeQ3DueDate);
        SetDate safeQ4DatePicker = new SetDate(this, safeQ4DueDate);

        SetDate medicalQ1DatePicker = new SetDate(this, medicalQ1DueDate);
        SetDate medicalQ2DatePicker = new SetDate(this, medicalQ2DueDate);
        SetDate medicalQ3DatePicker = new SetDate(this, medicalQ3DueDate);
        SetDate medicalQ4DatePicker = new SetDate(this, medicalQ4DueDate);
        SetDate medicalQ5DatePicker = new SetDate(this, medicalQ5DueDate);
        SetDate medicalQ6DatePicker = new SetDate(this, medicalQ6DueDate);

        SetDate assessmentLocationDatePicker = new SetDate(this, assessmentLocationDueDate);
        SetDate assessedByScoreDatePicker = new SetDate(this, assessedByScoreDueDate);
        SetDate riskPositionDatePicker = new SetDate(this, riskPositionDueDate);


    }


    private void initializeView() {
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        list = new ArrayList<>();

//        totalScore = findViewById(R.id.total_score);

        locQ1ac = findViewById(R.id.location_q1_ap);
        locQ2ac = findViewById(R.id.location_q2_ap);
        locQ3ac = findViewById(R.id.location_q3_ap);
        locQ4ac = findViewById(R.id.location_q4_ap);
        locQ5ac = findViewById(R.id.location_q5_ap);
        elLocation = findViewById(R.id.expandable_layout_location);

        elHouse = findViewById(R.id.expandable_layout_house);
        houseQ1ac = findViewById(R.id.house_fac_q1_ap);
        houseQ2ac = findViewById(R.id.house_fac_q2_ap);
        houseQ3ac = findViewById(R.id.house_fac_q3_ap);
        houseQ4ac = findViewById(R.id.house_fac_q4_ap);

        elInTheHome = findViewById(R.id.expandable_layout_inthehome);
        homeQ1ac = findViewById(R.id.inthehome_q1_ap);
        homeQ2ac = findViewById(R.id.inthehome_q2_ap);
        homeQ3ac = findViewById(R.id.inthehome_q3_ap);
        homeQ4ac = findViewById(R.id.inthehome_q4_ap);
        homeQ5ac = findViewById(R.id.inthehome_q5_ap);
        homeQ6ac = findViewById(R.id.inthehome_q6_ap);
        homeQ7ac = findViewById(R.id.inthehome_q7_ap);
        homeQ8ac = findViewById(R.id.inthehome_q8_ap);

        elCommunication = findViewById(R.id.expandable_layout_communication);
        comQ1ac = findViewById(R.id.communication_q1_ap);
        comQ2ac = findViewById(R.id.communication_q2_ap);
        comQ3ac = findViewById(R.id.communication_q3_ap);
        comQ4ac = findViewById(R.id.communication_q4_ap);
        comQ5ac = findViewById(R.id.communication_q5_ap);

        elShopping = findViewById(R.id.expandable_layout_shopping);
        shopQ1ac = findViewById(R.id.shopping_q1_ap);
        shopQ2ac = findViewById(R.id.shopping_q2_ap);

        elFoodCooking = findViewById(R.id.expandable_layout_foodcooking);
        foodQ1ac = findViewById(R.id.foodcooking_q1_ap);
        foodQ2ac = findViewById(R.id.foodcooking_q2_ap);
        foodQ3ac = findViewById(R.id.foodcooking_q3_ap);
        foodQ4ac = findViewById(R.id.foodcooking_q4_ap);

        elLaundry = findViewById(R.id.expandable_layout_laundry);
        laundryQ1ac = findViewById(R.id.laundry_q1_ap);

        elGettingAbout = findViewById(R.id.expandable_layout_getingabt);
        aboutQ1ac = findViewById(R.id.getting_about_q1_ap);
        aboutQ2ac = findViewById(R.id.getting_about_q2_ap);

        elMedicine = findViewById(R.id.expandable_layout_medicine);
        medicineQ1ac = findViewById(R.id.medicine_q1_ap);

        elHomeSupport = findViewById(R.id.expandable_layout_homesupport);
        supportQ1ac = findViewById(R.id.homesupport_q1_ap);
        supportQ2ac = findViewById(R.id.homesupport_q2_ap);
        supportQ3ac = findViewById(R.id.homesupport_q3_ap);
        supportQ4ac = findViewById(R.id.homesupport_q4_ap);

        elEconomic = findViewById(R.id.expandable_layout_economic);
        ecoQ1ac = findViewById(R.id.economic_q1_ap);
        ecoQ2ac = findViewById(R.id.economic_q2_ap);
        ecoQ3ac = findViewById(R.id.economic_q3_ap);

        elSafeguarding = findViewById(R.id.expandable_layout_safeguard);
        safeQ1ac = findViewById(R.id.safeguarding_q1_ap);
        safeQ2ac = findViewById(R.id.safeguarding_q2_ap);
        safeQ3ac = findViewById(R.id.safeguarding_q3_ap);
        safeQ4ac = findViewById(R.id.safeguarding_q4_ap);

        elMedical = findViewById(R.id.expandable_layout_medical);
        medicalQ1ac = findViewById(R.id.medical_q1_ap);
        medicalQ2ac = findViewById(R.id.medical_q2_ap);
        medicalQ3ac = findViewById(R.id.medical_q3_ap);
        medicalQ4ac = findViewById(R.id.medical_q4_ap);
        medicalQ5ac = findViewById(R.id.medical_q5_ap);
        medicalQ6ac = findViewById(R.id.medical_q6_ap);

        elLoa = findViewById(R.id.expandable_layout_loa);
        loaAc = findViewById(R.id.assessmentloc_ap);
        elAccessedBy = findViewById(R.id.expandable_layout_accessedby);
        assessedByAc = findViewById(R.id.assessbyscore_ap);
        elRiskPosition = findViewById(R.id.expandable_layout_riskassociated);
        riskPositionAc = findViewById(R.id.riskposition_ap);


        locationDropdown = findViewById(R.id.location_dropdown);
        houseDropdown = findViewById(R.id.house_dropdown);
        inTheHomeDropdown = findViewById(R.id.inthehome_dropdown);
        communicationDropdown = findViewById(R.id.communication_dropdown);
        shoppingDropdown = findViewById(R.id.shopping_dropdown);
        foodCookingDropdown = findViewById(R.id.foodcooking_dropdown);
        laundryDropdown = findViewById(R.id.laundry_dropdown);
        gettingAboutDropDown = findViewById(R.id.gettingabout_dropdown);
        medicineDropdown = findViewById(R.id.medicine_dropdown);
        homeSupportDropdown = findViewById(R.id.homesupport_dropdown);
        economicDropdown = findViewById(R.id.economic_dropdown);
        safeguardingDropdown = findViewById(R.id.safeguarding_dropdown);
        medicalDropdown = findViewById(R.id.medical_dropdown);
        loaDropdown = findViewById(R.id.loa_dropdown);
        accessedByDropdown = findViewById(R.id.accessedby_dropdown);
        riskPositionDropdown = findViewById(R.id.riskassociated_dropdown);

        assessment = findViewById(R.id.assessment_number);
        locQ1 = findViewById(R.id.loc_q1);
        locQ2 = findViewById(R.id.loc_q2);
        locQ3 = findViewById(R.id.loc_q3);
        locQ4 = findViewById(R.id.loc_q4);
        locQ5 = findViewById(R.id.loc_q5);
        locQ1Spinner = findViewById(R.id.location_q1_spinner);
        locQ2Spinner = findViewById(R.id.location_q2_spinner);
        locQ3Spinner = findViewById(R.id.location_q3_spinner);
        locQ4Spinner = findViewById(R.id.location_q4_spinner);
        locQ5Spinner = findViewById(R.id.location_q5_spinner);
        locQ1ActionPlan = findViewById(R.id.location_q1_actionplan);
        locQ2ActionPlan = findViewById(R.id.location_q2_actionplan);
        locQ3ActionPlan = findViewById(R.id.location_q3_actionplan);
        locQ4ActionPlan = findViewById(R.id.location_q4_actionplan);
        locQ5ActionPlan = findViewById(R.id.location_q5_actionplan);
        locQ1DueDate = findViewById(R.id.location_q1_due_date);
        locQ2DueDate = findViewById(R.id.location_q2_due_date);
        locQ3DueDate = findViewById(R.id.location_q3_due_date);
        locQ4DueDate = findViewById(R.id.location_q4_due_date);
        locQ5DueDate = findViewById(R.id.location_q5_due_date);
        locQ1Remarks = findViewById(R.id.location_q1_remarks);
        locQ2Remarks = findViewById(R.id.location_q2_remarks);
        locQ3Remarks = findViewById(R.id.location_q3_remarks);
        locQ4Remarks = findViewById(R.id.location_q4_remarks);
        locQ5Remarks = findViewById(R.id.location_q5_remarks);

        houseQ1 = findViewById(R.id.house_fac_q1);
        houseQ2 = findViewById(R.id.house_fac_q2);
        houseq3 = findViewById(R.id.house_fac_q3);
        houseq4 = findViewById(R.id.house_fac_q4);
        houseQ1Spinner = findViewById(R.id.house_fac_q1_spinner);
        houseQ2Spinner = findViewById(R.id.house_fac_q2_spinner);
        houseQ3Spinner = findViewById(R.id.house_fac_q3_spinner);
        houseQ4Spinner = findViewById(R.id.house_fac_q4_spinner);
        houseQ1ActionPlan = findViewById(R.id.house_fac_q1_actionplan);
        houseQ2ActionPlan = findViewById(R.id.house_fac_q2_actionplan);
        houseQ3ActionPlan = findViewById(R.id.house_fac_q3_actionplan);
        houseQ4ActionPlan = findViewById(R.id.house_fac_q4_actionplan);
        houseQ1DueDate = findViewById(R.id.house_fac_q1_due_date);
        houseQ2DueDate = findViewById(R.id.house_fac_q2_due_date);
        houseQ3DueDate = findViewById(R.id.house_fac_q3_due_date);
        houseQ4DueDate = findViewById(R.id.house_fac_q4_due_date);
        houseQ1Remarks = findViewById(R.id.house_fac_q1_remarks);
        houseQ2Remarks = findViewById(R.id.house_fac_q2_remarks);
        houseQ3Remarks = findViewById(R.id.house_fac_q3_remarks);
        houseQ4Remarks = findViewById(R.id.house_fac_q4_remarks);

        homeQ1 = findViewById(R.id.inthehome_q1);
        homeQ2 = findViewById(R.id.inthehome_q2);
        homeq3 = findViewById(R.id.inthehome_q3);
        homeq4 = findViewById(R.id.inthehome_q4);
        homeq5 = findViewById(R.id.inthehome_q5);
        homeQ6 = findViewById(R.id.inthehome_q6);
        homeQ7 = findViewById(R.id.inthehome_q7);
        homeQ8 = findViewById(R.id.inthehome_q8);
        homeQ1Spinner = findViewById(R.id.inthehome_q1_spinner);
        homeQ2Spinner = findViewById(R.id.inthehome_q2_spinner);
        homeQ3Spinner = findViewById(R.id.inthehome_q3_spinner);
        homeQ4Spinner = findViewById(R.id.inthehome_q4_spinner);
        homeQ5Spinner = findViewById(R.id.inthehome_q5_spinner);
        homeQ6Spinner = findViewById(R.id.inthehome_q6_spinner);
        homeQ7Spinner = findViewById(R.id.inthehome_q7_spinner);
        homeQ8Spinner = findViewById(R.id.inthehome_q8_spinner);
        homeQ1ActionPlan = findViewById(R.id.inthehome_q1_actionplan);
        homeQ2ActionPlan = findViewById(R.id.inthehome_q2_actionplan);
        homeQ3ActionPlan = findViewById(R.id.inthehome_q3_actionplan);
        homeQ4ActionPlan = findViewById(R.id.inthehome_q4_actionplan);
        homeQ5ActionPlan = findViewById(R.id.inthehome_q5_actionplan);
        homeQ6ActionPlan = findViewById(R.id.inthehome_q6_actionplan);
        homeQ7ActionPlan = findViewById(R.id.inthehome_q7_actionplan);
        homeQ8ActionPlan = findViewById(R.id.inthehome_q8_actionplan);
        homeQ1DueDate = findViewById(R.id.inthehome_q1_due_date);
        homeQ2DueDate = findViewById(R.id.inthehome_q2_due_date);
        homeQ3DueDate = findViewById(R.id.inthehome_q3_due_date);
        homeQ4DueDate = findViewById(R.id.inthehome_q4_due_date);
        homeQ5DueDate = findViewById(R.id.inthehome_q5_due_date);
        homeQ6DueDate = findViewById(R.id.inthehome_q6_due_date);
        homeQ7DueDate = findViewById(R.id.inthehome_q7_due_date);
        homeQ8DueDate = findViewById(R.id.inthehome_q8_due_date);
        homeQ1Remarks = findViewById(R.id.inthehome_q1_remarks);
        homeQ2Remarks = findViewById(R.id.inthehome_q2_remarks);
        homeQ3Remarks = findViewById(R.id.inthehome_q3_remarks);
        homeQ4Remarks = findViewById(R.id.inthehome_q4_remarks);
        homeQ5Remarks = findViewById(R.id.inthehome_q5_remarks);
        homeQ6Remarks = findViewById(R.id.inthehome_q6_remarks);
        homeQ7Remarks = findViewById(R.id.inthehome_q7_remarks);
        homeQ8Remarks = findViewById(R.id.inthehome_q8_remarks);

        comQ1 = findViewById(R.id.communication_q1);
        comQ2 = findViewById(R.id.communication_q2);
        comQ3 = findViewById(R.id.communication_q3);
        comQ4 = findViewById(R.id.communication_q4);
        comQ5 = findViewById(R.id.communication_q5);
        comQ1Spinner = findViewById(R.id.communication_q1_spinner);
        comQ2Spinner = findViewById(R.id.communication_q2_spinner);
        comQ3Spinner = findViewById(R.id.communication_q3_spinner);
        comQ4Spinner = findViewById(R.id.communication_q4_spinner);
        comQ5Spinner = findViewById(R.id.communication_q5_spinner);
        comQ1ActionPlan = findViewById(R.id.communication_q1_actionplan);
        comQ2ActionPlan = findViewById(R.id.communication_q2_actionplan);
        comQ3ActionPlan = findViewById(R.id.communication_q3_actionplan);
        comQ4ActionPlan = findViewById(R.id.communication_q4_actionplan);
        comQ5ActionPlan = findViewById(R.id.communication_q5_actionplan);
        comQ1DueDate = findViewById(R.id.communication_q1_due_date);
        comQ2DueDate = findViewById(R.id.communication_q2_due_date);
        comQ3DueDate = findViewById(R.id.communication_q3_due_date);
        comQ4DueDate = findViewById(R.id.communication_q4_due_date);
        comQ5DueDate = findViewById(R.id.communication_q5_due_date);
        comQ1Remarks = findViewById(R.id.communication_q1_remarks);
        comQ2Remarks = findViewById(R.id.communication_q2_remarks);
        comQ3Remarks = findViewById(R.id.communication_q3_remarks);
        comQ4Remarks = findViewById(R.id.communication_q4_remarks);
        comQ5Remarks = findViewById(R.id.communication_q5_remarks);

        shopQ1 = findViewById(R.id.shopping_q1);
        shopQ2 = findViewById(R.id.shopping_q2);
        shopQ1Spinner = findViewById(R.id.shopping_q1_spinner);
        shopQ2Spinner = findViewById(R.id.shopping_q2_spinner);
        shopQ1ActionPlan = findViewById(R.id.shopping_q1_actionplan);
        shopQ2ActionPlan = findViewById(R.id.shopping_q2_actionplan);
        shopQ1DueDate = findViewById(R.id.shopping_q1_due_date);
        shopQ2DueDate = findViewById(R.id.shopping_q2_due_date);
        shopQ1Remarks = findViewById(R.id.shopping_q1_remarks);
        shopQ2Remarks = findViewById(R.id.shopping_q2_remarks);

        foodQ1 = findViewById(R.id.foodcooking_q1);
        foodQ2 = findViewById(R.id.foodcooking_q2);
        foodQ3 = findViewById(R.id.foodcooking_q3);
        foodQ4 = findViewById(R.id.foodcooking_q4);
        foodQ1Spinner = findViewById(R.id.foodcooking_q1_spinner);
        foodQ2Spinner = findViewById(R.id.foodcooking_q2_spinner);
        foodQ3Spinner = findViewById(R.id.foodcooking_q3_spinner);
        foodQ4Spinner = findViewById(R.id.foodcooking_q4_spinner);
        foodQ1ActionPlan = findViewById(R.id.foodcooking_q1_actionplan);
        foodQ2ActionPlan = findViewById(R.id.foodcooking_q2_actionplan);
        foodQ3ActionPlan = findViewById(R.id.foodcooking_q3_actionplan);
        foodQ4ActionPlan = findViewById(R.id.foodcooking_q4_actionplan);
        foodQ1DueDate = findViewById(R.id.foodcooking_q1_due_date);
        foodQ2DueDate = findViewById(R.id.foodcooking_q2_due_date);
        foodQ3DueDate = findViewById(R.id.foodcooking_q3_due_date);
        foodQ4DueDate = findViewById(R.id.foodcooking_q4_due_date);
        foodQ1Remarks = findViewById(R.id.foodcooking_q1_remarks);
        foodQ2Remarks = findViewById(R.id.foodcooking_q2_remarks);
        foodQ3Remarks = findViewById(R.id.foodcooking_q3_remarks);
        foodQ4Remarks = findViewById(R.id.foodcooking_q4_remarks);

        laundryQ1 = findViewById(R.id.laundry_q1);
        laundryQ1Spinner = findViewById(R.id.laundry_q1_spinner);
        laundryQ1ActionPlan = findViewById(R.id.laundry_q1_actionplan);
        laundryQ1DueDate = findViewById(R.id.laundry_q1_due_date);
        laundryQ1Remarks = findViewById(R.id.laundry_q1_remarks);

        aboutQ1 = findViewById(R.id.getting_about_q1);
        aboutQ2 = findViewById(R.id.getting_about_q2);
        aboutQ1Spinner = findViewById(R.id.getting_about_q1_spinner);
        aboutQ2Spinner = findViewById(R.id.getting_about_q2_spinner);
        aboutQ1ActionPlan = findViewById(R.id.getting_about_q1_actionplan);
        aboutQ2ActionPlan = findViewById(R.id.getting_about_q2_actionplan);
        aboutQ1DueDate = findViewById(R.id.getting_about_q1_due_date);
        aboutQ2DueDate = findViewById(R.id.getting_about_q2_due_date);
        aboutQ1Remarks = findViewById(R.id.getting_about_q1_remarks);
        aboutQ2Remarks = findViewById(R.id.getting_about_q2_remarks);

        medicineQ1 = findViewById(R.id.medicine_q1);
        medicineQ1Spinner = findViewById(R.id.medicine_q1_spinner);
        medicineQ1ActionPlan = findViewById(R.id.medicine_q1_actionplan);
        medicineQ1DueDate = findViewById(R.id.medicine_q1_due_date);
        medicineQ1Remarks = findViewById(R.id.medicine_q1_remarks);

        supportQ1 = findViewById(R.id.homesupport_q1);
        supportQ2 = findViewById(R.id.homesupport_q2);
        supportQ3 = findViewById(R.id.homesupport_q3);
        supportQ4 = findViewById(R.id.homesupport_q4);
        supportQ1Spinner = findViewById(R.id.homesupport_q1_spinner);
        supportQ2Spinner = findViewById(R.id.homesupport_q2_spinner);
        supportQ3Spinner = findViewById(R.id.homesupport_q3_spinner);
        supportQ4Spinner = findViewById(R.id.homesupport_q4_spinner);
        supportQ1ActionPlan = findViewById(R.id.homesupport_q1_actionplan);
        supportQ2ActionPlan = findViewById(R.id.homesupport_q2_actionplan);
        supportQ3ActionPlan = findViewById(R.id.homesupport_q3_actionplan);
        supportQ4ActionPlan = findViewById(R.id.homesupport_q4_actionplan);
        supportQ1DueDate = findViewById(R.id.homesupport_q1_due_date);
        supportQ2DueDate = findViewById(R.id.homesupport_q2_due_date);
        supportQ3DueDate = findViewById(R.id.homesupport_q3_due_date);
        supportQ4DueDate = findViewById(R.id.homesupport_q4_due_date);
        supportQ1Remarks = findViewById(R.id.homesupport_q1_remarks);
        supportQ2Remarks = findViewById(R.id.homesupport_q2_remarks);
        supportQ3Remarks = findViewById(R.id.homesupport_q3_remarks);
        supportQ4Remarks = findViewById(R.id.homesupport_q4_remarks);


        ecoQ1 = findViewById(R.id.economic_q1);
        ecoQ2 = findViewById(R.id.economic_q2);
        ecoQ3 = findViewById(R.id.economic_q3);
        ecoQ1Spinner = findViewById(R.id.economic_q1_spinner);
        ecoQ2Spinner = findViewById(R.id.economic_q2_spinner);
        ecoQ3Spinner = findViewById(R.id.economic_q3_spinner);
        ecoQ1ActionPlan = findViewById(R.id.economic_q1_actionplan);
        ecoQ2ActionPlan = findViewById(R.id.economic_q2_actionplan);
        ecoQ3ActionPlan = findViewById(R.id.economic_q3_actionplan);
        ecoQ1DueDate = findViewById(R.id.economic_q1_due_date);
        ecoQ2DueDate = findViewById(R.id.economic_q2_due_date);
        ecoQ3DueDate = findViewById(R.id.economic_q3_due_date);
        ecoQ1Remarks = findViewById(R.id.economic_q1_remarks);
        ecoQ2Remarks = findViewById(R.id.economic_q2_remarks);
        ecoQ3Remarks = findViewById(R.id.economic_q3_remarks);


        safeQ1 = findViewById(R.id.safeguarding_q1);
        safeQ2 = findViewById(R.id.safeguarding_q2);
        safeQ3 = findViewById(R.id.safeguarding_q3);
        safeQ4 = findViewById(R.id.safeguarding_q4);
        safeQ1Spinner = findViewById(R.id.safeguarding_q1_spinner);
        safeQ2Spinner = findViewById(R.id.safeguarding_q2_spinner);
        safeQ3Spinner = findViewById(R.id.safeguarding_q3_spinner);
        safeQ4Spinner = findViewById(R.id.safeguarding_q4_spinner);
        safeQ1ActionPlan = findViewById(R.id.safeguarding_q1_actionplan);
        safeQ2ActionPlan = findViewById(R.id.safeguarding_q2_actionplan);
        safeQ3ActionPlan = findViewById(R.id.safeguarding_q3_actionplan);
        safeQ4ActionPlan = findViewById(R.id.safeguarding_q4_actionplan);
        safeQ1DueDate = findViewById(R.id.safeguarding_q1_due_date);
        safeQ2DueDate = findViewById(R.id.safeguarding_q2_due_date);
        safeQ3DueDate = findViewById(R.id.safeguarding_q3_due_date);
        safeQ4DueDate = findViewById(R.id.safeguarding_q4_due_date);
        safeQ1Remarks = findViewById(R.id.safeguarding_q1_remarks);
        safeQ2Remarks = findViewById(R.id.safeguarding_q2_remarks);
        safeQ3Remarks = findViewById(R.id.safeguarding_q3_remarks);
        safeQ4Remarks = findViewById(R.id.safeguarding_q4_remarks);

        medicalQ1 = findViewById(R.id.medical_q1);
        medicalQ2 = findViewById(R.id.medical_q2);
        medicalQ3 = findViewById(R.id.medical_q3);
        medicalQ4 = findViewById(R.id.medical_q4);
        medicalQ5 = findViewById(R.id.medical_q5);
        medicalQ6 = findViewById(R.id.medical_q6);
        medicalQ1Spinner = findViewById(R.id.medical_q1_spinner);
        medicalQ2Spinner = findViewById(R.id.medical_q2_spinner);
        medicalQ3Spinner = findViewById(R.id.medical_q3_spinner);
        medicalQ4Spinner = findViewById(R.id.medical_q4_spinner);
        medicalQ5Spinner = findViewById(R.id.medical_q5_spinner);
        medicalQ6Spinner = findViewById(R.id.medical_q6_spinner);
        medicalQ1ActionPlan = findViewById(R.id.medical_q1_actionplan);
        medicalQ2ActionPlan = findViewById(R.id.medical_q2_actionplan);
        medicalQ3ActionPlan = findViewById(R.id.medical_q3_actionplan);
        medicalQ4ActionPlan = findViewById(R.id.medical_q4_actionplan);
        medicalQ5ActionPlan = findViewById(R.id.medical_q5_actionplan);
        medicalQ6ActionPlan = findViewById(R.id.medical_q6_actionplan);
        medicalQ1DueDate = findViewById(R.id.medical_q1_due_date);
        medicalQ2DueDate = findViewById(R.id.medical_q2_due_date);
        medicalQ3DueDate = findViewById(R.id.medical_q3_due_date);
        medicalQ4DueDate = findViewById(R.id.medical_q4_due_date);
        medicalQ5DueDate = findViewById(R.id.medical_q5_due_date);
        medicalQ6DueDate = findViewById(R.id.medical_q6_due_date);
        medicalQ1Remarks = findViewById(R.id.medical_q1_remarks);
        medicalQ2Remarks = findViewById(R.id.medical_q2_remarks);
        medicalQ3Remarks = findViewById(R.id.medical_q3_remarks);
        medicalQ4Remarks = findViewById(R.id.medical_q4_remarks);
        medicalQ5Remarks = findViewById(R.id.medical_q5_remarks);
        medicalQ6Remarks = findViewById(R.id.medical_q6_remarks);

        assessmentLocation = findViewById(R.id.assessmentloc_spinner);
        assessedByScore = findViewById(R.id.assessbyscore);
        riskPosition = findViewById(R.id.risk_position);

        assessmentLocationActionPlan = findViewById(R.id.assessmentloc_actionplan);
        assessmentLocationDueDate = findViewById(R.id.assessmentloc_due_date);
        assessmentLocationRemarks = findViewById(R.id.assessmentloc_remarks);

        assessedByScoreActionPlan = findViewById(R.id.assessbyscore_actionplan);
        assessedByScoreDueDate = findViewById(R.id.assessbyscore_due_date);
        assessedByScoreRemarks = findViewById(R.id.assessbyscore_remarks);

        riskPositionActionPlan = findViewById(R.id.riskposition_actionplan);
        riskPositionDueDate = findViewById(R.id.riskposition_due_date);
        riskPositionRemarks = findViewById(R.id.riskposition_remarks);

        done = findViewById(R.id.btn_done);

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
            if (!InternetConnection.checkConnection(AssessmentQuestions.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(AssessmentQuestions.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new AssessmentQuestions.ResponseCacheInterceptor())
                .addInterceptor(new AssessmentQuestions.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(AssessmentQuestions.this
                        .getCacheDir(),
                        "ApiResponses"), 1024 * 1024))
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

        call = retrofit.create(ApiInterface.class).getResponse("gws/action_plan/api/action_plan/" + criteriaId + "?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("SocialPoll", t.toString());
                Toast.makeText(AssessmentQuestions.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                 /*   if (!(response.isSuccessful())) {
                        Toast.makeText(AssessmentQuestions.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }*/
                    final String myResponse = response.body().string();

                    Log.e("getResponse:", myResponse);
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("Action Plans");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);
                        String name = innerObject.getString("action_plan");
                        list.add(name);
                    }

                    actionPlanItems = list.toArray();

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                AssessmentQuestions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_plans = new ArrayAdapter<Object>(AssessmentQuestions.this, R.layout.support_simple_spinner_dropdown_item, actionPlanItems) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        switch (criteriaId) {
                            case "1":
                                locQ1ActionPlan.setAdapter(adapt_plans);
                                locQ2ActionPlan.setAdapter(adapt_plans);
                                locQ3ActionPlan.setAdapter(adapt_plans);
                                locQ4ActionPlan.setAdapter(adapt_plans);
                                locQ5ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "2":
                                houseQ1ActionPlan.setAdapter(adapt_plans);
                                houseQ2ActionPlan.setAdapter(adapt_plans);
                                houseQ3ActionPlan.setAdapter(adapt_plans);
                                houseQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "3":
                                homeQ1ActionPlan.setAdapter(adapt_plans);
                                homeQ2ActionPlan.setAdapter(adapt_plans);
                                homeQ3ActionPlan.setAdapter(adapt_plans);
                                homeQ4ActionPlan.setAdapter(adapt_plans);
                                homeQ5ActionPlan.setAdapter(adapt_plans);
                                homeQ6ActionPlan.setAdapter(adapt_plans);
                                homeQ7ActionPlan.setAdapter(adapt_plans);
                                homeQ8ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "4":
                                comQ1ActionPlan.setAdapter(adapt_plans);
                                comQ2ActionPlan.setAdapter(adapt_plans);
                                comQ3ActionPlan.setAdapter(adapt_plans);
                                comQ4ActionPlan.setAdapter(adapt_plans);
                                comQ5ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "5":
                                shopQ1ActionPlan.setAdapter(adapt_plans);
                                shopQ2ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "6":
                                foodQ1ActionPlan.setAdapter(adapt_plans);
                                foodQ2ActionPlan.setAdapter(adapt_plans);
                                foodQ3ActionPlan.setAdapter(adapt_plans);
                                foodQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "7":
                                laundryQ1ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "8":
                                aboutQ1ActionPlan.setAdapter(adapt_plans);
                                aboutQ2ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "9":
                                medicineQ1ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "10":
                                supportQ1ActionPlan.setAdapter(adapt_plans);
                                supportQ2ActionPlan.setAdapter(adapt_plans);
                                supportQ3ActionPlan.setAdapter(adapt_plans);
                                supportQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "11":
                                ecoQ1ActionPlan.setAdapter(adapt_plans);
                                ecoQ2ActionPlan.setAdapter(adapt_plans);
                                ecoQ3ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "12":
                                safeQ1ActionPlan.setAdapter(adapt_plans);
                                safeQ2ActionPlan.setAdapter(adapt_plans);
                                safeQ3ActionPlan.setAdapter(adapt_plans);
                                safeQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "13":
                                medicalQ1ActionPlan.setAdapter(adapt_plans);
                                medicalQ2ActionPlan.setAdapter(adapt_plans);
                                medicalQ3ActionPlan.setAdapter(adapt_plans);
                                medicalQ4ActionPlan.setAdapter(adapt_plans);
                                medicalQ5ActionPlan.setAdapter(adapt_plans);
                                medicalQ6ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "14":
                                assessmentLocationActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "15":
                                assessedByScoreActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "16":
                                riskPositionActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;
                        }

                    }

                });
                AssessmentQuestions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_plans = new ArrayAdapter<Object>(AssessmentQuestions.this, R.layout.support_simple_spinner_dropdown_item, actionPlanItems) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        switch (criteriaId) {
                            case "1":
                                locQ1ActionPlan.setAdapter(adapt_plans);
                                locQ2ActionPlan.setAdapter(adapt_plans);
                                locQ3ActionPlan.setAdapter(adapt_plans);
                                locQ4ActionPlan.setAdapter(adapt_plans);
                                locQ5ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "2":
                                houseQ1ActionPlan.setAdapter(adapt_plans);
                                houseQ2ActionPlan.setAdapter(adapt_plans);
                                houseQ3ActionPlan.setAdapter(adapt_plans);
                                houseQ4ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "3":
                                homeQ1ActionPlan.setAdapter(adapt_plans);
                                homeQ2ActionPlan.setAdapter(adapt_plans);
                                homeQ3ActionPlan.setAdapter(adapt_plans);
                                homeQ4ActionPlan.setAdapter(adapt_plans);
                                homeQ5ActionPlan.setAdapter(adapt_plans);
                                homeQ6ActionPlan.setAdapter(adapt_plans);
                                homeQ7ActionPlan.setAdapter(adapt_plans);
                                homeQ8ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "4":
                                comQ1ActionPlan.setAdapter(adapt_plans);
                                comQ2ActionPlan.setAdapter(adapt_plans);
                                comQ3ActionPlan.setAdapter(adapt_plans);
                                comQ4ActionPlan.setAdapter(adapt_plans);
                                comQ5ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "5":
                                shopQ1ActionPlan.setAdapter(adapt_plans);
                                shopQ2ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "6":
                                foodQ1ActionPlan.setAdapter(adapt_plans);
                                foodQ2ActionPlan.setAdapter(adapt_plans);
                                foodQ3ActionPlan.setAdapter(adapt_plans);
                                foodQ4ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "7":
                                laundryQ1ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "8":
                                aboutQ1ActionPlan.setAdapter(adapt_plans);
                                aboutQ2ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "9":
                                medicineQ1ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "10":
                                supportQ1ActionPlan.setAdapter(adapt_plans);
                                supportQ2ActionPlan.setAdapter(adapt_plans);
                                supportQ3ActionPlan.setAdapter(adapt_plans);
                                supportQ4ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "11":
                                ecoQ1ActionPlan.setAdapter(adapt_plans);
                                ecoQ2ActionPlan.setAdapter(adapt_plans);
                                ecoQ3ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "12":
                                safeQ1ActionPlan.setAdapter(adapt_plans);
                                safeQ2ActionPlan.setAdapter(adapt_plans);
                                safeQ3ActionPlan.setAdapter(adapt_plans);
                                safeQ4ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "13":
                                medicalQ1ActionPlan.setAdapter(adapt_plans);
                                medicalQ2ActionPlan.setAdapter(adapt_plans);
                                medicalQ3ActionPlan.setAdapter(adapt_plans);
                                medicalQ4ActionPlan.setAdapter(adapt_plans);
                                medicalQ5ActionPlan.setAdapter(adapt_plans);
                                medicalQ6ActionPlan.setAdapter(adapt_plans);
                                break;

                            case "14":
                                assessmentLocationActionPlan.setAdapter(adapt_plans);
                                break;

                            case "15":
                                assessedByScoreActionPlan.setAdapter(adapt_plans);
                                break;

                            case "16":
                                riskPositionActionPlan.setAdapter(adapt_plans);
                                break;
                        }

                    }

                });

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
}
