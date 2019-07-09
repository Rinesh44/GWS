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

public class awc_details extends AppCompatActivity implements ResponseListener {
    private static final String url = "http://gws.pagodalabs.com.np/awc/api/editAwc";

    TextView name, address, wpsp, armyno, approvedgrant, cost, supervisor, remarks, status, location, category;
    Toolbar toolbar;

    FloatingActionButton fabSave;
    String token;
    JobManager mJobManager;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awc_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

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

        fabSave = findViewById(R.id.save);

        Intent i = getIntent();

        String awc_id = i.getStringExtra("id");

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

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(awc_details.this);
                } else {
                    builder = new AlertDialog.Builder(awc_details.this);
                }
                builder.setTitle("Edit")
                        .setMessage("Are you sure to save the edited fields?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with edit
                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                String formattedDate = df.format(c.getTime());

                                String mHiddenDate = formattedDate.trim();

//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("awc", SelectAwc.awc);
                                params.put("id", awc_id);
                                params.put("name", name.getText().toString());
                                params.put("address", address.getText().toString());
                                params.put("approved_grant", approvedgrant.getText().toString());
                                params.put("army_no", armyno.getText().toString().trim());
                                params.put("supervisor", supervisor.getText().toString().trim());
                                params.put("remarks", remarks.getText().toString().trim());
                                params.put("category", category.getText().toString().trim());
                                params.put("wp_sp", wpsp.getText().toString().trim());
                                params.put("status", status.getText().toString().trim());
//                                params.put("awc", mAwc);
                                params.put("created_at", mHiddenDate);
                                params.put("api_token", token);


                                JSONObject parameter = new JSONObject(params);
                                Log.e("JSON:", parameter.toString());

                                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), awc_details.this));
                                Toast.makeText(awc_details.this, "awc Details Updated", Toast.LENGTH_SHORT).show();
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

        name.setFocusableInTouchMode(true);
        address.setFocusableInTouchMode(true);
        approvedgrant.setFocusableInTouchMode(true);
        cost.setFocusableInTouchMode(true);
        supervisor.setFocusableInTouchMode(true);
        remarks.setFocusableInTouchMode(true);
        status.setFocusableInTouchMode(true);
        location.setFocusableInTouchMode(true);
        location.requestFocus();
        wpsp.setFocusableInTouchMode(true);
        category.setFocusableInTouchMode(true);
        armyno.setFocusableInTouchMode(true);
    }

    @Override
    public void responseSuccess(Response response) {

    }

    @Override
    public void responseFail(String msg) {

    }
}
