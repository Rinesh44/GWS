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

public class SelectAwc extends AppCompatActivity {
    private SearchableSpinner awcSpinner;
    static String awc;
    Typeface face;
    AppCompatButton btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_awc);

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        awcSpinner = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal",
                "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "Dharan", "Kaski"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTypeface(face);
                return view;
            }
        };
        awcSpinner.setAdapter(adapt_awc);


        btn = (AppCompatButton)findViewById(R.id.btnNext);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awc = awcSpinner.getSelectedItem().toString();
                if(awc.equals("Select Area Welfare Center")) {
                    Toast.makeText(SelectAwc.this, "Please select an AWC", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent next = new Intent(SelectAwc.this, Category.class);
                startActivity(next);
                Log.e("selectedAwc:", awc);
            }
        });

    }
}
