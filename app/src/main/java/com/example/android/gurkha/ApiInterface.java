package com.example.android.gurkha;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;


/**
 * Created by Shaakya on 10/13/2017.
 */

public interface ApiInterface {

    @GET
    Call<ResponseBody> getResponse(@Url String url);
}
