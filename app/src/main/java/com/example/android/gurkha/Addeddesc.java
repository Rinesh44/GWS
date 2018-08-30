package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Addeddesc extends AppCompatActivity {
    Toolbar toolbar;
    TextView name, location, latitude, longitude, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addeddesc);

        toolbar = (Toolbar) findViewById(R.id.select);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");

        name = (TextView) findViewById(R.id.textName);
        name.setTypeface(face);
        location = (TextView) findViewById(R.id.textlocation);
        location.setTypeface(face);
        age = (TextView) findViewById(R.id.textage);
        age.setTypeface(face);
        latitude = (TextView) findViewById(R.id.textlatidude);
        latitude.setTypeface(face);
        longitude = (TextView) findViewById(R.id.textlongitude);
        longitude.setTypeface(face);

        Intent i = getIntent();

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtlocation = i.getStringExtra("address");
        location.setText(txtlocation);

        String txtage = i.getStringExtra("age");
        age.setText(txtage);

        String txtlatitude = i.getStringExtra("latitude");
        latitude.setText(txtlatitude);

        String txtlongitude = i.getStringExtra("longitude");
        longitude.setText(txtlongitude);
    }
}
