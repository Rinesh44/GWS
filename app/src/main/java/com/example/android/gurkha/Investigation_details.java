package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Investigation_details extends AppCompatActivity {
    TextView name, surname, army_no, investigator, paymentbase, date, startdate, reviewdate;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation_details);

        toolbar = (Toolbar) findViewById(R.id.select);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        name = (TextView) findViewById(R.id.textperson);
        name.setTypeface(face);
        surname = (TextView) findViewById(R.id.textpersonsurname);
        surname.setTypeface(face);
        investigator = (TextView) findViewById(R.id.textinvestigator);
        investigator.setTypeface(face);
        paymentbase = (TextView) findViewById(R.id.textpaymentbase);
        paymentbase.setTypeface(face);
        date = (TextView) findViewById(R.id.textdate);
        date.setTypeface(face);
        startdate = (TextView) findViewById(R.id.textstartdate);
        startdate.setTypeface(face);
        reviewdate = (TextView) findViewById(R.id.textreviewdate);
        reviewdate.setTypeface(face);
        army_no = (TextView) findViewById(R.id.textarmyno);
        army_no.setTypeface(face);

        Intent i = getIntent();

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtinvestigator = i.getStringExtra("investigator");
        investigator.setText(txtinvestigator);

        String txtsurname = i.getStringExtra("surname");
        surname.setText(txtsurname);

        String txtpaymentbase = i.getStringExtra("payment_base");
        paymentbase.setText(txtpaymentbase);

        String txtdate = i.getStringExtra("date");
        date.setText(txtdate);

        String txtstartdate = i.getStringExtra("start_date");
        startdate.setText(txtstartdate);

        String txtreviewdate = i.getStringExtra("review_date");
        reviewdate.setText(txtreviewdate);

        String txtarmyno = i.getStringExtra("army_no");
        army_no.setText(txtarmyno);
    }

}
