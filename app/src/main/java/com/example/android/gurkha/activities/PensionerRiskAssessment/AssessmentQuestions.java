package com.example.android.gurkha.activities.PensionerRiskAssessment;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.application.GurkhaApplication;
import com.example.android.gurkha.utils.SetDate;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class AssessmentQuestions extends AppCompatActivity implements ResponseListener {

    public static String url = "http://gws.pagodalabs.com.np/pensioner_risk_assessment/api/pensioner_risk_assessment";
    private static String base_url = "http://gws.pagodalabs.com.np/";

    String token;
    TextView totalScore;
    TextView housingQ1ac, housingQ2ac, housingQ3ac, housingQ4ac, housingQ5ac, housingQ6ac, housingQ7ac, housingQ8ac, housingQ9ac,
            housingQ10ac, housingQ11ac, housingQ12ac;
//    TextView houseQ1ac, houseQ2ac, houseQ3ac, houseQ4ac;

    TextView socialQ1ac, socialQ2ac, socialQ3ac, socialQ4ac, socialQ5ac, socialQ6ac, socialQ7ac, socialQ8ac;

    TextView healthQ1ac, healthQ2ac, healthQ3ac, healthQ4ac, healthQ5ac, healthQ6ac, healthQ7ac, healthQ8ac,
            healthQ9ac, healthQ10ac, healthQ11ac, healthQ12ac, healthQ13ac, healthQ14ac;
    Toolbar toolbar;
    TextView mentalQ1ac, mentalQ2ac;
    TextView meaningQ1ac, meaningQ2ac, meaningQ3ac, meaningQ4ac;
    //    TextView laundryQ1ac;
//    TextView aboutQ1ac, aboutQ2ac;
//    TextView medicineQ1ac;
    TextView dailyQ1ac, dailyQ2ac, dailyQ3ac;
    //    TextView ecoQ1ac, ecoQ2ac, ecoQ3ac;
    TextView practicalQ1ac, practicalQ2ac, practicalQ3ac, practicalQ4ac, practicalQ5ac, practicalQ6ac, practicalQ7ac;

    TextView financesQ1ac, financesQ2ac, financesQ3ac, financesQ4ac, financesQ5ac, financesQ6ac;

    //    TextView loaAc, assessedByAc, riskPositionAc;
    private ArrayList<Object> list;
    Object[] actionPlanItems;
    JobManager mJobManager;
    JSONArray[] houseArray;

    /* Spinner assessmentLocation, assessedByScore, riskPosition;
     Spinner assessmentLocationActionPlan, assessedByScoreActionPlan, riskPositionActionPlan;
     EditText assessmentLocationDueDate, assessedByScoreDueDate, riskPositionDueDate;
     EditText assessmentLocationRemarks, assessedByScoreRemarks, riskPositionRemarks;*/
//    ExpandableLayout elhousing, elHouse, elInTheHome, elCommunication, elShopping, elFoodCooking, elLaundry, elGettingAbout, elMedicine, elHomeSupport, elEconomic, elSafeguarding, elMedical, elLoa, elAccessedBy, elRiskPosition;
    ExpandableLayout elHousing, elFinances, elSocial, elMental, elMeaning, elHealth, elPractical, elDaily;

    Button done;
    //    Button locationDropdown, houseDropdown, inTheHomeDropdown, communicationDropdown, shoppingDropdown, foodCookingDropdown, laundryDropdown, gettingAboutDropDown, medicineDropdown, homeSupportDropdown, economicDropdown, safeguardingDropdown, medicalDropdown, loaDropdown, accessedByDropdown, riskPositionDropdown;
    Button housingDropdown, financesDropdown, socialDropdown, mentalDropdown, meaningDropdown, healthDropdown, practicalDropdown, dailyDropdown;

    Spinner housingQ1ActionPlan, housingQ2ActionPlan, housingQ3ActionPlan, housingQ4ActionPlan, housingQ5ActionPlan,
            housingQ6ActionPlan, housingQ7ActionPlan, housingQ8ActionPlan, housingQ9ActionPlan, housingQ10ActionPlan, housingQ11ActionPlan, housingQ12ActionPlan,
            financesQ1ActionPlan, financesQ2ActionPlan, financesQ3ActionPlan, financesQ4ActionPlan, financesQ5ActionPlan, financesQ6ActionPlan,
            socialQ1ActionPlan, socialQ2ActionPlan, socialQ3ActionPlan, socialQ4ActionPlan, socialQ5ActionPlan, socialQ6ActionPlan,
            socialQ7ActionPlan, socialQ8ActionPlan, mentalQ1ActionPlan, mentalQ2ActionPlan, meaningQ1ActionPlan, meaningQ2ActionPlan,
            meaningQ3ActionPlan, meaningQ4ActionPlan, healthQ1ActionPlan, healthQ2ActionPlan, healthQ3ActionPlan, healthQ4ActionPlan,
            healthQ5ActionPlan, healthQ6ActionPlan, healthQ7ActionPlan, healthQ8ActionPlan, healthQ9ActionPlan, healthQ10ActionPlan,
            healthQ11ActionPlan, healthQ12ActionPlan, healthQ13ActionPlan, healthQ14ActionPlan, practicalQ1ActionPlan, practicalQ2ActionPlan,
            practicalQ3ActionPlan, practicalQ4ActionPlan, practicalQ5ActionPlan, practicalQ6ActionPlan, practicalQ7ActionPlan, dailyQ1ActionPlan,
            dailyQ2ActionPlan, dailyQ3ActionPlan;

    TextView assessment, housingQ1, housingQ2, housingQ3, housingQ4, housingQ5, housingQ6, housingQ7, housingQ8, housingQ9,
            housingQ10, housingQ11, housingQ12;
    Spinner housingQ1Spinner, housingQ2Spinner, housingQ3Spinner, housingQ4Spinner, housingQ5Spinner,
            housingQ6Spinner, housingQ7Spinner, housingQ8Spinner, housingQ9Spinner, housingQ10Spinner, housingQ11Spinner, housingQ12Spinner;
    EditText housingQ1DueDate, housingQ1Remarks, housingQ2DueDate, housingQ2Remarks,
            housingQ3DueDate, housingQ3Remarks, housingQ4DueDate, housingQ4Remarks, housingQ5DueDate, housingQ5Remarks,
            housingQ6DueDate, housingQ7DueDate, housingQ8DueDate, housingQ9DueDate, housingQ10DueDate, housingQ11DueDate,
            housingQ12DueDate, housingQ6Remarks, housingQ7Remarks, housingQ8Remarks, housingQ9Remarks, housingQ10Remarks,
            housingQ11Remarks, housingQ12Remarks;

    TextView houseQ1, houseQ2, houseq3, houseq4;
    Spinner houseQ1Spinner, houseQ2Spinner, houseQ3Spinner, houseQ4Spinner;
    EditText houseQ1DueDate, houseQ1Remarks, houseQ2DueDate, houseQ2Remarks, houseQ3DueDate, houseQ3Remarks, houseQ4DueDate, houseQ4Remarks;

    TextView socialQ1, socialQ2, socialq3, socialq4, socialq5, socialQ6, socialQ7, socialQ8;
    Spinner socialQ1Spinner, socialQ2Spinner, socialQ3Spinner, socialQ4Spinner, socialQ5Spinner, socialQ6Spinner, socialQ7Spinner, socialQ8Spinner;
    EditText socialQ1DueDate, socialQ1Remarks, socialQ2DueDate, socialQ2Remarks, socialQ3DueDate, socialQ3Remarks, socialQ4DueDate, socialQ4Remarks, socialQ5DueDate, socialQ5Remarks, socialQ6DueDate, socialQ6Remarks, socialQ7DueDate, socialQ7Remarks, socialQ8DueDate, socialQ8Remarks;

    TextView healthQ1, healthQ2, healthQ3, healthQ4, healthQ5, healthQ6, healthQ7, healthQ8, healthQ9, healthQ10,
            healthQ11, healthQ12, healthQ13, healthQ14;
    Spinner healthQ1Spinner, healthQ2Spinner, healthQ3Spinner, healthQ4Spinner, healthQ5Spinner, healthQ6Spinner,
            healthQ7Spinner, healthQ8Spinner, healthQ9Spinner, healthQ10Spinner, healthQ11Spinner, healthQ12Spinner,
            healthQ13Spinner, healthQ14Spinner;
    EditText healthQ1DueDate, healthQ1Remarks, healthQ2DueDate, healthQ2Remarks,
            healthQ3DueDate, healthQ3Remarks, healthQ4DueDate, healthQ4Remarks, healthQ5DueDate, healthQ5Remarks,
            healthQ6DueDate, healthQ7DueDate, healthQ8DueDate, healthQ9DueDate, healthQ10DueDate, healthQ11DueDate,
            healthQ12DueDate, healthQ13DueDate, healthQ14DueDate, healthQ6Remarks, healthQ7Remarks, healthQ8Remarks,
            healthQ9Remarks, healthQ10Remarks, healthQ11Remarks, healthQ12Remarks, healthQ13Remarks, healthQ14Remarks;

    TextView mentalQ1, mentalQ2;
    Spinner mentalQ1Spinner, mentalQ2Spinner;
    EditText mentalQ1DueDate, mentalQ1Remarks, mentalQ2DueDate, mentalQ2Remarks;

    TextView practicalQ1, practicalQ2, practicalQ3, practicalQ4, practicalQ5, practicalQ6, practicalQ7;
    Spinner practicalQ1Spinner, practicalQ2Spinner, practicalQ3Spinner, practicalQ4Spinner, practicalQ5Spinner,
            practicalQ6Spinner, practicalQ7Spinner;
    EditText practicalQ1DueDate, practicalQ1Remarks, practicalQ2DueDate, practicalQ2Remarks, practicalQ3DueDate, practicalQ3Remarks, practicalQ4DueDate, practicalQ4Remarks,
            practicalQ5DueDate, practicalQ6DueDate, practicalQ7DueDate, practicalQ5Remarks, practicalQ6Remarks, practicalQ7Remarks;

    TextView laundryQ1;
    Spinner laundryQ1Spinner;
    EditText laundryQ1DueDate, laundryQ1Remarks;

    TextView aboutQ1, aboutQ2;
    Spinner aboutQ1Spinner, aboutQ2Spinner;
    EditText aboutQ1DueDate, aboutQ1Remarks, aboutQ2DueDate, aboutQ2Remarks;

    TextView medicineQ1;
    Spinner medicineQ1Spinner;
    EditText medicineQ1DueDate, medicineQ1Remarks;

    TextView meaningQ1, meaningQ2, meaningQ3, meaningQ4;
    Spinner meaningQ1Spinner, meaningQ2Spinner, meaningQ3Spinner, meaningQ4Spinner;
    EditText meaningQ1DueDate, meaningQ1Remarks, meaningQ2DueDate, meaningQ2Remarks, meaningQ3DueDate, meaningQ3Remarks, meaningQ4DueDate, meaningQ4Remarks;

    TextView dailyQ1, dailyQ2, dailyQ3;
    Spinner dailyQ1Spinner, dailyQ2Spinner, dailyQ3Spinner;
    EditText dailyQ1DueDate, dailyQ1Remarks, dailyQ2DueDate, dailyQ2Remarks, dailyQ3DueDate, dailyQ3Remarks;

    TextView safeQ1, safeQ2, safeQ3, safeQ4;
    Spinner safeQ1Spinner, safeQ2Spinner, safeQ3Spinner, safeQ4Spinner;
    EditText safeQ1DueDate, safeQ1Remarks, safeQ2DueDate, safeQ2Remarks, safeQ3DueDate, safeQ3Remarks, safeQ4DueDate, safeQ4Remarks;

    TextView financesQ1, financesQ2, financesQ3, financesQ4, financesQ5, financesQ6;
    Spinner financesQ1Spinner, financesQ2Spinner, financesQ3Spinner, financesQ4Spinner, financesQ5Spinner, financesQ6Spinner;
    EditText financesQ1DueDate, financesQ1Remarks, financesQ2DueDate, financesQ2Remarks, financesQ3DueDate, financesQ3Remarks, financesQ4DueDate, financesQ4Remarks, financesQ5DueDate, financesQ5Remarks, financesQ6DueDate, financesQ6Remarks;

    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    ProgressDialog progressDialog;
    Typeface face;
    Call<ResponseBody> call;
    String criteriaId;

    String[] spinner_items_actionplan_housing = new String[]{"Supply other specialist medical equipment"
            , "Supply a stove", "Free Text", "Pension assessment", "Specialist medical assessment (OT, Hearing, etc)", "CML review"
            , "Other Dr review"};

    String[] spinner_items_actionplan_finances = new String[]{"Build a toilet", "Build a house", "Install a tap", "No further action"};
    String[] spinner_items_actionplan_social = new String[]{"Install a tap", "Other construction required", "Supply a stove"};
    String[] spinner_items_actionplan_mental = new String[]{"No further action"};
    String[] spinner_items_actionplan_meaning = new String[]{"Free text"};
    String[] spinner_items_actionplan_health = new String[]{"Supply other specialist medical equipment", "Supply a stove"};
    String[] spinner_items_actionplan_practical = new String[]{"No further action"};
    String[] spinner_items_actionplan_daily = new String[]{"Other construction required", "No further action"};

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


        ArrayAdapter<String> adapt_items_actionplan = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_housing) {
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

        housingQ1Spinner.setAdapter(adapt_items);
        housingQ2Spinner.setAdapter(adapt_items);
        housingQ3Spinner.setAdapter(adapt_items);
        housingQ4Spinner.setAdapter(adapt_items);
        housingQ5Spinner.setAdapter(adapt_items);
        housingQ6Spinner.setAdapter(adapt_items);
        housingQ7Spinner.setAdapter(adapt_items);
        housingQ8Spinner.setAdapter(adapt_items);
        housingQ9Spinner.setAdapter(adapt_items);
        housingQ10Spinner.setAdapter(adapt_items);
        housingQ11Spinner.setAdapter(adapt_items);
        housingQ12Spinner.setAdapter(adapt_items);


   /*     houseQ1ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ2ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ3ActionPlan.setAdapter(adapt_items_actionplan);
        houseQ4ActionPlan.setAdapter(adapt_items_actionplan);*/

        meaningQ1Spinner.setAdapter(adapt_items);
        meaningQ2Spinner.setAdapter(adapt_items);
        meaningQ3Spinner.setAdapter(adapt_items);
        meaningQ4Spinner.setAdapter(adapt_items);

        socialQ1ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ2ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ3ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ4ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ5ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ6ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ7ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ8ActionPlan.setAdapter(adapt_items_actionplan);
        socialQ1Spinner.setAdapter(adapt_items);
        socialQ2Spinner.setAdapter(adapt_items);
        socialQ3Spinner.setAdapter(adapt_items);
        socialQ4Spinner.setAdapter(adapt_items);
        socialQ5Spinner.setAdapter(adapt_items);
        socialQ6Spinner.setAdapter(adapt_items);
        socialQ7Spinner.setAdapter(adapt_items);
        socialQ8Spinner.setAdapter(adapt_items);

        healthQ1ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ2ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ3ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ4ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ5ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ6ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ7ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ8ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ9ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ10ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ11ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ12ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ13ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ14ActionPlan.setAdapter(adapt_items_actionplan);
        healthQ1Spinner.setAdapter(adapt_items);
        healthQ2Spinner.setAdapter(adapt_items);
        healthQ3Spinner.setAdapter(adapt_items);
        healthQ4Spinner.setAdapter(adapt_items);
        healthQ5Spinner.setAdapter(adapt_items);
        healthQ5Spinner.setAdapter(adapt_items);
        healthQ6Spinner.setAdapter(adapt_items);
        healthQ7Spinner.setAdapter(adapt_items);
        healthQ8Spinner.setAdapter(adapt_items);
        healthQ9Spinner.setAdapter(adapt_items);
        healthQ10Spinner.setAdapter(adapt_items);
        healthQ11Spinner.setAdapter(adapt_items);
        healthQ12Spinner.setAdapter(adapt_items);
        healthQ13Spinner.setAdapter(adapt_items);
        healthQ14Spinner.setAdapter(adapt_items);

        mentalQ1ActionPlan.setAdapter(adapt_items_actionplan);
        mentalQ2ActionPlan.setAdapter(adapt_items_actionplan);
        mentalQ1Spinner.setAdapter(adapt_items);
        mentalQ2Spinner.setAdapter(adapt_items);

        practicalQ1ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ2ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ3ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ4ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ5ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ6ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ7ActionPlan.setAdapter(adapt_items_actionplan);
        practicalQ1Spinner.setAdapter(adapt_items);
        practicalQ2Spinner.setAdapter(adapt_items);
        practicalQ3Spinner.setAdapter(adapt_items);
        practicalQ4Spinner.setAdapter(adapt_items);
        practicalQ5Spinner.setAdapter(adapt_items);
        practicalQ6Spinner.setAdapter(adapt_items);
        practicalQ7Spinner.setAdapter(adapt_items);

/*
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
*/

        dailyQ1ActionPlan.setAdapter(adapt_items_actionplan);
        dailyQ2ActionPlan.setAdapter(adapt_items_actionplan);
        dailyQ3ActionPlan.setAdapter(adapt_items_actionplan);
        dailyQ1Spinner.setAdapter(adapt_items);
        dailyQ2Spinner.setAdapter(adapt_items);
        dailyQ3Spinner.setAdapter(adapt_items);

    /*    safeQ1ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ2ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ3ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ4ActionPlan.setAdapter(adapt_items_actionplan);
        safeQ1Spinner.setAdapter(adapt_items);
        safeQ2Spinner.setAdapter(adapt_items);
        safeQ3Spinner.setAdapter(adapt_items);
        safeQ4Spinner.setAdapter(adapt_items);*/

        financesQ1ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ2ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ3ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ4ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ5ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ6ActionPlan.setAdapter(adapt_items_actionplan);
        financesQ1Spinner.setAdapter(adapt_items);
        financesQ2Spinner.setAdapter(adapt_items);
        financesQ3Spinner.setAdapter(adapt_items);
        financesQ4Spinner.setAdapter(adapt_items);
        financesQ5Spinner.setAdapter(adapt_items);
        financesQ6Spinner.setAdapter(adapt_items);

      /*  assessmentLocation.setAdapter(adapt_assessmentloc_items);
        assessedByScore.setAdapter(adapt_assessscore_items);
        riskPosition.setAdapter(adapt_risk_items);
        assessmentLocationActionPlan.setAdapter(adapt_items_actionplan);
        assessedByScoreActionPlan.setAdapter(adapt_items_actionplan);
        riskPositionActionPlan.setAdapter(adapt_items_actionplan);*/


        housingQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ1Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ1ActionPlan.setVisibility(View.VISIBLE);
                    housingQ1ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ1ActionPlan.setVisibility(View.GONE);
                    housingQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ2Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ2ActionPlan.setVisibility(View.VISIBLE);
                    housingQ2ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ2ActionPlan.setVisibility(View.GONE);
                    housingQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        housingQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ3Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ3ActionPlan.setVisibility(View.VISIBLE);
                    housingQ3ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ3ActionPlan.setVisibility(View.GONE);
                    housingQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ4Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ4ActionPlan.setVisibility(View.VISIBLE);
                    housingQ4ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ4ActionPlan.setVisibility(View.GONE);
                    housingQ4ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ5Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ5ActionPlan.setVisibility(View.VISIBLE);
                    housingQ5ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ5ActionPlan.setVisibility(View.GONE);
                    housingQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ6Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ6ActionPlan.setVisibility(View.VISIBLE);
                    housingQ6ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ6ActionPlan.setVisibility(View.GONE);
                    housingQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ7Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ7Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ7ActionPlan.setVisibility(View.VISIBLE);
                    housingQ7ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ7ActionPlan.setVisibility(View.GONE);
                    housingQ7ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ8Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ8Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ8ActionPlan.setVisibility(View.VISIBLE);
                    housingQ8ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ8ActionPlan.setVisibility(View.GONE);
                    housingQ8ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ9Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ9Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ9ActionPlan.setVisibility(View.VISIBLE);
                    housingQ9ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ9ActionPlan.setVisibility(View.GONE);
                    housingQ9ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ10Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ10Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ10ActionPlan.setVisibility(View.VISIBLE);
                    housingQ10ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ10ActionPlan.setVisibility(View.GONE);
                    housingQ10ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ11Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ11Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ11ActionPlan.setVisibility(View.VISIBLE);
                    housingQ11ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ11ActionPlan.setVisibility(View.GONE);
                    housingQ11ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        housingQ12Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!housingQ12Spinner.getSelectedItem().toString().equals("0")) {
                    housingQ12ActionPlan.setVisibility(View.VISIBLE);
                    housingQ12ac.setVisibility(View.VISIBLE);
                } else {
                    housingQ12ActionPlan.setVisibility(View.GONE);
                    housingQ12ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        meaningQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!meaningQ1Spinner.getSelectedItem().toString().equals("0")) {
                    meaningQ1ActionPlan.setVisibility(View.VISIBLE);
                    meaningQ1ac.setVisibility(View.VISIBLE);
                } else {
                    meaningQ1ActionPlan.setVisibility(View.GONE);
                    meaningQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        meaningQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!meaningQ2Spinner.getSelectedItem().toString().equals("0")) {
                    meaningQ2ActionPlan.setVisibility(View.VISIBLE);
                    meaningQ2ac.setVisibility(View.VISIBLE);
                } else {
                    meaningQ2ActionPlan.setVisibility(View.GONE);
                    meaningQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        meaningQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!meaningQ3Spinner.getSelectedItem().toString().equals("0")) {
                    meaningQ3ActionPlan.setVisibility(View.VISIBLE);
                    meaningQ3ac.setVisibility(View.VISIBLE);
                } else {
                    meaningQ3ActionPlan.setVisibility(View.GONE);
                    meaningQ3ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        meaningQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!meaningQ4Spinner.getSelectedItem().toString().equals("0")) {
                    meaningQ4ActionPlan.setVisibility(View.VISIBLE);
                    meaningQ4ac.setVisibility(View.VISIBLE);
                } else {
                    meaningQ4ActionPlan.setVisibility(View.GONE);
                    meaningQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ1Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ1ActionPlan.setVisibility(View.VISIBLE);
                    socialQ1ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ1ActionPlan.setVisibility(View.GONE);
                    socialQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ2Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ2ActionPlan.setVisibility(View.VISIBLE);
                    socialQ2ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ2ActionPlan.setVisibility(View.GONE);
                    socialQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ3Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ3ActionPlan.setVisibility(View.VISIBLE);
                    socialQ3ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ3ActionPlan.setVisibility(View.GONE);
                    socialQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ4Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ4ActionPlan.setVisibility(View.VISIBLE);
                    socialQ4ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ4ActionPlan.setVisibility(View.GONE);
                    socialQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ5Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ5ActionPlan.setVisibility(View.VISIBLE);
                    socialQ5ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ5ActionPlan.setVisibility(View.GONE);
                    socialQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ6Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ6ActionPlan.setVisibility(View.VISIBLE);
                    socialQ6ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ6ActionPlan.setVisibility(View.GONE);
                    socialQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ7Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ7Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ7ActionPlan.setVisibility(View.VISIBLE);
                    socialQ7ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ7ActionPlan.setVisibility(View.GONE);
                    socialQ7ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        socialQ8Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!socialQ8Spinner.getSelectedItem().toString().equals("0")) {
                    socialQ8ActionPlan.setVisibility(View.VISIBLE);
                    socialQ8ac.setVisibility(View.VISIBLE);
                } else {
                    socialQ8ActionPlan.setVisibility(View.GONE);
                    socialQ8ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ1Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ1ActionPlan.setVisibility(View.VISIBLE);
                    healthQ1ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ1ActionPlan.setVisibility(View.GONE);
                    healthQ1ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ2Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ2ActionPlan.setVisibility(View.VISIBLE);
                    healthQ2ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ2ActionPlan.setVisibility(View.GONE);
                    healthQ2ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ3Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ3ActionPlan.setVisibility(View.VISIBLE);
                    healthQ3ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ3ActionPlan.setVisibility(View.GONE);
                    healthQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ4Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ4ActionPlan.setVisibility(View.VISIBLE);
                    healthQ4ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ4ActionPlan.setVisibility(View.GONE);
                    healthQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ5Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ5ActionPlan.setVisibility(View.VISIBLE);
                    healthQ5ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ5ActionPlan.setVisibility(View.GONE);
                    healthQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ6Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ6ActionPlan.setVisibility(View.VISIBLE);
                    healthQ6ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ6ActionPlan.setVisibility(View.GONE);
                    healthQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ7Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ7Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ7ActionPlan.setVisibility(View.VISIBLE);
                    healthQ7ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ7ActionPlan.setVisibility(View.GONE);
                    healthQ7ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ8Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ8Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ8ActionPlan.setVisibility(View.VISIBLE);
                    healthQ8ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ8ActionPlan.setVisibility(View.GONE);
                    healthQ8ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ9Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ9Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ9ActionPlan.setVisibility(View.VISIBLE);
                    healthQ9ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ9ActionPlan.setVisibility(View.GONE);
                    healthQ9ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ10Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ10Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ10ActionPlan.setVisibility(View.VISIBLE);
                    healthQ10ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ10ActionPlan.setVisibility(View.GONE);
                    healthQ10ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ11Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ11Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ11ActionPlan.setVisibility(View.VISIBLE);
                    healthQ11ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ11ActionPlan.setVisibility(View.GONE);
                    healthQ11ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ12Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ12Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ12ActionPlan.setVisibility(View.VISIBLE);
                    healthQ12ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ12ActionPlan.setVisibility(View.GONE);
                    healthQ12ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ13Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ13Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ13ActionPlan.setVisibility(View.VISIBLE);
                    healthQ13ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ13ActionPlan.setVisibility(View.GONE);
                    healthQ13ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        healthQ14Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!healthQ14Spinner.getSelectedItem().toString().equals("0")) {
                    healthQ14ActionPlan.setVisibility(View.VISIBLE);
                    healthQ14ac.setVisibility(View.VISIBLE);
                } else {
                    healthQ14ActionPlan.setVisibility(View.GONE);
                    healthQ14ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mentalQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mentalQ1Spinner.getSelectedItem().toString().equals("0")) {
                    mentalQ1ActionPlan.setVisibility(View.VISIBLE);
                    mentalQ1ac.setVisibility(View.VISIBLE);
                } else {
                    mentalQ1ActionPlan.setVisibility(View.GONE);
                    mentalQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mentalQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mentalQ2Spinner.getSelectedItem().toString().equals("0")) {
                    mentalQ2ActionPlan.setVisibility(View.VISIBLE);
                    mentalQ2ac.setVisibility(View.VISIBLE);
                } else {
                    mentalQ2ActionPlan.setVisibility(View.GONE);
                    mentalQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ1Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ1ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ1ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ1ActionPlan.setVisibility(View.GONE);
                    practicalQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ2Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ2ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ2ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ2ActionPlan.setVisibility(View.GONE);
                    practicalQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ3Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ3ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ3ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ3ActionPlan.setVisibility(View.GONE);
                    practicalQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ4Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ4ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ4ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ4ActionPlan.setVisibility(View.GONE);
                    practicalQ4ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ5Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ5ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ5ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ5ActionPlan.setVisibility(View.GONE);
                    practicalQ5ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ6Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ6ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ6ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ6ActionPlan.setVisibility(View.GONE);
                    practicalQ6ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        practicalQ7Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!practicalQ7Spinner.getSelectedItem().toString().equals("0")) {
                    practicalQ7ActionPlan.setVisibility(View.VISIBLE);
                    practicalQ7ac.setVisibility(View.VISIBLE);
                } else {
                    practicalQ7ActionPlan.setVisibility(View.GONE);
                    practicalQ7ac.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

/*
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
        });*/

        dailyQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!dailyQ1Spinner.getSelectedItem().toString().equals("0")) {
                    dailyQ1ActionPlan.setVisibility(View.VISIBLE);
                    dailyQ1ac.setVisibility(View.VISIBLE);
                } else {
                    dailyQ1ActionPlan.setVisibility(View.GONE);
                    dailyQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dailyQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!dailyQ2Spinner.getSelectedItem().toString().equals("0")) {
                    dailyQ2ActionPlan.setVisibility(View.VISIBLE);
                    dailyQ2ac.setVisibility(View.VISIBLE);
                } else {
                    dailyQ2ActionPlan.setVisibility(View.GONE);
                    dailyQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dailyQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!dailyQ3Spinner.getSelectedItem().toString().equals("0")) {
                    dailyQ3ActionPlan.setVisibility(View.VISIBLE);
                    dailyQ3ac.setVisibility(View.VISIBLE);
                } else {
                    dailyQ3ActionPlan.setVisibility(View.GONE);
                    dailyQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
      /*  medicineQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/


        financesQ1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ1Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ1ActionPlan.setVisibility(View.VISIBLE);
                    financesQ1ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ1ActionPlan.setVisibility(View.GONE);
                    financesQ1ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        financesQ2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ2Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ2ActionPlan.setVisibility(View.VISIBLE);
                    financesQ2ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ2ActionPlan.setVisibility(View.GONE);
                    financesQ2ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        financesQ3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ3Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ3ActionPlan.setVisibility(View.VISIBLE);
                    financesQ3ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ3ActionPlan.setVisibility(View.GONE);
                    financesQ3ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        financesQ4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ4Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ4ActionPlan.setVisibility(View.VISIBLE);
                    financesQ4ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ4ActionPlan.setVisibility(View.GONE);
                    financesQ4ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        financesQ5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ5Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ5ActionPlan.setVisibility(View.VISIBLE);
                    financesQ5ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ5ActionPlan.setVisibility(View.GONE);
                    financesQ5ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        financesQ6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!financesQ6Spinner.getSelectedItem().toString().equals("0")) {
                    financesQ6ActionPlan.setVisibility(View.VISIBLE);
                    financesQ6ac.setVisibility(View.VISIBLE);
                } else {
                    financesQ6ActionPlan.setVisibility(View.GONE);
                    financesQ6ac.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* assessmentLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/


        housingDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elHousing.isExpanded()) {
                    elHousing.collapse();
                } else {
                    criteriaId = "1";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elHousing.expand();
                }
            }
        });

        financesDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elFinances.isExpanded()) {
                    elFinances.collapse();
                } else {
                    criteriaId = "2";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elFinances.expand();
                }
            }
        });

        socialDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elSocial.isExpanded()) {
                    elSocial.collapse();
                } else {
                    criteriaId = "3";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elSocial.expand();
                }
            }
        });

        mentalDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elMental.isExpanded()) {
                    elMental.collapse();
                } else {
                    criteriaId = "4";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elMental.expand();
                }
            }
        });

        meaningDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elMeaning.isExpanded()) {
                    elMeaning.collapse();
                } else {
                    criteriaId = "5";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elMeaning.expand();
                }
            }
        });

        healthDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elHealth.isExpanded()) {
                    elHealth.collapse();
                } else {
                    criteriaId = "6";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elHealth.expand();
                }
            }
        });

        practicalDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elPractical.isExpanded()) {
                    elPractical.collapse();
                } else {
                    criteriaId = "7";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elPractical.expand();
                }
            }
        });

        dailyDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elDaily.isExpanded()) {
                    elDaily.collapse();
                } else {
                    criteriaId = "8";
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elDaily.expand();
                }
            }
        });

      /*  medicineDropdown.setOnClickListener(new View.OnClickListener() {
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
*/

        mJobManager = GurkhaApplication.getInstance().getJobManager();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                String mPersonalId = getIntent().getStringExtra("personal_id");

                String mHousingQ1Value = housingQ1Spinner.getSelectedItem().toString();
                String mHousingQ2Value = housingQ2Spinner.getSelectedItem().toString();
                String mHousingQ3Value = housingQ3Spinner.getSelectedItem().toString();
                String mHousingQ4Value = housingQ4Spinner.getSelectedItem().toString();
                String mHousingQ5Value = housingQ5Spinner.getSelectedItem().toString();
                String mHousingQ6Value = housingQ6Spinner.getSelectedItem().toString();
                String mHousingQ7Value = housingQ7Spinner.getSelectedItem().toString();
                String mHousingQ8Value = housingQ8Spinner.getSelectedItem().toString();
                String mHousingQ9Value = housingQ9Spinner.getSelectedItem().toString();
                String mHousingQ10Value = housingQ10Spinner.getSelectedItem().toString();
                String mHousingQ11Value = housingQ11Spinner.getSelectedItem().toString();
                String mHousingQ12Value = housingQ12Spinner.getSelectedItem().toString();
                String mHousingQ1ActionPlan = housingQ1ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ2ActionPlan = housingQ2ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ3ActionPlan = housingQ3ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ4ActionPlan = housingQ4ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ5ActionPlan = housingQ5ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ6ActionPlan = housingQ6ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ7ActionPlan = housingQ7ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ8ActionPlan = housingQ8ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ9ActionPlan = housingQ9ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ10ActionPlan = housingQ10ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ11ActionPlan = housingQ11ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ12ActionPlan = housingQ12ActionPlan.getSelectedItem().toString().trim();
                String mHousingQ1DueDate = housingQ1DueDate.getText().toString().trim();
                String mHousingQ2DueDate = housingQ2DueDate.getText().toString().trim();
                String mHousingQ3DueDate = housingQ3DueDate.getText().toString().trim();
                String mHousingQ4DueDate = housingQ4DueDate.getText().toString().trim();
                String mHousingQ5DueDate = housingQ5DueDate.getText().toString().trim();
                String mHousingQ6DueDate = housingQ6DueDate.getText().toString().trim();
                String mHousingQ7DueDate = housingQ7DueDate.getText().toString().trim();
                String mHousingQ8DueDate = housingQ8DueDate.getText().toString().trim();
                String mHousingQ9DueDate = housingQ9DueDate.getText().toString().trim();
                String mHousingQ10DueDate = housingQ10DueDate.getText().toString().trim();
                String mHousingQ11DueDate = housingQ11DueDate.getText().toString().trim();
                String mHousingQ12DueDate = housingQ12DueDate.getText().toString().trim();
                String mHousingQ1Remarks = housingQ1Remarks.getText().toString().trim();
                String mHousingQ2Remarks = housingQ2Remarks.getText().toString().trim();
                String mHousingQ3Remarks = housingQ3Remarks.getText().toString().trim();
                String mHousingQ4Remarks = housingQ4Remarks.getText().toString().trim();
                String mHousingQ5Remarks = housingQ5Remarks.getText().toString().trim();
                String mHousingQ6Remarks = housingQ6Remarks.getText().toString().trim();
                String mHousingQ7Remarks = housingQ7Remarks.getText().toString().trim();
                String mHousingQ8Remarks = housingQ8Remarks.getText().toString().trim();
                String mHousingQ9Remarks = housingQ9Remarks.getText().toString().trim();
                String mHousingQ10Remarks = housingQ10Remarks.getText().toString().trim();
                String mHousingQ11Remarks = housingQ11Remarks.getText().toString().trim();
                String mHousingQ12Remarks = housingQ12Remarks.getText().toString().trim();

                String mMeaningQ1Value = meaningQ1Spinner.getSelectedItem().toString();
                String mMeaningQ2Value = meaningQ2Spinner.getSelectedItem().toString();
                String mMeaningQ3Value = meaningQ3Spinner.getSelectedItem().toString();
                String mMeaningQ4Value = meaningQ4Spinner.getSelectedItem().toString();
                String mMeaningQ1ActionPlan = meaningQ1ActionPlan.getSelectedItem().toString().trim();
                String mMeaningQ2ActionPlan = meaningQ2ActionPlan.getSelectedItem().toString().trim();
                String mMeaningQ3ActionPlan = meaningQ3ActionPlan.getSelectedItem().toString().trim();
                String mMeaningQ4ActionPlan = meaningQ4ActionPlan.getSelectedItem().toString().trim();
                String mMeaningQ1DueDate = meaningQ1DueDate.getText().toString().trim();
                String mMeaningQ2DueDate = meaningQ2DueDate.getText().toString().trim();
                String mMeaningQ3DueDate = meaningQ3DueDate.getText().toString().trim();
                String mMeaningQ4DueDate = meaningQ4DueDate.getText().toString().trim();
                String mMeaningQ1Remarks = meaningQ1Remarks.getText().toString().trim();
                String mMeaningQ2Remarks = meaningQ2Remarks.getText().toString().trim();
                String mMeaningQ3Remarks = meaningQ3Remarks.getText().toString().trim();
                String mMeaningQ4Remarks = meaningQ4Remarks.getText().toString().trim();

                String mSocialQ1Value = socialQ1Spinner.getSelectedItem().toString();
                String mSocialQ2Value = socialQ2Spinner.getSelectedItem().toString();
                String mSocialQ3Value = socialQ3Spinner.getSelectedItem().toString();
                String mSocialQ4Value = socialQ4Spinner.getSelectedItem().toString();
                String mSocialQ5Value = socialQ5Spinner.getSelectedItem().toString();
                String mSocialQ6Value = socialQ6Spinner.getSelectedItem().toString();
                String mSocialQ7Value = socialQ7Spinner.getSelectedItem().toString();
                String mSocialQ8Value = socialQ8Spinner.getSelectedItem().toString();
                String mSocialQ1ActionPlan = socialQ1ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ2ActionPlan = socialQ2ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ3ActionPlan = socialQ3ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ4ActionPlan = socialQ4ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ5ActionPlan = socialQ5ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ6ActionPlan = socialQ6ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ7ActionPlan = socialQ7ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ8ActionPlan = socialQ8ActionPlan.getSelectedItem().toString().trim();
                String mSocialQ1DueDate = socialQ1DueDate.getText().toString().trim();
                String mSocialQ2DueDate = socialQ2DueDate.getText().toString().trim();
                String mSocialQ3DueDate = socialQ3DueDate.getText().toString().trim();
                String mSocialQ4DueDate = socialQ4DueDate.getText().toString().trim();
                String mSocialQ5DueDate = socialQ5DueDate.getText().toString().trim();
                String mSocialQ6DueDate = socialQ6DueDate.getText().toString().trim();
                String mSocialQ7DueDate = socialQ7DueDate.getText().toString().trim();
                String mSocialQ8DueDate = socialQ8DueDate.getText().toString().trim();
                String mSocialQ1Remarks = socialQ1Remarks.getText().toString().trim();
                String mSocialQ2Remarks = socialQ2Remarks.getText().toString().trim();
                String mSocialQ3Remarks = socialQ3Remarks.getText().toString().trim();
                String mSocialQ4Remarks = socialQ4Remarks.getText().toString().trim();
                String mSocialQ5Remarks = socialQ5Remarks.getText().toString().trim();
                String mSocialQ6Remarks = socialQ6Remarks.getText().toString().trim();
                String mSocialQ7Remarks = socialQ7Remarks.getText().toString().trim();
                String mSocialQ8Remarks = socialQ8Remarks.getText().toString().trim();

                String mHealthQ1Value = healthQ1Spinner.getSelectedItem().toString();
                String mHealthQ2Value = healthQ2Spinner.getSelectedItem().toString();
                String mHealthQ3Value = healthQ3Spinner.getSelectedItem().toString();
                String mHealthQ4Value = healthQ4Spinner.getSelectedItem().toString();
                String mHealthQ5Value = healthQ5Spinner.getSelectedItem().toString();
                String mHealthQ6Value = healthQ6Spinner.getSelectedItem().toString();
                String mHealthQ7Value = healthQ7Spinner.getSelectedItem().toString();
                String mHealthQ8Value = healthQ8Spinner.getSelectedItem().toString();
                String mHealthQ9Value = healthQ9Spinner.getSelectedItem().toString();
                String mHealthQ10Value = healthQ10Spinner.getSelectedItem().toString();
                String mHealthQ11Value = healthQ11Spinner.getSelectedItem().toString();
                String mHealthQ12Value = healthQ12Spinner.getSelectedItem().toString();
                String mHealthQ13Value = healthQ13Spinner.getSelectedItem().toString();
                String mHealthQ14Value = healthQ14Spinner.getSelectedItem().toString();
                String mHealthQ1ActionPlan = healthQ1ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ2ActionPlan = healthQ2ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ3ActionPlan = healthQ3ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ4ActionPlan = healthQ4ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ5ActionPlan = healthQ5ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ6ActionPlan = healthQ6ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ7ActionPlan = healthQ7ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ8ActionPlan = healthQ8ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ9ActionPlan = healthQ9ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ10ActionPlan = healthQ10ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ11ActionPlan = healthQ11ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ12ActionPlan = healthQ12ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ13ActionPlan = healthQ13ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ14ActionPlan = healthQ14ActionPlan.getSelectedItem().toString().trim();
                String mHealthQ1DueDate = healthQ1DueDate.getText().toString().trim();
                String mHealthQ2DueDate = healthQ2DueDate.getText().toString().trim();
                String mHealthQ3DueDate = healthQ3DueDate.getText().toString().trim();
                String mHealthQ4DueDate = healthQ4DueDate.getText().toString().trim();
                String mHealthQ5DueDate = healthQ5DueDate.getText().toString().trim();
                String mHealthQ6DueDate = healthQ6DueDate.getText().toString().trim();
                String mHealthQ7DueDate = healthQ7DueDate.getText().toString().trim();
                String mHealthQ8DueDate = healthQ8DueDate.getText().toString().trim();
                String mHealthQ9DueDate = healthQ9DueDate.getText().toString().trim();
                String mHealthQ10DueDate = healthQ10DueDate.getText().toString().trim();
                String mHealthQ11DueDate = healthQ11DueDate.getText().toString().trim();
                String mHealthQ12DueDate = healthQ12DueDate.getText().toString().trim();
                String mHealthQ13DueDate = healthQ13DueDate.getText().toString().trim();
                String mHealthQ14DueDate = healthQ14DueDate.getText().toString().trim();
                String mHealthQ1Remarks = healthQ1Remarks.getText().toString().trim();
                String mHealthQ2Remarks = healthQ2Remarks.getText().toString().trim();
                String mHealthQ3Remarks = healthQ3Remarks.getText().toString().trim();
                String mHealthQ4Remarks = healthQ4Remarks.getText().toString().trim();
                String mHealthQ5Remarks = healthQ5Remarks.getText().toString().trim();
                String mHealthQ6Remarks = healthQ6Remarks.getText().toString().trim();
                String mHealthQ7Remarks = healthQ7Remarks.getText().toString().trim();
                String mHealthQ8Remarks = healthQ8Remarks.getText().toString().trim();
                String mHealthQ9Remarks = healthQ9Remarks.getText().toString().trim();
                String mHealthQ10Remarks = healthQ10Remarks.getText().toString().trim();
                String mHealthQ11Remarks = healthQ11Remarks.getText().toString().trim();
                String mHealthQ12Remarks = healthQ12Remarks.getText().toString().trim();
                String mHealthQ13Remarks = healthQ13Remarks.getText().toString().trim();
                String mHealthQ14Remarks = healthQ14Remarks.getText().toString().trim();

                String mMentalQ1Value = mentalQ1Spinner.getSelectedItem().toString();
                String mMentalQ2Value = mentalQ2Spinner.getSelectedItem().toString();
                String mMentalQ1ActionPlan = mentalQ1ActionPlan.getSelectedItem().toString().trim();
                String mMentalQ2ActionPlan = mentalQ2ActionPlan.getSelectedItem().toString().trim();
                String mMentalQ1DueDate = mentalQ1DueDate.getText().toString().trim();
                String mMentalQ2DueDate = mentalQ2DueDate.getText().toString().trim();
                String mMentalQ1Remarks = mentalQ1Remarks.getText().toString().trim();
                String mMentalQ2Remarks = mentalQ2Remarks.getText().toString().trim();

                String mPracticalQ1Value = practicalQ1Spinner.getSelectedItem().toString();
                String mPracticalQ2Value = practicalQ2Spinner.getSelectedItem().toString();
                String mPracticalQ3Value = practicalQ3Spinner.getSelectedItem().toString();
                String mPracticalQ4Value = practicalQ4Spinner.getSelectedItem().toString();
                String mPracticalQ5Value = practicalQ5Spinner.getSelectedItem().toString();
                String mPracticalQ6Value = practicalQ6Spinner.getSelectedItem().toString();
                String mPracticalQ7Value = practicalQ7Spinner.getSelectedItem().toString();
                String mPracticalQ1ActionPlan = practicalQ1ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ2ActionPlan = practicalQ2ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ3ActionPlan = practicalQ3ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ4ActionPlan = practicalQ4ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ5ActionPlan = practicalQ5ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ6ActionPlan = practicalQ6ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ7ActionPlan = practicalQ7ActionPlan.getSelectedItem().toString().trim();
                String mPracticalQ1DueDate = practicalQ1DueDate.getText().toString().trim();
                String mPracticalQ2DueDate = practicalQ2DueDate.getText().toString().trim();
                String mPracticalQ3DueDate = practicalQ3DueDate.getText().toString().trim();
                String mPracticalQ4DueDate = practicalQ4DueDate.getText().toString().trim();
                String mPracticalQ5DueDate = practicalQ5DueDate.getText().toString().trim();
                String mPracticalQ6DueDate = practicalQ6DueDate.getText().toString().trim();
                String mPracticalQ7DueDate = practicalQ7DueDate.getText().toString().trim();
                String mPracticalQ1Remarks = practicalQ1Remarks.getText().toString().trim();
                String mPracticalQ2Remarks = practicalQ2Remarks.getText().toString().trim();
                String mPracticalQ3Remarks = practicalQ3Remarks.getText().toString().trim();
                String mPracticalQ4Remarks = practicalQ4Remarks.getText().toString().trim();
                String mPracticalQ5Remarks = practicalQ5Remarks.getText().toString().trim();
                String mPracticalQ6Remarks = practicalQ6Remarks.getText().toString().trim();
                String mPracticalQ7Remarks = practicalQ7Remarks.getText().toString().trim();

          /*      String mLaundryQ1Value = laundryQ1Spinner.getSelectedItem().toString();
                String mLaundryQ1ActionPlan = laundryQ1ActionPlan.getSelectedItem().toString().trim();
                String mLaundryQ1DueDate = laundryQ1DueDate.getText().toString().trim();
                String mLaundryQ1Remarks = laundryQ1Remarks.getText().toString().trim();*/

                String mDailyQ1Value = dailyQ1Spinner.getSelectedItem().toString();
                String mDailyQ2Value = dailyQ2Spinner.getSelectedItem().toString();
                String mDailyQ3Value = dailyQ3Spinner.getSelectedItem().toString();
                String mDailyQ1ActionPlan = dailyQ1ActionPlan.getSelectedItem().toString().trim();
                String mDailyQ2ActionPlan = dailyQ2ActionPlan.getSelectedItem().toString().trim();
                String mDailyQ3ActionPlan = dailyQ3ActionPlan.getSelectedItem().toString().trim();
                String mDailyQ1DueDate = dailyQ1DueDate.getText().toString().trim();
                String mDailyQ2DueDate = dailyQ2DueDate.getText().toString().trim();
                String mDailyQ3DueDate = dailyQ3DueDate.getText().toString().trim();
                String mDailyQ1Remarks = dailyQ1Remarks.getText().toString().trim();
                String mDailyQ2Remarks = dailyQ2Remarks.getText().toString().trim();
                String mDailyQ3Remarks = dailyQ3Remarks.getText().toString().trim();

              /*  String mMedicineQ1Value = medicineQ1Spinner.getSelectedItem().toString();
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
                String mSafeguardingQ4Remarks = safeQ4Remarks.getText().toString().trim();*/


                String mFinancesQ1Value = financesQ1Spinner.getSelectedItem().toString();
                String mFinancesQ2Value = financesQ2Spinner.getSelectedItem().toString();
                String mFinancesQ3Value = financesQ3Spinner.getSelectedItem().toString();
                String mFinancesQ4Value = financesQ4Spinner.getSelectedItem().toString();
                String mFinancesQ5Value = financesQ5Spinner.getSelectedItem().toString();
                String mFinancesQ6Value = financesQ6Spinner.getSelectedItem().toString();
                String mFinancesQ1ActionPlan = financesQ1ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ2ActionPlan = financesQ2ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ3ActionPlan = financesQ3ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ4ActionPlan = financesQ4ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ5ActionPlan = financesQ5ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ6ActionPlan = financesQ6ActionPlan.getSelectedItem().toString().trim();
                String mFinancesQ1DueDate = financesQ1DueDate.getText().toString().trim();
                String mFinancesQ2DueDate = financesQ2DueDate.getText().toString().trim();
                String mFinancesQ3DueDate = financesQ3DueDate.getText().toString().trim();
                String mFinancesQ4DueDate = financesQ4DueDate.getText().toString().trim();
                String mFinancesQ5DueDate = financesQ5DueDate.getText().toString().trim();
                String mFinancesQ6DueDate = financesQ6DueDate.getText().toString().trim();
                String mFinancesQ1Remarks = financesQ1Remarks.getText().toString().trim();
                String mFinancesQ2Remarks = financesQ2Remarks.getText().toString().trim();
                String mFinancesQ3Remarks = financesQ3Remarks.getText().toString().trim();
                String mFinancesQ4Remarks = financesQ4Remarks.getText().toString().trim();
                String mFinancesQ5Remarks = financesQ5Remarks.getText().toString().trim();
                String mFinancesQ6Remarks = financesQ6Remarks.getText().toString().trim();
/*
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
                String mRiskAssociatedRemarks = riskPositionRemarks.getText().toString().trim();*/

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
                params.put("housing_and_environment_q1_value", mHousingQ1Value);
                params.put("housing_and_environment_q2_value", mHousingQ2Value);
                params.put("housing_and_environment_q3_value", mHousingQ3Value);
                params.put("housing_and_environment_q4_value", mHousingQ4Value);
                params.put("housing_and_environment_q5_value", mHousingQ5Value);
                params.put("housing_and_environment_q6_value", mHousingQ6Value);
                params.put("housing_and_environment_q7_value", mHousingQ7Value);
                params.put("housing_and_environment_q8_value", mHousingQ8Value);
                params.put("housing_and_environment_q9_value", mHousingQ9Value);
                params.put("housing_and_environment_q10_value", mHousingQ10Value);
                params.put("housing_and_environment_q11_value", mHousingQ11Value);
                params.put("housing_and_environment_q12_value", mHousingQ12Value);
                params.put("housing_and_environment_q1_action_plan", mHousingQ1ActionPlan);
                params.put("housing_and_environment_q2_action_plan", mHousingQ2ActionPlan);
                params.put("housing_and_environment_q3_action_plan", mHousingQ3ActionPlan);
                params.put("housing_and_environment_q4_action_plan", mHousingQ4ActionPlan);
                params.put("housing_and_environment_q5_action_plan", mHousingQ5ActionPlan);
                params.put("housing_and_environment_q6_action_plan", mHousingQ6ActionPlan);
                params.put("housing_and_environment_q7_action_plan", mHousingQ7ActionPlan);
                params.put("housing_and_environment_q8_action_plan", mHousingQ8ActionPlan);
                params.put("housing_and_environment_q9_action_plan", mHousingQ9ActionPlan);
                params.put("housing_and_environment_q10_action_plan", mHousingQ10ActionPlan);
                params.put("housing_and_environment_q11_action_plan", mHousingQ11ActionPlan);
                params.put("housing_and_environment_q12_action_plan", mHousingQ12ActionPlan);
                params.put("housing_and_environment_q1_due_date", mHousingQ1DueDate);
                params.put("housing_and_environment_q2_due_date", mHousingQ2DueDate);
                params.put("housing_and_environment_q3_due_date", mHousingQ3DueDate);
                params.put("housing_and_environment_q4_due_date", mHousingQ4DueDate);
                params.put("housing_and_environment_q5_due_date", mHousingQ5DueDate);
                params.put("housing_and_environment_q6_due_date", mHousingQ6DueDate);
                params.put("housing_and_environment_q7_due_date", mHousingQ7DueDate);
                params.put("housing_and_environment_q8_due_date", mHousingQ8DueDate);
                params.put("housing_and_environment_q9_due_date", mHousingQ9DueDate);
                params.put("housing_and_environment_q10_due_date", mHousingQ10DueDate);
                params.put("housing_and_environment_q11_due_date", mHousingQ11DueDate);
                params.put("housing_and_environment_q12_due_date", mHousingQ12DueDate);
                params.put("housing_and_environment_q1_remarks", mHousingQ1Remarks);
                params.put("housing_and_environment_q2_remarks", mHousingQ2Remarks);
                params.put("housing_and_environment_q3_remarks", mHousingQ3Remarks);
                params.put("housing_and_environment_q4_remarks", mHousingQ4Remarks);
                params.put("housing_and_environment_q5_remarks", mHousingQ5Remarks);
                params.put("housing_and_environment_q6_remarks", mHousingQ6Remarks);
                params.put("housing_and_environment_q7_remarks", mHousingQ7Remarks);
                params.put("housing_and_environment_q8_remarks", mHousingQ8Remarks);
                params.put("housing_and_environment_q9_remarks", mHousingQ9Remarks);
                params.put("housing_and_environment_q10_remarks", mHousingQ10Remarks);
                params.put("housing_and_environment_q11_remarks", mHousingQ11Remarks);
                params.put("housing_and_environment_q12_remarks", mHousingQ12Remarks);

                params.put("meaning_in_life_q1_value", mMeaningQ1Value);
                params.put("meaning_in_life_q2_value", mMeaningQ2Value);
                params.put("meaning_in_life_q3_value", mMeaningQ3Value);
                params.put("meaning_in_life_q4_value", mMeaningQ4Value);
                params.put("meaning_in_life_q1_action_plan", mMeaningQ1ActionPlan);
                params.put("meaning_in_life_q2_action_plan", mMeaningQ2ActionPlan);
                params.put("meaning_in_life_q3_action_plan", mMeaningQ3ActionPlan);
                params.put("meaning_in_life_q4_action_plan", mMeaningQ4ActionPlan);
                params.put("meaning_in_life_q1_due_date", mMeaningQ1DueDate);
                params.put("meaning_in_life_q2_due_date", mMeaningQ2DueDate);
                params.put("meaning_in_life_q3_due_date", mMeaningQ3DueDate);
                params.put("meaning_in_life_q4_due_date", mMeaningQ4DueDate);
                params.put("meaning_in_life_q1_remarks", mMeaningQ1Remarks);
                params.put("meaning_in_life_q2_remarks", mMeaningQ2Remarks);
                params.put("meaning_in_life_q3_remarks", mMeaningQ3Remarks);
                params.put("meaning_in_life_q4_remarks", mMeaningQ4Remarks);

                params.put("social_functioning_q1_value", mSocialQ1Value);
                params.put("social_functioning_q2_value", mSocialQ2Value);
                params.put("social_functioning_q3_value", mSocialQ3Value);
                params.put("social_functioning_q4_value", mSocialQ4Value);
                params.put("social_functioning_q5_value", mSocialQ5Value);
                params.put("social_functioning_q6_value", mSocialQ6Value);
                params.put("social_functioning_q7_value", mSocialQ7Value);
                params.put("social_functioning_q8_value", mSocialQ8Value);
                params.put("social_functioning_q1_action_plan", mSocialQ1ActionPlan);
                params.put("social_functioning_q2_action_plan", mSocialQ2ActionPlan);
                params.put("social_functioning_q3_action_plan", mSocialQ3ActionPlan);
                params.put("social_functioning_q4_action_plan", mSocialQ4ActionPlan);
                params.put("social_functioning_q5_action_plan", mSocialQ5ActionPlan);
                params.put("social_functioning_q6_action_plan", mSocialQ6ActionPlan);
                params.put("social_functioning_q7_action_plan", mSocialQ7ActionPlan);
                params.put("social_functioning_q7_action_plan", mSocialQ8ActionPlan);
                params.put("social_functioning_q1_due_date", mSocialQ1DueDate);
                params.put("social_functioning_q2_due_date", mSocialQ2DueDate);
                params.put("social_functioning_q3_due_date", mSocialQ3DueDate);
                params.put("social_functioning_q4_due_date", mSocialQ4DueDate);
                params.put("social_functioning_q5_due_date", mSocialQ5DueDate);
                params.put("social_functioning_q6_due_date", mSocialQ6DueDate);
                params.put("social_functioning_q7_due_date", mSocialQ7DueDate);
                params.put("social_functioning_q8_due_date", mSocialQ8DueDate);
                params.put("social_functioning_q1_remarks", mSocialQ1Remarks);
                params.put("social_functioning_q2_remarks", mSocialQ2Remarks);
                params.put("social_functioning_q3_remarks", mSocialQ3Remarks);
                params.put("social_functioning_q4_remarks", mSocialQ4Remarks);
                params.put("social_functioning_q5_remarks", mSocialQ5Remarks);
                params.put("social_functioning_q6_remarks", mSocialQ6Remarks);
                params.put("social_functioning_q7_remarks", mSocialQ7Remarks);
                params.put("social_functioning_q8_remarks", mSocialQ8Remarks);

                params.put("health_q1_value", mHealthQ1Value);
                params.put("health_q2_value", mHealthQ2Value);
                params.put("health_q3_value", mHealthQ3Value);
                params.put("health_q4_value", mHealthQ4Value);
                params.put("health_q5_value", mHealthQ5Value);
                params.put("health_q6_value", mHealthQ6Value);
                params.put("health_q7_value", mHealthQ7Value);
                params.put("health_q8_value", mHealthQ8Value);
                params.put("health_q9_value", mHealthQ9Value);
                params.put("health_q10_value", mHealthQ10Value);
                params.put("health_q11_value", mHealthQ11Value);
                params.put("health_q12_value", mHealthQ12Value);
                params.put("health_q13_value", mHealthQ13Value);
                params.put("health_q14_value", mHealthQ14Value);
                params.put("health_q1_action_plan", mHealthQ1ActionPlan);
                params.put("health_q2_action_plan", mHealthQ2ActionPlan);
                params.put("health_q3_action_plan", mHealthQ3ActionPlan);
                params.put("health_q4_action_plan", mHealthQ4ActionPlan);
                params.put("health_q5_action_plan", mHealthQ5ActionPlan);
                params.put("health_q6_action_plan", mHealthQ6ActionPlan);
                params.put("health_q7_action_plan", mHealthQ7ActionPlan);
                params.put("health_q8_action_plan", mHealthQ8ActionPlan);
                params.put("health_q9_action_plan", mHealthQ9ActionPlan);
                params.put("health_q10_action_plan", mHealthQ10ActionPlan);
                params.put("health_q11_action_plan", mHealthQ11ActionPlan);
                params.put("health_q12_action_plan", mHealthQ12ActionPlan);
                params.put("health_q13_action_plan", mHealthQ13ActionPlan);
                params.put("health_q14_action_plan", mHealthQ14ActionPlan);
                params.put("health_q1_due_date", mHealthQ1DueDate);
                params.put("health_q2_due_date", mHealthQ2DueDate);
                params.put("health_q3_due_date", mHealthQ3DueDate);
                params.put("health_q4_due_date", mHealthQ4DueDate);
                params.put("health_q5_due_date", mHealthQ5DueDate);
                params.put("health_q6_due_date", mHealthQ6DueDate);
                params.put("health_q7_due_date", mHealthQ7DueDate);
                params.put("health_q8_due_date", mHealthQ8DueDate);
                params.put("health_q9_due_date", mHealthQ9DueDate);
                params.put("health_q10_due_date", mHealthQ10DueDate);
                params.put("health_q11_due_date", mHealthQ11DueDate);
                params.put("health_q12_due_date", mHealthQ12DueDate);
                params.put("health_q13_due_date", mHealthQ13DueDate);
                params.put("health_q14_due_date", mHealthQ14DueDate);
                params.put("health_q1_remarks", mHealthQ1Remarks);
                params.put("health_q2_remarks", mHealthQ2Remarks);
                params.put("health_q3_remarks", mHealthQ3Remarks);
                params.put("health_q4_remarks", mHealthQ4Remarks);
                params.put("health_q5_remarks", mHealthQ5Remarks);
                params.put("health_q6_remarks", mHealthQ6Remarks);
                params.put("health_q7_remarks", mHealthQ7Remarks);
                params.put("health_q8_remarks", mHealthQ8Remarks);
                params.put("health_q9_remarks", mHealthQ9Remarks);
                params.put("health_q10_remarks", mHealthQ10Remarks);
                params.put("health_q11_remarks", mHealthQ11Remarks);
                params.put("health_q12_remarks", mHealthQ12Remarks);
                params.put("health_q13_remarks", mHealthQ13Remarks);
                params.put("health_q14_remarks", mHealthQ14Remarks);


                params.put("mental_health_q1_value", mMentalQ1Value);
                params.put("mental_health_q2_value", mMentalQ2Value);
                params.put("mental_health_q1_action_plan", mMentalQ1ActionPlan);
                params.put("mental_health_q2_action_plan", mMentalQ2ActionPlan);
                params.put("mental_health_q1_due_date", mMentalQ1DueDate);
                params.put("mental_health_q2_due_date", mMentalQ2DueDate);
                params.put("mental_health_q1_remarks", mMentalQ1Remarks);
                params.put("mental_health_q2_remarks", mMentalQ2Remarks);

                params.put("practical_functioning_q1_value", mPracticalQ1Value);
                params.put("practical_functioning_q2_value", mPracticalQ2Value);
                params.put("practical_functioning_q3_value", mPracticalQ3Value);
                params.put("practical_functioning_q4_value", mPracticalQ4Value);
                params.put("practical_functioning_q5_value", mPracticalQ5Value);
                params.put("practical_functioning_q6_value", mPracticalQ6Value);
                params.put("practical_functioning_q7_value", mPracticalQ7Value);
                params.put("practical_functioning_q1_action_plan", mPracticalQ1ActionPlan);
                params.put("practical_functioning_q2_action_plan", mPracticalQ2ActionPlan);
                params.put("practical_functioning_q3_action_plan", mPracticalQ3ActionPlan);
                params.put("practical_functioning_q4_action_plan", mPracticalQ4ActionPlan);
                params.put("practical_functioning_q5_action_plan", mPracticalQ5ActionPlan);
                params.put("practical_functioning_q6_action_plan", mPracticalQ6ActionPlan);
                params.put("practical_functioning_q7_action_plan", mPracticalQ7ActionPlan);
                params.put("practical_functioning_q1_due_date", mPracticalQ1DueDate);
                params.put("practical_functioning_q2_due_date", mPracticalQ2DueDate);
                params.put("practical_functioning_q3_due_date", mPracticalQ3DueDate);
                params.put("practical_functioning_q4_due_date", mPracticalQ4DueDate);
                params.put("practical_functioning_q5_due_date", mPracticalQ5DueDate);
                params.put("practical_functioning_q6_due_date", mPracticalQ6DueDate);
                params.put("practical_functioning_q7_due_date", mPracticalQ7DueDate);
                params.put("practical_functioning_q1_remarks", mPracticalQ1Remarks);
                params.put("practical_functioning_q2_remarks", mPracticalQ2Remarks);
                params.put("practical_functioning_q3_remarks", mPracticalQ3Remarks);
                params.put("practical_functioning_q4_remarks", mPracticalQ4Remarks);
                params.put("practical_functioning_q5_remarks", mPracticalQ5Remarks);
                params.put("practical_functioning_q6_remarks", mPracticalQ6Remarks);
                params.put("practical_functioning_q7_remarks", mPracticalQ7Remarks);

           /*     params.put("laundry_n_washing_q1_value", mLaundryQ1Value);
                params.put("laundry_n_washing_q1_action_plan", mLaundryQ1ActionPlan);
                params.put("laundry_n_washing_q1_due_date", mLaundryQ1DueDate);
                params.put("laundry_n_washing_q1_remarks", mLaundryQ1Remarks);
*/

           /*     params.put("getting_about_q1_value", mGettingAboutQ1Value);
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
                params.put("home_support_q4_remarks", mHomeSupportQ4Remarks);*/

                params.put("daily_functioning_q1_value", mDailyQ1Value);
                params.put("daily_functioning_q2_value", mDailyQ2Value);
                params.put("daily_functioning_q3_value", mDailyQ3Value);
                params.put("daily_functioning_q1_action_plan", mDailyQ1ActionPlan);
                params.put("daily_functioning_q2_action_plan", mDailyQ2ActionPlan);
                params.put("daily_functioning_q3_action_plan", mDailyQ3ActionPlan);
                params.put("daily_functioning_q1_due_date", mDailyQ1DueDate);
                params.put("daily_functioning_q2_due_date", mDailyQ2DueDate);
                params.put("daily_functioning_q3_due_date", mDailyQ3DueDate);
                params.put("daily_functioning_q1_remarks", mDailyQ1Remarks);
                params.put("daily_functioning_q2_remarks", mDailyQ2Remarks);
                params.put("daily_functioning_q3_remarks", mDailyQ3Remarks);

     /*           params.put("safeguarding_q1_value", mSafeguardingQ1Value);
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
                params.put("safeguarding_q4_remarks", mSafeguardingQ4Remarks);*/

                params.put("economic_and_finances_q1_value", mFinancesQ1Value);
                params.put("economic_and_finances_q2_value", mFinancesQ2Value);
                params.put("economic_and_finances_q3_value", mFinancesQ3Value);
                params.put("economic_and_finances_q4_value", mFinancesQ4Value);
                params.put("economic_and_finances_q5_value", mFinancesQ5Value);
                params.put("economic_and_finances_q6value", mFinancesQ6Value);
                params.put("economic_and_finances_q1_action_plan", mFinancesQ1ActionPlan);
                params.put("economic_and_finances_q2_action_plan", mFinancesQ2ActionPlan);
                params.put("economic_and_finances_q3_action_plan", mFinancesQ3ActionPlan);
                params.put("economic_and_finances_q4_action_plan", mFinancesQ4ActionPlan);
                params.put("economic_and_finances_q5_action_plan", mFinancesQ5ActionPlan);
                params.put("economic_and_finances_q6_action_plan", mFinancesQ6ActionPlan);
                params.put("economic_and_finances_q1_due_date", mFinancesQ1DueDate);
                params.put("economic_and_finances_q2_due_date", mFinancesQ2DueDate);
                params.put("economic_and_finances_q3_due_date", mFinancesQ3DueDate);
                params.put("economic_and_finances_q4_due_date", mFinancesQ4DueDate);
                params.put("economic_and_finances_q5_due_date", mFinancesQ5DueDate);
                params.put("economic_and_finances_q6_due_date", mFinancesQ6DueDate);
                params.put("economic_and_finances_q1_remarks", mFinancesQ1Remarks);
                params.put("economic_and_finances_q2_remarks", mFinancesQ2Remarks);
                params.put("economic_and_finances_q3_remarks", mFinancesQ3Remarks);
                params.put("economic_and_finances_q4_remarks", mFinancesQ4Remarks);
                params.put("economic_and_finances_q5_remarks", mFinancesQ5Remarks);
                params.put("economic_and_finances_q6_remarks", mFinancesQ6Remarks);

        /*        params.put("location_of_assessment_value", mLocationOfAssessmentValue);
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
                params.put("risk_associated_remarks", mRiskAssociatedRemarks);*/

                params.put("api_token", token);


                int total = Integer.valueOf(mHousingQ1Value) + Integer.valueOf(mHousingQ2Value) + Integer.valueOf(mHousingQ3Value) +
                        Integer.valueOf(mHousingQ4Value) + Integer.valueOf(mHousingQ5Value) + Integer.valueOf(mHousingQ6Value) +
                        Integer.valueOf(mHousingQ7Value) + Integer.valueOf(mHousingQ8Value) + Integer.valueOf(mHousingQ9Value) +
                        Integer.valueOf(mHousingQ10Value) + Integer.valueOf(mHousingQ11Value) + Integer.valueOf(mHousingQ12Value) +
                        Integer.valueOf(mSocialQ1Value) + Integer.valueOf(mSocialQ2Value) + Integer.valueOf(mSocialQ3Value) + Integer.valueOf(mSocialQ4Value) +
                        Integer.valueOf(mSocialQ5Value) + Integer.valueOf(mSocialQ6Value) + Integer.valueOf(mSocialQ7Value) + Integer.valueOf(mSocialQ8Value) +
                        Integer.valueOf(mHealthQ1Value) + Integer.valueOf(mHealthQ2Value) + Integer.valueOf(mHealthQ3Value) +
                        Integer.valueOf(mHealthQ4Value) + Integer.valueOf(mHealthQ5Value) + Integer.valueOf(mHealthQ6Value)
                        + Integer.valueOf(mHealthQ7Value) + Integer.valueOf(mHealthQ8Value) + Integer.valueOf(mHealthQ9Value) + Integer.valueOf(mHealthQ10Value)
                        + Integer.valueOf(mHealthQ11Value) + Integer.valueOf(mHealthQ12Value) + Integer.valueOf(mHealthQ13Value) + Integer.valueOf(mHealthQ14Value) +
                        Integer.valueOf(mMentalQ1Value) + Integer.valueOf(mMentalQ2Value) + Integer.valueOf(mMeaningQ1Value) + Integer.valueOf(mMeaningQ2Value) +
                        Integer.valueOf(mMeaningQ3Value) + Integer.valueOf(mMeaningQ4Value) + Integer.valueOf(mPracticalQ1Value) + Integer.valueOf(mPracticalQ2Value)
                        + Integer.valueOf(mPracticalQ3Value) + Integer.valueOf(mPracticalQ4Value) + Integer.valueOf(mPracticalQ5Value) + Integer.valueOf(mPracticalQ6Value)
                        + Integer.valueOf(mPracticalQ7Value) + Integer.valueOf(mDailyQ1Value) + Integer.valueOf(mDailyQ2Value) +
                        Integer.valueOf(mDailyQ3Value) + Integer.valueOf(mFinancesQ1Value) + Integer.valueOf(mFinancesQ2Value)
                        + Integer.valueOf(mFinancesQ3Value) + Integer.valueOf(mFinancesQ4Value) + Integer.valueOf(mFinancesQ5Value) +
                        Integer.valueOf(mFinancesQ6Value);

//                    totalScore.setText(String.valueOf(total));


          /*      if (mSafeguardingQ1Value.equals("0")) {
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
*/

                JSONObject parameter = new JSONObject(params);

//                    OkHttpClient client = new OkHttpClient();

                Log.e("JSON:", parameter.toString());
                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), AssessmentQuestions.this));


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
        SetDate housingQ1DatePicker = new SetDate(this, housingQ1DueDate);
        SetDate housingQ2DatePicker = new SetDate(this, housingQ2DueDate);
        SetDate housingQ3DatePicker = new SetDate(this, housingQ3DueDate);
        SetDate housingQ4DatePicker = new SetDate(this, housingQ4DueDate);
        SetDate housingQ5DatePicker = new SetDate(this, housingQ5DueDate);
        SetDate housingQ6DatePicker = new SetDate(this, housingQ6DueDate);
        SetDate housingQ7DatePicker = new SetDate(this, housingQ7DueDate);
        SetDate housingQ8DatePicker = new SetDate(this, housingQ8DueDate);
        SetDate housingQ9DatePicker = new SetDate(this, housingQ9DueDate);
        SetDate housingQ10DatePicker = new SetDate(this, housingQ10DueDate);
        SetDate housingQ11DatePicker = new SetDate(this, housingQ11DueDate);
        SetDate housingQ12DatePicker = new SetDate(this, housingQ12DueDate);

    /*    SetDate houseQ1DatePicker = new SetDate(this, houseQ1DueDate);
        SetDate houseQ2DatePicker = new SetDate(this, houseQ2DueDate);
        SetDate houseQ3DatePicker = new SetDate(this, houseQ3DueDate);
        SetDate houseQ4DatePicker = new SetDate(this, houseQ4DueDate);*/

        SetDate socialQ1DatePicker = new SetDate(this, socialQ1DueDate);
        SetDate socialQ2DatePicker = new SetDate(this, socialQ2DueDate);
        SetDate socialQ3DatePicker = new SetDate(this, socialQ3DueDate);
        SetDate socialQ4DatePicker = new SetDate(this, socialQ4DueDate);
        SetDate socialQ5DatePicker = new SetDate(this, socialQ5DueDate);
        SetDate socialQ6DatePicker = new SetDate(this, socialQ6DueDate);
        SetDate socialQ7DatePicker = new SetDate(this, socialQ7DueDate);
        SetDate socialQ8DatePicker = new SetDate(this, socialQ8DueDate);

        SetDate healthQ1DatePicker = new SetDate(this, healthQ1DueDate);
        SetDate healthQ2DatePicker = new SetDate(this, healthQ2DueDate);
        SetDate healthQ3DatePicker = new SetDate(this, healthQ3DueDate);
        SetDate healthQ4DatePicker = new SetDate(this, healthQ4DueDate);
        SetDate healthQ5DatePicker = new SetDate(this, healthQ5DueDate);
        SetDate healthQ6DatePicker = new SetDate(this, healthQ6DueDate);
        SetDate healthQ7DatePicker = new SetDate(this, healthQ7DueDate);
        SetDate healthQ8DatePicker = new SetDate(this, healthQ8DueDate);
        SetDate healthQ9DatePicker = new SetDate(this, healthQ9DueDate);
        SetDate healthQ10DatePicker = new SetDate(this, healthQ10DueDate);
        SetDate healthQ11DatePicker = new SetDate(this, healthQ11DueDate);
        SetDate healthQ12DatePicker = new SetDate(this, healthQ12DueDate);
        SetDate healthQ13DatePicker = new SetDate(this, healthQ13DueDate);
        SetDate healthQ14DatePicker = new SetDate(this, healthQ14DueDate);

        SetDate mentalQ1DatePicker = new SetDate(this, mentalQ1DueDate);
        SetDate mentalQ2DatePicker = new SetDate(this, mentalQ2DueDate);

        SetDate meaningQ1DatePicker = new SetDate(this, meaningQ1DueDate);
        SetDate meaningQ2DatePicker = new SetDate(this, meaningQ2DueDate);
        SetDate meaningQ3DatePicker = new SetDate(this, meaningQ3DueDate);
        SetDate meaningQ4DatePicker = new SetDate(this, meaningQ4DueDate);

