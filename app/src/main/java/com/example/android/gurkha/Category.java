package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Category extends AppCompatActivity {
    ListView category;
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    TextView title;
    private static final String url = "http://gws.pagodalabs.com.np/awc/api/awc/";
    List<String> values;
    Typeface face;
    String awc = "Jiri";
    private int lastPosition = -1;
    private boolean searchByAwc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(face);

        checkIfSearchByAwc();

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        category = (ListView) findViewById(R.id.list_category);
        values = new ArrayList<String>();
        values.add("Individual Details");
        values.add("Payment Details");
        values.add("Investigation Details");
        values.add("Area Welfare Center");
        values.add("Community Aid");
        values.add("Device Info");


        adapter = new ArrayAdapter<String>(Category.this, android.R.layout.simple_list_item_1, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTypeface(face);
                Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.down_from_top : R.anim.down_from_top);
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
                    // String mId = String.valueOf(databaseHelper.getId(textInputEditTextEmail.getText().toString().trim()));

                    Intent personal = new Intent(Category.this, People.class);
                    personal.putExtra("search_by_awc", searchByAwc);
                    startActivity(personal);
                }

                if (position == 1) {
                    Intent payment = new Intent(Category.this, Payment.class);
                    payment.putExtra("search_by_awc", searchByAwc);
                    startActivity(payment);
                }

                if (position == 2) {
                    Intent investigation = new Intent(Category.this, Investigation.class);
                    investigation.putExtra("search_by_awc", searchByAwc);
                    startActivity(investigation);
                }

                if (position == 3) {
                    Intent districtawc = new Intent(Category.this, awc_name1.class);
                    districtawc.putExtra("search_by_awc", searchByAwc);
                    startActivity(districtawc);
                }

                if (position == 4) {
                    Intent ca = new Intent(Category.this, Ca_servicetype.class);
                    ca.putExtra("search_by_awc", searchByAwc);
                    startActivity(ca);
                }

                if (position == 5) {
                    Intent info = new Intent(Category.this, SimDetails.class);
                    startActivity(info);
                }

            }

        });
    }

    private void checkIfSearchByAwc() {
        Intent i = getIntent();
        searchByAwc = i.getBooleanExtra("search_by_awc", false);
    }


}
