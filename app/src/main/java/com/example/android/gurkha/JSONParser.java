package com.example.android.gurkha;

/**
 * Created by Shaakya on 7/19/2017.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class JSONParser {
    /********
     * URLS
     *******/
    private static final String MAIN_URL = "http://pagodalabs.com.np/gws/personal_detail/api/personal_detail?api_token=" + MainActivity.accessToken;
    private static final String MAIN_URL2 = "http://pagodalabs.com.np/gws/payment_distribution/api/payment_distribution";
    private static final String MAIN_URL3 = "http://pagodalabs.com.np/gws/investigate/api/investigate";
    private static final String MAIN_URL4 = "http://pagodalabs.com.np/gws/ca/api/ca?api_token=" + MainActivity.accessToken;
    private static final String MAIN_URL5 = "http://pagodalabs.com.np/gws/track/api/track?api_token=" + MainActivity.accessToken;


    /**
     * TAGs Defined Here...
     */
    public static final String TAG = "TAG";
    /**
     * Key to Send
     */
    private static final String KEY_USER_ID = "user_id";
    /**
     * Response
     */
    private static Response response;

    /**
     * Get Data From WEB
     *
     * @return JSON Object
     */
    public static JSONObject getDataFromWeb() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }

    public static JSONObject getPaymentData() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL2)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }

    public static JSONObject getInvestigationData() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL3)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }

    public static JSONObject getCAData() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL4)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }

    public static JSONObject getPath() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL5)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }


}
