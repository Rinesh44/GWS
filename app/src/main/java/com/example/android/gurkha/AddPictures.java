package com.example.android.gurkha;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.bumptech.glide.Glide;
import com.example.android.gurkha.JobQueue.PostJob;
import com.example.android.gurkha.application.GurkhaApplication;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPictures extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int PASSPORTPICTURE = 1, GALLERYFORSTORY = 2, CAMERAFORSTORY = 3;
    private static final int CAMERAFORSTORYAGAIN = 4, GALLERYFORSTORYAGAIN = 5;
    private static final String url = "http://pagodalabs.com.np/gws/pensioner_information/api/pensioner_information";
    private Toolbar toolbar;
    private ImageView picturesImage, passportImage, storyImage;
    private EditText storyDescription;
    private FloatingActionButton fabPassport, fabStory;
    private CardView passportPicHolder, storyPicHolder;
    private String base64PassportImage;
    private LinearLayout storyContainer;
    private AppCompatButton anotherStoryBtn;
    private Typeface face;
    private SessionManager sessionManager;
    private FbSessionManager fbSessionManager;
    private String token, userId;
    private ArrayList<String> storyImages, storyImageDescription;
    private JobManager mJobManager;
    private EditText newEditText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictures);

        toolbar = findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }
        }

        face = Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf");
        picturesImage = findViewById(R.id.pictures_image);
        fabPassport = findViewById(R.id.fab_passport_image);
        fabStory = findViewById(R.id.fab_story_image);
        passportImage = findViewById(R.id.passport_image);
        passportPicHolder = findViewById(R.id.passport_pic_holder);
        storyImage = findViewById(R.id.story_image);
        storyDescription = findViewById(R.id.story_desc);

        storyContainer = findViewById(R.id.story_container);
        storyPicHolder = findViewById(R.id.story_pic_holder);
        anotherStoryBtn = findViewById(R.id.another_story_btn);

        storyImages = new ArrayList<>();
        storyImageDescription = new ArrayList<>();

        sessionManager = new SessionManager(getApplicationContext());
        fbSessionManager = new FbSessionManager(getApplicationContext());

        mJobManager = GurkhaApplication.getInstance().getJobManager();

        fabPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePassportPhotoFromCamera();
            }
        });

        fabStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        anotherStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialogForStory();
            }
        });

    }

    private void postToWeb() {
        if (sessionManager.getUserDetails() != null) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            token = user.get(SessionManager.KEY_TOKEN);
            userId = user.get(SessionManager.KEY_ID);
        }

        if (fbSessionManager.getUserDetails().get(SessionManager.KEY_ID) != null) {
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            if (fbUser.get(SessionManager.KEY_TOKEN) != null)
                token = fbUser.get(SessionManager.KEY_TOKEN);
            userId = fbUser.get(SessionManager.KEY_ID);
        }

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
        String currentDate = df.format(c.getTime());
        String currentTime = tf.format(c.getTime());

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("date", currentDate);
        params.put("time", currentTime);
        params.put("passport_image", base64PassportImage);
