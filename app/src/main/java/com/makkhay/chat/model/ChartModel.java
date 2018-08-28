package com.makkhay.chat.model;

/**
 * Created by Lincoln on 18/05/16.
 */
public class ChartModel {
    private String name;
    private int thumbnail;

    public ChartModel() {
    }

    public ChartModel(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
