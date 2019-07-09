package com.example.android.gurkha.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.gurkha.R;
import com.example.android.gurkha.modal.Pictures;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PicturesAdapter extends Adapter<PicturesAdapter.MyViewHolder> {
    private Context mContext;
    private List<Pictures> picturesList;

    public interface ClickListener {
        void onClick(View view, int i);

        void onLongClick(View view, int i);
    }

    public class MyViewHolder extends ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public static class RecyclerTouchListener implements OnItemTouchListener {
        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (!(child == null || this.clickListener == null || !this.gestureDetector.onTouchEvent(e))) {
                this.clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public PicturesAdapter(Context mContext, List<Pictures> picturesList) {
        this.mContext = mContext;
        this.picturesList = picturesList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_row, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(this.mContext).load(bitmapToByte(((Pictures) this.picturesList.get(position)).getThumbnail())).crossFade().placeholder((int) R.drawable.ic_pic).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.thumbnail);
    }

    public int getItemCount() {
        return this.picturesList.size();
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}