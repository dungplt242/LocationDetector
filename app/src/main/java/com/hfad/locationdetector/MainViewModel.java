package com.hfad.locationdetector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public static final double INVALID = 812;

    private String imagePath;
    private String uploadURL;
    private MutableLiveData<Bitmap> currentImage = new MutableLiveData<>();
    private double imageLongitude = INVALID;
    private double imageLatitude = INVALID;

    /** The angle that the current device makes with the North pole
     *  counter clockwise, range from -179 to 180.
     *  North: 0.0, West: 90.0, South: 180.0, East: -90.0 **/
    private double imageDirection = INVALID;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUploadURL() {
        return uploadURL;
    }

    public void setUploadURL(String uploadURL) {
        this.uploadURL = uploadURL;
    }

    public LiveData<Bitmap> getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Bitmap currentImage) {
        this.currentImage.setValue(currentImage);
    }

    public double getImageDirection() {
        return imageDirection;
    }

    public void setImageDirection(double imageDirection) {
        this.imageDirection = imageDirection;
    }

    public double getImageLongitude() {
        return imageLongitude;
    }

    public void setImageLongitude(double imageLongitude) {
        this.imageLongitude = imageLongitude;
    }

    public double getImageLatitude() {
        return imageLatitude;
    }

    public void setImageLatitude(double imageLatitude) {
        this.imageLatitude = imageLatitude;
    }

    public void update(Bundle data) {
        imagePath = data.getString("imagePath");
        imageDirection = data.getDouble("imageDirection", SendPackage.INVALID);
        imageLongitude = data.getDouble("imageLongitude", SendPackage.INVALID);
        imageLatitude = data.getDouble("imageLatitude", SendPackage.INVALID);
        currentImage.setValue(getCapturedImageFromOutPath());
    }

    private Bitmap getCapturedImageFromOutPath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imagePath, options);
        return ImageHandling.Builder().adjustImageOrientation(image, imagePath);
    }
}