//        SetDate laundryQ1DatePicker = new SetDate(this, laundryQ1DueDate);

     /*   SetDate aboutQ1DatePicker = new SetDate(this, aboutQ1DueDate);
        SetDate aboutQ2DatePicker = new SetDate(this, aboutQ2DueDate);*/

//        SetDate medicineQ1DatePicker = new SetDate(this, medicineQ1DueDate);

        SetDate practicalQ1DatePicker = new SetDate(this, practicalQ1DueDate);
        SetDate practicalQ2DatePicker = new SetDate(this, practicalQ2DueDate);
        SetDate practicalQ3DatePicker = new SetDate(this, practicalQ3DueDate);
        SetDate practicalQ4DatePicker = new SetDate(this, practicalQ4DueDate);
        SetDate practicalQ5DatePicker = new SetDate(this, practicalQ5DueDate);
        SetDate practicalQ6DatePicker = new SetDate(this, practicalQ6DueDate);
        SetDate practicalQ7DatePicker = new SetDate(this, practicalQ7DueDate);

        SetDate dailyQ1DatePicker = new SetDate(this, dailyQ1DueDate);
        SetDate dailyQ2DatePicker = new SetDate(this, dailyQ2DueDate);
        SetDate dailyQ3DatePicker = new SetDate(this, dailyQ3DueDate);

