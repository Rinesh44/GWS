package com.example.android.gurkha;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Shaakya on 10/16/2017.
 */

public interface ChoosePathInterface {

    @GET("gws/track/api/track/")
    Call<ResponseBody> getResponse();
}
