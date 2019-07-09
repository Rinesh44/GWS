package com.example.android.gurkha;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.example.android.gurkha.Adapters.AlbumsAdapter;
import com.example.android.gurkha.modal.Album;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private static final String TAG = Gallery.class.getSimpleName();
    private AlbumsAdapter adapter;
    private List<Album> albumList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView emptyTextView;

    public class GridSpacingItemDecoration extends ItemDecoration {
        private boolean includeEdge;
        private int spacing;
        private int spanCount;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % this.spanCount;
            if (this.includeEdge) {
                outRect.left = this.spacing - ((this.spacing * column) / this.spanCount);
                outRect.right = ((column + 1) * this.spacing) / this.spanCount;
                if (position < this.spanCount) {
                    outRect.top = this.spacing;
                }
                outRect.bottom = this.spacing;
                return;
            }
            outRect.left = (this.spacing * column) / this.spanCount;
            outRect.right = this.spacing - (((column + 1) * this.spacing) / this.spanCount);
            if (position >= this.spanCount) {
                outRect.top = this.spacing;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_gallery);
        this.toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((TextView) findViewById(R.id.toolbar_title)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/core_regular.otf"));

        emptyTextView = findViewById(R.id.empty_text_view);
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.albumList = new ArrayList();
        this.adapter = new AlbumsAdapter(this, this.albumList);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        this.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.adapter);
        prepareAlbums();

        if (albumList.isEmpty() && emptyTextView.getVisibility() == View.GONE) {
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void prepareAlbums() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        stringBuilder.append("/GWS/");
        File directory = new File(stringBuilder.toString());
        if (directory.exists()) {
            File[] fList = directory.listFiles();
            if (fList.length > 0) {
                this.albumList.clear();
                for (File file : fList) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(Environment.getExternalStorageDirectory().getAbsolutePath());
                    stringBuilder2.append("/GWS/");
                    stringBuilder2.append(file.getName());
                    directory = new File(stringBuilder2.toString());
                    String str = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("directory: ");
                    stringBuilder2.append(directory);
                    Log.e(str, stringBuilder2.toString());
                    this.albumList.add(new Album(file.getName(), directory.listFiles().length, R.drawable.ic_album, directory.toString()));
                }
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics()));
    }
}
