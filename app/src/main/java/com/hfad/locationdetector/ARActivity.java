package com.hfad.locationdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.camera.CameraSettings;

import java.io.IOException;

public class ARActivity extends Activity implements LocationListener {

    private ArchitectView architectView;

    private String[] permissions = {"android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION"};

    private LocationProvider locationProvider;

    private final LocationProvider.ErrorCallback errorCallback =
            new LocationProvider.ErrorCallback() {
        @Override
        public void noProvidersEnabled() {}
    };


    private final ArchitectView.SensorAccuracyChangeListener sensorAccuracyChangeListener =
            new ArchitectView.SensorAccuracyChangeListener() {
        @Override
        public void onCompassAccuracyChanged(int accuracy) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //WebView.setWebContentsDebuggingEnabled(true);
        initComponents();
        architectView.onCreate(setARViewConfig());
        setContentView(architectView);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private ArchitectStartupConfiguration setARViewConfig() {
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(getString(R.string.wikitude_key));
        config.setCameraPosition(CameraSettings.CameraPosition.BACK);
        config.setCameraResolution(CameraSettings.CameraResolution.AUTO);
        config.setCameraFocusMode(CameraSettings.CameraFocusMode.CONTINUOUS);
        config.setCamera2Enabled(true);
        return config;
    }

    private void initComponents() {
        architectView = new ArchitectView(this);
        locationProvider = new LocationProvider(this, this, errorCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArchitectView.getPermissionManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause();
        locationProvider.onPause();
        architectView.unregisterSensorAccuracyChangeListener(sensorAccuracyChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume();
        locationProvider.onResume();
        architectView.registerSensorAccuracyChangeListener(sensorAccuracyChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        architectView.clearCache();
        architectView.onDestroy();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try { architectView.load("index.html"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        float accuracy = location.hasAccuracy() ? location.getAccuracy() : 1000;
        if (location.hasAltitude()) {
            architectView.setLocation(location.getLatitude(),
                    location.getLongitude(), location.getAltitude(), accuracy);
        }
        else {
            architectView.setLocation(location.getLatitude(), location.getLongitude(), accuracy);
        }
    }
}