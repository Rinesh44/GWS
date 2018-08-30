package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
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
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.JobQueue.PutJob;
import com.example.android.gurkha.application.GurkhaApplication;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Investigation_details extends AppCompatActivity {
    private static final String url = "http://pagodalabs.com.np/gws/investigate/api/investigate?api_token=";
    TextView name, surname, army_no, investigator, paymentbase, date, startdate, reviewdate;
    Toolbar toolbar;
    FloatingActionButton fabSave;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    String token;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        if(sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if(fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if(fbUser.get(SessionManager.KEY_TOKEN) != null)
                token = fbUser.get(SessionManager.KEY_TOKEN);
        }

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

        fabSave = findViewById(R.id.save);

        Intent i = getIntent();

        String txtId = i.getStringExtra("personal_id");

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

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());

                Map<String, String> params = new HashMap<String, String>();
                params.put("date", date.getText().toString());
                params.put("start_date", startdate.getText().toString());
                params.put("personal_id", txtId);
                params.put("investigator", investigator.getText().toString());
                params.put("payment_base", paymentbase.getText().toString());
                params.put("review_date", reviewdate.getText().toString());
//                params.put("awc", aw);
                params.put("created_at", formattedDate);
                params.put("api_token", token);

                JSONObject parameter = new JSONObject(params);
                Log.e("JSON:", parameter.toString());

                mJobManager.addJobInBackground(new PutJob(url, parameter.toString()));
                Toast.makeText(Investigation_details.this, "Investigation Details Updated", Toast.LENGTH_SHORT).show();
                finish();
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
        switch (item.getItemId()){
            case R.id.action_edit:
                makeTextEditable();
                break;
        }
        return false;
    }

    private void makeTextEditable() {
        fabSave.setVisibility(View.VISIBLE);

        name.setFocusableInTouchMode(true);
        investigator.setFocusableInTouchMode(true);
        surname.setFocusableInTouchMode(true);
        paymentbase.setFocusableInTouchMode(true);
        date.setFocusableInTouchMode(true);
        startdate.setFocusableInTouchMode(true);
        reviewdate.setFocusableInTouchMode(true);
        army_no.setFocusableInTouchMode(true);
    }
}
