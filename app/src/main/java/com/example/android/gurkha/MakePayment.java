package com.example.android.gurkha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.NFCInterface.Listener;
import com.example.android.gurkha.NfcFragments.NFCReadFragment;
import com.example.android.gurkha.NfcFragments.NFCWriteFragment;
import com.example.android.gurkha.application.GurkhaApplication;
import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MakePayment extends AppCompatActivity implements Listener {
    private static final String TAG = MakePayment.class.getSimpleName();
    SearchableSpinner recipientName;
    Button QR, done, NFC;
    EditText amount, topic, transactionDate, transactionNo;
    private static final String IMAGE_DIRECTORY = "/topicImage";
    private int GALLERY = 1, CAMERA = 2;
    private ImageView image;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private static final String url = "http://pagodalabs.com.np/gws/payment_detail/api/payment_detail";
    private Calendar calendar;
    private int year, month, day;
    private FbSessionManager fbSessionManager;
    private String token;
    private ArrayList<Object> list;
    Toolbar toolbar;
    ArrayAdapter<Object> adapt_person;
    private EditText desc;
    private static String base_url = "http://pagodalabs.com.np/";
    Call<ResponseBody> call;
    Typeface face;
    Object[] person_items;
    private boolean isWrite = false;
    JobManager mJobManager;
    String base64Image;
    private boolean isDialogDisplayed = false;
    private NFCWriteFragment mNfcWriteFragment;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initializeViews();

        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startScanner = new Intent(MakePayment.this, Scanner.class);
                startActivity(startScanner);
            }
        });

        NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWriteFragment();
            }
        });

        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
        }


        if (fbSessionManager.getUserDetails() != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(FbSessionManager.KEY_TOKEN) != null)
                token = fbUser.get(FbSessionManager.KEY_TOKEN);
        }

        transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTransactionDate(v);
            }
        });


        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mJobManager = GurkhaApplication.getInstance().getJobManager();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPerson = recipientName.getSelectedItem().toString();
                String id = mPerson.substring(mPerson.lastIndexOf(":") + 1);
                String mAmount = amount.getText().toString().trim();
//                String mTopic = topic.getText().toString().trim();
                String mDateOfTransaction = transactionDate.getText().toString().trim();
                String mTransactionNumber = transactionNo.getText().toString().trim();
                String mDescription = desc.getText().toString().trim();

                if (TextUtils.isEmpty(mAmount)) {
                    amount.setError(getString(R.string.emptyEdittext));
                    return;
                }
              /*  if(TextUtils.isEmpty(mTopic)){
                    topic.setError(getString(R.string.emptyEdittext));
                    return;
                }*/
                if (TextUtils.isEmpty(mDateOfTransaction)) {
                    transactionDate.setError(getString(R.string.emptyEdittext));
                    return;
                }
                if (TextUtils.isEmpty(mTransactionNumber)) {
                    transactionNo.setError(getString(R.string.emptyEdittext));
                    return;
                }
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String mHiddenDate = df.format(c.getTime());

                if (sessionManager.getUserDetails() != null) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    token = user.get(SessionManager.KEY_TOKEN);
                }

                if (fbSessionManager.getUserDetails() != null) {
                    HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
                    if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                        token = fbUser.get(SessionManager.KEY_TOKEN);
                }


                Map<String, String> params = new HashMap<>();
                params.put("personal_id", id);
