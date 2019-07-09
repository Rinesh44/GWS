package com.example.android.gurkha;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.activities.PensionerRiskAssessment.SendPersonId;
import com.example.android.gurkha.activities.QnA.EconomicPoll;
import com.example.android.gurkha.activities.QnA.HealthPoll;
import com.example.android.gurkha.activities.QnA.Questions;
import com.example.android.gurkha.activities.QnA.SocialPoll;
import com.example.android.gurkha.app.Config.Config;
import com.example.android.gurkha.utils.NotificationUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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


public class Menu extends AppCompatActivity {
    private static final String TAG = Menu.class.getSimpleName();
    private static String base_url = "http://gws.pagodalabs.com.np/";
    private Toolbar toolbar;
    public static String textName;
    public static String numAge;
    public static String textAddress;
    public static String numlatitude;
    public static String numlongitude;
    public static String onlyName;
    private Boolean exit = false;
    Boolean gps_enabled;
    public static boolean marker;
    private TextView topleft, topright, middleleft, middleright, bottomright, bottom, bottom2, scanner,
            poll, risk_update, psa, addPhotos;
    private static final int REQUEST_PERMISSIONS = 123;
    SessionManager session;
    FbSessionManager fbSessionManager;
    int PERMISSION_ALL = 1;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;
    private TextView gallery;
    private Handler mHandler;
    private Runnable mRunnable;
    private String token;
    public static OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }


        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        tv.setTypeface(face);

        session = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

   /*     txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);*/

     /*   if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Menu.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    112);
        }*/

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps_enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!hasPermissions(this, PERMISSIONS_STORAGE)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, PERMISSION_ALL);
        }


        if (session.getUserDetails() != null) {
            HashMap<String, String> user = session.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }


        mHandler = new Handler();
//        callAllAPIs();





/*        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

//                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText(message);
                }
            }
        };*/

//        displayFirebaseRegId();

        topleft = (TextView) findViewById(R.id.top_left);
        topleft.setTypeface(face);
        topleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Menu.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.select_search_type);
                dialog.show();

