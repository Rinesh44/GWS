package com.example.android.gurkha;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gurkha.Adapters.Adapter;
import com.example.android.gurkha.activities.QnA.AnswersResult;
import com.example.android.gurkha.activities.QnA.Results;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */


public class NavigationDrawer extends Fragment {
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private ActionBarDrawerToggle mDrawerToggle;
    public RecyclerView recyclerView;
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    public static final String PREF_FILE_NAME = "testpref";
    public DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    public View containerView;
    public Adapter adapter;
    static public TextView txtName, txtFbName;
    private static final int RC_SIGN_IN = 007;
    public ProfilePictureView imgPic;
    CircleImageView userImage;
    public File[] allFiles;
    SessionManager session;
    FbSessionManager fbSessionManager;
    static String getUserName;
    public static Bitmap thumbnail;
    private static final int RESULT_LOAD_IMG = 3;

    public NavigationDrawer() {
        // Required empty public constructor
    }


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        session = new SessionManager(getActivity().getApplicationContext());
        fbSessionManager = new FbSessionManager(getActivity().getApplicationContext());

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState == null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, true);
        imgPic = (ProfilePictureView) layout.findViewById(R.id.imgProfilePic);
        txtName = (TextView) layout.findViewById(R.id.txtName);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nunito.otf");
        txtName.setTypeface(face);

        HashMap<String, String> user = session.getUserDetails();
        // email
        String email = user.get(SessionManager.KEY_EMAIL);
        getUserName = email;
        txtFbName = (TextView) layout.findViewById(R.id.txtFbName);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new Adapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);

        userImage = (CircleImageView) layout.findViewById(R.id.userImage);

        File imageFile = new File(Environment.getExternalStorageDirectory().getPath() + "/GWS/User Images/" + getUserName + ".jpg");
        if (imageFile.exists()) {

            String filePath = imageFile.getPath();
            Bitmap loadBitmap = BitmapFactory.decodeFile(filePath);
            userImage.setImageBitmap(loadBitmap);
        }

        final Intent getLoginData = getActivity().getIntent();
        if (getLoginData != null) {
            //String fbName = getLoginData.getStringExtra("name");
            HashMap<String, String> fbUser = fbSessionManager.getUserDetails();
            String fbName = fbUser.get(FbSessionManager.KEY_NAME);
            txtFbName.setText(fbName);

            String userId = fbUser.get(FbSessionManager.KEY_PROPIC);
            imgPic.setProfileId(userId);
        }

        if (txtFbName.getText().equals(""))
            txtName.setText(getUserName);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (position == 0) {
                    Intent menu = new Intent(getActivity(), Menu.class);
                    menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(menu);
                }

               /* if (position == 1) {
                    if (Build.VERSION.SDK_INT > 22) {
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }
                    }


                    File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWS/");
                    if (folder.exists()) {
                       *//* allFiles = folder.listFiles();
                        for (int i = 0; i < allFiles.length; i++) {
                            new SingleMediaScanner(getActivity(), allFiles[i]);
                        }*//*
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWS/");
                        intent.setDataAndType(selectedUri, "text/csv");
                        startActivity(Intent.createChooser(intent, "Open folder"));

                      *//*  if (intent.resolveActivityInfo(getActivity().getPackageManager(), 0) != null)
                        {
                            startActivity(intent);
                        }*//*
                 *//* else
                        {
                            // if you reach this place, it means there is no any file
                            // explorer app installed on your device
                            Toast.makeText(getActivity(), "Problem locating to the directory", Toast.LENGTH_SHORT).show();
                        }*//*

                    } else {
                        Toast.makeText(getActivity(), "No saved pictures found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }*/

                if (position == 1) {
                    Intent nfc = new Intent(getActivity(), NFC.class);
                    startActivity(nfc);
                }

                if (position == 2) {
                    Intent results = new Intent(getActivity(), Results.class);
                    startActivity(results);
                }

                if (position == 3) {
                    Intent pollResult = new Intent(getActivity(), AnswersResult.class);
                    startActivity(pollResult);
                }

                if (position == 4) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Delete Cache")
                            .setMessage("Are you sure you want to delete all cached data?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        File dir = getActivity().getCacheDir();
                                        deleteDir(dir);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Cahced data deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }

                if (position == 5) {
                    LoginManager.getInstance().logOut();
                    getLoginData.removeExtra("name");
                    getLoginData.removeExtra("profile_picID");
                    session.logoutUser();
                    fbSessionManager.logoutUser();
                    //to logout and start login activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }


            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == -1) {

            try {
                final int thumbnailSize = 200;
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                thumbnail = Bitmap.createScaledBitmap(selectedImage, thumbnailSize, thumbnailSize, false);
                userImage.setImageBitmap(thumbnail);
                imgPic.setVisibility(View.GONE);

                File image = new File(appFolderCheckandCreate(), getUserName + ".jpg");
                FileOutputStream out = new FileOutputStream(image);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public static List<Information> getData() {
        int i;
        List<Information> data = new ArrayList<>();
        int[] icons = {R.drawable.menu, R.drawable.ic_nfc, R.drawable.ic_result, R.drawable.ic_result, R.drawable.ic_clear_cache, R.drawable.ic_account_circle};
        String[] titles = {"Menu", "Read NFC Tag", "Conditions Outome", "Poll Outcome", "Clear Cached data", "Logout"};


        for (i = 0; i < titles.length && i < icons.length; i++) {
            Information current = new Information();
            current.iconId = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideSoftKeyboard(getActivity());
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");

                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.addDrawerListener(mDrawerToggle
        );
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();

    }

    public static String readFromPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, preferenceValue);

    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            Log.d("HOME", "constructor invoked");
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                    Log.d("HOME", "onLongPress" + e);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d("HOME", "onTouchEvent" + e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

/*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    */

    private void displayMessage(Profile profile) {
        if (profile != null) {
            txtFbName.setText(profile.getName());
            String userID = profile.getId();
            imgPic.setProfileId(userID);
            userImage.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        public void onScanCompleted(String path, Uri uri) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            mMs.disconnect();
        }

    }

    private String appFolderCheckandCreate() {

        String appFolderPath = "";
        File externalStorage = Environment.getExternalStorageDirectory();

        if (externalStorage.canWrite()) {
            appFolderPath = externalStorage.getAbsolutePath() + "/GWS/User Images/";
            File dir = new File(appFolderPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

        } else {

        }

        return appFolderPath;
    }


}




