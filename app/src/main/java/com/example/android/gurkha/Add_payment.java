package com.example.android.gurkha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Add_payment extends AppCompatActivity {
    EditText paiddate, paid, unpaid_amount, itemsgiven, category, total_cost, granted_amount,
            date_granted_over, pvno, sponser_name;
    Button button;
    private static final String url = "http://pagodalabs.com.np/gws/payment_distribution/api/payment_distribution";
    private ArrayList<Object> list;
    SearchableSpinner name;
    SearchableSpinner awc;
    private SpinnerAdapter adapter;
    private Calendar calendar;
    private ProgressDialog progressDialog;
    private static String base_url = "http://pagodalabs.com.np/";
    private int year, month, day;
    Object[] person_items;
    Call<ResponseBody> call;
    TextView hiddenDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        final Spinner grant = (Spinner) findViewById(R.id.textgrant);
        String[] grant_items = new String[]{"GRANT", "ADF", "ADF/EQRT", "ADF/HARDSHIP", "EQRT", "HARDSHIP", "ISOS", "ISOS/ADF"};
        ArrayAdapter<String> adapt_grant = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, grant_items);
        grant.setAdapter(adapt_grant);

        awc = (SearchableSpinner) findViewById(R.id.spinnerAwc);
        String[] awc_items = new String[]{"Select Area Welfare Center", "Bheri", "Myagdi", "Syangja", "Butwal", "Tanahun", "Lamjung", "Gulmi", "Chitwan", "Gorkha", "Bagmati",
                "Jiri", "Rumjatar", "Diktel", "Bhojpur", "Khandbari", "Tehrathum", "Taplejung", "Phidim", "Damak",
                "Darjeeling", "The Kulbir Thapa VC Residental Home", "The Rambahadur Limbu VC Residential Home"};
        ArrayAdapter<String> adapt_awc = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, awc_items);
        awc.setAdapter(adapt_awc);

        name = (SearchableSpinner) findViewById(R.id.textperson);
        /*
        ArrayAdapter<String> adapt_person = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        name.setAdapter(adapt_person);
*/

        paiddate = (EditText) findViewById(R.id.textpaiddate);
        paid = (EditText) findViewById(R.id.textpaid);
        unpaid_amount = (EditText) findViewById(R.id.textunpaidamount);
        category = (EditText) findViewById(R.id.textcategory);
        total_cost = (EditText) findViewById(R.id.texttotalcost);
        itemsgiven = (EditText) findViewById(R.id.textitemsgiven);
        granted_amount = (EditText) findViewById(R.id.textgrantedamount);
        date_granted_over = (EditText) findViewById(R.id.textdategrantedover);
        pvno = (EditText) findViewById(R.id.textpvno);
        sponser_name = (EditText) findViewById(R.id.textsponsername);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hiddenDate = (TextView) findViewById(R.id.hiddenDate);

        list = new ArrayList<>();
        button = (Button) findViewById(R.id.btn);

        paiddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaidDate(v);
            }
        });

        date_granted_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateGrantedOver(v);
            }
        });

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new GetDataTask().execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mPerson = name.getSelectedItem().toString();
                    String id = mPerson.substring(mPerson.lastIndexOf(":") + 1);
                    String mpaiddate = paiddate.getText().toString();
                    String mpaid = paid.getText().toString();
                    String munpaidamount = unpaid_amount.getText().toString();
                    String mcategory = category.getText().toString();
                    String mtotalcost = total_cost.getText().toString();
                    String mitemsgiven = itemsgiven.getText().toString();
                    String mgranted_amount = granted_amount.getText().toString();
                    String mdategrantedover = date_granted_over.getText().toString();
                    String mpvno = pvno.getText().toString();
                    String msponsername = sponser_name.getText().toString();
                    String mgrant = grant.getSelectedItem().toString();
                    String mAwc = awc.getSelectedItem().toString();

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    hiddenDate.setText(formattedDate);

                    String mHiddenDate = hiddenDate.getText().toString().trim();

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
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

                    JSONObject parameter = new JSONObject(params);

                    OkHttpClient client = new OkHttpClient();

                    final okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("content-type", "application/json; charset=utf-8")
                            .build();


                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.e("response", call.request().body().toString());

                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Log.e("response", response.body().string());
                        }

                    });
                    Toast.makeText(Add_payment.this, "Payment Details Added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Add_payment.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    /**
     * Interceptor to cache data and maintain it for a minute.
     * <p>
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     * <p>
     * If the device is offline, stale (at most eight hours old)
     * response is fetched from the cache.
     */
    private class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!InternetConnection.checkConnection(Add_payment.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 28800)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(Add_payment.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new Add_payment.ResponseCacheInterceptor())
                .addInterceptor(new Add_payment.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(Add_payment.this
                        .getCacheDir(),
                        "addPaymentApiResponses"), 1024 * 1024))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail");

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Add_payment", t.toString());
                Toast.makeText(Add_payment.this, "Error has occurred: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("personal");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);

                        String name = innerObject.getString("name");
                        String surname = innerObject.getString("surname");
                        String army_no = innerObject.getString("army_no");
                        String personal_id = innerObject.getString("personal_id");

                        list.add(name + " " + surname + "," + " " + "Army_No:" + army_no + "," + " " + "id:" + personal_id);
                        person_items = list.toArray();

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                Add_payment.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Add_payment.this, R.layout.support_simple_spinner_dropdown_item, person_items);
                        name.setAdapter(adapt_person);
                    }

                });
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setPaidDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setDateGrantedOver(View view) {
        showDialog(777);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myPaidDateListener, year, month, day);
        }
        if (id == 777) {
            return new DatePickerDialog(this,
                    myDateGrantedOverListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myPaidDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showPaidDate(arg3, arg2 + 1, arg1);
                }
            };

    private DatePickerDialog.OnDateSetListener myDateGrantedOverListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDateGrantedOver(arg3, arg2 + 1, arg1);
                }
            };

    private void showPaidDate(int day, int month, int year) {

        paiddate.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    private void showDateGrantedOver(int day, int month, int year) {
        date_granted_over.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }

    //Creating Get Data Task for Getting Data From Web

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            dialog = new ProgressDialog(Add_payment.this);
            dialog.setTitle("Please Wait...");
            dialog.setMessage("Getting Information");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = JSONParser.getDataFromWeb();

            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray("personal");

                        /**
                         * Check Length of Array...
                         */

                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (int jIndex = 0; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */


                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String name = innerObject.getString("name");
                                String surname = innerObject.getString("surname");
                                String army_no = innerObject.getString("army_no");
                                String personal_id = innerObject.getString("personal_id");
                                /**
                                 * Adding name and phone concatenation in List...
                                 */
                                list.add(name + " " + surname + "," + " " + "Army_No:" + army_no + "," + " " + "id:" + personal_id);
                                person_items = list.toArray();

                            }
                        }
                    }
                } else {
                    Toast.makeText(Add_payment.this, "Null JSON Object", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            //adapter = new SpinnerAdapter(Add_payment.this, list);
            ArrayAdapter<Object> adapt_person = new ArrayAdapter<Object>(Add_payment.this, R.layout.support_simple_spinner_dropdown_item, person_items);
            name.setAdapter(adapt_person);
            cancel(true);

            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            /*
            if(list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Add_investigation.this, "No Data Found!", Toast.LENGTH_SHORT).show();
            }
            */
        }
    }


}