//                    Button direct = (Button) dialog.findViewById(R.id.direct);
                Button searchByAWC = (Button) dialog.findViewById(R.id.search_by_awc);
                Button searchNationWide = (Button) dialog.findViewById(R.id.seach_nationwide);

                searchByAWC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Menu.this, SelectAwc.class));
                        dialog.dismiss();
                    }
                });

                searchNationWide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (session.getUserDetails() != null) {
                            Log.e(TAG, "userDetailsNotNull");
                            HashMap<String, String> user = session.getUserDetails();
                            String id = user.get(SessionManager.KEY_ID);
                            Log.e(TAG, "printUserId:" + id);
                            if (id != null) {
                                Intent list = new Intent(Menu.this, Category.class);
                                startActivity(list);

                            }
                        } else {
                            Toast.makeText(Menu.this, "User Details Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



/*
                if (fbSessionManager.getUserDetails() != null) {

                    HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                    String fbId = fbUser.get(FbSessionManager.KEY_ID);

                    if (fbId != null) {
                        if (fbId.matches("1")) {
                            Intent ifAdmin = new Intent(Menu.this, Category.class);

                            if (Build.VERSION.SDK_INT > 22) {
                                if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                                } else
                                    startActivity(ifAdmin);
                            } else

                                startActivity(ifAdmin);
                        } else {
                            Intent list = new Intent(Menu.this, SelectAwc.class);

                            if (Build.VERSION.SDK_INT > 22) {
                                if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                                } else
                                    startActivity(list);
                            } else

                                startActivity(list);
                        }
                    }
                }*/

            }
        });

        topright = (TextView) findViewById(R.id.top_right);
        topright.setTypeface(face);
        topright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (locationProviders == null || locationProviders.equals("")) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    Toast.makeText(Menu.this, "Please enable location", Toast.LENGTH_SHORT).show();
                } else {
                    Intent lastVisit = new Intent(Menu.this, LastVisit.class);
                    startActivity(lastVisit);
                }
            }
        });

        psa = findViewById(R.id.psa);
        psa.setTypeface(face);
        psa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assessment = new Intent(Menu.this, SendPersonId.class);
                startActivity(assessment);
            }
        });
        middleleft = (TextView) findViewById(R.id.middle_left);
        middleleft.setTypeface(face);
        middleleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getUserDetails() != null) {
                    HashMap<String, String> user = session.getUserDetails();
                    String id = user.get(SessionManager.KEY_ID);

                    if (id != null) {
                        final Dialog dialog = new Dialog(Menu.this);
                        dialog.getWindow();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.select_search_type);
                        dialog.show();

//                    Button direct = (Button) dialog.findViewById(R.id.direct);
                        Button searchByAWC = (Button) dialog.findViewById(R.id.search_by_awc);
                        Button searchNationWide = (Button) dialog.findViewById(R.id.seach_nationwide);

                        searchByAWC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(Menu.this, SelectAwc.class);
                                intent.putExtra("search_person_in_map", true);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        searchNationWide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (session.getUserDetails() != null) {
                                    Log.e(TAG, "userDetailsNotNull");
                                    HashMap<String, String> user = session.getUserDetails();
                                    String id = user.get(SessionManager.KEY_ID);
                                    Log.e(TAG, "printUserId:" + id);
                                    if (id != null) {
                                        Intent intent = new Intent(Menu.this, SearchPerson.class);
                                        startActivity(intent);

                                    }
                                } else {
                                    Toast.makeText(Menu.this, "User Details Null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }

            }
        });


        middleright = (TextView) findViewById(R.id.middle_right);
        middleright.setTypeface(face);
        middleright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (locationProviders == null || locationProviders.equals("")) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    Toast.makeText(Menu.this, "Please enable location", Toast.LENGTH_SHORT).show();
                } else {
                    Intent map = new Intent(Menu.this, Current_Location.class);
                    startActivity(map);
                }
            }
        });
        bottomright = (TextView) findViewById(R.id.bottom_right);
        bottomright.setTypeface(face);
        bottomright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PackageManager manager = getPackageManager();
                boolean hasCompass = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
                if (hasCompass) {
                    Intent ar;
                    ar = new Intent(Menu.this, ARView.class);

                    if (Build.VERSION.SDK_INT > 22) {

                        if ((ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                || (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                || (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(Menu.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);
                        } else
                            startActivity(ar);
                    } else

                        startActivity(ar);
                } else
                    Toast.makeText(Menu.this, "Unable to detect compass sensor in your device", Toast.LENGTH_SHORT).show();
            }
        });

        bottom = (TextView) findViewById(R.id.addinfo);
        bottom.setTypeface(face);
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(Menu.this, Category_to_add.class);
                startActivity(info);
            }
        });

        bottom2 = (TextView) findViewById(R.id.directions);
        bottom2.setTypeface(face);
        bottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (locationProviders == null || locationProviders.equals("")) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    Toast.makeText(Menu.this, "Please enable location", Toast.LENGTH_SHORT).show();
                } else {

                    final Dialog dialog = new Dialog(Menu.this);
                    dialog.getWindow();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.addperson);
                    dialog.show();

