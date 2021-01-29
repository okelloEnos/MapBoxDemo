package com.okellosoftwarez.personalmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.step;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Custom extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapViewDynamic;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";

    private Point origin;
    private Point destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.public_token));

        setContentView(R.layout.activity_custom);

        mapViewDynamic = findViewById(R.id.map_view_dynamic);
        mapViewDynamic.onCreate(savedInstanceState);
        mapViewDynamic.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat( 39.63, -4.02)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(  34.7571, 0.2754)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat( 39.5973, -4.0355)));

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

// Add the SymbolLayer icon image to the map style

                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        Custom.this.getResources(), R.drawable.mapbox_marker_icon_default))
                // Adding a GeoJson source for the SymbolLayer icons.
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
// the coordinate point. This is offset is not always needed and is dependent on the image
// that you use for the SymbolLayer icon.
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.

                /**
                 * Use when focusing on a singe point of location to set camera
                 */
//                CameraPosition position = new CameraPosition.Builder()
//                        .target(new LatLng( -4.0367, 39.6656))
//                        .zoom(11)
//                        .build();
//
//
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);


/**
 * Use when maintaining the view between the two or more regions at the same time
 * while placing the camera at the centre of the two location regions despite zooming influence
 */
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(0.2754,  34.7571))
                        .include(new LatLng(-4.0355,39.5973))
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));

            }
        });
    }

}