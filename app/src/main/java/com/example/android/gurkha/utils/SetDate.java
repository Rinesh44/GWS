package com.example.android.gurkha.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by darknight on 4/2/18.
 */

public class SetDate implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText mEditText;
    public static final String DATE_SERVER_PATTERN = "dd-MM-yyyy";
    private DatePickerDialog mDatePickerDialog;
    private Calendar mCalendar;
    Context mContext;

    public SetDate(Context context, EditText mEditText) {
        this.mEditText = mEditText;
        mEditText.setOnClickListener(this);
        mEditText.setFocusable(false);
        mEditText.setGravity(Gravity.CENTER);
        this.mContext = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date date = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_SERVER_PATTERN);
        mEditText.setText(formatter.format(date));
    }


    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        mDatePickerDialog = new DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        mDatePickerDialog.show();
    }

    public DatePickerDialog getDatePickerDialog() {
        return mDatePickerDialog;
    }

}
