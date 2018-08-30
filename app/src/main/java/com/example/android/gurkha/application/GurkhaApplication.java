package com.example.android.gurkha.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.example.android.gurkha.JobQueue.PostJob;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Shaakya on 6/29/2017.
 */

public class GurkhaApplication extends MultiDexApplication {
    private static GurkhaApplication sInstance;
    public JobManager jobManager;

    public GurkhaApplication() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getJobManager();// ensure it is created
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                });

        jobManager = new JobManager(builder.build());
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    public static GurkhaApplication getInstance() {
        return sInstance;
    }
}