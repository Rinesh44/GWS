package com.example.android.gurkha.activities.QnA;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.FbSessionManager;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;
import com.example.android.gurkha.application.GurkhaApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Questions extends AppCompatActivity {
    private static String base_url = "http://gws.pagodalabs.com.np/";
    Call<ResponseBody> call;
    String token;
    String userId;
    Typeface face;
    SessionManager sessionManager;
    FbSessionManager fbSessionManager;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    // SearchableSpinner name;
    private static final String url = "http://gws.pagodalabs.com.np/form_poll/api/form?api_token=";
    static final String TAG = Questions.class.getSimpleName();
    LinearLayout containerLayout;
    private ArrayList<Object> list;
    RadioGroup grp;
    TextView question;
    AppCompatButton done;
    Object[] person_items;
    JobManager mJobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        name = (SearchableSpinner) findViewById(R.id.textperson);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        list = new ArrayList<>();
//        done = findViewById(R.id.btn_done);
        containerLayout = findViewById(R.id.container_layout);

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        try {
//            run();
            getDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }

        done = new AppCompatButton(Questions.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 40);
        done.setLayoutParams(params);
        done.setClickable(true);
        done.setFocusable(true);
        done.setText("done");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (InternetConnection.checkConnection(getApplicationContext())) {
//                    String mPerson = name.getSelectedItem().toString();
//                    String id = mPerson.substring(mPerson.lastIndexOf(":") + 1);
//                    Log.e("identity", id);
                String mFormId = "1";
                int checkedRbtnId = grp.getCheckedRadioButtonId();
                RadioButton rBtn = (RadioButton) findViewById(checkedRbtnId);
                String mFieldName = question.getText().toString().trim();
                String mValue = rBtn.getText().toString().trim();

                if (sessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                    userId = user.get(SessionManager.KEY_ID);

                }

                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = fbSessionManager.getUserDetails();
                    if (user.get(SessionManager.KEY_TOKEN) != null) {
                        token = user.get(SessionManager.KEY_TOKEN);
                        userId = user.get(SessionManager.KEY_ID);
                    }
                }


                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> params = new HashMap<String, String>();
                params.put("form_id", mFormId);
                params.put("field_name", mFieldName);
                params.put("personal_id", userId);
                params.put("value", mValue);

                JSONObject jsonFirst = new JSONObject(params);

                JSONObject parameter = new JSONObject();
                try {
                    parameter.put("0", jsonFirst);
                    parameter.put("api_token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("JSON:", parameter.toString());
                mJobManager.addJobInBackground(new PostJob(url, parameter.toString(), Questions.this));


              /*  OkHttpClient client = new OkHttpClient();
                    final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("content-type", "application/json; charset=utf-8")
                            .build();

                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.e(TAG + "postResponse:", call.request().body().toString());
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                                Log.e(TAG + "postResponse:", response.body().string());

                        }

                    });*/

                Toast.makeText(Questions.this, "Data Added", Toast.LENGTH_SHORT).show();

                finish();
           /* } else

            {
                Toast.makeText(Questions.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
            }*/
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }

    /**
     * Interceptor to cache data and maintain it for a minute.
     * <p>
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     * <p>
     * If the device is offline, stale (at most eight hours old)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(Questions.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 1209600)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }

    }

   /* void run() throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Questions.ResponseCacheInterceptor())
                .addInterceptor(new Questions.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Questions.this
                        .getCacheDir(),
                        "ApiResponses"), 256 * 1024))
                .build();


        if(sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if(fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> user = fbSessionManager.getUserDetails();
            if(user.get(SessionManager.KEY_TOKEN) != null)
            token = user.get(SessionManager.KEY_TOKEN);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Questions", t.toString());
                Toast.makeText(Questions.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())){
                        Toast.makeText(Questions.this, "null response", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);

                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("personal");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);

                        String name = innerObject.getString("name");
                        String surname = innerObject.getString("surname");
                        String army_no = innerObject.getString("army_no");
                        String personal_id = innerObject.getString("personal_id");

                        list.add(name + " " + surname + " " + army_no + "," + " " + "id:" + personal_id);
                        person_items = list.toArray();

                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                Questions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Questions.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        name.setAdapter(adapt_person);
                    }

                });
            }
        });
    }*/

    void getDetails() throws IOException {
        progressDialog = new ProgressDialog(Questions.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Questions.ResponseCacheInterceptor())
                .addInterceptor(new Questions.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Questions.this
                        .getCacheDir(),
                        "ApiResponses"), 1024 * 1024))
                .build();

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }
        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> user = fbSessionManager.getUserDetails();
            if (user.get(SessionManager.KEY_TOKEN) != null)
                token = user.get(SessionManager.KEY_TOKEN);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("form_poll/api/form?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Questions", t.toString());
                Toast.makeText(Questions.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(Questions.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);


                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("rows");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);

                        String qName = innerObject.getString("column_name");
                        String qValues = innerObject.getString("column_value");
                        List<String> valueList = Arrays.asList(qValues.split(","));

                        question = new TextView(Questions.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMarginStart(20);
                        question.setText(qName);
                        question.setLayoutParams(params);
                        question.setTextSize(18);
                        question.setTextColor(Color.BLACK);
                        question.setTypeface(face);


                        containerLayout.addView(question);

                        grp = new RadioGroup(Questions.this);
                        for (int v = 0; v < valueList.size(); v++) {
                            RadioButton rBtn = new RadioButton(Questions.this);
                            rBtn.setText(valueList.get(v));
                            rBtn.setTypeface(face);
                            rBtn.setLayoutParams(params);
                            grp.addView(rBtn);
                        }

                        containerLayout.addView(grp);

                    }

                    containerLayout.addView(done);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                Questions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }

                });
            }
        });
    }
}
