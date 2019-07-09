package com.example.android.gurkha;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class Ca_details extends AppCompatActivity implements ResponseListener {
    public static String editUrl = "http://gws.pagodalabs.com.np/ca/api/editCa";
    TextView scheme, village, vdc, wno, servicetype, hhtotal, tenta, population, level, totpopulation, gws, community,
            total, funder, location;
    Toolbar toolbar;
    FloatingActionButton fabSave;

    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    String token;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                token = fbUser.get(SessionManager.KEY_TOKEN);
        }

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

        String caId = i.getStringExtra("ca_id");

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

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Ca_details.this);
                } else {
                    builder = new AlertDialog.Builder(Ca_details.this);
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
                                params.put("scheme_no", scheme.getText().toString().trim());
                                params.put("location", location.getText().toString().trim());
                                params.put("village", village.getText().toString().trim());
                                params.put("vdc", vdc.getText().toString().trim());
                                params.put("ward_no", wno.getText().toString().trim());
                                params.put("service_type", servicetype.getText().toString().trim());
                                params.put("hh_total", hhtotal.getText().toString().trim());
                                params.put("population_total", population.getText().toString().trim());
                                params.put("tenta_tap_no", tenta.getText().toString().trim());
                                params.put("level", level.getText().toString().trim());
                                params.put("school_popu_total", totpopulation.getText().toString().trim());
                                params.put("gws_kaa", gws.getText().toString().trim());
                                params.put("community", community.getText().toString().trim());
                                params.put("total", total.getText().toString().trim());
                                params.put("funder", funder.getText().toString().trim());
//                                params.put("awc", aw);
                                params.put("created_at", formattedDate.trim());
                                params.put("api_token", token);
                                params.put("ca_id", caId);


                                JSONObject parameter = new JSONObject(params);
                                Log.e("JSON:", parameter.toString());

                                mJobManager.addJobInBackground(new PostJob(editUrl, parameter.toString(), Ca_details.this));
                                Toast.makeText(Ca_details.this, "Ca Details Updated", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
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
        fabSave.setVisibility(View.VISIBLE);

        scheme.setFocusableInTouchMode(true);
        village.setFocusableInTouchMode(true);
        location.setFocusableInTouchMode(true);
        location.requestFocus();
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

    @Override
    public void responseSuccess(Response response) {

    }

    @Override
    public void responseFail(String msg) {

    }
}
