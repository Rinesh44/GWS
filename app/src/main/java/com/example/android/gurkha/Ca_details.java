package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Ca_details extends AppCompatActivity {
    TextView scheme, village, vdc, wno, servicetype, hhtotal, tenta, population, level, totpopulation, gws, community,
            total, funder, location;
    Toolbar toolbar;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        scheme = (TextView) findViewById(R.id.textscheme);
        scheme.setTypeface(face);
        village = (TextView) findViewById(R.id.textvillage);
        village.setTypeface(face);
        location = (TextView) findViewById(R.id.textlocation);
        location.setTypeface(face);
        vdc = (TextView) findViewById(R.id.textvdc);
        vdc.setTypeface(face);
        wno = (TextView) findViewById(R.id.textwno);
        wno.setTypeface(face);
        servicetype = (TextView) findViewById(R.id.textservicetype);
        servicetype.setTypeface(face);
        hhtotal = (TextView) findViewById(R.id.texthhtotal);
        hhtotal.setTypeface(face);
        tenta = (TextView) findViewById(R.id.texttenta);
        tenta.setTypeface(face);
        population = (TextView) findViewById(R.id.textpopulation);
        population.setTypeface(face);
        level = (TextView) findViewById(R.id.textlevel);
        level.setTypeface(face);
        totpopulation = (TextView) findViewById(R.id.texttotpopulation);
        totpopulation.setTypeface(face);
        gws = (TextView) findViewById(R.id.textgws);
        gws.setTypeface(face);
        community = (TextView) findViewById(R.id.textcommunity);
        community.setTypeface(face);
        total = (TextView) findViewById(R.id.texttotal);
        total.setTypeface(face);
        funder = (TextView) findViewById(R.id.textfunder);
        funder.setTypeface(face);

        fabSave = findViewById(R.id.save);

        Intent i = getIntent();

        String txtscheme = i.getStringExtra("scheme_no");
        scheme.setText(txtscheme);

        String txtvillage = i.getStringExtra("village");
        village.setText(txtvillage);

        String txtlocation = i.getStringExtra("location");
        location.setText(txtlocation);

        String txtvdc = i.getStringExtra("vdc");
        vdc.setText(txtvdc);

        String txtwardno = i.getStringExtra("ward_no");
        wno.setText(txtwardno);

        String txtservicetype = i.getStringExtra("service_type");
        servicetype.setText(txtservicetype);

        String txthhtotal = i.getStringExtra("hh_total");
        hhtotal.setText(txthhtotal);

        String txttenta = i.getStringExtra("tenta_tap_no");
        tenta.setText(txttenta);

        String txtpopulation = i.getStringExtra("population_total");
        population.setText(txtpopulation);

        String txtlevel = i.getStringExtra("level");
        level.setText(txtlevel);

        String txttotpopu = i.getStringExtra("school_popu_total");
        totpopulation.setText(txttotpopu);

        String txtgws = i.getStringExtra("gws_kaaa");
        gws.setText(txtgws);

        String txtcommunity = i.getStringExtra("community");
        community.setText(txtcommunity);

        String txttotal = i.getStringExtra("total");
        total.setText(txttotal);

        String txtfunder = i.getStringExtra("funder");
        funder.setText(txtfunder);

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
        fabSave.setVisibility(View.VISIBLE);

        scheme.setFocusableInTouchMode(true);
        village.setFocusableInTouchMode(true);
        location.setFocusableInTouchMode(true);
        vdc.setFocusableInTouchMode(true);
        wno.setFocusableInTouchMode(true);
        servicetype.setFocusableInTouchMode(true);
        hhtotal.setFocusableInTouchMode(true);
        tenta.setFocusableInTouchMode(true);
        population.setFocusableInTouchMode(true);
        total.setFocusableInTouchMode(true);
        level.setFocusableInTouchMode(true);
        totpopulation.setFocusableInTouchMode(true);
        gws.setFocusableInTouchMode(true);
        community.setFocusableInTouchMode(true);
        funder.setFocusableInTouchMode(true);
    }
}
