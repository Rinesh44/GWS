package com.example.android.gurkha;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.helpers.InputValidation;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final AppCompatActivity activity = MainActivity.this;
    private Button appCompatButtonLogin;
    private TextView textViewLinkRegister;
    private TextView forgotpassword, title;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private LinearLayoutCompat root;
    private InputValidation inputValidation;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private Boolean exit = false;
    private ProfileTracker profileTracker;
    private static final String url = "http://pagodalabs.com.np/gws/auth/api/login";
    private static final String fbUrl = "http://pagodalabs.com.np/gws/auth/api/account";
    static String awcName;
    static String checkAdmin;
    static String mEmail, fbUserId;
    String fbEmail, fbId, fbFirstName, fbLastName, finalName, fbAwc;
    public static final String change_password_url = "http://pagodalabs.com.np/gws/auth/api/forget";
    String sendName;
    SearchableSpinner selectAwc;
    static SessionManager session;
    FbSessionManager fbSession;
    public static boolean normalLogin = false;
    public static boolean fbLogin = false;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            final Profile profile = Profile.getCurrentProfile();

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login

                    Bundle bFacebookData = getFacebookData(object);
                    if (bFacebookData != null) {
                        fbEmail = bFacebookData.get("email").toString();
                        fbFirstName = bFacebookData.get("first_name").toString();
                        fbLastName = bFacebookData.get("last_name").toString();
                        fbId = bFacebookData.get("idFacebook").toString();
                        //fbAwc = selectAwc.getSelectedItem().toString();
                        finalName = fbFirstName + " " + fbLastName;
                    } else
                        Toast.makeText(activity, "facebook data not found", Toast.LENGTH_SHORT).show();
                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        //---get the IMEI number---
                        String facebookImei = tm.getDeviceId();

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("facebook_name", finalName);
                    params.put("facebook_email", fbEmail);
                    params.put("facebook_id", fbId);
                    params.put("facebook_imei", facebookImei);
                    //params.put("facebook_awc", fbAwc);

                    JSONObject parameter = new JSONObject(params);
                    Log.e("JSON:", parameter.toString());
                    OkHttpClient client = new OkHttpClient();
                    final RequestBody body = RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(fbUrl)
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
                                Log.e("postResponse:", responseString);

                                if (responseString.contains("Success")) {
                                    try {
                                        JSONObject jsonObj = new JSONObject(responseString);
                                        JSONArray data = jsonObj.getJSONArray("user_data");
                                        JSONObject c = data.getJSONObject(0);
                                        checkAdmin = c.getString("id");
                                        awcName = c.getString("awc");
                                        sendName = c.getString("name");
                                        fbSession.createLoginSession(checkAdmin, sendName, fbId);
                                        normalLogin = false;
                                        fbLogin = true;
                                        if (fbUserId != null) {
                                            Log.i("fbUserId:", fbUserId);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Intent fbLogin = new Intent(activity, Menu.class);
                                    getIntent().removeExtra("startTest");
                                    fbLogin.putExtra("profile_picID", fbId);
                                    fbLogin.putExtra("name", sendName);
                                    startActivity(fbLogin);
                                    finish();
                                } else
                                    Snackbar.make(root, getString(R.string.facebook_login_error), Snackbar.LENGTH_LONG).show();
                                    LoginManager.getInstance().logOut();
                                // emptyInputEditText();
                            } else
                                Snackbar.make(root, "Unable to get response", Snackbar.LENGTH_LONG).show();
                        }
                    });

                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // parameters
            request.setParameters(parameters);
            request.executeAsync();
            //finish();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(activity, "Error Connecting to Facebook, Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setView(R.layout.fb_awc);
        builder.setTitle("Only for Facebook Login");

        final AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(activity, "Cancel if you want to use default login", Toast.LENGTH_LONG).show();

        Button ok = (Button) dialog.findViewById(R.id.btn);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        selectAwc = (SearchableSpinner) dialog.findViewById(R.id.selectAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktal", "Bhojpur", "Khandbari", "Tehradhun", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, awc_items);
        selectAwc.setAdapter(adapt_awc);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
*/

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // updateWithToken(AccessToken.getCurrentAccessToken());

        //Session Manager
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        fbSession = new FbSessionManager(getApplicationContext());
        fbSession.checkLogin();

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/semi.otf");
        tv.setTypeface(face);

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                // updateWithToken(newToken);
            }
        };

        initViews();
        initListeners();
        initObjects();

