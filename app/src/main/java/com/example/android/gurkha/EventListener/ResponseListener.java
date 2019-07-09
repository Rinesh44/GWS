package com.example.android.gurkha.EventListener;

import org.json.JSONObject;

import okhttp3.Response;

public interface ResponseListener {

    void responseSuccess(Response response);

    void responseFail(String msg);
}
