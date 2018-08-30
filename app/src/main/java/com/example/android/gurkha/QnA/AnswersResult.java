package com.example.android.gurkha.QnA;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnswersResult extends AppCompatActivity {
    private TextView yes, no, maybe;
    ProgressDialog progressDialog;
    private static String base_url = "http://pagodalabs.com.np/";
    Call<ResponseBody> call;
    String token;
    Toolbar toolbar;

    SessionManager sessionManager;
    String totalYes, totalNo, totalMaybe, percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_result);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initViews();

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }

    public void initViews(){
        yes = findViewById(R.id.total_yes_value);
        no = findViewById(R.id.total_no_value);
        maybe = findViewById(R.id.total_maybe_value);
        sessionManager = new SessionManager(getApplicationContext());

    }

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
            if (!InternetConnection.checkConnection(AnswersResult.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 28800)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(AnswersResult.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new AnswersResult.ResponseCacheInterceptor())
                .addInterceptor(new AnswersResult.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(AnswersResult.this
                        .getCacheDir(),
                        "addInvestigationApiResponses"), 512 * 1024))
                .build();

        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/form_poll/api/total?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("AnswersResult", t.toString());
                Toast.makeText(AnswersResult.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (!(response.isSuccessful())){
                        Toast.makeText(AnswersResult.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);

                    JSONObject jsonObject = new JSONObject(myResponse);
                    Log.e("json", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("poll");
                    for(int i = 0; i<=jsonArray.length();i++ ) {
                        JSONObject innerObject = jsonArray.getJSONObject(i);

                        percentage = innerObject.getString("percentage");

//                    hHighest = jsonObject.getString("health_higest");
//                    eHighest = jsonObject.getString("economic_higest");
//                    sHighest = jsonObject.getString("social_higest");
                    }


                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();


                AnswersResult.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

//                        socialHighest.setText(sHighest);
//                        economicalHighest.setText(eHighest);
//                        healthHighest.setText(hHighest);

                        yes.setText(percentage);
                        no.setText(percentage);
                        maybe.setText(percentage);

                    }

                });
            }
        });
    }
}

