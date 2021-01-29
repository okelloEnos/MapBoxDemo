package com.okellosoftwarez.personalmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private Button button, routeBtn;
    private TextView textView;
    private Point origin, destination;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private MapboxDirections client;
    private DirectionsRoute currentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.public_token));

// This contains the MapView in XML and needs to be called after the access token is configured.

        setContentView(R.layout.activity_main);

        origin = Point.fromLngLat(34.7571, 0.2754);
        destination =Point.fromLngLat(39.5973, -4.0355);

        button = findViewById(R.id.button);
        routeBtn = findViewById(R.id.routeBtn);
        textView = findViewById(R.id.textView);
        textView.setText("Distance : N/A            Time : N/A" );

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

                        routeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //                        Get the directions route from Map box Direction API
                                getRoutes(mapboxMap, origin, destination);
                            }
                        });
////                        Get the directions route from Map box Direction API
//                        getRoutes(mapboxMap, origin, destination);

                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Places.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     * @param mapboxMap the Mapbox map object that the route will be drawn on
     * @param origin      the starting point of the route
     * @param destination the desired finish point of the route
     */
    private void getRoutes(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.public_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                if (response.body() == null) {
                    Toast.makeText(MainActivity.this, "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                    return;

                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(MainActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
                    return;
                }

//                Get the directions route
                currentRoute = response.body().routes().get(0);

                textView.setText("Distance : " + currentRoute.distance() + "            Time : " + currentRoute.duration() );
                // Make a toast which displays the route's distance
                Toast.makeText(MainActivity.this, "Total Distance : " + currentRoute.distance()
                        + "   Estimated Time : " + currentRoute.duration(), Toast.LENGTH_LONG).show();

                if (mapboxMap != null){
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

//              Create a LineString with the directions route's geometry and
//              reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkerIconsToMap(Style style) {
        // Add the red marker icon image to the map
        style.addImage("icon-id", BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.marker_location)));

//        Add the marker sources to the map
        style.addSource(new GeoJsonSource("source-id",
                FeatureCollection.fromFeatures(new Feature[] {
                        Feature.fromGeometry(Point.fromLngLat( 34.7571, 0.2754)),
                        Feature.fromGeometry(Point.fromLngLat(39.5973, -4.0355)),
//                        Feature.fromGeometry(Point.fromLngLat(37.6456, 0.0515)),
                })));

        // Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer("layer-id",
                "source-id").withProperties(
                iconImage("icon-id"),
                iconOffset(new Float[]{0f,-8f})
        ));

//        Add the route sources to the map
        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

//        Add the route layer using lineLayer(This layer will display the directions route) to the map
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        style.addLayer(routeLayer);

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