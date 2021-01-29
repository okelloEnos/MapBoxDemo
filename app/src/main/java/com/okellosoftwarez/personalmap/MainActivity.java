package com.okellosoftwarez.personalmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.public_token));

// This contains the MapView in XML and needs to be called after the access token is configured.

        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                        addMarkerIconsToMap(style);

                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(0.2754,  34.7571))
//                                .include(new LatLng(0.0515, 37.6456))
                                .include(new LatLng(-4.0355,39.5973))
                                .build();

                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));

                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Custom.class);
                startActivity(intent);
            }
        });
    }

    private void addMarkerIconsToMap(Style style) {
        style.addImage("icon-id", BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.marker_location)));

        style.addSource(new GeoJsonSource("source-id",
                FeatureCollection.fromFeatures(new Feature[] {
                        Feature.fromGeometry(Point.fromLngLat( 34.7571, 0.2754)),
                        Feature.fromGeometry(Point.fromLngLat(39.5973, -4.0355)),
//                        Feature.fromGeometry(Point.fromLngLat(37.6456, 0.0515)),
                })));

        style.addLayer(new SymbolLayer("layer-id",
                "source-id").withProperties(
                iconImage("icon-id"),
                iconOffset(new Float[]{0f,-8f})
        ));
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}