//                    Button direct = (Button) dialog.findViewById(R.id.direct);
                    Button route = (Button) dialog.findViewById(R.id.track);
                    Button load = (Button) dialog.findViewById(R.id.load);


                 /*   direct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent direction = new Intent(Menu.this, BreadCrumbs.class);
                            startActivity(direction);
                            dialog.dismiss();
                        }
                    });*/

                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent loader = new Intent(Menu.this, ChoosePath.class);
                            startActivity(loader);
                            dialog.dismiss();
                        }
                    });

                    route.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.CAMERA},
                                        1);
                            }
                            dialog.dismiss();
                            final Dialog category = new Dialog(Menu.this);
                            category.getWindow();
                            category.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            category.setContentView(R.layout.select_category);
                            category.show();

                            Button onfoot = (Button) category.findViewById(R.id.foot);
                            Button onvehicle = (Button) category.findViewById(R.id.vehicle);

                            onfoot.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Menu.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);
                                    if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(Menu.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                111);
                                    }
                                    Intent tracker = new Intent(Menu.this, Tracking.class);
                                    startActivity(tracker);
                                    category.dismiss();
                                }
                            });

                            onvehicle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent vehicletracker = new Intent(Menu.this, OnVehicle.class);
                                    startActivity(vehicletracker);
                                    category.dismiss();
                                }
                            });
                        }
                    });

                }
            }
        });

        risk_update = findViewById(R.id.risk_update);
        risk_update.setTypeface(face);
        risk_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent questions = new Intent(Menu.this, Questions.class);
                startActivity(questions);
            }
        });


        poll = findViewById(R.id.poll);
        poll.setTypeface(face);
        poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Menu.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.criteria);
                dialog.show();

                Button health = (Button) dialog.findViewById(R.id.direct);
                Button economical = (Button) dialog.findViewById(R.id.track);
                Button social = (Button) dialog.findViewById(R.id.load);

                health.setText("Health");
                economical.setText("Economical");
                social.setText("Social");


                health.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent healthPoll = new Intent(Menu.this, HealthPoll.class);
                        startActivity(healthPoll);
                        dialog.dismiss();
                    }
                });

                economical.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent economicalPoll = new Intent(Menu.this, EconomicPoll.class);
                        startActivity(economicalPoll);
                        dialog.dismiss();
                    }
                });

                social.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent socialPoll = new Intent(Menu.this, SocialPoll.class);
                        startActivity(socialPoll);
                        dialog.dismiss();
                    }
                });
            }
        });

     /*   scanner = (TextView) findViewById(R.id.qrscanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerActivity = new Intent(Menu.this, MakePayment.class);
                startActivity(scannerActivity);
            }
        });*/

        addPhotos = findViewById(R.id.add_photos);
        addPhotos.setTypeface(face);
        addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPicturesActivity = new Intent(Menu.this, AddPictures.class);
                startActivity(addPicturesActivity);
            }
        });

        gallery = findViewById(R.id.gallery);
        gallery.setTypeface(face);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryActivity = new Intent(Menu.this, Gallery.class);
                startActivity(galleryActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
//        getMenuInflater().inflate(R.menu.download_offline_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
//                callAllAPIs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callAllAPIs() {
        if (InternetConnection.checkConnection(this)) {
            Log.d(TAG, "callAllAPIs: called()");
            ProgressDialog fetchDialog = new ProgressDialog(this);
            fetchDialog.setMessage("Fetching offline data. Please Wait...");
            fetchDialog.setCancelable(true);
            fetchDialog.show();

            okHttpClient = new OkHttpClient.Builder()
                    // Enable response caching
                    .addNetworkInterceptor(new ResponseCacheInterceptor())
                    .addInterceptor(new OfflineResponseCacheInterceptor())
                    // Set the cache location and size (1 MB)
                    .cache(new Cache(new File(Menu.this
                            .getCacheDir(),
                            "Responses"), 1024 * 1024))
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url) // that means base url + the left url in interface
                    .client(okHttpClient)//adding okhttp3 object that we have created
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            String[] apiUrls = new String[]{"personal_detail/api/personal_detail/",
                    "payment_distribution/api/payment_distribution/",
                    "investigate/api/investigate/",
                    "awc/api/awc/",
                    "ca/api/ca/",
                    ""};

            String[] apiUrls2 = new String[]{"path/api/path",
                    "payment_distribution/api/payment_distribution",
                    "investigate/api/investigate",
                    "awc/api/awc",
                    "ca/api/ca",
                    "personal_detail/api/personal_detail",
                    "track/api/track",
                    "form_poll/api/form"};

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String awc = preferences.getString("awc", null);

            Log.d(TAG, "callAllAPIs: " + awc);
            for (String apiUrl2 : apiUrls2) {

                Call<ResponseBody> call = retrofit.create(InvestigationInterface.class).getResponse(apiUrl2 + "?api_token=" + token);

                if (call.isExecuted())
                    call = call.clone();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "onResponse: " + response.body(), null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
            }


            for (String apiUrl : apiUrls) {

                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Call<ResponseBody> call = retrofit.create(InvestigationInterface.class).getResponse(apiUrl + awc + "?api_token=" + token);
                        if (call.isExecuted())
                            call = call.clone();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                Log.d(TAG, "onResponse: " + response.body(), null);

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                            }
                        });

                        fetchDialog.dismiss();
                    }
                };
                mHandler.postDelayed(mRunnable, 10000);
            }

            Log.d(TAG, "callAllAPIs: complete", null);

        } else
            Toast.makeText(this, "Failed to fetch offline data. No internet Connection", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * ask required permissions
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            if (!InternetConnection.checkConnection(Menu.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 2 weeks
                        .build();
            }
            return chain.proceed(request);
        }
    }

}

