package com.example.android.gurkha;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ShowNotification extends Service {

    private final static String TAG = "ShowNotification";

    @Override
    public void onCreate() {
        super.onCreate();

        Intent mainIntent = new Intent(this, Tracking.class);

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("GWS")
                .setContentText("The path is being recorded")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Path Tracking is On")
                .setWhen(System.currentTimeMillis() + 60 * 1000)
                .build();

        notificationManager.notify(0, noti);

        Log.i(TAG, "Notification created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}