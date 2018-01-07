package com.example.android.gurkha;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Add_personal extends AppCompatActivity {
    public static EditText name, surname, age, armyno, rank, unit, subunit, armyid, dob, doe, dod, documents, dependent_name,
            dependent_age, dependent_dob, relation, nominee, health_state, village, vcid, ward_no, po, district, paidto, latitude,longitude;
    Button button, getLocation;
    TextView imagebtn1, imagebtn2, imagebtn3, hiddenDate;
    String fileName;
    String encodedImage1, encodedImage2, encodedImage3;
    private Calendar calendar;
    SearchableSpinner awc;
    private int year, month, day;
    ArrayList<String> imageArrayString;
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;

    // Storage Permissions
    private static final int REQUEST_STORAGE_ACCESS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String url = "http://pagodalabs.com.np/gws/personal_detail/api/personal_detail";
    private static final int PICK_IMAGE1 = 1;
    private static final int PICK_IMAGE2 = 2;
    private static final int PICK_IMAGE3 = 3;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        if (uri != null) {
            if (uri.toString().startsWith("file:")) {
                fileName = uri.getPath();
            } else { // uri.startsWith("content:")

                Cursor c = getContentResolver().query(uri, null, null, null, null);

                if (c != null && c.moveToFirst()) {

                    int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (id != -1) {
                        fileName = c.getString(id);
                    }
                }
            }
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;  //you can also calculate your inSampleSize
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];
        if (requestCode == PICK_IMAGE1) {
            imagebtn1.setText(fileName);

            Bitmap bm = BitmapFactory.decodeFile(fileName, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage1 = Base64.encodeToString(b, Base64.DEFAULT);
            Log.i("personalEncodedImage", encodedImage1);
            imageArrayString.add(encodedImage1);
            Log.i("arrayStringImage", imageArrayString.toString());
            bm.recycle();
        }

        if (requestCode == PICK_IMAGE2) {
            imagebtn2.setText(fileName);

            Bitmap bm = BitmapFactory.decodeFile(fileName, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage2 = Base64.encodeToString(b, Base64.DEFAULT);
            bm.recycle();
        }

        if (requestCode == PICK_IMAGE3) {
            imagebtn3.setText(fileName);
            Bitmap bm = BitmapFactory.decodeFile(fileName, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage3 = Base64.encodeToString(b, Base64.DEFAULT);
            bm.recycle();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal);

        name = (EditText) findViewById(R.id.textName);
        surname = (EditText) findViewById(R.id.textsurname);
        age = (EditText) findViewById(R.id.textage);
        latitude = (EditText) findViewById(R.id.textlatidude);
        longitude = (EditText) findViewById(R.id.textlongitude);
        armyno = (EditText) findViewById(R.id.textarmyno);
        rank = (EditText) findViewById(R.id.textrank);
        unit = (EditText) findViewById(R.id.textunit);
        subunit = (EditText) findViewById(R.id.textsubunit);
        armyid = (EditText) findViewById(R.id.textarmyid);
        dob = (EditText) findViewById(R.id.textdob);
        dod = (EditText) findViewById(R.id.textdod);
        doe = (EditText) findViewById(R.id.textdoe);
        documents = (EditText) findViewById(R.id.textdc);
        dependent_name = (EditText) findViewById(R.id.textdependentname);
        dependent_age = (EditText) findViewById(R.id.textdependentage);
        dependent_dob = (EditText) findViewById(R.id.textdependentdob);
        relation = (EditText) findViewById(R.id.textrelation);
        nominee = (EditText) findViewById(R.id.textnominee);
        health_state = (EditText) findViewById(R.id.texthealthstate);
        name = (EditText) findViewById(R.id.textName);
        village = (EditText) findViewById(R.id.textvillage);
        vcid = (EditText) findViewById(R.id.textvdcid);
        ward_no = (EditText) findViewById(R.id.textwardno);
        po = (EditText) findViewById(R.id.textpo);
        district = (EditText) findViewById(R.id.textdistrict);
        paidto = (EditText) findViewById(R.id.textpaidto);
        getLocation = (Button) findViewById(R.id.btnGetLocation);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        imageArrayString = new ArrayList<>();


        button = (Button) findViewById(R.id.btn);
        imagebtn1 = (TextView) findViewById(R.id.imagebtn1);
        imagebtn2 = (TextView) findViewById(R.id.imagebtn2);
        imagebtn3 = (TextView) findViewById(R.id.imagebtn3);

        final Spinner retain = (Spinner) findViewById(R.id.textretain);
        String[] retain_items = new String[]{"RETAIN", "Yes", "No"};
        ArrayAdapter<String> adapt_retain = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, retain_items);
        retain.setAdapter(adapt_retain);

        final Spinner armyid = (Spinner) findViewById(R.id.textarmy);
        String[] army_items = new String[]{"ARMY_ID", "IA", "SP", "BA"};
        ArrayAdapter<String> adapt_army = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, army_items);
        armyid.setAdapter(adapt_army);

        final Spinner payee = (Spinner) findViewById(R.id.textpayee);
        String[] payee_items = new String[]{"PAYEE", "Self", "Widow", "Orphan"};
        ArrayAdapter<String> adapt_payee = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, payee_items);
        payee.setAdapter(adapt_payee);

        final Spinner background = (Spinner) findViewById(R.id.spinnerBackground);
        String[] background_items = new String[]{"Select Background", "Normal", "Health", "Economical", "Social"};
        ArrayAdapter<String> adapt_background = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, background_items);
        background.setAdapter(adapt_background);

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items);
        awc.setAdapter(adapt_awc);

        final Spinner type = (Spinner) findViewById(R.id.texttypeofwelfarepensioner);
        String[] type_items = new String[]{"TYPE OF WELFARE PENSIONER", "Child", "Self", "Widow"};
        ArrayAdapter<String> adapt_type = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, type_items);
        type.setAdapter(adapt_type);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE_ACCESS);

        }

        imagebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE1);
            }
        });

        imagebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE2);
            }
        });

        imagebtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE3);
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(Add_personal.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Add_personal.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }

                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean enabled = service
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!enabled) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(Add_personal.this, "Please enable location and try again", Toast.LENGTH_SHORT).show();
                } else {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Add_personal.this);
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            latitude.setText(String.valueOf(location.getLatitude()));
                            longitude.setText(String.valueOf(location.getLongitude()));
                        }
                    });
                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDob(v);
            }
        });

        doe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDoe(v);
            }
        });

        dod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDod(v);
            }
        });

        dependent_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDependentDob(v);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mName = name.getText().toString();
                    String mSurname = surname.getText().toString();
                    String mAge = age.getText().toString();
                    String mLatitude = latitude.getText().toString();
                    String mLongitude = longitude.getText().toString();
                    String mArmyno = armyno.getText().toString();
                    String mRank = rank.getText().toString();
                    String mUnit = unit.getText().toString();
                    String mSubunit = subunit.getText().toString();
                    String mDob = dob.getText().toString();
                    String mDoe = doe.getText().toString();
                    String mDod = dod.getText().toString();
                    String mDocuments = documents.getText().toString();
                    String mDependentname = dependent_name.getText().toString();
                    String mDependentage = dependent_age.getText().toString();
                    String mDependentdob = dependent_dob.getText().toString();
                    String mRelation = relation.getText().toString();
                    String mNominee = nominee.getText().toString();
                    String mHealthstate = health_state.getText().toString();
                    String mVillage = village.getText().toString();
                    String mVcid = vcid.getText().toString();
                    String mWardno = ward_no.getText().toString();
                    String mPostoffice = po.getText().toString();
                    String mPaidto = paidto.getText().toString();
                    String mDistrict = district.getText().toString();
                    String mAwc = awc.getSelectedItem().toString();
                    Log.e("personalselectedawc:", mAwc);
                    String mBackground = background.getSelectedItem().toString();

                    String mRetain = retain.getSelectedItem().toString();
                    String mPayee = payee.getSelectedItem().toString();
                    String mTypeOfWelfare = type.getSelectedItem().toString();
                    String mArmyid = armyid.getSelectedItem().toString();

                    String mEncoded1 = encodedImage1;
                    String mEncoded2 = encodedImage2;
                    String mEncoded3 = encodedImage3;

                    String vacant1 = "null";
                    String vacant2 = "null";
                    String vacant3 = "null";

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();

                    if (mLatitude.isEmpty() && mLongitude.isEmpty()) {
                        Toast.makeText(Add_personal.this, "Please enter latitude and longitude values", Toast.LENGTH_SHORT).show();
                    } else {

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", mName);
                        params.put("surname", mSurname);
                        params.put("age", mAge);
                        params.put("latitude", mLatitude);
                        params.put("longitude", mLongitude);
                        params.put("army_no", mArmyno);
                        params.put("rank", mRank);
                        params.put("unit", mUnit);
                        params.put("subunit", mSubunit);
                        params.put("army", mArmyid);
                        params.put("dob", mDob);
                        params.put("doe", mDoe);
                        params.put("dod", mDod);
                        params.put("dc", mDocuments);
                        params.put("dependent_name", mDependentname);
                        params.put("dependent_age", mDependentage);
                        params.put("dependent_dob", mDependentdob);
                        params.put("relation", mRelation);
                        params.put("nominee", mNominee);
                        params.put("health_state", mHealthstate);
                        params.put("village", mVillage);
                        params.put("vdc", mVcid);
                        params.put("ward_no", mWardno);
                        params.put("po", mPostoffice);
                        params.put("retain", mRetain);
                        params.put("payee", mPayee);
                        params.put("type_of_welfare_pensioner", mTypeOfWelfare);
                        params.put("paid_to", mPaidto);
                        params.put("district", mDistrict);
                        params.put("awc", mAwc);
                        params.put("background", mBackground);
                        params.put("created_at", mHiddenDate);

                        if (imagebtn1.getText().equals("Browse...")) {
                            params.put("photo1", vacant1);
                        } else
                            params.put("photo1", mEncoded1);
                        if (imagebtn2.getText().equals("Browse...")) {
                            params.put("photo2", vacant2);
                        } else
                            params.put("photo2", mEncoded2);
                        if (imagebtn3.getText().equals("Browse...")) {
                            params.put("photo3", vacant3);
                        } else
                            params.put("photo3", mEncoded3);


                        JSONObject parameter = new JSONObject(params);
                        Log.e("Json:", parameter.toString());
                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = RequestBody.create(JSON, parameter.toString());
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("response", call.request().body().toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.e("response", response.body().string());

                            }

                        });

                        Toast.makeText(Add_personal.this, "Personal Details Added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(Add_personal.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @SuppressWarnings("deprecation")
    public void setDob(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setDoe(View view) {
        showDialog(777);
    }

    @SuppressWarnings("deprecation")
    public void setDod(View view) {
        showDialog(555);
    }

    @SuppressWarnings("deprecation")
    public void setDependentDob(View view) {
        showDialog(333);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    dobListener, year, month, day);
        }
        if (id == 777) {
            return new DatePickerDialog(this,
                    doeListener, year, month, day);
        }

        if (id == 555) {
            return new DatePickerDialog(this,
                    dodListener, year, month, day);
        }
        if (id == 333) {
            return new DatePickerDialog(this,
                    dependentDobListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dobListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDob(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener doeListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDoe(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener dodListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDod(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener dependentDobListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDependentDob(arg3, arg2 + 1, arg1);
                }
            };

    private void showDob(int day, int month, int year) {

        dob.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showDoe(int day, int month, int year) {
        doe.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showDod(int day, int month, int year) {
        dod.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showDependentDob(int day, int month, int year) {
        dependent_dob.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

}
