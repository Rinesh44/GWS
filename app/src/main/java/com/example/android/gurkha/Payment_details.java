package com.example.android.gurkha;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Payment_details extends AppCompatActivity {
    TextView name, paiddate, granted, paid, unpaidamount, grant, itemsgiven, category, totalcost, grantedamount, datehandedover, pvno, sponsername, surname;
    EditText armyno;
    Toolbar toolbar;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        name = (TextView) findViewById(R.id.textperson);
        name.setTypeface(face);
        surname = (TextView) findViewById(R.id.textpersonsurname);
        surname.setTypeface(face);
        armyno = (EditText) findViewById(R.id.textarmyno);
        armyno.setTypeface(face);
        paiddate = (TextView) findViewById(R.id.textpaiddate);
        paiddate.setTypeface(face);
        granted = (TextView) findViewById(R.id.textgranted);
        granted.setTypeface(face);
        paid = (TextView) findViewById(R.id.textpaid);
        paid.setTypeface(face);
        unpaidamount = (TextView) findViewById(R.id.textunpaidamount);
        unpaidamount.setTypeface(face);
        grant = (TextView) findViewById(R.id.textgrant);
        grant.setTypeface(face);
        itemsgiven = (TextView) findViewById(R.id.textitemsgiven);
        itemsgiven.setTypeface(face);
        category = (TextView) findViewById(R.id.textcategory);
        category.setTypeface(face);
        totalcost = (TextView) findViewById(R.id.texttotalcost);
        totalcost.setTypeface(face);
        grantedamount = (TextView) findViewById(R.id.textgrantedamount);
        grantedamount.setTypeface(face);
        datehandedover = (TextView) findViewById(R.id.textdategrantedover);
        datehandedover.setTypeface(face);
        pvno = (TextView) findViewById(R.id.textpvno);
        pvno.setTypeface(face);
        sponsername = (TextView) findViewById(R.id.textsponsername);
        sponsername.setTypeface(face);


        fabSave = findViewById(R.id.save);

        Intent i = getIntent();

        String txtname = i.getStringExtra("name");
        name.setText(txtname);

        String txtpaiddate = i.getStringExtra("paid_date");
        paiddate.setText(txtpaiddate);

        String txtsurname = i.getStringExtra("surname");
        surname.setText(txtsurname);

        String txtarmyno = i.getStringExtra("army_no");
        armyno.setText(txtarmyno);

        String txtgranted = i.getStringExtra("granted");
        granted.setText(txtgranted);

        String txtpaid = i.getStringExtra("paid");
        paid.setText(txtpaid);

        String txtunpaidamount = i.getStringExtra("unpaid_amount");
        unpaidamount.setText(txtunpaidamount);

        String txtgrant = i.getStringExtra("grant");
        grant.setText(txtgrant);

        String txtitemsgiven = i.getStringExtra("items_given");
        itemsgiven.setText(txtitemsgiven);

        String txtcategory = i.getStringExtra("category");
        category.setText(txtcategory);

        String txttotalcost = i.getStringExtra("total_cost");
        totalcost.setText(txttotalcost);

        String txtgrantedamount = i.getStringExtra("granted_amount");
        grantedamount.setText(txtgrantedamount);

        String txtdatehandedover = i.getStringExtra("date_handed_over");
        datehandedover.setText(txtdatehandedover);

        String txtpvno = i.getStringExtra("pv_no");
        pvno.setText(txtpvno);

        String txtsponsername = i.getStringExtra("sponsor_name");
        sponsername.setText(txtsponsername);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Map<String, String> params = new HashMap<String, String>();
                params.put("personal_id", id);
                params.put("paid_date", mpaiddate);
                params.put("paid", mpaid);
                params.put("unpaid_amount", munpaidamount);
                params.put("items_given", mitemsgiven);
                params.put("category", mcategory);
                params.put("total_cost", mtotalcost);
                params.put("date_handed_over", mdategrantedover);
                params.put("granted_amount", mgranted_amount);
                params.put("sponsor_name", msponsername);
                params.put("pv_no", mpvno);
                params.put("grant", mgrant);
                params.put("awc", mAwc);
                params.put("created_at", mHiddenDate);
                params.put("api_token", token);*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                makeTextEditable();
                break;
        }
        return false;
    }

    private void makeTextEditable() {
        fabSave.setVisibility(View.VISIBLE);

        name.setFocusableInTouchMode(true);
        paiddate.setFocusableInTouchMode(true);
        surname.setFocusableInTouchMode(true);
        armyno.setFocusableInTouchMode(true);
        granted.setFocusableInTouchMode(true);
        paid.setFocusableInTouchMode(true);
        unpaidamount.setFocusableInTouchMode(true);
        grant.setFocusableInTouchMode(true);
        itemsgiven.setFocusableInTouchMode(true);
        category.setFocusableInTouchMode(true);
        totalcost.setFocusableInTouchMode(true);
        grantedamount.setFocusableInTouchMode(true);
        datehandedover.setFocusableInTouchMode(true);
        pvno.setFocusableInTouchMode(true);
        sponsername.setFocusableInTouchMode(true);
    }
}
