package com.example.android.gurkha;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.gurkha.modal.Pictures;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SingleScreenPic extends DialogFragment {
    private static final String TAG = SingleScreenPic.class.getSimpleName();
    private ArrayList<Pictures> images;
    private TextView lblCount;
    private ImageView mImageView;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;
    private ViewPager viewPager;
    OnPageChangeListener viewPagerPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            SingleScreenPic.this.displayMetaInfo(position);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public Object instantiateItem(ViewGroup container, int position) {
            this.layoutInflater = (LayoutInflater) SingleScreenPic.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = this.layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            Glide.with(SingleScreenPic.this.getActivity()).load(SingleScreenPic.this.bitmapToByte(((Pictures) SingleScreenPic.this.images.get(position)).getThumbnail())).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) view.findViewById(R.id.image_preview));
            container.addView(view);
            return view;
        }

        public int getCount() {
            return SingleScreenPic.this.images.size();
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    static SingleScreenPic newInstance() {
        return new SingleScreenPic();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_single_screen_pic, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        this.lblCount = (TextView) v.findViewById(R.id.lbl_count);
        this.images = (ArrayList) getArguments().getSerializable("images");
        this.selectedPosition = getArguments().getInt("position");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("position: ");
        stringBuilder.append(this.selectedPosition);
        Log.e(str, stringBuilder.toString());
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("images size: ");
        stringBuilder.append(this.images.size());
        Log.e(str, stringBuilder.toString());
        this.myViewPagerAdapter = new MyViewPagerAdapter();
        this.viewPager.setAdapter(this.myViewPagerAdapter);
        this.viewPager.addOnPageChangeListener(this.viewPagerPageChangeListener);
        setCurrentItem(this.selectedPosition);
        return v;
    }

    private void setCurrentItem(int position) {
        this.viewPager.setCurrentItem(position, false);
        displayMetaInfo(this.selectedPosition);
    }

    private void displayMetaInfo(int position) {
        TextView textView = this.lblCount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(position + 1);
        stringBuilder.append(" of ");
        stringBuilder.append(this.images.size());
        textView.setText(stringBuilder.toString());
        Pictures image = this.images.get(position);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, 16973834);
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}