//        JSONArray imageArray = new JSONArray(storyImages);
//        JSONArray descriptionArray = new JSONArray(storyImageDescription);
        for (int i = 0; i < storyImages.size(); i++) {
            params.put("story_image" + i, storyImages.get(i));
        }
        for (int j = 0; j < storyImageDescription.size(); j++) {
            params.put("story_desc" + j, storyImageDescription.get(j));
        }

        params.put("api_token", token);

        JSONObject parameter = new JSONObject(params);

        Log.e("JSON", parameter.toString());
        Log.e("TOKEN", token);
        Log.e("description", storyImageDescription.toString());

        mJobManager.addJobInBackground(new PostJob(url, parameter.toString()));
        Toast.makeText(this, "Pictures added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showPictureDialogForStory() {
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
                                chooseStoryPhotoFromGallaryAgain();
                                break;
                            case 1:
                                takeStoryPhotoFromCameraAgain();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void chooseStoryPhotoFromGallaryAgain() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERYFORSTORYAGAIN);
    }

    private void takeStoryPhotoFromCameraAgain() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERAFORSTORYAGAIN);
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
                                chooseStoryPhotoFromGallary();
                                break;
                            case 1:
                                takeStoryPhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takeStoryPhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERAFORSTORY);
    }

    private void chooseStoryPhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERYFORSTORY);
    }

    private void takePassportPhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PASSPORTPICTURE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_done:
                if (base64PassportImage == null) {
                    Toast.makeText(this, "Please add an image first", Toast.LENGTH_SHORT).show();
                } else {
                    if(storyDescription != null) {
                        String storyDesc = storyDescription.getText().toString().trim();
                        storyImageDescription.add(storyDesc);
                    }
                    if(newEditText != null){
                        String storyDescriptionAgain = newEditText.getText().toString().trim();
                        storyImageDescription.add(storyDescriptionAgain);
                    }
                    postToWeb();
                }

                break;
        }
        return false;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == PASSPORTPICTURE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            passportPicHolder.setVisibility(View.VISIBLE);
            picturesImage.setVisibility(View.GONE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(this)
                    .load(stream.toByteArray())
                    .into(passportImage);
            base64PassportImage = convertToBase64(thumbnail);
        }

        if (requestCode == GALLERYFORSTORY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    storyContainer.setVisibility(View.VISIBLE);
                    storyPicHolder.setVisibility(View.VISIBLE);
                    picturesImage.setVisibility(View.GONE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap = getResizedBitmap(bitmap, 400);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Glide.with(this)
                            .load(stream.toByteArray())
                            .into(storyImage);

                    String base64StoryImage = convertToBase64(bitmap);
                    storyImages.add(base64StoryImage);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPictures.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == CAMERAFORSTORY) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            storyContainer.setVisibility(View.VISIBLE);
            storyPicHolder.setVisibility(View.VISIBLE);
            picturesImage.setVisibility(View.GONE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(this)
                    .load(stream.toByteArray())
                    .into(storyImage);
            String base64StoryImage = convertToBase64(thumbnail);
            storyImages.add(base64StoryImage);

        }

        if (requestCode == CAMERAFORSTORYAGAIN) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            if (thumbnail != null)
                createDynamicCard(thumbnail);
        }

        if (requestCode == GALLERYFORSTORYAGAIN) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    createDynamicCard(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void createDynamicCard(Bitmap bitmap) {
        CardView newCard = new CardView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(15, 15, 15, 15);
        newCard.setLayoutParams(params);
        newCard.setClickable(true);
        newCard.setFocusable(true);
        newCard.setRadius(8);
        newCard.setCardElevation(8);
        newCard.setUseCompatPadding(true);

        LinearLayout newLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        linearLayoutParams.setMargins(5, 5, 5, 5);
        linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        newLayout.setLayoutParams(linearLayoutParams);
        newLayout.setDividerPadding(5);
        newLayout.setOrientation(LinearLayout.VERTICAL);
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.horizontalDivider, typedValue, true);
        newLayout.setDividerDrawable(getResources().getDrawable(android.R.drawable.divider_horizontal_dark));
        newLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);


        ImageView newImage = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        imageParams.setMargins(5, 5, 5, 5);
        imageParams.gravity = Gravity.CENTER_HORIZONTAL;
        newImage.setLayoutParams(imageParams);

        newEditText = new EditText(this);
        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayoutParams.setMargins(5, 5, 5, 5);
        linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        newEditText.setLayoutParams(editTextLayoutParams);
        newEditText.setGravity(Gravity.CENTER);
        newEditText.setTypeface(face);
        newEditText.setHint("write something about picture");
        newEditText.setMaxLines(2);
        newEditText.setTextSize(20);

        newLayout.addView(newImage);
        newLayout.addView(newEditText);

        newCard.addView(newLayout);
        storyContainer.addView(newCard);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap = getResizedBitmap(bitmap, 400);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(this)
                .load(stream.toByteArray())
                .into(newImage);
        String base64StoryImageAgain = convertToBase64(bitmap);
        storyImages.add(base64StoryImageAgain);

    }

    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bitmap is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
}
