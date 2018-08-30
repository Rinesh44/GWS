package com.example.android.gurkha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.helpers.InputValidation;
import com.example.android.gurkha.modal.User;
import com.example.android.gurkha.sql.DatabaseHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;
    private Toolbar toolbar;
    private LinearLayoutCompat root;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutContactNo;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextContactNo;
    private TextInputEditText textInputEditTextUsername;
    private TextInputEditText textInputEditTextConfirmPassword;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;
    private SpinnerAdapter districtAdapter;
    private SearchableSpinner awc;
    private ArrayList<String> list;
    TextView hiddenText;
    String token;
    SessionManager sessionManager;
    private static final String url = "http://pagodalabs.com.np/gws/auth/api/register?api_token=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.registration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView tv = (TextView) findViewById(R.id.register);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        tv.setTypeface(face);

        initViews();
        initListeners();
        initObjects();
/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new RegisterActivity.GetDataTask().execute();
        } else {
            Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
        */
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        root = (LinearLayoutCompat) findViewById(R.id.rootview);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutContactNo = (TextInputLayout) findViewById(R.id.textInputLayoutContactNo);
        textInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutUserName);


        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);
        textInputEditTextContactNo = (TextInputEditText) findViewById(R.id.textInputEditTextContactNo);
        textInputEditTextUsername = (TextInputEditText) findViewById(R.id.textInputEditTextUserName);

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items);
        awc.setAdapter(adapt_awc);

        list = new ArrayList<>();
        hiddenText = (TextView) findViewById(R.id.hiddenGroup);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        inputValidation = new InputValidation(activity);
        sessionManager = new SessionManager(getApplicationContext());
        //databaseHelper = new DatabaseHelper(activity);

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                postData();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void postData() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextUsername, textInputLayoutUsername, "Enter Username")) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextContactNo, textInputLayoutContactNo, getString(R.string.error_message_contact_no))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

                if (InternetConnection.checkConnection(getApplicationContext())) {
/*
                user.setName(textInputEditTextName.getText().toString().trim());
                user.setEmail(textInputEditTextEmail.getText().toString().trim());
                user.setPassword(textInputEditTextPassword.getText().toString().trim());
                user.setContactNo(textInputEditTextContactNo.getText().toString().trim());
                user.setDistrictName(awc.getSelectedItem().toString().trim());

*/
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                //---get the IMEI number---
                String IMEI = tm.getDeviceId();
                String mName = textInputEditTextName.getText().toString().trim();
                String mContact_no = textInputEditTextContactNo.getText().toString().trim();
                String mAwc = awc.getSelectedItem().toString().trim();
                String mEmail = textInputEditTextEmail.getText().toString().trim();
                String mPassword = textInputEditTextPassword.getText().toString().trim();
                String hiddenValue = hiddenText.getText().toString().trim();
                String mUsername = textInputEditTextUsername.getText().toString().trim();
                // String mId = String.valueOf(databaseHelper.getId(textInputEditTextEmail.getText().toString().trim()));

                    if(mAwc.matches("Select Area Welfare Center")) {
                        Snackbar.make(root, getString(R.string.select_awc), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> params = new HashMap<String, String>();
                params.put("imei_number", IMEI);
                params.put("name", mName);
                params.put("contact_no", mContact_no);
                params.put("awc", mAwc);
                params.put("email", mEmail);
                params.put("password", mPassword);
                params.put("group", hiddenValue);
                params.put("username", mUsername);
                //params.put("id", mId);

                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                JSONObject parameter = new JSONObject(params);
                Log.e("JSON:", parameter.toString());
                OkHttpClient client = new OkHttpClient();
                final RequestBody body = RequestBody.create(JSON, parameter.toString());
                Request request = new Request.Builder()
                        .url(url + token)
                        .post(body)
                        .addHeader("content-type", "application/json; charset=utf-8")
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("FailureResponse", call.request().body().toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e("postResponse:", responseString);
                        }
                    }

                });

                // Snack Bar to show success message that record saved successfully
                Snackbar.make(root, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
                emptyInputEditText();
            } else {
                Toast.makeText(RegisterActivity.this, "Sorry internet connection is required.", Toast.LENGTH_SHORT).show();
            }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
        textInputEditTextContactNo.setText(null);
        textInputEditTextUsername.setText(null);
    }


/*
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction

            dialog = new ProgressDialog(RegisterActivity.this);
            dialog.setTitle("Please Wait...");
            dialog.setMessage("Getting Information");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp

            JSONObject jsonObject = JSONParser.getCAData();
            try {
                /**
                 * Check Whether Its NULL???

                if (jsonObject != null) {
                    /**
                     * Check Length...

                    if (jsonObject.length() > 0) {
                        /**
                         * Getting Array named "personal" From MAIN Json Object

                        JSONArray array = jsonObject.getJSONArray("District");

                        /**
                         * Check Length of Array...

                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (int jIndex = 0; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */


                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *

                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String name = innerObject.getString("district");

                                /**
                                 * Adding name and phone concatenation in List...

                                list.add(name);

                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            // districtAdapter = new SpinnerAdapter(RegisterActivity.this,list);
            //awc.setAdapter(districtAdapter);
            cancel(true);
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            /*
            if(list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Add_investigation.this, "No Data Found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
    */


}

