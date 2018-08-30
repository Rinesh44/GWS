package com.example.android.gurkha;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Add_awc extends AppCompatActivity {
    EditText name, armyno, address, grant, cost, supervisor, remarks;
    Button button;
    private static final String url = "http://pagodalabs.com.np/gws/awc/api/awc?api_token=";
    SearchableSpinner awc;
    TextView hiddenDate;
    Typeface face;
    Toolbar toolbar;
    String token;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_awc);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling","Dharan", "Kaski","The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        awc.setAdapter(adapt_awc);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        final Spinner wpsp = (Spinner) findViewById(R.id.textwpsp);
        String[] wpsp_items = new String[]{"WP/SP", "WP", "SP"};
        ArrayAdapter<String> adapat_wpsp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, wpsp_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        wpsp.setAdapter(adapat_wpsp);

        final Spinner stat = (Spinner) findViewById(R.id.textstatus);
        String[] stat_items = new String[]{"STATUS", "Expired", "Ceased", "Civil", "Move To Uk"};
        ArrayAdapter<String> adapt_stat = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stat_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        stat.setAdapter(adapt_stat);

        final Spinner type = (Spinner) findViewById(R.id.texttype);
        String[] type_items = new String[]{"CONDITION", "EQ Rebuild", "EQ Referbishment", "Hardship Grant"};
        ArrayAdapter<String> adapt_type = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, type_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        type.setAdapter(adapt_type);

        name = (EditText) findViewById(R.id.textName);
        armyno = (EditText) findViewById(R.id.textarmyno);
        address = (EditText) findViewById(R.id.textaddress);
        grant = (EditText) findViewById(R.id.textgrant);
        cost = (EditText) findViewById(R.id.textcost);
        supervisor = (EditText) findViewById(R.id.textsupervisor);
        remarks = (EditText) findViewById(R.id.textremarks);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        button = (Button) findViewById(R.id.btn);

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mname = name.getText().toString();
                    String marmyno = armyno.getText().toString();
                    String maddress = address.getText().toString();
                    String mgrant = grant.getText().toString();
                    String mcost = cost.getText().toString();
                    String msupervisor = supervisor.getText().toString();
                    String mremarks = remarks.getText().toString();

                    String mcondition = type.getSelectedItem().toString();
                    String mwpsp = wpsp.getSelectedItem().toString();
                    String mstatus = stat.getSelectedItem().toString();
                    String mAwc = awc.getSelectedItem().toString();

                    Calendar c = Calendar.getInstance();

                    if(sessionManager.getUserDetails() != null) {

                        HashMap<String, String> user = sessionManager.getUserDetails();
                        token = user.get(SessionManager.KEY_TOKEN);
                    }

                    if(fbSessionManager.getUserDetails() != null) {
                        HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                        if(fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                    }

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();

//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", mname);
                    params.put("address", maddress);
                    params.put("approved_grant", mgrant);
                    params.put("army_no", marmyno);
                    params.put("construction_cost", mcost);
                    params.put("supervisor", msupervisor);
                    params.put("remarks", mremarks);
                    params.put("category", mcondition);
                    params.put("wp_sp", mwpsp);
                    params.put("status", mstatus);
                    params.put("awc", mAwc);
                    params.put("created_at", mHiddenDate);
                    params.put("api_token", token);

                if(mAwc.equals("Select Area Welfare Center")){
                    Toast.makeText(Add_awc.this, "Please select Area welfare center", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mcondition.equals("CONDITION")){
                    Toast.makeText(Add_awc.this, "Please select condition", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mwpsp.equals("WP/SP")){
                    Toast.makeText(Add_awc.this, "Please select wp/sp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mstatus.equals("STATUS")){
                    Toast.makeText(Add_awc.this, "Please select status", Toast.LENGTH_SHORT).show();
                    return;
                }

                    JSONObject parameter = new JSONObject(params);
//                    OkHttpClient client = new OkHttpClient();

                    mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));


                    /*final RequestBody body = RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("content-type", "application/json; charset=utf-8")
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("response", call.request().body().toString());

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.e("response", response.body().string());
                        }

                    });
*/
                    Toast.makeText(Add_awc.this, "Area Welfare Details Added", Toast.LENGTH_SHORT).show();
                    finish();
                /*} else {
                    Toast.makeText(Add_awc.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }
}
