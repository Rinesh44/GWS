
package com.example.android.gurkha.modal;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Pictures implements Serializable {
    private String name;
    private Bitmap thumbnail;

    public Pictures(String name, Bitmap thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}