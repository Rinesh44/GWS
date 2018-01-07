package com.example.android.gurkha;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Shaakya on 12/17/2017.
 */

public class Service extends android.app.Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //signs out after 8 weeks
        CountDownTimer timer = new CountDownTimer(1209600000, 1000) {

            public void onTick(long millisUntilFinished) {
                //Some code
            }

            public void onFinish() {
                //Logout
                MainActivity.session.logoutUser();
            }
        };

        timer.start();

    }


}
