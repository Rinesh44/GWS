package com.example.android.gurkha.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.android.gurkha.R;

/**
 * Created by Shaakya on 6/29/2017.
 */
public class VolleyErrorMessage {

    public static void handleVolleyErrors(TextView tvVolleyError, VolleyError error){
        tvVolleyError.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            tvVolleyError.setText(R.string.error_timeout);
        }else if (error instanceof AuthFailureError) {
            tvVolleyError.setText(R.string.error_auth_failure);
        } else if (error instanceof ServerError) {
            tvVolleyError.setText(R.string.error_server);
        } else if (error instanceof NetworkError) {
            tvVolleyError.setText(R.string.error_network);
        } else if (error instanceof ParseError) {
            tvVolleyError.setText(R.string.error_parse);
        }
    }

    public static String handleVolleyErrors(Context context, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            return context.getResources().getString(R.string.error_timeout);
        } else if (error instanceof AuthFailureError) {
            return context.getResources().getString(R.string.error_auth_failure);
        } else if (error instanceof ServerError) {
            return context.getResources().getString(R.string.error_server);
        } else if (error instanceof NetworkError) {
            return context.getResources().getString(R.string.error_network);
        } else if (error instanceof ParseError) {
            return context.getResources().getString(R.string.error_parse);
        }
        return null;
    }

}
