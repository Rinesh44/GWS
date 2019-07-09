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
import android.widget.EditText;
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

public class Payment_details extends AppCompatActivity implements ResponseListener {
    static String editUrl = "http://gws.pagodalabs.com.np/payment_distribution/api/editpayment_distribution";
    TextView name, paiddate, granted, paid, unpaidamount, grant, itemsgiven, category, totalcost, grantedamount, datehandedover, pvno, sponsername, surname;
    EditText armyno;
    Toolbar toolbar;
    FloatingActionButton fabSave;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    String token;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

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

        name = (TextView) findViewById(R.id.textperson);
        name.setTypeface(face);
        surname = (TextView) findViewById(R.id.textpersonsurname);
        surname.setTypeface(face);
        armyno = (EditText) findViewById(R.id.textarmyno);
        armyno.setTypeface(face);
        paiddate = (TextView) findViewById(R.id.textpaiddate);
        paiddate.setTypeface(face);
        granted = (TextView) findViewById(R.id.textgranted);
        granted.setTypeface(face);
        paid = (TextView) findViewById(R.id.textpaid);
        paid.setTypeface(face);
        unpaidamount = (TextView) findViewById(R.id.textunpaidamount);
        unpaidamount.setTypeface(face);
        grant = (TextView) findViewById(R.id.textgrant);
        grant.setTypeface(face);
        itemsgiven = (TextView) findViewById(R.id.textitemsgiven);
        itemsgiven.setTypeface(face);
        category = (TextView) findViewById(R.id.textcategory);
        category.setTypeface(face);
        totalcost = (TextView) findViewById(R.id.texttotalcost);
        totalcost.setTypeface(face);
        grantedamount = (TextView) findViewById(R.id.textgrantedamount);
        grantedamount.setTypeface(face);
        datehandedover = (TextView) findViewById(R.id.textdategrantedover);
        datehandedover.setTypeface(face);
        pvno = (TextView) findViewById(R.id.textpvno);
        pvno.setTypeface(face);
        sponsername = (TextView) findViewById(R.id.textsponsername);
        sponsername.setTypeface(face);


        fabSave = findViewById(R.id.save);


        Intent i = getIntent();

        String paymentId = i.getStringExtra("payment_id");

        String personalId = i.getStringExtra("personal_id");

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtpaiddate = i.getStringExtra("paid_date");
        paiddate.setText(txtpaiddate);

        String txtsurname = i.getStringExtra("surname");
        surname.setText(txtsurname);

        String txtarmyno = i.getStringExtra("army_no");
        armyno.setText(txtarmyno);

        String txtgranted = i.getStringExtra("granted");
        granted.setText(txtgranted);

        String txtpaid = i.getStringExtra("paid");
        paid.setText(txtpaid);

        String txtunpaidamount = i.getStringExtra("unpaid_amount");
        unpaidamount.setText(txtunpaidamount);

        String txtgrant = i.getStringExtra("grant");
        grant.setText(txtgrant);

        String txtitemsgiven = i.getStringExtra("items_given");
        itemsgiven.setText(txtitemsgiven);

        String txtcategory = i.getStringExtra("category");
        category.setText(txtcategory);

        String txttotalcost = i.getStringExtra("total_cost");
        totalcost.setText(txttotalcost);

        String txtgrantedamount = i.getStringExtra("granted_amount");
        grantedamount.setText(txtgrantedamount);

        String txtdatehandedover = i.getStringExtra("date_handed_over");
        datehandedover.setText(txtdatehandedover);

        String txtpvno = i.getStringExtra("pv_no");
        pvno.setText(txtpvno);

        String txtsponsername = i.getStringExtra("sponsor_name");
        sponsername.setText(txtsponsername);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Payment_details.this);
                } else {
                    builder = new AlertDialog.Builder(Payment_details.this);
                }
                builder.setTitle("Edit")
                        .setMessage("Are you sure to save the edited fields?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with edit

                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                String formattedDate = df.format(c.getTime());


//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                Map<String, String> params = new HashMap<String, String>();
                                params.put("personal_id", personalId);
                                params.put("awc", SelectAwc.awc);
                                params.put("paid_date", paiddate.getText().toString().trim());
                                params.put("paid", paid.getText().toString().trim());
                                params.put("unpaid_amount", unpaidamount.getText().toString().trim());
                                params.put("items_given", itemsgiven.getText().toString().trim());
                                params.put("category", category.getText().toString().trim());
                                params.put("total_cost", totalcost.getText().toString().trim());
                                params.put("date_handed_over", datehandedover.getText().toString().trim());
                                params.put("granted_amount", grantedamount.getText().toString().trim());
                                params.put("sponsor_name", sponsername.getText().toString().trim());
                                params.put("pv_no", pvno.getText().toString().trim());
                                params.put("grant", grant.getText().toString().trim());
//                                params.put("awc", mAwc);
                                params.put("created_at", formattedDate.trim());
                                params.put("api_token", token);
                                params.put("payment_id", paymentId);

                                JSONObject parameter = new JSONObject(params);
                                Log.e("JSON:", parameter.toString());

                                mJobManager.addJobInBackground(new PostJob(editUrl, parameter.toString(), Payment_details.this));
                                Toast.makeText(Payment_details.this, "Payment Details Updated", Toast.LENGTH_SHORT).show();
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
        paiddate.setFocusableInTouchMode(true);
        surname.setFocusableInTouchMode(true);
        armyno.setFocusableInTouchMode(true);
        armyno.requestFocus();
        granted.setFocusableInTouchMode(true);
        paid.setFocusableInTouchMode(true);
        unpaidamount.setFocusableInTouchMode(true);
        grant.setFocusableInTouchMode(true);
        itemsgiven.setFocusableInTouchMode(true);
        category.setFocusableInTouchMode(true);
        totalcost.setFocusableInTouchMode(true);
        grantedamount.setFocusableInTouchMode(true);
        datehandedover.setFocusableInTouchMode(true);
        pvno.setFocusableInTouchMode(true);
        sponsername.setFocusableInTouchMode(true);
    }

    @Override
    public void responseSuccess(Response response) {

    }

    @Override
    public void responseFail(String msg) {

    }
}
