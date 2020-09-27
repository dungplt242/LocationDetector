package com.hfad.locationdetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SendPackage {

    public static final double INVALID = 812;

    private String imagePath;
    private String uploadURL;
    private Bitmap currentImage;
    private double imageLongitude;
    private double imageLatitude;

    /** The angle that the current device makes with the North pole
     *  counter clockwise, range from -179 to 180.
     *  North: 0.0, West: 90.0, South: 180.0, East: -90.0 **/
    private double imageDirection;

    public SendPackage() {
        imageDirection = imageLongitude = imageLatitude = INVALID;
    }

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

    public Bitmap getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Bitmap currentImage) {
        this.currentImage = currentImage;
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

    public void update(Intent data) {
        imagePath = data.getStringExtra("imagePath");
        imageDirection = data.getDoubleExtra("imageDirection", SendPackage.INVALID);
        imageLongitude = data.getDoubleExtra("imageLongitude", SendPackage.INVALID);
        imageLatitude = data.getDoubleExtra("imageLatitude", SendPackage.INVALID);
        currentImage = getCapturedImageFromOutPath();
    }

    private Bitmap getCapturedImageFromOutPath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imagePath, options);
        return ImageHandling.Builder().adjustImageOrientation(image, imagePath);
    }
}