/*
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("facebook_name", finalName);
                    params.put("facebook_id", fbId);
                    params.put("facebook_email", fbEmail);

                    JSONObject parameter = new JSONObject(params);
                    Log.e("JSON:", parameter.toString());
                    OkHttpClient client = new OkHttpClient();
                    final RequestBody body = RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(fbUrl)
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

                                if (responseString.contains("Success")) {
                                    Intent login = new Intent(activity, Menu.class);
                                    login.putExtra("userName", textInputEditTextEmail.toString());
                                    getIntent().removeExtra("startTest");
                                    startActivity(login);
                                    finish();
                                } else
                                    Snackbar.make(root, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                                // emptyInputEditText();

                            }
                        }
                    });



                } else {
                    Toast.makeText(MainActivity.this, "Sorry internet connection is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
*/

    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Intent i = new Intent(MainActivity.this, Menu.class);
            getIntent().removeExtra("startTest");
            startActivity(i);
            finish();
        }
    }

    private void initViews() {
        root = (LinearLayoutCompat) findViewById(R.id.rootview);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        forgotpassword = (TextView) findViewById(R.id.forgot);
        textViewLinkRegister = (TextView) findViewById(R.id.textViewLinkRegister);
        appCompatButtonLogin = (Button) findViewById(R.id.appCompatButtonLogin);
        loginButton = (LoginButton) findViewById(R.id.login_button);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
        forgotpassword.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        inputValidation = new InputValidation(activity);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromWeb();
                break;

            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.forgot:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgotpassword);
                dialog.show();

                final EditText email = (EditText) dialog.findViewById(R.id.email);
                final EditText getpass = (EditText) dialog.findViewById(R.id.newpassword);
                final EditText confirm = (EditText) dialog.findViewById(R.id.confirm);

                Button ok = (Button) dialog.findViewById(R.id.ok);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value1 = getpass.getText().toString().trim();
                        String value2 = confirm.getText().toString().trim();
                        String fixed;
                        String userName = email.getText().toString();
                        if (userName.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                            Toast.makeText(getApplicationContext(), "Invalid EmailID!", Toast.LENGTH_SHORT).show();
                        } else if (!value1.contentEquals(value2)) {
                            Toast.makeText(getApplicationContext(), "Passwords Doesnt Match!", Toast.LENGTH_SHORT).show();
                        } else if (value1.isEmpty() || value2.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();

                        } else if (InternetConnection.checkConnection(getApplicationContext())) {

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("email", email.getText().toString());
                            params.put("password", value2);

                            JSONObject parameter = new JSONObject(params);
                            Log.e("JSON:", parameter.toString());

                            OkHttpClient client = new OkHttpClient();

                            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                            Request request = new Request.Builder()
                                    .url(change_password_url)
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
                                        String responseString = response.body().string();
                                        Log.e("changePasswordResponse", responseString);
                                        if (responseString.contains("Success")) {
                                            Snackbar.make(root, getString(R.string.changed), Snackbar.LENGTH_LONG).show();
                                        } else
                                            Snackbar.make(root, getString(R.string.not_changed), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                            dialog.dismiss();

                        } else {
                            Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

        }
    }

    private void verifyFromWeb() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

        if (InternetConnection.checkConnection(getApplicationContext())) {
            Toast.makeText(activity, "Please Wait", Toast.LENGTH_SHORT).show();
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            //---get the IMEI number---
            String IMEI = tm.getDeviceId();
            mEmail = textInputEditTextEmail.getText().toString().trim();
            String mPassword = textInputEditTextPassword.getText().toString().trim();
            // String mId = String.valueOf(databaseHelper.getId(textInputEditTextEmail.getText().toString().trim()));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> params = new HashMap<String, String>();
            params.put("imei_number", IMEI);
            params.put("email", mEmail);
            params.put("password", mPassword);

            JSONObject parameter = new JSONObject(params);
            Log.e("JSON:", parameter.toString());
            OkHttpClient client = new OkHttpClient();
            final RequestBody body = RequestBody.create(JSON, parameter.toString());
            Request request = new Request.Builder()
                    .url(url)
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
                        try {
                            JSONObject jsonObj = new JSONObject(responseString);

                            // Getting JSON Array node
                            JSONArray data = jsonObj.getJSONArray("user_data");
                            JSONObject c = data.getJSONObject(0);
                            awcName = c.getString("awc");
                            checkAdmin = c.getString("id");
                            Log.e("finalAwc", awcName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("postResponse:", responseString);
                        if (responseString.contains("Success")) {

                            session.createLoginSession(checkAdmin, mEmail);
                            normalLogin = true;
                            fbLogin = false;
                            Intent login = new Intent(activity, Menu.class);
                            Log.e("underResponseSuccess:", "True");
                            login.putExtra("userName", textInputEditTextEmail.toString());
                            getIntent().removeExtra("startTest");
                            startActivity(login);
                            Intent runService = new Intent(MainActivity.this, Service.class);
                            startService(runService);
                            finish();
                        } else
                            Snackbar.make(root, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                            // emptyInputEditText();
                    }
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "Sorry internet connection is required.", Toast.LENGTH_SHORT).show();
        }
        //session.createLoginSession(textInputEditTextEmail.getText().toString().trim(), textInputEditTextPassword.getText().toString().trim());

    }

    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LoginManager.getInstance().logOut();
            startActivity(a);
            // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }


    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email")) {
                bundle.putString("email", object.getString("email"));
            } else
                bundle.putString("email", "");
            if (object.has("password"))
                bundle.putString("password", object.getString("password"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }

}


