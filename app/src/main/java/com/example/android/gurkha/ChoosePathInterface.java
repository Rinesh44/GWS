package com.example.android.gurkha;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Shaakya on 10/16/2017.
 */

public interface ChoosePathInterface {

    @GET
    Call<ResponseBody> getResponse(@Url String url);
}
