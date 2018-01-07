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

public class awc_details extends AppCompatActivity {
    TextView name, address, wpsp, armyno, approvedgrant, cost, supervisor, remarks, status, location, category;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awc_details);

        toolbar = (Toolbar) findViewById(R.id.select);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        name = (TextView) findViewById(R.id.textName);
        name.setTypeface(face);
        address = (TextView) findViewById(R.id.textaddress);
        address.setTypeface(face);
        armyno = (TextView) findViewById(R.id.textarmyno);
        armyno.setTypeface(face);
        approvedgrant = (TextView) findViewById(R.id.textgrant);
        approvedgrant.setTypeface(face);
        cost = (TextView) findViewById(R.id.textcost);
        cost.setTypeface(face);
        supervisor = (TextView) findViewById(R.id.textsupervisor);
        supervisor.setTypeface(face);
        remarks = (TextView) findViewById(R.id.textremarks);
        remarks.setTypeface(face);
        status = (TextView) findViewById(R.id.textstatus);
        status.setTypeface(face);
        location = (TextView) findViewById(R.id.textlocation);
        location.setTypeface(face);
        wpsp = (TextView) findViewById(R.id.textwpsp);
        wpsp.setTypeface(face);
        category = (TextView) findViewById(R.id.textcategory);
        category.setTypeface(face);

        Intent i = getIntent();

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtaddress = i.getStringExtra("address");
        address.setText(txtaddress);

        //String txtarmyno = i.getStringExtra("army_no");
        //army_no.setText(txtarmyno);

        String txtapprovedgrant = i.getStringExtra("approved_grant");
        approvedgrant.setText(txtapprovedgrant);

        String txtcost = i.getStringExtra("construction_cost");
        cost.setText(txtcost);

        String txtsupervisor = i.getStringExtra("project_supervisor");
        supervisor.setText(txtsupervisor);

        String txtremarks = i.getStringExtra("remarks");
        remarks.setText(txtremarks);

        String txtstatus = i.getStringExtra("status");
        status.setText(txtstatus);

        String txtlocation = i.getStringExtra("location");
        location.setText(txtlocation);

        String txtwpsp = i.getStringExtra("wp_sp");
        wpsp.setText(txtwpsp);

        String txtcategory = i.getStringExtra("category");
        category.setText(txtcategory);

        String txtarmyno = i.getStringExtra("army_no");
        armyno.setText(txtarmyno);

    }


}
