package com.hfad.locationdetector;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageHandling {

    private static final ImageHandling imageHandling = new ImageHandling();

    public static ImageHandling Builder() {
        return imageHandling;
    }

    private ImageHandling() {}

    public String generateImageName() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("hh-mm-ss", new Locale.Builder().build());
        return df.format(date) + ".jpg";
    }

    public Bitmap adjustImageOrientation(Bitmap bitmap, String outPath) {
        try {
            ExifInterface ei = new ExifInterface(outPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            return checkOrientationAndRotate(bitmap, orientation);
        }
        catch (IOException e) { e.printStackTrace(); }
        return bitmap;
    }

    private Bitmap checkOrientationAndRotate(Bitmap bitmap, int orientation) {
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            case ExifInterface.ORIENTATION_NORMAL:
            default: return bitmap;
        }
    }

    private Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(), matrix, true);
    }
}
