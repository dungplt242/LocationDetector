package com.hfad.locationdetector;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import android.util.Log;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

public class ImageCaptureFragment extends Fragment implements SensorEventListener {

    private String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"};

    private String outPath;
    private Bundle fragmentResult;
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

    MainViewModel sendPackage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    /** Finish taking photo **/
    private void postCaptureHandle() {
        fragmentResult.putString("imagePath", outPath);
        fragmentResult.putDouble("imageDirection", direction);
        fragmentResult.putDouble("imageLongitude", longitude);
        fragmentResult.putDouble("imageLatitude", latitude);
        sendPackage.update(fragmentResult);
    }

    /** Handle orientation change **/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!outPath.isEmpty()) postCaptureHandle();
    }

    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request code
                        postCaptureHandle();
                    }
                }
            });

    private void openCamera() {
        setStrictMode();
        Intent intent = (new Intent()).setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        setCaptureImageTemporaryPath(intent);
        cameraResultLauncher.launch(intent);
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
        fragmentResult = new Bundle();
        locationHandling = new LocationHandling(getActivity());
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE); // init sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sendPackage = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission() {
        int requestCode = 200;
        while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
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