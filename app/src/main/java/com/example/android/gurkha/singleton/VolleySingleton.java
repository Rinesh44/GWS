package com.example.android.gurkha.singleton;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.android.gurkha.application.GurkhaApplication;

/**
 * Created by Shaakya on 6/29/2017.
 */

public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private VolleySingleton(){
        mRequestQueue= Volley.newRequestQueue(GurkhaApplication.getAppContext());
        mImageLoader=new ImageLoader(mRequestQueue,new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache=new LruCache<>((int)(Runtime.getRuntime().maxMemory()/1024)/8);
            @Override
            public Bitmap getBitmap(String url) {
                /*Toast.makeText(NewlyWedApplication.getAppContext(), "GET"+url, Toast.LENGTH_SHORT).show();*/

                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                /*Toast.makeText(NewlyWedApplication.getAppContext(), "PUT"+url, Toast.LENGTH_SHORT).show();*/
                cache.put(url, bitmap);
            }
        });
    }
    public static VolleySingleton getInstance(){
        if(sInstance==null)
        {
            sInstance=new VolleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
    public ImageLoader getImageLoader(){
        return mImageLoader;
    }
}