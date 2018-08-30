package com.example.android.gurkha.QnA;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.ApiInterface;
import com.example.android.gurkha.InternetConnection;
import com.example.android.gurkha.R;
import com.example.android.gurkha.SearchPerson;
import com.example.android.gurkha.SessionManager;

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

public class Results extends AppCompatActivity {
TextView social, economical, health, total, socialHighest, healthHighest, economicalHighest;
ProgressDialog progressDialog;
    private static String base_url = "http://pagodalabs.com.np/";
    String totalHealth, totalSocial, totalEconomical, vtotal;
    Call<ResponseBody> call;
    String token;
    Toolbar toolbar;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sessionManager = new SessionManager(getApplicationContext());

//        socialHighest = findViewById(R.id.social_value);
//        economicalHighest = findViewById(R.id.economical_value);
//        healthHighest = findViewById(R.id.health_value);

        total = findViewById(R.id.total_value);

        social = findViewById(R.id.total_social_value);
        economical = findViewById(R.id.total_economic_value);
        health = findViewById(R.id.total_health_value);

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
            if (!InternetConnection.checkConnection(Results.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 28800)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(Results.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Results.ResponseCacheInterceptor())
                .addInterceptor(new Results.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Results.this
                        .getCacheDir(),
                        "addInvestigationApiResponses"), 1024 * 1024))
                .build();

        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/form/api/total?api_token=" + token);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Add_investigation", t.toString());
                Toast.makeText(Results.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (!(response.isSuccessful())){
                        Toast.makeText(Results.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);

                    JSONObject jsonObject = new JSONObject(myResponse);
                    Log.e("json", jsonObject.toString());
                    totalHealth = jsonObject.getString("total_health");
                    totalEconomical = jsonObject.getString("total_economic");
                    totalSocial = jsonObject.getString("total_social");
                    vtotal = jsonObject.getString("total");
//                    hHighest = jsonObject.getString("health_higest");
//                    eHighest = jsonObject.getString("economic_higest");
//                    sHighest = jsonObject.getString("social_higest");



                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();


                Results.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

//                        socialHighest.setText(sHighest);
//                        economicalHighest.setText(eHighest);
//                        healthHighest.setText(hHighest);
                        total.setText(vtotal);
                        social.setText(totalSocial);
                        health.setText(totalHealth);
                        economical.setText(totalEconomical);

                    }

                });
            }
        });
    }
}
