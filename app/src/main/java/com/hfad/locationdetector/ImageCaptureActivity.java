package com.hfad.locationdetector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;

public class ImageCaptureActivity extends AppCompatActivity {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA"};

    private final int CAMERA_PIC_REQUEST = 24;
    private ImageView imageView;
    private String outPath;
    private String uploadURL;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        initComponents();
        askForPermission();
        openCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {
            postCaptureHandle();
        }
    }

    private void postCaptureHandle() {
        bitmap = getCapturedImageFromOutPath();
        imageView.setImageBitmap(bitmap);
        sendImageToServer();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!outPath.isEmpty()) postCaptureHandle();
    }

    private void sendImageToServer() {
        // TODO: Implement this method
        VolleyMultipartRequest uploadRequest =
                ImageRequest.Builder().createRequest(bitmap, uploadURL, outPath);
        Volley.newRequestQueue(this).add(uploadRequest);
    }

    private Bitmap getCapturedImageFromOutPath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(outPath, options);
        return ImageHandling.Builder().adjustImageOrientation(image, outPath);
    }

    private void openCamera() {
        setStrictMode();
        Intent intent = (new Intent()).setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        setCaptureImageTemporaryPath(intent);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    private void setCaptureImageTemporaryPath(Intent intent) {
        outPath = getString(R.string.image_store_folder) +
                ImageHandling.Builder().generateImageName();
        File outFile = new File(outPath);
        Uri outUri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
    }

    private void setStrictMode() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build()); // detect accidents in VM process
    }

    private void initComponents() {
        imageView = findViewById(R.id.capturedImageView);
        uploadURL = getResources().getString(R.string.image_upload_url);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission() {
        int requestCode = 200;
        while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, requestCode);
        }
    }
}