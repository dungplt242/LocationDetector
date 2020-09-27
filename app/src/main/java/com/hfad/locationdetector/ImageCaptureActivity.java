package com.hfad.locationdetector;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class ImageCaptureActivity extends Activity implements SensorEventListener, LocationListener {

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

    //fields for location
    private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        askForPermission();
        registerSensors();
        openCamera();
        getCurrentLocation();

    }

    private void registerSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {
            postCaptureHandle();
        } else finish();
    }

    private void getCurrentLocation() {
        // TODO: Implement this method
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkAndRequestLocationPermission();
        Location loc = null;
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER);
        }
        if (loc != null) {
            Log.d("Location: ", "not nulllllll");
            getLatLng(loc);
        }
    }

/*    private void getCurrentLocation() {
        Location location = null;
        Log.d("GPS Network", "get current location");
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        //    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPDEnabled) {
            Log.d("GPS ", "get current location");
            location = getLocationFromGPS();
        }
        else if (isNetworkEnabled) {
            Log.d("Network", "get current location");
            location = getLocationFromNetwork();
        }
        getLatLng(location);
    }
*/
    private Location getLocationFromNetwork() {
        checkAndRequestLocationPermission();
        Log.d("Network", "get location from network");
    //    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    //            MIN_TIME_BW_UPDATES,
    //            MIN_DISTANCE_CHANGE_FOR_UPDATES,
    //            this);

        Log.d("Network", "Network");
        if (locationManager != null)
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return null;
    }


    private Location getLocationFromGPS() {
        checkAndRequestLocationPermission();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

        Log.d("GPS Enabled", "GPS Enabled");
        if (locationManager != null)
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return null;
    }

    private void getLatLng(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Location: ", String.valueOf(latitude)+" "+String.valueOf(longitude));
        }
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

    private void checkAndRequestLocationPermission() {
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }


}