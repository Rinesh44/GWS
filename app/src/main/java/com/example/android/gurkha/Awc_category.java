package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Awc_category extends AppCompatActivity {
    ListView category;
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    TextView title;
    List<String> values;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nunito.otf");
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        category = (ListView) findViewById(R.id.list_category);
        values = new ArrayList<String>();
        values.add("Earthquake Rebuild");
        values.add("Earthquake Referbishment");
        values.add("Hardship Grant");


        adapter = new ArrayAdapter<String>(Awc_category.this, android.R.layout.simple_list_item_1, values);
        category.setAdapter(adapter);

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent names1 = new Intent(Awc_category.this, awc_name1.class);
                    startActivity(names1);
                }

              /*  if (position == 1) {
                    Intent names2 = new Intent(Awc_category.this, awc_name2.class);
                    startActivity(names2);
                }

                if (position == 2) {
                    Intent names3 = new Intent(Awc_category.this, awc_name3.class);
                    startActivity(names3);
                }
*/
            }


        });
    }
}
