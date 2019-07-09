package com.example.android.gurkha.activities.AddData;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.EventListener.ResponseListener;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.application.GurkhaApplication;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import okhttp3.MediaType;
import okhttp3.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Add_ca extends AppCompatActivity implements ResponseListener {
    EditText district, scheme, village, vdc, wardno, servicetype, hhtotal, tapno, totalpopn, level, population, gws, community, total;
    Button button;
    private static final String url = "http://gws.pagodalabs.com.np/ca/api/ca?api_token=";
    SearchableSpinner awc;
    Typeface face;
    String token;
    Toolbar toolbar;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    TextView hiddenDate;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ca);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        district = (EditText) findViewById(R.id.textdistrict);
        scheme = (EditText) findViewById(R.id.textscheme);
        village = (EditText) findViewById(R.id.textvillage);
        vdc = (EditText) findViewById(R.id.textvdc);
        wardno = (EditText) findViewById(R.id.textwno);
        servicetype = (EditText) findViewById(R.id.textservicetype);
        hhtotal = (EditText) findViewById(R.id.texthhtotal);
        tapno = (EditText) findViewById(R.id.texttenta);
        totalpopn = (EditText) findViewById(R.id.texttotpopulation);
        level = (EditText) findViewById(R.id.textlevel);
        population = (EditText) findViewById(R.id.textpopulation);
        gws = (EditText) findViewById(R.id.textgws);
        community = (EditText) findViewById(R.id.textcommunity);
        total = (EditText) findViewById(R.id.texttotal);
//        funder = (EditText) findViewById(R.id.textfunder);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "Kaski", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
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

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mdistrict = district.getText().toString().trim();
                    String mscheme = scheme.getText().toString().trim();
                    String mvillage = village.getText().toString().trim();
                    String mvdc = vdc.getText().toString().trim();
                    String mwardno = wardno.getText().toString().trim();
                    String mservicetype = servicetype.getText().toString().trim();
                    String mhhtotal = hhtotal.getText().toString().trim();
                    String mtapno = tapno.getText().toString().trim();
                    String mtotalpopn = totalpopn.getText().toString().trim();
                    String mlevel = level.getText().toString().trim();
                    String mpopulation = population.getText().toString().trim();
                    String mgws = gws.getText().toString().trim();
                    String mcommunity = community.getText().toString().trim();
                    String mtotal = total.getText().toString().trim();
//                    String mfunder = funder.getText().toString().trim();
                    String mAwc = awc.getSelectedItem().toString().trim();

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();

                    if(sessionManager.getUserDetails() != null) {

                        HashMap<String, String> user = sessionManager.getUserDetails();
                        token = user.get(SessionManager.KEY_TOKEN);
                    }
                    if(fbSessionManager.getUserDetails() != null) {
                        HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                        if(fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                    }

                    if(servicetype.getText().toString().equals("")){
                        Toast.makeText(Add_ca.this, "Please enter service type", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("scheme_no", mscheme);
                    params.put("location", mdistrict);
                    params.put("village", mvillage);
                    params.put("vdc", mvdc);
                    params.put("ward_no", mwardno);
                    params.put("service_type", mservicetype);
                    params.put("hh_total", mhhtotal);
                    params.put("population_total", mtotalpopn);
                    params.put("tenta_tap_no", mtapno);
                    params.put("level", mlevel);
                    params.put("school_popu_total", mpopulation);
                    params.put("gws_kaa", mgws);
                    params.put("community", mcommunity);
                    params.put("total", mtotal);
//                    params.put("funder", mfunder);
                    params.put("awc", mAwc);
                    params.put("created_at", mHiddenDate);
                    params.put("api_token", token);

                if(mAwc.equals("Select Area Welfare Center")){
                    Toast.makeText(Add_ca.this, "Please select Area welfare center", Toast.LENGTH_SHORT).show();
                    return;
                }

                    JSONObject parameter = new JSONObject(params);


//                    OkHttpClient client = new OkHttpClient();

                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), Add_ca.this));

                /*RequestBody body = RequestBody.create(JSON, parameter.toString());
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

                    });*/

                    Toast.makeText(Add_ca.this, "Community Aid Details Added", Toast.LENGTH_SHORT).show();
                    finish();
               /* } else {
                    Toast.makeText(Add_ca.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
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

    @Override
    public void responseSuccess(Response response) {

    }

    @Override
    public void responseFail(String msg) {

    }
}
