package com.hfad.locationdetector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;

import java.io.File;

public class ImageCaptureActivity extends Activity {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA"};

    private final int CAMERA_PIC_REQUEST = 24;
    private String outPath;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        else finish();
    }

    private void postCaptureHandle() {
        intent.putExtra("imagePath", outPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!outPath.isEmpty()) postCaptureHandle();
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
        intent = getIntent();
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