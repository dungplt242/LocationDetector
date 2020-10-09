package com.hfad.locationdetector.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hfad.locationdetector.models.ImageItem;

import java.util.List;

public class ImagesViewModel extends ViewModel {
    private MutableLiveData<List<ImageItem>> imageItems;

    public MutableLiveData<List<ImageItem>> getImageItems() {
        return imageItems;
    }

    public void setImageItems(MutableLiveData<List<ImageItem>> imageItems) {
        this.imageItems = imageItems;
    }
}
