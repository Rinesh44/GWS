package com.example.android.gurkha;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.location.LocationManager;
import android.os.Build;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import com.example.android.gurkha.app.Config.Config;
import com.example.android.gurkha.utils.NotificationUtils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class Menu extends AppCompatActivity {
    private static final String TAG = Menu.class.getSimpleName();
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
    private TextView topleft, topright, middleleft, middleright, bottomright, bottom, bottom2, scanner;
    private static final int REQUEST_PERMISSIONS = 123;
    SessionManager session;
    FbSessionManager fbSessionManager;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = (Toolbar) findViewById(R.id.select);

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        tv.setTypeface(face);

        session = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps_enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Menu.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    112);
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();

        topleft = (TextView) findViewById(R.id.top_left);
        topleft.setTypeface(face);
        topleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getUserDetails() != null) {
                    HashMap<String, String> user = session.getUserDetails();
                    String id = user.get(SessionManager.KEY_ID);

                    if (id != null) {

                        if (id.matches("1")) {
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
                }

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
                }

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
        middleleft = (TextView) findViewById(R.id.middle_left);
        middleleft.setTypeface(face);
        middleleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getUserDetails() != null) {
                    HashMap<String, String> user = session.getUserDetails();
                    String id = user.get(SessionManager.KEY_ID);

                    if (id != null) {

                        if (id.matches("1")) {
                            Intent ifAdminSearhPerson = new Intent(Menu.this, SearchPerson.class);
                            startActivity(ifAdminSearhPerson);
                        } else {
                            Intent search = new Intent(Menu.this, SelectAwcForMap.class);
                            startActivity(search);
                        }
                    }
                }

                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                    String fbId = fbUser.get(SessionManager.KEY_ID);

                    if (fbId != null) {

                        if (fbId.matches("1")) {
                            Intent ifAdminSearhPerson = new Intent(Menu.this, SearchPerson.class);
                            startActivity(ifAdminSearhPerson);
                        } else {
                            Intent search = new Intent(Menu.this, SelectAwcForMap.class);
                            startActivity(search);
                        }
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

                    Button direct = (Button) dialog.findViewById(R.id.direct);
                    Button route = (Button) dialog.findViewById(R.id.track);
                    Button load = (Button) dialog.findViewById(R.id.load);


                    direct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent direction = new Intent(Menu.this, BreadCrumbs.class);
                            startActivity(direction);
                            dialog.dismiss();
                        }
                    });

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
                                                112);
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

        scanner = (TextView) findViewById(R.id.qrscanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerActivity = new Intent(Menu.this, Scanner.class);
                startActivity(scannerActivity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        fragment.onActivityResult(requestCode, resultCode, data);
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

}