/*        SetDate safeQ1DatePicker = new SetDate(this, safeQ1DueDate);
        SetDate safeQ2DatePicker = new SetDate(this, safeQ2DueDate);
        SetDate safeQ3DatePicker = new SetDate(this, safeQ3DueDate);
        SetDate safeQ4DatePicker = new SetDate(this, safeQ4DueDate);*/

        SetDate financesQ1DatePicker = new SetDate(this, financesQ1DueDate);
        SetDate financesQ2DatePicker = new SetDate(this, financesQ2DueDate);
        SetDate financesQ3DatePicker = new SetDate(this, financesQ3DueDate);
        SetDate financesQ4DatePicker = new SetDate(this, financesQ4DueDate);
        SetDate financesQ5DatePicker = new SetDate(this, financesQ5DueDate);
        SetDate financesQ6DatePicker = new SetDate(this, financesQ6DueDate);

/*        SetDate assessmentLocationDatePicker = new SetDate(this, assessmentLocationDueDate);
        SetDate assessedByScoreDatePicker = new SetDate(this, assessedByScoreDueDate);
        SetDate riskPositionDatePicker = new SetDate(this, riskPositionDueDate);*/


    }


    private void initializeView() {
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        list = new ArrayList<>();

//        totalScore = findViewById(R.id.total_score);

        housingQ1ac = findViewById(R.id.housing_q1_ap);
        housingQ2ac = findViewById(R.id.housing_q2_ap);
        housingQ3ac = findViewById(R.id.housing_q3_ap);
        housingQ4ac = findViewById(R.id.housing_q4_ap);
        housingQ5ac = findViewById(R.id.housing_q5_ap);
        housingQ6ac = findViewById(R.id.housing_q6_ap);
        housingQ7ac = findViewById(R.id.housing_q7_ap);
        housingQ8ac = findViewById(R.id.housing_q8_ap);
        housingQ9ac = findViewById(R.id.housing_q9_ap);
        housingQ10ac = findViewById(R.id.housing_q10_ap);
        housingQ11ac = findViewById(R.id.housing_q11_ap);
        housingQ12ac = findViewById(R.id.housing_q12_ap);
        elHousing = findViewById(R.id.expandable_layout_housing);

    /*    elHouse = findViewById(R.id.expandable_layout_house);
        houseQ1ac = findViewById(R.id.house_fac_q1_ap);
        houseQ2ac = findViewById(R.id.house_fac_q2_ap);
        houseQ3ac = findViewById(R.id.house_fac_q3_ap);
        houseQ4ac = findViewById(R.id.house_fac_q4_ap);*/

        elSocial = findViewById(R.id.expandable_layout_social);
        socialQ1ac = findViewById(R.id.social_q1_ap);
        socialQ2ac = findViewById(R.id.social_q2_ap);
        socialQ3ac = findViewById(R.id.social_q3_ap);
        socialQ4ac = findViewById(R.id.social_q4_ap);
        socialQ5ac = findViewById(R.id.social_q5_ap);
        socialQ6ac = findViewById(R.id.social_q6_ap);
        socialQ7ac = findViewById(R.id.social_q7_ap);
        socialQ8ac = findViewById(R.id.social_q8_ap);

        elHealth = findViewById(R.id.expandable_layout_health);
        healthQ1ac = findViewById(R.id.health_q1_ap);
        healthQ2ac = findViewById(R.id.health_q2_ap);
        healthQ3ac = findViewById(R.id.health_q3_ap);
        healthQ4ac = findViewById(R.id.health_q4_ap);
        healthQ5ac = findViewById(R.id.health_q5_ap);
        healthQ6ac = findViewById(R.id.health_q6_ap);
        healthQ7ac = findViewById(R.id.health_q7_ap);
        healthQ8ac = findViewById(R.id.health_q8_ap);
        healthQ9ac = findViewById(R.id.health_q9_ap);
        healthQ10ac = findViewById(R.id.health_q10_ap);
        healthQ11ac = findViewById(R.id.health_q11_ap);
        healthQ12ac = findViewById(R.id.health_q12_ap);
        healthQ13ac = findViewById(R.id.health_q13_ap);
        healthQ14ac = findViewById(R.id.health_q14_ap);

        elMental = findViewById(R.id.expandable_layout_mental);
        mentalQ1ac = findViewById(R.id.mental_q1_ap);
        mentalQ2ac = findViewById(R.id.mental_q2_ap);

        elPractical = findViewById(R.id.expandable_layout_practical);
        practicalQ1ac = findViewById(R.id.practical_q1_ap);
        practicalQ2ac = findViewById(R.id.practical_q2_ap);
        practicalQ3ac = findViewById(R.id.practical_q3_ap);
        practicalQ4ac = findViewById(R.id.practical_q4_ap);
        practicalQ5ac = findViewById(R.id.practical_q5_ap);
        practicalQ6ac = findViewById(R.id.practical_q6_ap);
        practicalQ7ac = findViewById(R.id.practical_q7_ap);

    /*    elLaundry = findViewById(R.id.expandable_layout_laundry);
        laundryQ1ac = findViewById(R.id.laundry_q1_ap);

        elGettingAbout = findViewById(R.id.expandable_layout_getingabt);
        aboutQ1ac = findViewById(R.id.getting_about_q1_ap);
        aboutQ2ac = findViewById(R.id.getting_about_q2_ap);

        elMedicine = findViewById(R.id.expandable_layout_medicine);
        medicineQ1ac = findViewById(R.id.medicine_q1_ap);*/

        elMeaning = findViewById(R.id.expandable_layout_meaning);
        meaningQ1ac = findViewById(R.id.meaning_q1_ap);
        meaningQ2ac = findViewById(R.id.meaning_q2_ap);
        meaningQ3ac = findViewById(R.id.meaning_q3_ap);
        meaningQ4ac = findViewById(R.id.meaning_q4_ap);

        elDaily = findViewById(R.id.expandable_layout_daily);
        dailyQ1ac = findViewById(R.id.daily_q1_ap);
        dailyQ2ac = findViewById(R.id.daily_q2_ap);
        dailyQ3ac = findViewById(R.id.daily_q3_ap);

  /*      elSafeguarding = findViewById(R.id.expandable_layout_safeguard);
        safeQ1ac = findViewById(R.id.safeguarding_q1_ap);
        safeQ2ac = findViewById(R.id.safeguarding_q2_ap);
        safeQ3ac = findViewById(R.id.safeguarding_q3_ap);
        safeQ4ac = findViewById(R.id.safeguarding_q4_ap);*/

        elFinances = findViewById(R.id.expandable_layout_finances);
        financesQ1ac = findViewById(R.id.finances_q1_ap);
        financesQ2ac = findViewById(R.id.finances_q2_ap);
        financesQ3ac = findViewById(R.id.finances_q3_ap);
        financesQ4ac = findViewById(R.id.finances_q4_ap);
        financesQ5ac = findViewById(R.id.finances_q5_ap);
        financesQ6ac = findViewById(R.id.finances_q6_ap);

     /*   elLoa = findViewById(R.id.expandable_layout_loa);
        loaAc = findViewById(R.id.assessmentloc_ap);
        elAccessedBy = findViewById(R.id.expandable_layout_accessedby);
        assessedByAc = findViewById(R.id.assessbyscore_ap);
        elRiskPosition = findViewById(R.id.expandable_layout_riskassociated);
        riskPositionAc = findViewById(R.id.riskposition_ap);*/


        housingDropdown = findViewById(R.id.housing_dropdown);
        financesDropdown = findViewById(R.id.finances_dropdown);
        socialDropdown = findViewById(R.id.social_dropdown);
        mentalDropdown = findViewById(R.id.mental_dropdown);
        meaningDropdown = findViewById(R.id.meaning_dropdown);
        healthDropdown = findViewById(R.id.health_dropdown);
        practicalDropdown = findViewById(R.id.practical_dropdown);
        dailyDropdown = findViewById(R.id.daily_dropdown);
      /*  medicineDropdown = findViewById(R.id.medicine_dropdown);
        homeSupportDropdown = findViewById(R.id.homesupport_dropdown);
        economicDropdown = findViewById(R.id.economic_dropdown);
        safeguardingDropdown = findViewById(R.id.safeguarding_dropdown);
        medicalDropdown = findViewById(R.id.medical_dropdown);
        loaDropdown = findViewById(R.id.loa_dropdown);
        accessedByDropdown = findViewById(R.id.accessedby_dropdown);
        riskPositionDropdown = findViewById(R.id.riskassociated_dropdown);*/

        assessment = findViewById(R.id.assessment_number);
        housingQ1 = findViewById(R.id.housing_q1);
        housingQ2 = findViewById(R.id.housing_q2);
        housingQ3 = findViewById(R.id.housing_q3);
        housingQ4 = findViewById(R.id.housing_q4);
        housingQ5 = findViewById(R.id.housing_q5);
        housingQ6 = findViewById(R.id.housing_q6);
        housingQ7 = findViewById(R.id.housing_q7);
        housingQ8 = findViewById(R.id.housing_q8);
        housingQ9 = findViewById(R.id.housing_q9);
        housingQ10 = findViewById(R.id.housing_q10);
        housingQ11 = findViewById(R.id.housing_q11);
        housingQ12 = findViewById(R.id.housing_q12);
        housingQ1Spinner = findViewById(R.id.housing_q1_spinner);
        housingQ2Spinner = findViewById(R.id.housing_q2_spinner);
        housingQ3Spinner = findViewById(R.id.housing_q3_spinner);
        housingQ4Spinner = findViewById(R.id.housing_q4_spinner);
        housingQ5Spinner = findViewById(R.id.housing_q5_spinner);
        housingQ6Spinner = findViewById(R.id.housing_q6_spinner);
        housingQ7Spinner = findViewById(R.id.housing_q7_spinner);
        housingQ8Spinner = findViewById(R.id.housing_q8_spinner);
        housingQ9Spinner = findViewById(R.id.housing_q9_spinner);
        housingQ10Spinner = findViewById(R.id.housing_q10_spinner);
        housingQ11Spinner = findViewById(R.id.housing_q11_spinner);
        housingQ12Spinner = findViewById(R.id.housing_q12_spinner);
        housingQ1ActionPlan = findViewById(R.id.housing_q1_actionplan);
        housingQ2ActionPlan = findViewById(R.id.housing_q2_actionplan);
        housingQ3ActionPlan = findViewById(R.id.housing_q3_actionplan);
        housingQ4ActionPlan = findViewById(R.id.housing_q4_actionplan);
        housingQ5ActionPlan = findViewById(R.id.housing_q5_actionplan);
        housingQ6ActionPlan = findViewById(R.id.housing_q6_actionplan);
        housingQ7ActionPlan = findViewById(R.id.housing_q7_actionplan);
        housingQ8ActionPlan = findViewById(R.id.housing_q8_actionplan);
        housingQ9ActionPlan = findViewById(R.id.housing_q9_actionplan);
        housingQ10ActionPlan = findViewById(R.id.housing_q10_actionplan);
        housingQ11ActionPlan = findViewById(R.id.housing_q11_actionplan);
        housingQ12ActionPlan = findViewById(R.id.housing_q12_actionplan);
        housingQ1DueDate = findViewById(R.id.housing_q1_due_date);
        housingQ2DueDate = findViewById(R.id.housing_q2_due_date);
        housingQ3DueDate = findViewById(R.id.housing_q3_due_date);
        housingQ4DueDate = findViewById(R.id.housing_q4_due_date);
        housingQ5DueDate = findViewById(R.id.housing_q5_due_date);
        housingQ6DueDate = findViewById(R.id.housing_q6_due_date);
        housingQ7DueDate = findViewById(R.id.housing_q7_due_date);
        housingQ8DueDate = findViewById(R.id.housing_q8_due_date);
        housingQ9DueDate = findViewById(R.id.housing_q9_due_date);
        housingQ10DueDate = findViewById(R.id.housing_q10_due_date);
        housingQ11DueDate = findViewById(R.id.housing_q11_due_date);
        housingQ12DueDate = findViewById(R.id.housing_q12_due_date);
        housingQ1Remarks = findViewById(R.id.housing_q1_remarks);
        housingQ2Remarks = findViewById(R.id.housing_q2_remarks);
        housingQ3Remarks = findViewById(R.id.housing_q3_remarks);
        housingQ4Remarks = findViewById(R.id.housing_q4_remarks);
        housingQ5Remarks = findViewById(R.id.housing_q5_remarks);
        housingQ6Remarks = findViewById(R.id.housing_q6_remarks);
        housingQ7Remarks = findViewById(R.id.housing_q7_remarks);
        housingQ8Remarks = findViewById(R.id.housing_q8_remarks);
        housingQ9Remarks = findViewById(R.id.housing_q9_remarks);
        housingQ10Remarks = findViewById(R.id.housing_q10_remarks);
        housingQ11Remarks = findViewById(R.id.housing_q11_remarks);
        housingQ12Remarks = findViewById(R.id.housing_q12_remarks);

        meaningQ1 = findViewById(R.id.meaning_q1);
        meaningQ2 = findViewById(R.id.meaning_q2);
        meaningQ3 = findViewById(R.id.meaning_q3);
        meaningQ4 = findViewById(R.id.meaning_q4);
        meaningQ1Spinner = findViewById(R.id.meaning_q1_spinner);
        meaningQ2Spinner = findViewById(R.id.meaning_q2_spinner);
        meaningQ3Spinner = findViewById(R.id.meaning_q3_spinner);
        meaningQ4Spinner = findViewById(R.id.meaning_q4_spinner);
        meaningQ1ActionPlan = findViewById(R.id.meaning_q1_actionplan);
        meaningQ2ActionPlan = findViewById(R.id.meaning_q2_actionplan);
        meaningQ3ActionPlan = findViewById(R.id.meaning_q3_actionplan);
        meaningQ4ActionPlan = findViewById(R.id.meaning_q4_actionplan);
        meaningQ1DueDate = findViewById(R.id.meaning_q1_due_date);
        meaningQ2DueDate = findViewById(R.id.meaning_q2_due_date);
        meaningQ3DueDate = findViewById(R.id.meaning_q3_due_date);
        meaningQ4DueDate = findViewById(R.id.meaning_q4_due_date);
        meaningQ1Remarks = findViewById(R.id.meaning_q1_remarks);
        meaningQ2Remarks = findViewById(R.id.meaning_q2_remarks);
        meaningQ3Remarks = findViewById(R.id.meaning_q3_remarks);
        meaningQ4Remarks = findViewById(R.id.meaning_q4_remarks);

        socialQ1 = findViewById(R.id.social_q1);
        socialQ2 = findViewById(R.id.social_q2);
        socialq3 = findViewById(R.id.social_q3);
        socialq4 = findViewById(R.id.social_q4);
        socialq5 = findViewById(R.id.social_q5);
        socialQ6 = findViewById(R.id.social_q6);
        socialQ7 = findViewById(R.id.social_q7);
        socialQ8 = findViewById(R.id.social_q8);
        socialQ1Spinner = findViewById(R.id.social_q1_spinner);
        socialQ2Spinner = findViewById(R.id.social_q2_spinner);
        socialQ3Spinner = findViewById(R.id.social_q3_spinner);
        socialQ4Spinner = findViewById(R.id.social_q4_spinner);
        socialQ5Spinner = findViewById(R.id.social_q5_spinner);
        socialQ6Spinner = findViewById(R.id.social_q6_spinner);
        socialQ7Spinner = findViewById(R.id.social_q7_spinner);
        socialQ8Spinner = findViewById(R.id.social_q8_spinner);
        socialQ1ActionPlan = findViewById(R.id.social_q1_actionplan);
        socialQ2ActionPlan = findViewById(R.id.social_q2_actionplan);
        socialQ3ActionPlan = findViewById(R.id.social_q3_actionplan);
        socialQ4ActionPlan = findViewById(R.id.social_q4_actionplan);
        socialQ5ActionPlan = findViewById(R.id.social_q5_actionplan);
        socialQ6ActionPlan = findViewById(R.id.social_q6_actionplan);
        socialQ7ActionPlan = findViewById(R.id.social_q7_actionplan);
        socialQ8ActionPlan = findViewById(R.id.social_q8_actionplan);
        socialQ1DueDate = findViewById(R.id.social_q1_due_date);
        socialQ2DueDate = findViewById(R.id.social_q2_due_date);
        socialQ3DueDate = findViewById(R.id.social_q3_due_date);
        socialQ4DueDate = findViewById(R.id.social_q4_due_date);
        socialQ5DueDate = findViewById(R.id.social_q5_due_date);
        socialQ6DueDate = findViewById(R.id.social_q6_due_date);
        socialQ7DueDate = findViewById(R.id.social_q7_due_date);
        socialQ8DueDate = findViewById(R.id.social_q8_due_date);
        socialQ1Remarks = findViewById(R.id.social_q1_remarks);
        socialQ2Remarks = findViewById(R.id.social_q2_remarks);
        socialQ3Remarks = findViewById(R.id.social_q3_remarks);
        socialQ4Remarks = findViewById(R.id.social_q4_remarks);
        socialQ5Remarks = findViewById(R.id.social_q5_remarks);
        socialQ6Remarks = findViewById(R.id.social_q6_remarks);
        socialQ7Remarks = findViewById(R.id.social_q7_remarks);
        socialQ8Remarks = findViewById(R.id.social_q8_remarks);

        healthQ1 = findViewById(R.id.health_q1);
        healthQ2 = findViewById(R.id.health_q2);
        healthQ3 = findViewById(R.id.health_q3);
        healthQ4 = findViewById(R.id.health_q4);
        healthQ5 = findViewById(R.id.health_q5);
        healthQ6 = findViewById(R.id.health_q6);
        healthQ7 = findViewById(R.id.health_q7);
        healthQ8 = findViewById(R.id.health_q8);
        healthQ9 = findViewById(R.id.health_q9);
        healthQ10 = findViewById(R.id.health_q10);
        healthQ11 = findViewById(R.id.health_q11);
        healthQ12 = findViewById(R.id.health_q12);
        healthQ13 = findViewById(R.id.health_q13);
        healthQ14 = findViewById(R.id.health_q14);
        healthQ1Spinner = findViewById(R.id.health_q1_spinner);
        healthQ2Spinner = findViewById(R.id.health_q2_spinner);
        healthQ3Spinner = findViewById(R.id.health_q3_spinner);
        healthQ4Spinner = findViewById(R.id.health_q4_spinner);
        healthQ5Spinner = findViewById(R.id.health_q5_spinner);
        healthQ6Spinner = findViewById(R.id.health_q6_spinner);
        healthQ7Spinner = findViewById(R.id.health_q7_spinner);
        healthQ8Spinner = findViewById(R.id.health_q8_spinner);
        healthQ9Spinner = findViewById(R.id.health_q9_spinner);
        healthQ10Spinner = findViewById(R.id.health_q10_spinner);
        healthQ11Spinner = findViewById(R.id.health_q11_spinner);
        healthQ12Spinner = findViewById(R.id.health_q12_spinner);
        healthQ13Spinner = findViewById(R.id.health_q13_spinner);
        healthQ14Spinner = findViewById(R.id.health_q14_spinner);
        healthQ1ActionPlan = findViewById(R.id.health_q1_actionplan);
        healthQ2ActionPlan = findViewById(R.id.health_q2_actionplan);
        healthQ3ActionPlan = findViewById(R.id.health_q3_actionplan);
        healthQ4ActionPlan = findViewById(R.id.health_q4_actionplan);
        healthQ5ActionPlan = findViewById(R.id.health_q5_actionplan);
        healthQ6ActionPlan = findViewById(R.id.health_q6_actionplan);
        healthQ7ActionPlan = findViewById(R.id.health_q7_actionplan);
        healthQ8ActionPlan = findViewById(R.id.health_q8_actionplan);
        healthQ9ActionPlan = findViewById(R.id.health_q9_actionplan);
        healthQ10ActionPlan = findViewById(R.id.health_q10_actionplan);
        healthQ11ActionPlan = findViewById(R.id.health_q11_actionplan);
        healthQ12ActionPlan = findViewById(R.id.health_q12_actionplan);
        healthQ13ActionPlan = findViewById(R.id.health_q13_actionplan);
        healthQ14ActionPlan = findViewById(R.id.health_q14_actionplan);
        healthQ1DueDate = findViewById(R.id.health_q1_due_date);
        healthQ2DueDate = findViewById(R.id.health_q2_due_date);
        healthQ3DueDate = findViewById(R.id.health_q3_due_date);
        healthQ4DueDate = findViewById(R.id.health_q4_due_date);
        healthQ5DueDate = findViewById(R.id.health_q5_due_date);
        healthQ6DueDate = findViewById(R.id.health_q6_due_date);
        healthQ7DueDate = findViewById(R.id.health_q7_due_date);
        healthQ8DueDate = findViewById(R.id.health_q8_due_date);
        healthQ9DueDate = findViewById(R.id.health_q9_due_date);
        healthQ10DueDate = findViewById(R.id.health_q10_due_date);
        healthQ11DueDate = findViewById(R.id.health_q11_due_date);
        healthQ12DueDate = findViewById(R.id.health_q12_due_date);
        healthQ13DueDate = findViewById(R.id.health_q13_due_date);
        healthQ14DueDate = findViewById(R.id.health_q14_due_date);
        healthQ1Remarks = findViewById(R.id.health_q1_remarks);
        healthQ2Remarks = findViewById(R.id.health_q2_remarks);
        healthQ3Remarks = findViewById(R.id.health_q3_remarks);
        healthQ4Remarks = findViewById(R.id.health_q4_remarks);
        healthQ5Remarks = findViewById(R.id.health_q5_remarks);
        healthQ6Remarks = findViewById(R.id.health_q6_remarks);
        healthQ7Remarks = findViewById(R.id.health_q7_remarks);
        healthQ8Remarks = findViewById(R.id.health_q8_remarks);
        healthQ9Remarks = findViewById(R.id.health_q9_remarks);
        healthQ10Remarks = findViewById(R.id.health_q10_remarks);
        healthQ11Remarks = findViewById(R.id.health_q11_remarks);
        healthQ12Remarks = findViewById(R.id.health_q12_remarks);
        healthQ13Remarks = findViewById(R.id.health_q13_remarks);
        healthQ14Remarks = findViewById(R.id.health_q14_remarks);

        mentalQ1 = findViewById(R.id.mental_q1);
        mentalQ2 = findViewById(R.id.mental_q2);
        mentalQ1Spinner = findViewById(R.id.mental_q1_spinner);
        mentalQ2Spinner = findViewById(R.id.mental_q2_spinner);
        mentalQ1ActionPlan = findViewById(R.id.mental_q1_actionplan);
        mentalQ2ActionPlan = findViewById(R.id.mental_q2_actionplan);
        mentalQ1DueDate = findViewById(R.id.mental_q1_due_date);
        mentalQ2DueDate = findViewById(R.id.mental_q2_due_date);
        mentalQ1Remarks = findViewById(R.id.mental_q1_remarks);
        mentalQ2Remarks = findViewById(R.id.mental_q2_remarks);

        practicalQ1 = findViewById(R.id.practical_q1);
        practicalQ2 = findViewById(R.id.practical_q2);
        practicalQ3 = findViewById(R.id.practical_q3);
        practicalQ4 = findViewById(R.id.practical_q4);
        practicalQ5 = findViewById(R.id.practical_q5);
        practicalQ6 = findViewById(R.id.practical_q6);
        practicalQ7 = findViewById(R.id.practical_q7);
        practicalQ1Spinner = findViewById(R.id.practical_q1_spinner);
        practicalQ2Spinner = findViewById(R.id.practical_q2_spinner);
        practicalQ3Spinner = findViewById(R.id.practical_q3_spinner);
        practicalQ4Spinner = findViewById(R.id.practical_q4_spinner);
        practicalQ5Spinner = findViewById(R.id.practical_q5_spinner);
        practicalQ6Spinner = findViewById(R.id.practical_q6_spinner);
        practicalQ7Spinner = findViewById(R.id.practical_q7_spinner);
        practicalQ1ActionPlan = findViewById(R.id.practical_q1_actionplan);
        practicalQ2ActionPlan = findViewById(R.id.practical_q2_actionplan);
        practicalQ3ActionPlan = findViewById(R.id.practical_q3_actionplan);
        practicalQ4ActionPlan = findViewById(R.id.practical_q4_actionplan);
        practicalQ5ActionPlan = findViewById(R.id.practical_q5_actionplan);
        practicalQ6ActionPlan = findViewById(R.id.practical_q6_actionplan);
        practicalQ7ActionPlan = findViewById(R.id.practical_q7_actionplan);
        practicalQ1DueDate = findViewById(R.id.practical_q1_due_date);
        practicalQ2DueDate = findViewById(R.id.practical_q2_due_date);
        practicalQ3DueDate = findViewById(R.id.practical_q3_due_date);
        practicalQ4DueDate = findViewById(R.id.practical_q4_due_date);
        practicalQ5DueDate = findViewById(R.id.practical_q5_due_date);
        practicalQ6DueDate = findViewById(R.id.practical_q6_due_date);
        practicalQ7DueDate = findViewById(R.id.practical_q7_due_date);
        practicalQ1Remarks = findViewById(R.id.practical_q1_remarks);
        practicalQ2Remarks = findViewById(R.id.practical_q2_remarks);
        practicalQ3Remarks = findViewById(R.id.practical_q3_remarks);
        practicalQ4Remarks = findViewById(R.id.practical_q4_remarks);
        practicalQ5Remarks = findViewById(R.id.practical_q5_remarks);
        practicalQ6Remarks = findViewById(R.id.practical_q6_remarks);
        practicalQ7Remarks = findViewById(R.id.practical_q7_remarks);
/*
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
        medicineQ1Remarks = findViewById(R.id.medicine_q1_remarks);*/

      /*  supportQ1 = findViewById(R.id.homesupport_q1);
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
        supportQ4Remarks = findViewById(R.id.homesupport_q4_remarks);*/


        dailyQ1 = findViewById(R.id.daily_q1);
        dailyQ2 = findViewById(R.id.daily_q2);
        dailyQ3 = findViewById(R.id.daily_q3);
        dailyQ1Spinner = findViewById(R.id.daily_q1_spinner);
        dailyQ2Spinner = findViewById(R.id.daily_q2_spinner);
        dailyQ3Spinner = findViewById(R.id.daily_q3_spinner);
        dailyQ1ActionPlan = findViewById(R.id.daily_q1_actionplan);
        dailyQ2ActionPlan = findViewById(R.id.daily_q2_actionplan);
        dailyQ3ActionPlan = findViewById(R.id.daily_q3_actionplan);
        dailyQ1DueDate = findViewById(R.id.daily_q1_due_date);
        dailyQ2DueDate = findViewById(R.id.daily_q2_due_date);
        dailyQ3DueDate = findViewById(R.id.daily_q3_due_date);
        dailyQ1Remarks = findViewById(R.id.daily_q1_remarks);
        dailyQ2Remarks = findViewById(R.id.daily_q2_remarks);
        dailyQ3Remarks = findViewById(R.id.daily_q3_remarks);


/*        safeQ1 = findViewById(R.id.safeguarding_q1);
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
        safeQ4Remarks = findViewById(R.id.safeguarding_q4_remarks);*/

        financesQ1 = findViewById(R.id.finances_q1);
        financesQ2 = findViewById(R.id.finances_q2);
        financesQ3 = findViewById(R.id.finances_q3);
        financesQ4 = findViewById(R.id.finances_q4);
        financesQ5 = findViewById(R.id.finances_q5);
        financesQ6 = findViewById(R.id.finances_q6);
        financesQ1Spinner = findViewById(R.id.finances_q1_spinner);
        financesQ2Spinner = findViewById(R.id.finances_q2_spinner);
        financesQ3Spinner = findViewById(R.id.finances_q3_spinner);
        financesQ4Spinner = findViewById(R.id.finances_q4_spinner);
        financesQ5Spinner = findViewById(R.id.finances_q5_spinner);
        financesQ6Spinner = findViewById(R.id.finances_q6_spinner);
        financesQ1ActionPlan = findViewById(R.id.finances_q1_actionplan);
        financesQ2ActionPlan = findViewById(R.id.finances_q2_actionplan);
        financesQ3ActionPlan = findViewById(R.id.finances_q3_actionplan);
        financesQ4ActionPlan = findViewById(R.id.finances_q4_actionplan);
        financesQ5ActionPlan = findViewById(R.id.finances_q5_actionplan);
        financesQ6ActionPlan = findViewById(R.id.finances_q6_actionplan);
        financesQ1DueDate = findViewById(R.id.finances_q1_due_date);
        financesQ2DueDate = findViewById(R.id.finances_q2_due_date);
        financesQ3DueDate = findViewById(R.id.finances_q3_due_date);
        financesQ4DueDate = findViewById(R.id.finances_q4_due_date);
        financesQ5DueDate = findViewById(R.id.finances_q5_due_date);
        financesQ6DueDate = findViewById(R.id.finances_q6_due_date);
        financesQ1Remarks = findViewById(R.id.finances_q1_remarks);
        financesQ2Remarks = findViewById(R.id.finances_q2_remarks);
        financesQ3Remarks = findViewById(R.id.finances_q3_remarks);
        financesQ4Remarks = findViewById(R.id.finances_q4_remarks);
        financesQ5Remarks = findViewById(R.id.finances_q5_remarks);
        financesQ6Remarks = findViewById(R.id.finances_q6_remarks);

/*        assessmentLocation = findViewById(R.id.assessmentloc_spinner);
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
        riskPositionRemarks = findViewById(R.id.riskposition_remarks);*/

        done = findViewById(R.id.btn_done);

    }

    @Override
    public void responseSuccess(Response response) {

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
      /*  progressDialog = new ProgressDialog(AssessmentQuestions.this);
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
                Log.e("AssessmentQuestions", t.toString());
                Toast.makeText(AssessmentQuestions.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                 *//*   if (!(response.isSuccessful())) {
                        Toast.makeText(AssessmentQuestions.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }*//*
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
                call.cancel();*/

              /*  AssessmentQuestions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {*/
//                        progressDialog.dismiss();

        ArrayAdapter<Object> adapt_plans_housing = new ArrayAdapter<Object>(AssessmentQuestions.this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_housing) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_finances = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_finances) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_social = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_social) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_mental = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_mental) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_meaning = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_meaning) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_health = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_health) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_practical = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_practical) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };

        ArrayAdapter<String> adapt_plans_daily = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinner_items_actionplan_daily) {
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
                housingQ1ActionPlan.setAdapter(adapt_plans_housing);
                housingQ2ActionPlan.setAdapter(adapt_plans_housing);
                housingQ3ActionPlan.setAdapter(adapt_plans_housing);
                housingQ4ActionPlan.setAdapter(adapt_plans_housing);
                housingQ5ActionPlan.setAdapter(adapt_plans_housing);
                housingQ6ActionPlan.setAdapter(adapt_plans_housing);
                housingQ7ActionPlan.setAdapter(adapt_plans_housing);
                housingQ8ActionPlan.setAdapter(adapt_plans_housing);
                housingQ9ActionPlan.setAdapter(adapt_plans_housing);
                housingQ10ActionPlan.setAdapter(adapt_plans_housing);
                housingQ11ActionPlan.setAdapter(adapt_plans_housing);
                housingQ12ActionPlan.setAdapter(adapt_plans_housing);
//                list.clear();
                break;

            case "2":
                financesQ1ActionPlan.setAdapter(adapt_plans_finances);
                financesQ2ActionPlan.setAdapter(adapt_plans_finances);
                financesQ3ActionPlan.setAdapter(adapt_plans_finances);
                financesQ4ActionPlan.setAdapter(adapt_plans_finances);
                financesQ5ActionPlan.setAdapter(adapt_plans_finances);
                financesQ6ActionPlan.setAdapter(adapt_plans_finances);
//                list.clear();

                break;

            case "3":
                socialQ1ActionPlan.setAdapter(adapt_plans_social);
                socialQ2ActionPlan.setAdapter(adapt_plans_social);
                socialQ3ActionPlan.setAdapter(adapt_plans_social);
                socialQ4ActionPlan.setAdapter(adapt_plans_social);
                socialQ5ActionPlan.setAdapter(adapt_plans_social);
                socialQ6ActionPlan.setAdapter(adapt_plans_social);
                socialQ7ActionPlan.setAdapter(adapt_plans_social);
                socialQ8ActionPlan.setAdapter(adapt_plans_social);
//                list.clear();
                break;

            case "4":
                mentalQ1ActionPlan.setAdapter(adapt_plans_mental);
                mentalQ2ActionPlan.setAdapter(adapt_plans_mental);
//                list.clear();

                break;

            case "5":
                meaningQ1ActionPlan.setAdapter(adapt_plans_meaning);
                meaningQ2ActionPlan.setAdapter(adapt_plans_meaning);
                meaningQ3ActionPlan.setAdapter(adapt_plans_meaning);
                meaningQ4ActionPlan.setAdapter(adapt_plans_meaning);
//                list.clear();
                break;

            case "6":
                healthQ1ActionPlan.setAdapter(adapt_plans_health);
                healthQ2ActionPlan.setAdapter(adapt_plans_health);
                healthQ3ActionPlan.setAdapter(adapt_plans_health);
                healthQ4ActionPlan.setAdapter(adapt_plans_health);
                healthQ5ActionPlan.setAdapter(adapt_plans_health);
                healthQ6ActionPlan.setAdapter(adapt_plans_health);
                healthQ7ActionPlan.setAdapter(adapt_plans_health);
                healthQ8ActionPlan.setAdapter(adapt_plans_health);
                healthQ9ActionPlan.setAdapter(adapt_plans_health);
                healthQ10ActionPlan.setAdapter(adapt_plans_health);
                healthQ11ActionPlan.setAdapter(adapt_plans_health);
                healthQ12ActionPlan.setAdapter(adapt_plans_health);
                healthQ13ActionPlan.setAdapter(adapt_plans_health);
                healthQ14ActionPlan.setAdapter(adapt_plans_health);
//                list.clear();

                break;

            case "7":
                practicalQ1ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ2ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ3ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ4ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ5ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ6ActionPlan.setAdapter(adapt_plans_practical);
                practicalQ7ActionPlan.setAdapter(adapt_plans_practical);
//                list.clear();
                break;

            case "8":
                dailyQ1ActionPlan.setAdapter(adapt_plans_daily);
                dailyQ2ActionPlan.setAdapter(adapt_plans_daily);
                dailyQ3ActionPlan.setAdapter(adapt_plans_daily);
//                list.clear();
                break;

                          /*  case "9":
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

                                break;

                            case "12":
                                safeQ1ActionPlan.setAdapter(adapt_plans);
                                safeQ2ActionPlan.setAdapter(adapt_plans);
                                safeQ3ActionPlan.setAdapter(adapt_plans);
                                safeQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "13":

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
                                break;*/
        }

               /*     }

                });*/
                
             /*   AssessmentQuestions.this.runOnUiThread(new Runnable() {
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
                                housingQ1ActionPlan.setAdapter(adapt_plans);
                                housingQ2ActionPlan.setAdapter(adapt_plans);
                                housingQ3ActionPlan.setAdapter(adapt_plans);
                                housingQ4ActionPlan.setAdapter(adapt_plans);
                                housingQ5ActionPlan.setAdapter(adapt_plans);
                                housingQ6ActionPlan.setAdapter(adapt_plans);
                                housingQ7ActionPlan.setAdapter(adapt_plans);
                                housingQ8ActionPlan.setAdapter(adapt_plans);
                                housingQ9ActionPlan.setAdapter(adapt_plans);
                                housingQ10ActionPlan.setAdapter(adapt_plans);
                                housingQ11ActionPlan.setAdapter(adapt_plans);
                                housingQ12ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "2":
                                financesQ1ActionPlan.setAdapter(adapt_plans);
                                financesQ2ActionPlan.setAdapter(adapt_plans);
                                financesQ3ActionPlan.setAdapter(adapt_plans);
                                financesQ4ActionPlan.setAdapter(adapt_plans);
                                financesQ5ActionPlan.setAdapter(adapt_plans);
                                financesQ6ActionPlan.setAdapter(adapt_plans);
                                list.clear();

                                break;

                            case "3":
                                socialQ1ActionPlan.setAdapter(adapt_plans);
                                socialQ2ActionPlan.setAdapter(adapt_plans);
                                socialQ3ActionPlan.setAdapter(adapt_plans);
                                socialQ4ActionPlan.setAdapter(adapt_plans);
                                socialQ5ActionPlan.setAdapter(adapt_plans);
                                socialQ6ActionPlan.setAdapter(adapt_plans);
                                socialQ7ActionPlan.setAdapter(adapt_plans);
                                socialQ8ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "4":
                                mentalQ1ActionPlan.setAdapter(adapt_plans);
                                mentalQ2ActionPlan.setAdapter(adapt_plans);
                                list.clear();

                                break;

                            case "5":
                                meaningQ1ActionPlan.setAdapter(adapt_plans);
                                meaningQ2ActionPlan.setAdapter(adapt_plans);
                                meaningQ3ActionPlan.setAdapter(adapt_plans);
                                meaningQ4ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "6":
                                healthQ1ActionPlan.setAdapter(adapt_plans);
                                healthQ2ActionPlan.setAdapter(adapt_plans);
                                healthQ3ActionPlan.setAdapter(adapt_plans);
                                healthQ4ActionPlan.setAdapter(adapt_plans);
                                healthQ5ActionPlan.setAdapter(adapt_plans);
                                healthQ6ActionPlan.setAdapter(adapt_plans);
                                healthQ7ActionPlan.setAdapter(adapt_plans);
                                healthQ8ActionPlan.setAdapter(adapt_plans);
                                healthQ9ActionPlan.setAdapter(adapt_plans);
                                healthQ10ActionPlan.setAdapter(adapt_plans);
                                healthQ11ActionPlan.setAdapter(adapt_plans);
                                healthQ12ActionPlan.setAdapter(adapt_plans);
                                healthQ13ActionPlan.setAdapter(adapt_plans);
                                healthQ14ActionPlan.setAdapter(adapt_plans);
                                list.clear();

                                break;

                            case "7":
                                practicalQ1ActionPlan.setAdapter(adapt_plans);
                                practicalQ2ActionPlan.setAdapter(adapt_plans);
                                practicalQ3ActionPlan.setAdapter(adapt_plans);
                                practicalQ4ActionPlan.setAdapter(adapt_plans);
                                practicalQ5ActionPlan.setAdapter(adapt_plans);
                                practicalQ6ActionPlan.setAdapter(adapt_plans);
                                practicalQ7ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;

                            case "8":
                                dailyQ1ActionPlan.setAdapter(adapt_plans);
                                dailyQ2ActionPlan.setAdapter(adapt_plans);
                                dailyQ3ActionPlan.setAdapter(adapt_plans);
                                list.clear();
                                break;
                        }

                    }

                });*/

    }
/*});
        }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }
}
