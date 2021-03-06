package com.example.android.gurkha;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.widget.TextView;


public class SimDetails extends AppCompatActivity {
    TextView id, ipAddress, imei;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_details);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        id = (TextView) findViewById(R.id.serialid);
        ipAddress = (TextView) findViewById(R.id.ip);
        imei = (TextView) findViewById(R.id.imei);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //---get the SIM card ID---
        String simID = tm.getSimSerialNumber();
        if (simID != null) {
            id.setText(simID);
        }

        //---get the IMEI number---
        String IMEI = tm.getDeviceId();
        if (IMEI != null) {
            imei.setText(IMEI);
        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        ipAddress.setText(ip);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }
}
