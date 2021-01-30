package com.okellosoftwarez.personalmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class UserLocation extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private PermissionsManager permissionsManager;
    private MapboxMap userLocMapboxMap;
    private MapView userLocMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.public_token));

        setContentView(R.layout.activity_user_location);

        userLocMapView = findViewById(R.id.userLocMapView);
        userLocMapView.onCreate(savedInstanceState);
        userLocMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        userLocMapboxMap = mapboxMap;
        userLocMapboxMap.setStyle(Style.MAPBOX_STREETS,
        new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(Style style) {

//Check if permissions are enabled if not request one
        if (PermissionsManager.areLocationPermissionsGranted(this)) {



            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .pulseEnabled(true)
                            .pulseColor(Color.BLUE)
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(this, style)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

//            Get an instance of locationComponent
            LocationComponent locationComponent = userLocMapboxMap.getLocationComponent();

//            Activate location component with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);


//            Get an instance of locationComponent
//            LocationComponent locationComponent = userLocMapboxMap.getLocationComponent();

//            Enable to make the component visible
            locationComponent.setLocationComponentEnabled(true);

//            set the component camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

//            set the component render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "In order to acquire Your Location you have to grant location permission", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {

            userLocMapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        }
        else {
            Toast.makeText(this, "Location Permission are not Granted!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        userLocMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLocMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userLocMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userLocMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        userLocMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userLocMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        userLocMapView.onLowMemory();
    }
}