package com.example.android.gurkha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.util.cache.BitmapCache;
import com.example.android.gurkha.Adapters.PicturesAdapter;
import com.example.android.gurkha.Adapters.PicturesAdapter.ClickListener;
import com.example.android.gurkha.Adapters.PicturesAdapter.RecyclerTouchListener;
import com.example.android.gurkha.modal.Pictures;

import java.io.File;
import java.util.ArrayList;

public class SavedPictures extends AppCompatActivity {
    private static final String TAG = SavedPictures.class.getSimpleName();
    public PicturesAdapter adapter;
    public String directory;
    ProgressDialog pd;
    public ArrayList<Pictures> picturesList;
    public RecyclerView recyclerView;
    public Toolbar toolbar;

    /* renamed from: com.example.android.gurkha.SavedPictures$1 */
    class C06681 extends Thread {
        C06681() {
        }

        public void run() {
            LayoutManager mLayoutManager = new GridLayoutManager(SavedPictures.this, 3);
            SavedPictures.this.recyclerView.setHasFixedSize(true);
            SavedPictures.this.recyclerView.setItemViewCacheSize(20);
            SavedPictures.this.recyclerView.setDrawingCacheEnabled(true);
            SavedPictures.this.recyclerView.setDrawingCacheQuality(1048576);
            SavedPictures.this.recyclerView.setLayoutManager(mLayoutManager);
            SavedPictures.this.recyclerView.setAdapter(SavedPictures.this.adapter);
            SavedPictures.this.preparePictures();
            long execution_time = 2000;
            try {
                synchronized (this) {
                    wait(2000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            SavedPictures.this.pd.dismiss();
        }
    }

    /* renamed from: com.example.android.gurkha.SavedPictures$3 */
    class C06693 implements OnClickListener {
        C06693() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: com.example.android.gurkha.SavedPictures$5 */
    class C06715 implements Runnable {
        C06715() {
        }

        public void run() {
            SavedPictures.this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: com.example.android.gurkha.SavedPictures$2 */
    class C11772 implements ClickListener {
        C11772() {
        }

        public void onClick(View view, int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", SavedPictures.this.picturesList);
            bundle.putInt("position", position);
            FragmentTransaction ft = SavedPictures.this.getSupportFragmentManager().beginTransaction();
            SingleScreenPic newFragment = SingleScreenPic.newInstance();
            newFragment.setArguments(bundle);
            newFragment.show(ft, "slideshow");
        }

        public void onLongClick(View view, int position) {
            Pictures picture = (Pictures) SavedPictures.this.picturesList.get(position);
            StringBuilder targetPictureDirectory = new StringBuilder();
            targetPictureDirectory.append(SavedPictures.this.directory);
            targetPictureDirectory.append(BitmapCache.HEADER_FILE_);
            targetPictureDirectory.append(picture.getName());
            File fileToDelete = new File(targetPictureDirectory.toString());
            String access$100 = SavedPictures.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("deleteDirec: ");
            stringBuilder.append(targetPictureDirectory);
            Log.e(access$100, stringBuilder.toString());
            SavedPictures.this.AskOption(fileToDelete, picture, position).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_saved_pictures);
        this.toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((TextView) findViewById(R.id.toolbar_title)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf"));
        this.pd = new ProgressDialog(this);
        this.pd.setProgressStyle(0);
        this.pd.setMessage("Loading. . . ");
        this.pd.setIndeterminate(true);
        this.pd.setCancelable(false);
        this.pd.show();
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.picturesList = new ArrayList();
        this.adapter = new PicturesAdapter(this, this.picturesList);
        new C06681().start();
        this.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, this.recyclerView, new C11772()));
    }

    private AlertDialog AskOption(final File fileToDelete, final Pictures picture, final int position) {
        return new Builder(this).setTitle((CharSequence) "Delete").setMessage((CharSequence) "Do you want to Delete?").setIcon(17301533).setPositiveButton((CharSequence) "Delete", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (fileToDelete.exists()) {
                    Context context;
                    StringBuilder stringBuilder;
                    if (fileToDelete.delete()) {
                        context = SavedPictures.this;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Deleted: ");
                        stringBuilder.append(picture.getName());
                        Toast.makeText(context, stringBuilder.toString(), 0).show();
                        SavedPictures.this.picturesList.remove(position);
                        SavedPictures.this.adapter.notifyItemRemoved(position);
                    } else {
                        context = SavedPictures.this;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Not deleted: ");
                        stringBuilder.append(picture.getName());
                        Toast.makeText(context, stringBuilder.toString(), 0).show();
                    }
                }
                dialog.dismiss();
            }
        }).setNegativeButton((CharSequence) "cancel", new C06693()).create();
    }

    private void preparePictures() {
        this.directory = getIntent().getStringExtra("directory");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("receivedDirec: ");
        stringBuilder.append(this.directory);
        Log.e(str, stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.directory);
        stringBuilder.append(BitmapCache.HEADER_FILE_);
        for (File file : new File(stringBuilder.toString()).listFiles()) {
            this.picturesList.add(new Pictures(file.getName(), BitmapFactory.decodeFile(file.getAbsolutePath())));
        }
        runOnUiThread(new C06715());
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
