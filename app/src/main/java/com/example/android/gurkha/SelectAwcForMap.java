package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectAwcForMap extends AppCompatActivity {
    private SearchableSpinner awcSpinner;
    static String awc;
    AppCompatButton btn;
    Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_awc_for_map);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        awcSpinner = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "Kaski"};

        List<String> awc_list = Arrays.asList(awc_items);

        Collections.sort(awc_list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });


        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        awcSpinner.setAdapter(adapt_awc);


        btn = (AppCompatButton) findViewById(R.id.btnNext);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awc = awcSpinner.getSelectedItem().toString();

                if (awc.equals("Select Area Welfare Center")) {
                    Toast.makeText(SelectAwcForMap.this, "Please select an AWC", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent next = new Intent(SelectAwcForMap.this, SearchPerson.class);
                startActivity(next);
                Log.e("selectedAwc:", awc);
            }
        });
    }
}
