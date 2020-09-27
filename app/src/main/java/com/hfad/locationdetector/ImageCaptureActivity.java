package com.hfad.locationdetector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;

import java.io.File;

public class ImageCaptureActivity extends Activity implements SensorEventListener {

    private String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"};

    private final int CAMERA_PIC_REQUEST = 24;
    private String outPath;
    private Intent intent;
    private double direction = SendPackage.INVALID;
    private double longitude;
    private double latitude;

    // fields for sensors
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity = null;
    float[] mGeomagnetic = null;

    // fields related to location
    LocationHandling locationHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        askForPermission();
        registerSensors();
        getCurrentLocation();
        openCamera();
    }

    private void registerSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    /** Get the current location of user **/
    private void getCurrentLocation() {
        Location loc = locationHandling.getCurrentLocation();
        locToLatLng(loc);
    }

    private void locToLatLng(Location location) {
        if (location == null) return;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    /** Unregister the sensors **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    /** Finish taking photo **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {
            postCaptureHandle();
        } else finish();
    }

    private void postCaptureHandle() {
        deliverInfoToIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deliverInfoToIntent() {
        intent.putExtra("imagePath", outPath);
        intent.putExtra("imageDirection", direction);
        intent.putExtra("imageLongitude", longitude);
        intent.putExtra("imageLatitude", latitude);
    }

    /** Handle orientation change **/
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
        locationHandling = new LocationHandling(this);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE); // init sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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

    /** Handle sensor changing **/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
        if (mGravity == null || mGeomagnetic == null) return;
        float[] R = new float[9], I = new float[9];
        if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            direction = -orientation[0] * 360 / (2 * 3.14159f);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}