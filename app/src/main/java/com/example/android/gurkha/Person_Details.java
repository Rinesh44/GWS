package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Person_Details extends AppCompatActivity {
    private static final String TAG = Person_Details.class.getSimpleName();
    EditText army_no, rank, name, surname, subunit, unit, army, age, dob, doe, dod, dc, retain, payee,
            dependent_name, dependent_age, dependent_dob, nominee, relation, paid_to, health_state, type_of_welfare_pensioner,
            longitude, latitude, village, district, vdc, ward_no, po, childName, childDOB, childAge;
    Toolbar toolbar;
    LinearLayout childContainer;
    ImageView photo1, photo2, photo3;
    FloatingActionButton btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

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

        btnSave = findViewById(R.id.save);


        Intent i = getIntent();

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

        String img1 = i.getStringExtra("photo1");
        byte[] decodedString = Base64.decode(img1, Base64.DEFAULT);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
        photo1.setImageBitmap(thumbnail);


        String img2 = i.getStringExtra("photo2");
        byte[] decodedString2 = Base64.decode(img2, Base64.DEFAULT);
        final Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
        Bitmap thumbnail2 = ThumbnailUtils.extractThumbnail(bitmap2, 300, 300);
        photo2.setImageBitmap(thumbnail2);


        String img3 = i.getStringExtra("photo3");
        byte[] decodedString3 = Base64.decode(img3, Base64.DEFAULT);
        final Bitmap bitmap3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
        Bitmap thumbnail3 = ThumbnailUtils.extractThumbnail(bitmap3, 300, 300);
        photo3.setImageBitmap(thumbnail3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                makeTextEditable();
                break;
        }
            return false;
    }

    private void makeTextEditable() {
        btnSave.setVisibility(View.VISIBLE);

        name.setFocusableInTouchMode(true);
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
}
