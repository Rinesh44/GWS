package com.example.android.gurkha;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class Loader extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ArrayList<LatLng> markerList;
    public static JSONArray markerArray;
    public static JSONArray polylineArray;
    public JSONArray imagePathArray, notesArray;
    public ArrayList<LatLng> polylineList;
    private TextView noteField;
    public File[] allFiles;
    String mImagePath, mNotes;
    private static final int REQUEST_PERMISSIONS = 1999;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    PolylineOptions polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT > 22) {
            ActivityCompat.requestPermissions(Loader.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);
        }


        markerList = new ArrayList<>();
        polylineList = new ArrayList<>();
        polylineOptions = new PolylineOptions();

        Intent i = getIntent();
        String mMarker = i.getStringExtra("marker");
        String mPolyline = i.getStringExtra("polyline");
        String mMarkerId = i.getStringExtra("marker_id");
        mNotes = i.getStringExtra("notes");
        final String mName = i.getStringExtra("name");
        mImagePath = i.getStringExtra("image_path");

        try {
            markerArray = new JSONArray(mMarker);
            polylineArray = new JSONArray(mPolyline);


            for (int p = 0; p < polylineArray.length(); p++) {
                String polylineCoord = String.valueOf(polylineArray.get(p));
                String[] separatedPoly = polylineCoord.split(":");
                double polyLat = Double.parseDouble(separatedPoly[0]);
                double polyLng = Double.parseDouble(separatedPoly[1]);
                LatLng latLngPoly = new LatLng(polyLat, polyLng);
                polylineList.add(latLngPoly);
            }

            for (int j = 0; j < markerArray.length(); j++) {
                String markerCoord = String.valueOf(markerArray.get(j));
                String[] separated = markerCoord.split(":");
                double markerLat = Double.parseDouble(separated[0]);
                double markerLng = Double.parseDouble(separated[1]);
                LatLng latLng = new LatLng(markerLat, markerLng);
                markerList.add(latLng);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        for (int i = 0; i < markerList.size(); i++) {
            LatLng putMarker = new LatLng(markerList.get(i).latitude, markerList.get(i).longitude);
            MarkerOptions add = new MarkerOptions().position(putMarker).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title(putMarker.toString());
            mMap.addMarker(add);
        }

        polylineOptions.addAll(polylineList);
        polylineOptions
                .width(7)
                .color(Color.GREEN);
        LatLng start = polylineList.get(0);
        LatLng end = polylineList.get(polylineList.size() - 1);

        mMap.addPolyline(polylineOptions);
        Marker startMarker = mMap.addMarker(new MarkerOptions().position(start).title("Start").snippet("start").icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        Marker endMarker = mMap.addMarker(new MarkerOptions().position(end).title("Stop").snippet("stop").icon(BitmapDescriptorFactory.fromResource(R.drawable.stop)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 12));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.loader_info_window, null);
                try {
                    imagePathArray = new JSONArray(mImagePath);
                    notesArray = new JSONArray(mNotes);
                    for (int k = 0; k < imagePathArray.length(); k++) {
                        String path = String.valueOf(imagePathArray.get(k));

                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;  //you can also calculate your inSampleSize
                        options.inJustDecodeBounds = false;
                        options.inTempStorage = new byte[16 * 1024];

                        Bitmap bm = BitmapFactory.decodeFile(path, options);
                        final ImageView image = (ImageView) v.findViewById(R.id.image);
                        final TextView imageDesc = (TextView) v.findViewById(R.id.imageDesc);
                        String markerId = marker.getId();
                        String separatedId = markerId.replace("m", "");
                        if (separatedId.equals(String.valueOf(k)))
                            image.setImageBitmap(bm);

                        for (int l = 0; l < notesArray.length(); l++) {
                            String note = String.valueOf(notesArray.get(l));
                            if (separatedId.equals(String.valueOf(l)))
                                imageDesc.setText(note);
                        }


                        if (marker.getSnippet() != null) {
                            if ((marker.getSnippet().equals("start"))) {
                                imageDesc.setText("Start");
                                image.setVisibility(View.GONE);
                            }

                            if ((marker.getSnippet().equals("stop"))) {
                                imageDesc.setText("Stop");
                                image.setVisibility(View.GONE);
                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return v;
            }
        });

    }
/*

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
    */

}
