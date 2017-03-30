package com.example.alex.maptest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorMagnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor sensorAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, sensorMagnetic, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        sm.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.addMarker(new MarkerOptions().position(new LatLng(10, 10)).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_face)));

        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_face)));

        map.addMarker(new MarkerOptions().position(new LatLng(-10, -10)).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_face)));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Popup Window")
                        .setMessage("Here you can display any layout you want")
                        .setPositiveButton("Ok", null)
                        .create();
                alertDialog.show();

                return false;
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0, 0)));
    }

    private void updateCameraBearing(float bearing) {
        CameraPosition camPos = CameraPosition.builder(map.getCameraPosition()).bearing(bearing).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    float[] gravity;
    float[] geomagnetic;

    public void onSensorChanged(SensorEvent event) {
        if (map != null) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                gravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomagnetic = event.values;
            if (gravity != null && geomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimuth = (int) Math.round(Math.toDegrees(orientation[0]));
                    updateCameraBearing(azimuth);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
