package com.example.android.gurkha.modal;


public class Album {
    private String directory;
    private String name;
    private int numOfImages;
    private int thumbnail;

    public Album(String name, int numOfSongs, int thumbnail, String directory) {
        this.name = name;
        this.numOfImages = numOfSongs;
        this.thumbnail = thumbnail;
        this.directory = directory;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfImages() {
        return this.numOfImages;
    }

    public void setNumOfImages(int numOfImages) {
        this.numOfImages = numOfImages;
    }

    public int getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}