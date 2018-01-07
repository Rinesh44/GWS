package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Person_Details extends AppCompatActivity {
    TextView army_no, rank, name, surname, subunit, unit, army, age, dob, doe, dod, dc, retain, payee,
            dependent_name, dependent_age, dependent_dob, nominee, relation, paid_to, health_state, type_of_welfare_pensioner,
            longitude, latitude, village, district, vdc, ward_no, po;
    Toolbar toolbar;
    ImageView photo1, photo2, photo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__details);

        toolbar = (Toolbar) findViewById(R.id.select);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        name = (TextView) findViewById(R.id.textName);
        name.setTypeface(face);
        latitude = (TextView) findViewById(R.id.textlatidude);
        latitude.setTypeface(face);
        longitude = (TextView) findViewById(R.id.textlongitude);
        longitude.setTypeface(face);
        relation = (TextView) findViewById(R.id.textrelation);
        relation.setTypeface(face);
        army_no = (TextView) findViewById(R.id.textarmyno);
        army_no.setTypeface(face);
        rank = (TextView) findViewById(R.id.textrank);
        rank.setTypeface(face);
        surname = (TextView) findViewById(R.id.textsurname);
        surname.setTypeface(face);
        subunit = (TextView) findViewById(R.id.textsubunit);
        subunit.setTypeface(face);
        unit = (TextView) findViewById(R.id.textunit);
        unit.setTypeface(face);
        army = (TextView) findViewById(R.id.textarmyid);
        army.setTypeface(face);
        age = (TextView) findViewById(R.id.textage);
        age.setTypeface(face);
        dob = (TextView) findViewById(R.id.textdob);
        dob.setTypeface(face);
        doe = (TextView) findViewById(R.id.textdoe);
        doe.setTypeface(face);
        dod = (TextView) findViewById(R.id.textdod);
        dod.setTypeface(face);
        dc = (TextView) findViewById(R.id.textdc);
        dc.setTypeface(face);
        retain = (TextView) findViewById(R.id.textretain);
        retain.setTypeface(face);
        payee = (TextView) findViewById(R.id.textpayee);
        payee.setTypeface(face);
        dependent_name = (TextView) findViewById(R.id.textdependentname);
        dependent_name.setTypeface(face);
        dependent_age = (TextView) findViewById(R.id.textdependentage);
        dependent_age.setTypeface(face);
        dependent_dob = (TextView) findViewById(R.id.textdependentdob);
        dependent_dob.setTypeface(face);
        nominee = (TextView) findViewById(R.id.textnominee);
        nominee.setTypeface(face);
        paid_to = (TextView) findViewById(R.id.textpaid);
        paid_to.setTypeface(face);
        health_state = (TextView) findViewById(R.id.texthealthstate);
        health_state.setTypeface(face);
        type_of_welfare_pensioner = (TextView) findViewById(R.id.texttypeofwelfarepensioner);
        type_of_welfare_pensioner.setTypeface(face);
        village = (TextView) findViewById(R.id.textvillage);
        village.setTypeface(face);
        district = (TextView) findViewById(R.id.textdistrict);
        district.setTypeface(face);
        vdc = (TextView) findViewById(R.id.textvdc);
        vdc.setTypeface(face);
        ward_no = (TextView) findViewById(R.id.textwardno);
        ward_no.setTypeface(face);
        po = (TextView) findViewById(R.id.textpo);
        po.setTypeface(face);

        photo1 = (ImageView) findViewById(R.id.imgphoto1);
        photo2 = (ImageView) findViewById(R.id.imgphoto2);
        photo3 = (ImageView) findViewById(R.id.imgphoto3);


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


}
