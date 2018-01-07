package com.example.android.gurkha.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shaakya on 6/29/2017.
 */

public class GurkhaApplication extends Application {
    private static GurkhaApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static GurkhaApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

}
