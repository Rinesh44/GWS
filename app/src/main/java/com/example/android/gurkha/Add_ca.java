package com.example.android.gurkha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Add_ca extends AppCompatActivity {
    EditText district, scheme, village, vdc, wardno, servicetype, hhtotal, tapno, totalpopn, level, population, gws, community, total, funder;
    Button button;
    private static final String url = "http://pagodalabs.com.np/gws/ca/api/ca";
    SearchableSpinner awc;
    TextView hiddenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ca);

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
        funder = (EditText) findViewById(R.id.textfunder);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);


        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items);
        awc.setAdapter(adapt_awc);

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mdistrict = district.getText().toString();
                    String mscheme = scheme.getText().toString();
                    String mvillage = village.getText().toString();
                    String mvdc = vdc.getText().toString();
                    String mwardno = wardno.getText().toString();
                    String mservicetype = servicetype.getText().toString();
                    String mhhtotal = hhtotal.getText().toString();
                    String mtapno = tapno.getText().toString();
                    String mtotalpopn = totalpopn.getText().toString();
                    String mlevel = level.getText().toString();
                    String mpopulation = population.getText().toString();
                    String mgws = gws.getText().toString();
                    String mcommunity = community.getText().toString();
                    String mtotal = total.getText().toString();
                    String mfunder = funder.getText().toString();
                    String mAwc = awc.getSelectedItem().toString();

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();


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
                    params.put("funder", mfunder);
                    params.put("awc", mAwc);
                    params.put("created_at", mHiddenDate);

                    JSONObject parameter = new JSONObject(params);


                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, parameter.toString());
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

                    Toast.makeText(Add_ca.this, "Community Aid Details Added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Add_ca.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }

            }


        });


    }
}