//                params.put("topic", mTopic);
                params.put("amount", mAmount);
                params.put("date_of_transaction", mDateOfTransaction);
                params.put("transaction_no", mTransactionNumber);
                params.put("image", base64Image);
                params.put("hidden_date", mHiddenDate);
                params.put("image_description", mDescription);
                params.put("api_token", token);

                JSONObject parameter = new JSONObject(params);

                Log.e("JSON:", parameter.toString());
                Log.e("Date:", mDateOfTransaction);

                mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));
                Toast.makeText(MakePayment.this, "Details added", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    private void showWriteFragment() {

        isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null) {

            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getFragmentManager(), NFCWriteFragment.TAG);

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                if (isWrite) {
                    String recipient = recipientName.getSelectedItem().toString();
                    String[] split = recipient.split(",");
                    String nameAndArmyNo = split[0];
                    String[] splitNameAndArmyNo = nameAndArmyNo.split(" ");
                    String firstName = splitNameAndArmyNo[0];
                    String lastName = splitNameAndArmyNo[1];
                    String armyNo = splitNameAndArmyNo[2];

                    StringBuilder sb = new StringBuilder("Name: " + firstName + " " + lastName);

//                    sb.append("\n" + "Last Name: " + lastName);
                    sb.append("\n" + "Army No: " + armyNo);
                    sb.append("\n" + "Amount: " + amount.getText().toString().trim());
//                    sb.append("\n" + "Topic: " + topic.getText().toString().trim());
                    sb.append("\n" + "Tran. Date: " + transactionDate.getText().toString().trim());
                    sb.append("\n" + "Tran. No: " + transactionNo.getText().toString().trim());

                    mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef, sb.toString());

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }

    private void initializeViews() {
        recipientName = findViewById(R.id.sp_recipient_name);
        QR = findViewById(R.id.btn_QR);
        NFC = findViewById(R.id.btn_NFC);
        done = findViewById(R.id.btn_done);
        amount = findViewById(R.id.et_amount);
//        topic = findViewById(R.id.et_topic);
        transactionDate = findViewById(R.id.et_transaction_date);
        transactionNo = findViewById(R.id.et_transaction_no);
        image = findViewById(R.id.iv);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());
        list = new ArrayList<>();
        desc = findViewById(R.id.imageDesc);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(MakePayment.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(bitmap);
                    desc.setVisibility(View.VISIBLE);
                    base64Image = convertToBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MakePayment.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            desc.setVisibility(View.VISIBLE);
            Toast.makeText(MakePayment.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            base64Image = convertToBase64(thumbnail);
        }
    }

    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bitmap is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;
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
            if (!InternetConnection.checkConnection(MakePayment.this)) {
                request = request.newBuilder()
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 28800)// 8 hrs
                        .build();
            }
            return chain.proceed(request);
        }
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(MakePayment.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Enable response caching
                .addNetworkInterceptor(new MakePayment.ResponseCacheInterceptor())
                .addInterceptor(new MakePayment.OfflineResponseCacheInterceptor())
                // Set the cache location and size (1 MB)
                .cache(new Cache(new File(MakePayment.this
                        .getCacheDir(),
                        "addPaymentApiResponses"), 1024 * 1024))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse("gws/personal_detail/api/personal_detail?api_token=" + token);
        Log.e(TAG, "Token:" + token);
        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("Make_payment", t.toString());
                Toast.makeText(MakePayment.this, "Something went wrong: \n" + t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (!(response.isSuccessful())) {
                        Toast.makeText(MakePayment.this, "Cache data not found. Please connect to internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    if (myResponse.contains("Access Denied")) {
                        Toast.makeText(MakePayment.this, "Token Expired", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("personal");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject innerObject = data.getJSONObject(i);

                        String name = innerObject.getString("name");
                        String surname = innerObject.getString("surname");
                        String army_no = innerObject.getString("army_no");
                        String personal_id = innerObject.getString("personal_id");

                        list.add(name + " " + surname + " " + army_no + "," + " " + "id:" + personal_id);
                        person_items = list.toArray();

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                MakePayment.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        adapt_person = new ArrayAdapter<Object>(MakePayment.this, R.layout.support_simple_spinner_dropdown_item, person_items) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView name = (TextView) view.findViewById(android.R.id.text1);
                                name.setTypeface(face);
                                return view;
                            }
                        };
                        recipientName.setAdapter(adapt_person);
                    }

                });
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setTransactionDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    transactionDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener transactionDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showTransactionDate(arg3, arg2 + 1, arg1);
                }
            };

    private void showTransactionDate(int day, int month, int year) {

        transactionDate.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }
}

