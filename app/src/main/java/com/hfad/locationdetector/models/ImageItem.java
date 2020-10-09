package com.hfad.locationdetector.models;

import android.graphics.drawable.Icon;

import java.util.Objects;

public class ImageItem {
    private Icon thumbnail;
    private int imageID;
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Icon getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Icon thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageItem imageItem = (ImageItem) o;
        return imageID == imageItem.imageID &&
                thumbnail.equals(imageItem.thumbnail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thumbnail, imageID);
    }
}
