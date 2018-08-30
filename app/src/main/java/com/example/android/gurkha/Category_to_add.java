package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Category_to_add extends AppCompatActivity {
    ListView category;
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    TextView title;
    Typeface face;
    List<String> values;
    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_to_add);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);


        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        category = (ListView) findViewById(R.id.list_category);
        values = new ArrayList<String>();
        values.add("Individual Details");
        values.add("Payment Details");
        values.add("Investigation Details");
        values.add("Area Welfare Center");
        values.add("Community Aid");


        adapter = new ArrayAdapter<String>(Category_to_add.this, android.R.layout.simple_list_item_1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(Category_to_add.this, (position > lastPosition) ? R.anim.item_animation_fall_down : R.anim.item_animation_fall_down);
                view.startAnimation(animation);
                lastPosition = position;
                return view;
            }
        };
        category.setAdapter(adapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent personal = new Intent(Category_to_add.this, Add_personal.class);
                    startActivity(personal);
                }

                if (position == 1) {
                    Intent payment = new Intent(Category_to_add.this, Add_payment.class);
                    startActivity(payment);
                }

                if (position == 2) {
                    Intent investigation = new Intent(Category_to_add.this, Add_investigation.class);
                    startActivity(investigation);
                }

                if (position == 3) {
                    Intent districtawc = new Intent(Category_to_add.this, Add_awc.class);
                    startActivity(districtawc);
                }

                if (position == 4) {
                    Intent ca = new Intent(Category_to_add.this, Add_ca.class);
                    startActivity(ca);
                }
            }


        });
    }
}
