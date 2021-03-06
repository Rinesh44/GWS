package com.example.android.gurkha;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.activities.IndividualMarker;
import com.example.android.gurkha.application.GurkhaApplication;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;


public class Person_Details extends AppCompatActivity implements ResponseListener, View.OnClickListener {
    private static String editUrl = "http://gws.pagodalabs.com.np/personal_detail/api/editPersonal";
    private static final String TAG = Person_Details.class.getSimpleName();
    EditText army_no, rank, name, surname, subunit, unit, army, age, dob, doe, dod, dc, retain, payee,
            dependent_name, dependent_age, dependent_dob, nominee, relation, paid_to, health_state, type_of_welfare_pensioner,
            longitude, latitude, village, district, vdc, ward_no, po, childName, childDOB, childAge;
    Toolbar toolbar;
    LinearLayout childContainer;
    ImageView photo1, photo2, photo3;
    FloatingActionButton btnSave;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    String token;
    JobManager mJobManager;
    private Button mShowMap, mPhoto1, mPhoto2, mPhoto3;
    private Dialog dialog;
    private String encodedImage1, encodedImage2, encodedImage3;
    private String img1, img2, img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        mJobManager = GurkhaApplication.getInstance().getJobManager();


        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        dialog = new Dialog(Person_Details.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addperson);

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                token = fbUser.get(SessionManager.KEY_TOKEN);
        }

        mShowMap = findViewById(R.id.btn_show_map);
        mPhoto1 = findViewById(R.id.btn_photo1);
        mPhoto2 = findViewById(R.id.btn_photo2);
        mPhoto3 = findViewById(R.id.btn_photo3);


        ((Button) mShowMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showMap = new Intent(Person_Details.this, IndividualMarker.class);
                showMap.putExtra("Lat", latitude.getText().toString().trim());
                showMap.putExtra("Lng", longitude.getText().toString().trim());
                showMap.putExtra("Name", name.getText().toString().trim());
                showMap.putExtra("Surname", surname.getText().toString().trim());
                showMap.putExtra("ArmyNo", army_no.getText().toString().trim());
                startActivity(showMap);
            }
        });

        name = (EditText) findViewById(R.id.textName);
        name.setTypeface(face);
        latitude = (EditText) findViewById(R.id.textlatidude);
        latitude.setTypeface(face);
        longitude = (EditText) findViewById(R.id.textlongitude);
        longitude.setTypeface(face);
        relation = (EditText) findViewById(R.id.textrelation);
        relation.setTypeface(face);
        army_no = (EditText) findViewById(R.id.textarmyno);
        army_no.setTypeface(face);
        rank = (EditText) findViewById(R.id.textrank);
        rank.setTypeface(face);
        surname = (EditText) findViewById(R.id.textsurname);
        surname.setTypeface(face);
        subunit = (EditText) findViewById(R.id.textsubunit);
        subunit.setTypeface(face);
        unit = (EditText) findViewById(R.id.textunit);
        unit.setTypeface(face);
        army = (EditText) findViewById(R.id.textarmyid);
        army.setTypeface(face);
        age = (EditText) findViewById(R.id.textage);
        age.setTypeface(face);
        dob = (EditText) findViewById(R.id.textdob);
        dob.setTypeface(face);
        doe = (EditText) findViewById(R.id.textdoe);
        doe.setTypeface(face);
        dod = (EditText) findViewById(R.id.textdod);
        dod.setTypeface(face);
        dc = (EditText) findViewById(R.id.textdc);
        dc.setTypeface(face);
        retain = (EditText) findViewById(R.id.textretain);
        retain.setTypeface(face);
        payee = (EditText) findViewById(R.id.textpayee);
        payee.setTypeface(face);
        dependent_name = (EditText) findViewById(R.id.textdependentname);
        dependent_name.setTypeface(face);
        dependent_age = (EditText) findViewById(R.id.textdependentage);
        dependent_age.setTypeface(face);
        dependent_dob = (EditText) findViewById(R.id.textdependentdob);
        dependent_dob.setTypeface(face);
        nominee = (EditText) findViewById(R.id.textnominee);
        nominee.setTypeface(face);
        paid_to = (EditText) findViewById(R.id.textpaid);
        paid_to.setTypeface(face);
        health_state = (EditText) findViewById(R.id.texthealthstate);
        health_state.setTypeface(face);
        type_of_welfare_pensioner = (EditText) findViewById(R.id.texttypeofwelfarepensioner);
        type_of_welfare_pensioner.setTypeface(face);
        village = (EditText) findViewById(R.id.textvillage);
        village.setTypeface(face);
        district = (EditText) findViewById(R.id.textdistrict);
        district.setTypeface(face);
        vdc = (EditText) findViewById(R.id.textvdc);
        vdc.setTypeface(face);
        ward_no = (EditText) findViewById(R.id.textwardno);
        ward_no.setTypeface(face);
        po = (EditText) findViewById(R.id.textpo);
        po.setTypeface(face);
        childContainer = findViewById(R.id.child_container);
        childName = findViewById(R.id.text_child_name);
        childDOB = findViewById(R.id.text_child_dob);
        childAge = findViewById(R.id.text_child_age);

        photo1 = (ImageView) findViewById(R.id.imgphoto1);
        photo2 = (ImageView) findViewById(R.id.imgphoto2);
        photo3 = (ImageView) findViewById(R.id.imgphoto3);

        mPhoto1.setOnClickListener(this);
        mPhoto2.setOnClickListener(this);
        mPhoto3.setOnClickListener(this);
        photo1.setOnClickListener(this);
        photo2.setOnClickListener(this);
        photo3.setOnClickListener(this);

        btnSave = findViewById(R.id.save);


        Intent i = getIntent();

        String editId = i.getStringExtra("personal_id");

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtlatitude = i.getStringExtra("latitude");
        latitude.setText(txtlatitude);

        String txtlongitude = i.getStringExtra("longitude");
        longitude.setText(txtlongitude);

        String txtrelation = i.getStringExtra("relation");
        relation.setText(txtrelation);

        String txtarmyno = i.getStringExtra("army_no");
        army_no.setText(txtarmyno);

        String txtrank = i.getStringExtra("rank");
        rank.setText(txtrank);

        String txtsurname = i.getStringExtra("surname");
        surname.setText(txtsurname);

        String txtsubunit = i.getStringExtra("subunit");
        subunit.setText(txtsubunit);

        String txtunit = i.getStringExtra("unit");
        unit.setText(txtunit);

        String txtarmy = i.getStringExtra("army");
        army.setText(txtarmy);

        String txtage = i.getStringExtra("age");
        age.setText(txtage);

        String txtdob = i.getStringExtra("dob");
        dob.setText(txtdob);

        String txtdoe = i.getStringExtra("doe");
        doe.setText(txtdoe);

        String txtdod = i.getStringExtra("dod");
        dod.setText(txtdod);

        String txtdc = i.getStringExtra("dc");
        dc.setText(txtdc);

        String txtretain = i.getStringExtra("retain");
        retain.setText(txtretain);

        String txtpayee = i.getStringExtra("payee");
        payee.setText(txtpayee);

        String txtdependentname = i.getStringExtra("dependent_name");
        dependent_name.setText(txtdependentname);

        String txtdependentage = i.getStringExtra("dependent_age");
        dependent_age.setText(txtdependentage);

        String txtdependentdob = i.getStringExtra("dependent_dob");
        dependent_dob.setText(txtdependentdob);

        String txtnominee = i.getStringExtra("nominee");
        nominee.setText(txtnominee);

        String txtpaidto = i.getStringExtra("paid_to");
        paid_to.setText(txtpaidto);

        String txthealthstate = i.getStringExtra("health_state");
        health_state.setText(txthealthstate);

        String txttypeofwelfare = i.getStringExtra("type_of_welfare_pensioner");
        type_of_welfare_pensioner.setText(txttypeofwelfare);

        String txtvillage = i.getStringExtra("village");
        village.setText(txtvillage);

        String txtdistrict = i.getStringExtra("district");
        district.setText(txtdistrict);

        String txtvdc = i.getStringExtra("vdc");
        vdc.setText(txtvdc);

        String txtwardno = i.getStringExtra("ward_no");
        ward_no.setText(txtwardno);

        String txtpo = i.getStringExtra("po");
        po.setText(txtpo);


        String txtChildName = i.getStringExtra("child_name");
        childName.setText(txtChildName);
//        Log.e(TAG, txtChildName);


        String txtChildDob = i.getStringExtra("child_dob");
        childDOB.setText(txtChildDob);
//        Log.e(TAG, txtChildDob);


        String txtChildAge = i.getStringExtra("child_age");
        childAge.setText(txtChildAge);
//        Log.e(TAG, txtChildAge);

        img1 = i.getStringExtra("photo1");

        byte[] decodedString = Base64.decode(img1, Base64.DEFAULT);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
        photo1.setImageBitmap(thumbnail);


        img2 = i.getStringExtra("photo2");
        byte[] decodedString2 = Base64.decode(img2, Base64.DEFAULT);
        final Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
        Bitmap thumbnail2 = ThumbnailUtils.extractThumbnail(bitmap2, 300, 300);
        photo2.setImageBitmap(thumbnail2);


        img3 = i.getStringExtra("photo3");
        byte[] decodedString3 = Base64.decode(img3, Base64.DEFAULT);
        final Bitmap bitmap3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
        Bitmap thumbnail3 = ThumbnailUtils.extractThumbnail(bitmap3, 300, 300);
        photo3.setImageBitmap(thumbnail3);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Person_Details.this);
                } else {
                    builder = new AlertDialog.Builder(Person_Details.this);
                }
                builder.setTitle("Edit")
                        .setMessage("Are you sure to save the edited fields?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with edit

                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                String formattedDate = df.format(c.getTime());

                                Map<String, String> params = new HashMap<String, String>();
                                params.put("awc", SelectAwc.awc);
                                params.put("personal_id", editId.trim());
                                params.put("name", name.getText().toString().trim());
                                params.put("surname", surname.getText().toString().trim());
                                params.put("age", age.getText().toString().trim());
                                params.put("latitude", latitude.getText().toString().trim());
                                params.put("longitude", longitude.getText().toString().trim());
                                params.put("army_no", army_no.getText().toString().trim());
                                params.put("rank", rank.getText().toString().trim());
                                params.put("unit", unit.getText().toString().trim());
                                params.put("subunit", subunit.getText().toString().trim());
                                params.put("army", army.getText().toString().trim());
                                params.put("dob", dob.getText().toString().trim());
                                params.put("doe", doe.getText().toString().trim());
                                params.put("dod", dod.getText().toString().trim());
                                params.put("dc", dc.getText().toString().trim());
                                params.put("dependent_name", dependent_name.getText().toString().trim());
                                params.put("dependent_age", dependent_age.getText().toString().trim());
                                params.put("dependent_dob", dependent_dob.getText().toString().trim());
                                params.put("relation", relation.getText().toString().trim());
                                params.put("nominee", nominee.getText().toString().trim());
                                params.put("health_state", health_state.getText().toString().trim());
                                params.put("village", village.getText().toString().trim());
                                params.put("vdc", vdc.getText().toString().trim());
                                params.put("ward_no", ward_no.getText().toString().trim());
                                params.put("po", po.getText().toString().trim());
                                params.put("retain", retain.getText().toString().trim());
                                params.put("payee", payee.getText().toString().trim());
                                if (!encodedImage1.isEmpty()) params.put("photo1", encodedImage1);
                                if (!encodedImage2.isEmpty()) params.put("photo2", encodedImage2);
                                if (!encodedImage3.isEmpty()) params.put("photo3", encodedImage3);

//                    params.put("paid_to", mPaidto);
                                params.put("district", district.getText().toString().trim());
//                                params.put("awc", mAwc);
//                                params.put("background", );
                                params.put("created_at", formattedDate.trim());
                                params.put("api_token", token);

                                JSONObject parameter = new JSONObject(params);
                                Log.e("JSON:", parameter.toString());

                                mJobManager.addJobInBackground(new PostJob(editUrl, parameter.toString(), Person_Details.this));
                                Toast.makeText(Person_Details.this, "Personal Detail Edited", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                makeTextEditable();
                break;
        }
        return false;
    }

    private void makeTextEditable() {
        btnSave.setVisibility(View.VISIBLE);

        if (img1.isEmpty() || img1.equals("null")) mPhoto1.setVisibility(View.VISIBLE);
        if (img2.isEmpty() || img2.equals("null")) mPhoto2.setVisibility(View.VISIBLE);
        if (img3.isEmpty() || img3.equals("null")) mPhoto3.setVisibility(View.VISIBLE);


        name.setFocusableInTouchMode(true);
        name.requestFocus();
        surname.setFocusableInTouchMode(true);
        relation.setFocusableInTouchMode(true);
        age.setFocusableInTouchMode(true);
        latitude.setFocusableInTouchMode(true);
        longitude.setFocusableInTouchMode(true);
        army_no.setFocusableInTouchMode(true);
        rank.setFocusableInTouchMode(true);
        unit.setFocusableInTouchMode(true);
        subunit.setFocusableInTouchMode(true);
        army.setFocusableInTouchMode(true);
        dob.setFocusableInTouchMode(true);
        doe.setFocusableInTouchMode(true);
        dod.setFocusableInTouchMode(true);
        dc.setFocusableInTouchMode(true);
        retain.setFocusableInTouchMode(true);
        payee.setFocusableInTouchMode(true);
        childName.setFocusableInTouchMode(true);
        childDOB.setFocusableInTouchMode(true);
        childAge.setFocusableInTouchMode(true);
        dependent_name.setFocusableInTouchMode(true);
        dependent_age.setFocusableInTouchMode(true);
        dependent_dob.setFocusableInTouchMode(true);
        health_state.setFocusableInTouchMode(true);
        paid_to.setFocusableInTouchMode(true);
        nominee.setFocusableInTouchMode(true);
        type_of_welfare_pensioner.setFocusableInTouchMode(true);
        village.setFocusableInTouchMode(true);
        district.setFocusableInTouchMode(true);
        vdc.setFocusableInTouchMode(true);
        ward_no.setFocusableInTouchMode(true);
        po.setFocusableInTouchMode(true);
    }

    @Override
    public void responseSuccess(Response response) {

    }

    @Override
    public void responseFail(String msg) {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (view.getId()) {
            case R.id.btn_photo1:
                showSourceSelectionDialog(10);

                break;

            case R.id.btn_photo2:
                showSourceSelectionDialog(20);
                break;

            case R.id.btn_photo3:
                showSourceSelectionDialog(30);

                break;

            case R.id.imgphoto1:
                showSourceSelectionDialog(10);

                break;

            case R.id.imgphoto2:
                showSourceSelectionDialog(20);

                break;

            case R.id.imgphoto3:
                showSourceSelectionDialog(30);

                break;

        }
    }

    private void showSourceSelectionDialog(int resultCode) {
        Button camera = dialog.findViewById(R.id.track);
        Button gallery = dialog.findViewById(R.id.load);

        camera.setText("Camera");
        gallery.setText("Gallery");

        dialog.show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, resultCode);//zero can be replaced with any action code
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), resultCode + 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;  //you can also calculate your inSampleSize
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];


        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mPhoto1.setVisibility(View.GONE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    photo1.setImageBitmap(bitmap);
                    photo1.setRotation(90);
                    byte[] byteArray = stream.toByteArray();
                    encodedImage1 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                }
            }

        }

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                photo1.setImageBitmap(bitmap);
                mPhoto1.setVisibility(View.GONE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] byteArray = stream.toByteArray();
                encodedImage1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.e(TAG, "encodedCameraImage:" + encodedImage1);

            }

        }

        if (requestCode == 21) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhoto2.setVisibility(View.GONE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    photo2.setImageBitmap(bitmap);
                    photo2.setRotation(90);
                    byte[] byteArray = stream.toByteArray();
                    encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            }


        }

        if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                photo2.setImageBitmap(bitmap);
                mPhoto2.setVisibility(View.GONE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] byteArray = stream.toByteArray();
                encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            }
        }

        if (requestCode == 31) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mPhoto3.setVisibility(View.GONE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    photo3.setImageBitmap(bitmap);
                    photo3.setRotation(90);
                    byte[] byteArray = stream.toByteArray();
                    encodedImage3 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                }
            }
        }

        if (requestCode == 30) {
            if (resultCode == RESULT_OK) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                photo3.setImageBitmap(bitmap);
                mPhoto3.setVisibility(View.GONE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] byteArray = stream.toByteArray();
                encodedImage3 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        }

    }
}
