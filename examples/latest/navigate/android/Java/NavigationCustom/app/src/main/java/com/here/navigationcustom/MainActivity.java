/*
 * Copyright (C) 2019-2025 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.navigationcustom;

import static com.here.sdk.mapview.LocationIndicator.IndicatorStyle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.here.sdk.animation.AnimationListener;
import com.here.sdk.animation.AnimationState;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoCoordinatesUpdate;
import com.here.sdk.core.GeoOrientation;
import com.here.sdk.core.GeoOrientationUpdate;
import com.here.sdk.core.Location;
import com.here.sdk.core.LocationListener;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.JsonStyleFactory;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapCamera;
import com.here.sdk.mapview.MapCameraAnimation;
import com.here.sdk.mapview.MapCameraAnimationFactory;
import com.here.sdk.mapview.MapCameraListener;
import com.here.sdk.mapview.MapContentSettings;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapFeatureModes;
import com.here.sdk.mapview.MapFeatures;
import com.here.sdk.mapview.MapMarker3DModel;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapMeasureRange;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.Style;
import com.here.sdk.mapview.VisibilityState;
import com.here.sdk.navigation.CameraBehavior;
import com.here.sdk.navigation.DynamicCameraBehavior;
import com.here.sdk.navigation.FixedCameraBehavior;
import com.here.sdk.navigation.LocationSimulator;
import com.here.sdk.navigation.LocationSimulatorOptions;
import com.here.sdk.navigation.RouteProgressColors;
import com.here.sdk.navigation.VisualNavigator;
import com.here.sdk.navigation.VisualNavigatorColors;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.SectionTransportMode;
import com.here.sdk.routing.Waypoint;
import com.here.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.here.sdk.units.core.utils.EnvironmentLogger;
import com.here.sdk.units.core.utils.PermissionsRequestor;

public class MainActivity extends AppCompatActivity {

    private EnvironmentLogger environmentLogger = new EnvironmentLogger();
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double DISTANCE_IN_METERS = 1000;
    private static final List<TiltSample> DEFAULT_TILT_SAMPLES = Arrays.asList(
        new TiltSample(4.0, 0.0, 5.0),
        new TiltSample(8.0, 0.0, 15.0),
        new TiltSample(12.0, 0.0, 20.0),
        new TiltSample(16.0, 0.0, 40.0),
        new TiltSample(19.0, 0.0, 60.0)
    );
    private static final List<Double> CAMERA_ZOOM_LEVEL_SEQUENCE = Arrays.asList(14.0, 17.0, 19.5);
    private static final long CAMERA_ZOOM_DELAY_MILLIS = 3000L;
    private static final String EXTRA_STYLE_JSON = "{\n"
        + "  \"definitions\": {\n"
        + "    \"General.Labels.Scale.Factor\": 2.0,\n"
        + "    \"Is.Area.WithOutline\": false,\n"
        + "    \"Is.Area.WithSecondOutline\": false,\n"
        + "    \"Landuse.MinZoomLevel\": 10,\n"
        + "    \"BuildingAddress.MinZoomLevel\": 30,\n"
        + "    \"Building.MinZoomLevel\": 18,\n"
        + "    \"Building.Label.MinZoomLevel\": 19,\n"
        + "    \"ExtrudedBuilding.Special.MinZoomLevel\": 18,\n"
        + "    \"ExtrudedBuilding.MinZoomLevel\": 18,\n"
        + "    \"Street.Category3.SimpleLine.MinZoomLevel\": 14,\n"
        + "    \"Street.Category4.SimpleLine.MinZoomLevel\": 16,\n"
        + "    \"Street.Pedestrian.SimpleLine.MinZoomLevel\": 30,\n"
        + "    \"Street.Walkway.SimpleLine.MinZoomLevel\": 30\n"
        + "  }\n"
        + "}";
    private static final String ORIGINAL_STYLE_JSON = "{\n"
        + "  \"definitions\": {\n"
        + "    \"General.Labels.Scale.Factor\": 2.0,\n"
        + "    \"Is.Area.WithOutline\": true,\n"
        + "    \"Is.Area.WithSecondOutline\": true,\n"
        + "    \"Landuse.MinZoomLevel\": 0,\n"
        + "    \"BuildingAddress.MinZoomLevel\": 30,\n"
        + "    \"Building.MinZoomLevel\": 17,\n"
        + "    \"Building.Label.MinZoomLevel\": 17,\n"
        + "    \"ExtrudedBuilding.Special.MinZoomLevel\": 16,\n"
        + "    \"ExtrudedBuilding.MinZoomLevel\": 17,\n"
        + "    \"Street.Category3.SimpleLine.MinZoomLevel\": 11,\n"
        + "    \"Street.Category4.SimpleLine.MinZoomLevel\": 13.5,\n"
        + "    \"Street.Pedestrian.SimpleLine.MinZoomLevel\": 15.5,\n"
        + "    \"Street.Walkway.SimpleLine.MinZoomLevel\": 15.5\n"
        + "  }\n"
        + "}";

    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;
    private ScaleBarView scaleBarView;
    private RoutingEngine routingEngine;
    private VisualNavigator visualNavigator;
    private LocationSimulator locationSimulator;
    private LocationIndicator defaultLocationIndicator;
    private LocationIndicator customLocationIndicator;
    private Location lastKnownLocation = null;
    private InterpolatedTiltRestorer tiltRestorer;
    private GeoCoordinates routeStartGeoCoordinates;
    private boolean isDefaultLocationIndicator = true;
    private boolean isCustomHaloColor;
    private Color defaultHaloColor;
    private final double defaultHaloAccurarcyInMeters = 30.0;
    private final double cameraTiltInDegrees = 40.0;
    private final double cameraDistanceInMeters = 200.0;
    private Handler cameraZoomHandler;
    private boolean cameraZoomSequenceActive;
    private int cameraZoomStepIndex;

    // UI references for framerate controls
    private EditText mapViewFrameRateInput;
    private EditText guidanceFrameRateInput;
    private Button setMapViewFrameRateButton;
    private Button setGuidanceFrameRateButton;
    private CheckBox tiltLimitCheckBox;
    private CheckBox extraStyleCheckBox;
    private boolean tiltLimitingEnabled = true;
    private boolean extraStyleEnabled = true;
    private boolean mapSceneConfiguredOnce;

    private MapCameraListener scaleBarCameraListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log application and device details.
        // It expects a string parameter that describes the application source language.
        environmentLogger.logEnvironment("Java");

        // Usually, you need to initialize the HERE SDK only once during the lifetime of an application.
        initializeHERESDK();

        setContentView(R.layout.activity_main);

        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // Initialize ScaleBarView
        scaleBarView = findViewById(R.id.scale_bar);
        scaleBarView.setMapView(mapView);

        // Initialize UI controls (framerate inputs and buttons).
        mapViewFrameRateInput = findViewById(R.id.mapview_framerate);
        guidanceFrameRateInput = findViewById(R.id.guidance_framerate);
        setMapViewFrameRateButton = findViewById(R.id.set_mapview_framerate_button);
        setGuidanceFrameRateButton = findViewById(R.id.set_guidance_framerate_button);
        tiltLimitCheckBox = findViewById(R.id.tilt_limit_checkbox);
        extraStyleCheckBox = findViewById(R.id.extra_style_checkbox);
        cameraZoomHandler = new Handler(Looper.getMainLooper());

        tiltLimitCheckBox.setChecked(tiltLimitingEnabled);
        extraStyleCheckBox.setChecked(extraStyleEnabled);

        tiltLimitCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onTiltLimitingChanged(isChecked);
            }
        });

        extraStyleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onExtraStyleChanged(isChecked);
            }
        });

        handleAndroidPermissions();

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }

        try {
            // Without a route set, this starts tracking mode.
            visualNavigator = new VisualNavigator();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of VisualNavigator failed: " + e.error.name());
        }

        // Update the UI state for framerate controls depending on whether guidance is active.
        updateFramerateUIState();

        showDialog("Custom Navigation",
                "Start / stop simulated route guidance. Toggle between custom / default LocationIndicator.");
    }

    private void initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        String accessKeyID = BuildConfig.HERE_ACCESS_KEY_ID;
        String accessKeySecret = BuildConfig.HERE_ACCESS_KEY_SECRET;
        AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret);
        SDKOptions options = new SDKOptions(authenticationMode);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener() {

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene() {
        loadMapScene(null);
    }

    private void loadMapScene(@Nullable MapCamera.State cameraStateToRestore) {
        final MapCamera.State stateToRestore = cameraStateToRestore;
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    onMapSceneLoaded(stateToRestore);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }

    private void onMapSceneLoaded(@Nullable MapCamera.State cameraStateToRestore) {
        if (cameraStateToRestore != null) {
            restoreCameraState(cameraStateToRestore);
        } else {
            if (routeStartGeoCoordinates == null) {
                // Berlin
                //routeStartGeoCoordinates = new GeoCoordinates(52.520798, 13.409408);
                // Hong Kong
                routeStartGeoCoordinates = new GeoCoordinates(22.28109, 114.16575);
            }
            MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, DISTANCE_IN_METERS);
            mapView.getCamera().lookAt(routeStartGeoCoordinates, mapMeasureZoom);
        }

        applyMapFeatures();

        if (tiltRestorer == null) {
            tiltRestorer = new InterpolatedTiltRestorer(
                    mapView.getCamera(),
                    DEFAULT_TILT_SAMPLES,
                    TiltPreference.MAX,
                    0.01
            );
            mapView.getCamera().addListener(tiltRestorer);
        }

        if (!mapSceneConfiguredOnce) {
            scaleBarCameraListener = new MapCameraListener() {
                @Override
                public void onMapCameraUpdated(@NonNull MapCamera.State cameraState) {
                    scaleBarView.updateScale(cameraState.zoomLevel);
                }
            };
            mapView.getCamera().addListener(scaleBarCameraListener);
            defaultLocationIndicator = createDefaultLocationIndicator();
            customLocationIndicator = createCustomLocationIndicator();
            isDefaultLocationIndicator = true;
            switchToPedestrianLocationIndicator();
            setMapViewFrameRateClicked(mapView);
        } else if (visualNavigator != null && visualNavigator.isRendering()) {
            switchToNavigationLocationIndicator();
        } else {
            switchToPedestrianLocationIndicator();
        }

        if (tiltRestorer != null) {
            tiltRestorer.setEnabled(tiltLimitingEnabled);
            if (tiltLimitingEnabled) {
                tiltRestorer.enforce();
            }
        }

        if (extraStyleEnabled) {
            applyExtraStyle();
        } else {
            applyOriginalStyle();
        }

        scaleBarView.updateScale(mapView.getCamera().getState().zoomLevel);

        mapSceneConfiguredOnce = true;
    }

    private void applyMapFeatures() {
        Map<String, String> mapFeatures = new HashMap<>();
        mapFeatures.put(MapFeatures.TRAFFIC_FLOW, MapFeatureModes.TRAFFIC_FLOW_WITHOUT_FREE_FLOW);
        mapFeatures.put(MapFeatures.TRAFFIC_INCIDENTS, MapFeatureModes.DEFAULT);
        mapView.getMapScene().enableFeatures(mapFeatures);

        MapContentSettings.setPoiCategoriesVisibility(
                new ArrayList<>(Arrays.asList("100", "200", "300", "350",
                        "400", "500", "550", "600", "700", "800", "900")),
                VisibilityState.VISIBLE); // Switch to hidden to disable POIs

        mapView.getCamera().getLimits().setZoomRange(new MapMeasureRange(
                MapMeasure.Kind.ZOOM_LEVEL, 6.0, 21.0)
        );
    }

    private void applyExtraStyle() {
        try {
            Style extraStyle = JsonStyleFactory.createFromString(EXTRA_STYLE_JSON);
            mapView.getHereMap().getStyle().update(extraStyle);
        } catch (JsonStyleFactory.InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyOriginalStyle() {
        try {
            Style originalStyle = JsonStyleFactory.createFromString(ORIGINAL_STYLE_JSON);
            mapView.getHereMap().getStyle().update(originalStyle);
        } catch (JsonStyleFactory.InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void restoreCameraState(@NonNull MapCamera.State cameraState) {
        GeoOrientation orientation = cameraState.orientationAtTarget;
        Double bearing = orientation != null ? orientation.bearing : null;
        double tilt = orientation != null ? orientation.tilt : 0.0;
        GeoOrientationUpdate orientationUpdate = new GeoOrientationUpdate(bearing, tilt);
        MapMeasure mapMeasure = new MapMeasure(MapMeasure.Kind.ZOOM_LEVEL, cameraState.zoomLevel);
        mapView.getCamera().lookAt(cameraState.targetCoordinates, orientationUpdate, mapMeasure);
    }

    private void onTiltLimitingChanged(boolean enabled) {
        tiltLimitingEnabled = enabled;
        if (tiltRestorer != null) {
            tiltRestorer.setEnabled(enabled);
            if (enabled) {
                tiltRestorer.enforce();
            }
        }
    }

    private void onExtraStyleChanged(boolean enabled) {
        extraStyleEnabled = enabled;
        if (!mapSceneConfiguredOnce || mapView == null) {
            return;
        }
        if (enabled) {
            applyExtraStyle();
        } else {
            applyOriginalStyle();
        }
    }

    private LocationIndicator createDefaultLocationIndicator() {
        LocationIndicator locationIndicator = new LocationIndicator();
        locationIndicator.setAccuracyVisualized(true);
        locationIndicator.setLocationIndicatorStyle(IndicatorStyle.PEDESTRIAN);
        defaultHaloColor = locationIndicator.getHaloColor(locationIndicator.getLocationIndicatorStyle());
        return locationIndicator;
    }

    private LocationIndicator createCustomLocationIndicator() {
        String pedGeometryFile = "custom_location_indicator_pedestrian.obj";
        String pedTextureFile = "custom_location_indicator_pedestrian.png";
        MapMarker3DModel pedestrianMapMarker3DModel = new MapMarker3DModel(pedGeometryFile, pedTextureFile);

        String navGeometryFile = "custom_location_indicator_navigation.obj";
        String navTextureFile = "custom_location_indicator_navigation.png";
        MapMarker3DModel navigationMapMarker3DModel = new MapMarker3DModel(navGeometryFile, navTextureFile);

        LocationIndicator locationIndicator = new LocationIndicator();
        double scaleFactor = 3;

        // Note: For this example app, we use only simulated location data.
        // Therefore, we do not create a custom LocationIndicator for
        // MarkerType.PEDESTRIAN_INACTIVE and MarkerType.NAVIGATION_INACTIVE.
        // If set with a gray texture model, the type can be switched by calling locationIndicator.setActive(false)
        // when the GPS accuracy is weak or no location was found.
        locationIndicator.setMarker3dModel(pedestrianMapMarker3DModel, scaleFactor, LocationIndicator.MarkerType.PEDESTRIAN);
        locationIndicator.setMarker3dModel(navigationMapMarker3DModel, scaleFactor, LocationIndicator.MarkerType.NAVIGATION);

        locationIndicator.setAccuracyVisualized(true);
        locationIndicator.setLocationIndicatorStyle(IndicatorStyle.PEDESTRIAN);

        return locationIndicator;
    }

    // Calculate a fixed route for testing and start guidance simulation along the route.
    public void startButtonClicked(View view) {
        if (visualNavigator.isRendering()) {
            return;
        }

        Waypoint startWaypoint = new Waypoint(getLastKnownLocation().coordinates);
        //Waypoint destinationWaypoint = new Waypoint(new GeoCoordinates(52.530905, 13.385007));
        // Hong Kong
        Waypoint destinationWaypoint = new Waypoint(new GeoCoordinates(22.315874, 114.175041));
        routingEngine.calculateRoute(
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint)),
                new CarOptions(),
                (routingError, routes) -> {
                    if (routingError == null) {
                        Route route = routes.get(0);
                        animateToRouteStart(route);
                    } else {
                        Log.e("Route calculation error", routingError.toString());
                    }
                });
    }

    // Stop guidance simulation and switch pedestrian LocationIndicator on.
    public void stopButtonClicked(View view) {
        stopGuidance();
    }

    public void cycleCameraZoomButtonClicked(View view) {
        if (mapView == null) {
            return;
        }

        stopCameraZoomSequence();
        cameraZoomSequenceActive = true;
        cameraZoomStepIndex = 0;
        performCameraZoomStep();
    }

    private void performCameraZoomStep() {

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<Integer, Integer> scaleMap = new HashMap<>();
                scaleMap.put(0, 18);//100 - 500m
                scaleMap.put(1, 17);//500M - 1km
                scaleMap.put(2, 16);//1KM -5KM
                scaleMap.put(3, 15);//5KM -
                scaleMap.put(4, 14);
                scaleMap.put(5, 13);
                scaleMap.put(6, 12);
                scaleMap.put(7, 11);
                scaleMap.put(8, 10);
                scaleMap.put(9, 9);
                scaleMap.put(10, 8);
                scaleMap.put(11, 7);
                scaleMap.put(12, 6);
                try {
                    for (int i = 0; i < scaleMap.size(); i++) {
                        for (int j = 0; j < 20; j++) {
                            double currentScale = scaleMap.get(i) - j * 0.05;
                            mapView.getCamera().zoomTo(currentScale);
                            Thread.sleep(100); // 20HZ
                        }
                        Thread.sleep(500);
                    }
                    Thread.sleep(3000);
                    for (int i = (scaleMap.size() - 1); i > 0; i--) {
                        for (int j = 0; j < 20; j++) {
                            double currentScale = scaleMap.get(i) + j * 0.05;
                            mapView.getCamera().zoomTo(currentScale);
                            Thread.sleep(100); // 20HZ
                        }
                        Thread.sleep(500);
                    }
                    Thread.sleep(3000);
                    scaleMap.clear();
                    scaleMap.put(0, 22);//100 - 500m
                    scaleMap.put(1, 21);//100 - 500m
                    scaleMap.put(2, 20);//100 - 500m
                    scaleMap.put(3, 19);//100 - 500m
                    scaleMap.put(4, 18);//100 - 500m
                    for (int i = (scaleMap.size() - 1); i > 0; i--) {
                        for (int j = 0; j < 20; j++) {
                            double currentScale = scaleMap.get(i) + j * 0.05;
                            mapView.getCamera().zoomTo(currentScale);
                            Thread.sleep(100); // 20HZ
                        }
                        Thread.sleep(500);
                    }
                    Thread.sleep(3000);
                    for (int i = 0; i < scaleMap.size(); i++) {
                        for (int j = 0; j < 20; j++) {
                            double currentScale = scaleMap.get(i) - j * 0.05;
                            mapView.getCamera().zoomTo(currentScale);
                            Thread.sleep(100); // 20HZ
                        }
                        Thread.sleep(500);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void scheduleNextZoomStep() {
        if (!cameraZoomSequenceActive || cameraZoomHandler == null) {
            return;
        }

        cameraZoomStepIndex++;
        if (cameraZoomStepIndex >= CAMERA_ZOOM_LEVEL_SEQUENCE.size()) {
            stopCameraZoomSequence();
            return;
        }

        cameraZoomHandler.postDelayed(this::performCameraZoomStep, CAMERA_ZOOM_DELAY_MILLIS);
    }

    private void stopCameraZoomSequence() {
        cameraZoomSequenceActive = false;
        cameraZoomStepIndex = 0;
        if (cameraZoomHandler != null) {
            cameraZoomHandler.removeCallbacksAndMessages(null);
        }
    }

    // Toggle between the default LocationIndicator and custom LocationIndicator.
    // The default LocationIndicator uses a 3D asset that is part of the HERE SDK.
    // The custom LocationIndicator uses different 3D assets, see asset folder.
    public void toggleStyleButtonClicked(View view) {
        // Toggle state.
        isDefaultLocationIndicator = !isDefaultLocationIndicator;

        // Select pedestrian or navigation assets.
        if (visualNavigator.isRendering()) {
            switchToNavigationLocationIndicator();
        } else {
            switchToPedestrianLocationIndicator();
        }
    }

    // Toggle the halo color of the default LocationIndicator.
    public void togglehaloColorButtonClicked(View view) {
        // Toggle state.
        isCustomHaloColor = !isCustomHaloColor;
        setSelectedHaloColor();
    }

    // Move camera back to the default start location at zoom level 17
    public void moveToDefaultLocationClicked(View view) {
        if (routeStartGeoCoordinates == null) {
            Toast.makeText(this, "Default location not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        MapMeasure mapMeasure = new MapMeasure(
                MapMeasure.Kind.ZOOM_LEVEL,
                17.0f);
        mapView.getCamera().lookAt(routeStartGeoCoordinates, mapMeasure);

        if (tiltRestorer != null) {
            tiltRestorer.enforce();
        }

        Log.d(TAG, "Camera moved to default location at zoom level 17.");
    }

    // Set the MapView framerate based on user input.
    public void setMapViewFrameRateClicked(View view) {
        String frameRateText = mapViewFrameRateInput.getText().toString();

        if (frameRateText.isEmpty()) {
            Toast.makeText(this, "Please enter a framerate value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int frameRate = Integer.parseInt(frameRateText);
            if (frameRate <= 0 || frameRate > 120) {
                Toast.makeText(this, "Framerate must be between 1 and 120", Toast.LENGTH_SHORT).show();
                return;
            }

            mapView.setFrameRate(frameRate);
            Toast.makeText(this, "MapView framerate set to " + frameRate + " FPS", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "MapView framerate set to: " + frameRate);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid framerate value", Toast.LENGTH_SHORT).show();
        }
    }

    // Set the Guidance (VisualNavigator) framerate based on user input.
    public void setGuidanceFrameRateClicked(View view) {
        String frameRateText = guidanceFrameRateInput.getText().toString();

        if (frameRateText.isEmpty()) {
            Toast.makeText(this, "Please enter a framerate value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int frameRate = Integer.parseInt(frameRateText);
            if (frameRate <= 0 || frameRate > 120) {
                Toast.makeText(this, "Framerate must be between 1 and 120", Toast.LENGTH_SHORT).show();
                return;
            }
            // Set the initial framerate for guidance rendering.
            visualNavigator.setGuidanceFrameRate(frameRate);
            Toast.makeText(this, "Guidance framerate set to " + frameRate + " FPS", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Guidance framerate set to: " + frameRate);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid framerate value", Toast.LENGTH_SHORT).show();
        }
    }

    // Update the enabled/disabled state of the framerate inputs and buttons.
    private void updateFramerateUIState() {
        boolean rendering = (visualNavigator != null && visualNavigator.isRendering());

        // When guidance is active, editing MapView framerate should be disabled.
        mapViewFrameRateInput.setEnabled(!rendering);
        setMapViewFrameRateButton.setEnabled(!rendering);

        // When guidance is not active, editing Guidance framerate should be disabled.
        guidanceFrameRateInput.setEnabled(rendering);
        setGuidanceFrameRateButton.setEnabled(rendering);
    }

    private void setSelectedHaloColor() {
        if (isCustomHaloColor) {
            Color customHaloColor = new Color(255F, 255F, 0F, 0.30F);
            defaultLocationIndicator.setHaloColor(defaultLocationIndicator.getLocationIndicatorStyle(), customHaloColor);
            customLocationIndicator.setHaloColor(customLocationIndicator.getLocationIndicatorStyle(), customHaloColor);
        } else {
            defaultLocationIndicator.setHaloColor(defaultLocationIndicator.getLocationIndicatorStyle(), defaultHaloColor);
            customLocationIndicator.setHaloColor(customLocationIndicator.getLocationIndicatorStyle(), defaultHaloColor);
        }
    }

    private void switchToPedestrianLocationIndicator() {
        if (isDefaultLocationIndicator) {
            defaultLocationIndicator.enable(mapView);
            defaultLocationIndicator.setLocationIndicatorStyle(IndicatorStyle.PEDESTRIAN);
            customLocationIndicator.disable();
        } else {
            defaultLocationIndicator.disable();
            customLocationIndicator.enable(mapView);
            customLocationIndicator.setLocationIndicatorStyle(IndicatorStyle.PEDESTRIAN);
        }

        // Set last location from LocationSimulator.
        defaultLocationIndicator.updateLocation(getLastKnownLocation());
        customLocationIndicator.updateLocation(getLastKnownLocation());

        setSelectedHaloColor();
    }

    private void switchToNavigationLocationIndicator() {
        if (isDefaultLocationIndicator) {
            // By default, the VisualNavigator adds a LocationIndicator on its own.
            // This can be kept by calling visualNavigator.customLocationIndicator = nil
            // However, here we want to be able to customize the halo for the default location indicator.
            // Therefore, we still need to set our own instance to the VisualNavigator.
            customLocationIndicator.disable();
            defaultLocationIndicator.enable(mapView);
            defaultLocationIndicator.setLocationIndicatorStyle(IndicatorStyle.NAVIGATION);
            visualNavigator.setCustomLocationIndicator(defaultLocationIndicator);
        } else {
            defaultLocationIndicator.disable();
            customLocationIndicator.enable(mapView);
            customLocationIndicator.setLocationIndicatorStyle(IndicatorStyle.NAVIGATION);
            visualNavigator.setCustomLocationIndicator(customLocationIndicator);

            // Note that the type of the LocationIndicator is taken from the route's TransportMode.
            // It cannot be overridden during guidance.
            // During tracking mode (not shown in this app) you can specify the marker type via:
            // visualNavigator.setTrackingTransportMode(TransportMode.PEDESTRIAN);
        }

        setSelectedHaloColor();
    }

    private Location getLastKnownLocation() {
        if (lastKnownLocation == null) {
            // A LocationIndicator is intended to mark the user's current location,
            // including a bearing direction.
            // For testing purposes, we create below a Location object. Usually, you want to get this from
            // a GPS sensor instead. Check the Positioning example app for this.
            Location location = new Location(routeStartGeoCoordinates);
            location.time = new Date();
            location.horizontalAccuracyInMeters = defaultHaloAccurarcyInMeters;
            return location;
        }
        // This location is taken from the LocationSimulator that provides locations along the route.
        return lastKnownLocation;
    }

    // Animate to custom guidance perspective, centered on start location of route.
    private void animateToRouteStart(Route route) {
        // The first coordinate marks the start location of the route.
        GeoCoordinates startOfRoute = route.getGeometry().vertices.get(0);
        GeoCoordinatesUpdate geoCoordinatesUpdate = new GeoCoordinatesUpdate(startOfRoute);

        Double bearingInDegrees = null;
        double tiltInDegrees = cameraTiltInDegrees;
        GeoOrientationUpdate orientationUpdate = new GeoOrientationUpdate(bearingInDegrees, tiltInDegrees);

        double distanceInMeters = cameraDistanceInMeters;
        MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters);

        double bowFactor = 1;
        MapCameraAnimation animation = MapCameraAnimationFactory.flyTo(
                geoCoordinatesUpdate, orientationUpdate, mapMeasureZoom, bowFactor, Duration.ofSeconds(3));
        mapView.getCamera().startAnimation(animation, new AnimationListener() {
            @Override
            public void onAnimationStateChanged(@NonNull AnimationState animationState) {
                if (animationState == AnimationState.COMPLETED
                        || animationState == AnimationState.CANCELLED) {
                    startGuidance(route);
                }
            }
        });
    }

    private void animateToDefaultMapPerspective() {
        GeoCoordinates targetLocation = mapView.getCamera().getState().targetCoordinates;
        GeoCoordinatesUpdate geoCoordinatesUpdate = new GeoCoordinatesUpdate(targetLocation);

        // By setting null we keep the current bearing rotation of the map.
        Double bearingInDegrees = null;
        double tiltInDegrees = 0;
        GeoOrientationUpdate orientationUpdate = new GeoOrientationUpdate(bearingInDegrees, tiltInDegrees);

        MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, DISTANCE_IN_METERS);
        double bowFactor = 1;
        MapCameraAnimation animation = MapCameraAnimationFactory.flyTo(
                geoCoordinatesUpdate, orientationUpdate, mapMeasureZoom, bowFactor, Duration.ofSeconds(3));
        mapView.getCamera().startAnimation(animation);
    }

    private void startGuidance(Route route) {
        if (visualNavigator.isRendering()) {
            return;
        }
        setGuidanceFrameRateClicked(mapView);
        if (tiltRestorer != null) {
            tiltRestorer.setEnabled(false);
        }
        // Set the route and maneuver arrow color.
        customizeVisualNavigatorColors();

        // Set custom guidance perspective.
        customizeGuidanceView();

        // This enables a navigation view and adds a LocationIndicator.
        visualNavigator.startRendering(mapView);

        // Note: By default, when VisualNavigator starts rendering, a default LocationIndicator is added
        // by the HERE SDK automatically.
        visualNavigator.setCustomLocationIndicator(customLocationIndicator);
        switchToNavigationLocationIndicator();

        visualNavigator.setCameraBehavior(new DynamicCameraBehavior());
        // Set a route to follow. This leaves tracking mode.
        visualNavigator.setRoute(route);

        // This app does not use real location updates. Instead it provides location updates based
        // on the geographic coordinates of a route using HERE SDK's LocationSimulator.
        startRouteSimulation(route);

        // Update UI to reflect that guidance is now active.
        updateFramerateUIState();
    }

    private void stopGuidance() {
        visualNavigator.stopRendering();

        if (locationSimulator != null) {
            locationSimulator.stop();
        }

        // Note: By default, when VisualNavigator stops rendering, no LocationIndicator is visible.
        switchToPedestrianLocationIndicator();

        animateToDefaultMapPerspective();

        if (tiltRestorer != null) {
            tiltRestorer.setEnabled(tiltLimitingEnabled);
            if (tiltLimitingEnabled) {
                tiltRestorer.enforce();
            }
        }

        // Update UI to reflect that guidance is no longer active.
        updateFramerateUIState();
    }

    private void customizeVisualNavigatorColors() {
        Color routeAheadColor =  Color.valueOf(android.graphics.Color.BLUE);
        Color routeBehindColor = Color.valueOf(android.graphics.Color.RED);
        Color routeAheadOutlineColor = Color.valueOf(android.graphics.Color.YELLOW);
        Color routeBehindOutlineColor = Color.valueOf(android.graphics.Color.DKGRAY);
        Color maneuverArrowColor = Color.valueOf(android.graphics.Color.GREEN);

        VisualNavigatorColors visualNavigatorColors = VisualNavigatorColors.dayColors();
        RouteProgressColors routeProgressColors = new RouteProgressColors(
                routeAheadColor,
                routeBehindColor,
                routeAheadOutlineColor,
                routeBehindOutlineColor);

        // Sets the color used to draw maneuver arrows.
        visualNavigatorColors.setManeuverArrowColor(maneuverArrowColor);
        // Sets route color for a single transport mode. Other modes are kept using defaults.
        visualNavigatorColors.setRouteProgressColors(SectionTransportMode.CAR, routeProgressColors);
        // Sets the adjusted colors for route progress and maneuver arrows based on the day color scheme.
        visualNavigator.setColors(visualNavigatorColors);
    }

    private void customizeGuidanceView() {
        FixedCameraBehavior cameraBehavior = new FixedCameraBehavior();
        // Set custom zoom level and tilt.
        cameraBehavior.setCameraDistanceInMeters(cameraDistanceInMeters);
        cameraBehavior.setCameraTiltInDegrees(cameraTiltInDegrees);
        // Disable North-Up mode by setting null. Enable North-up mode by setting Double.valueOf(0).
        // By default, North-Up mode is disabled.
        cameraBehavior.setCameraBearingInDegrees(null);
        cameraBehavior.setNormalizedPrincipalPoint(new Anchor2D(0.5, 0.5));

        // The CameraBehavior can be updated during guidance at any time as often as desired.
        // Alternatively, use DynamicCameraBehavior for auto-zoom.
        visualNavigator.setCameraBehavior(cameraBehavior);
    }

    private final LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationUpdated(@NonNull Location location) {
            // By default, accuracy is null during simulation, but we want to customize the halo,
            // so we hijack the location object and add an accuracy value.
            Location updatedLocation = addHorizontalAccuracy(location);
            // Feed location data into the VisualNavigator.
            visualNavigator.onLocationUpdated(updatedLocation);
            lastKnownLocation = updatedLocation;
        }
    };

    private Location addHorizontalAccuracy(Location simulatedLocation) {
        Location location = new Location(simulatedLocation.coordinates);
        location.time = simulatedLocation.time;
        location.bearingInDegrees = simulatedLocation.bearingInDegrees;
        location.horizontalAccuracyInMeters = defaultHaloAccurarcyInMeters;
        return location;
    }

    private void startRouteSimulation(Route route) {
        if (locationSimulator != null) {
            // Make sure to stop an existing LocationSimulator before starting a new one.
            locationSimulator.stop();
        }

        try {
            // Provides fake GPS signals based on the route geometry.
            locationSimulator = new LocationSimulator(route, new LocationSimulatorOptions());
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of LocationSimulator failed: " + e.error.name());
        }

        locationSimulator.setListener(myLocationListener);
        locationSimulator.start();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        stopCameraZoomSequence();
        // Keep UI state consistent when pausing the activity.
        updateFramerateUIState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        // Refresh UI state in case guidance rendering state changed while paused.
        updateFramerateUIState();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        visualNavigator.stopRendering();
        locationSimulator.stop();
        stopCameraZoomSequence();
        if (tiltRestorer != null) {
            mapView.getCamera().removeListener(tiltRestorer);
        }
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null);
        }
    }

    private enum TiltPreference { MIN, MAX }

    private static final class TiltSample {
        final double zoomLevel;
        final double minTiltDeg;
        final double maxTiltDeg;

        private TiltSample(double zoomLevel, double minTiltDeg, double maxTiltDeg) {
            this.zoomLevel = zoomLevel;
            this.minTiltDeg = minTiltDeg;
            this.maxTiltDeg = maxTiltDeg;
        }
    }

    private static final class InterpolatedTiltRestorer implements MapCameraListener {
        private final MapCamera camera;
        private final List<TiltSample> samples;
        private final TiltPreference preference;
        private final double toleranceDeg;
        private boolean ignoreNextUpdate;
    private boolean enabled = true;

        private InterpolatedTiltRestorer(MapCamera camera,
                                         List<TiltSample> samples,
                                         TiltPreference preference,
                                         double toleranceDeg) {
            this.camera = camera;
            this.samples = new ArrayList<>(samples);
            this.samples.sort((a, b) -> Double.compare(a.zoomLevel, b.zoomLevel));
            this.preference = preference;
            this.toleranceDeg = toleranceDeg;
        }

        @Override
        public void onMapCameraUpdated(@NonNull MapCamera.State state) {
            if (ignoreNextUpdate) {
                ignoreNextUpdate = false;
                return;
            }
            if (!enabled) {
                return;
            }
            if (samples.isEmpty()) {
                return;
            }

            double targetTilt = desiredTiltForZoom(state.zoomLevel);
            double currentTilt = state.orientationAtTarget.tilt;

            if (Math.abs(currentTilt - targetTilt) <= toleranceDeg) {
                return;
            }

            ignoreNextUpdate = true;
            camera.setOrientationAtTarget(new GeoOrientationUpdate(null, targetTilt));
        }

        void enforce() {
            if (!enabled || samples.isEmpty()) {
                return;
            }

            MapCamera.State state = camera.getState();
            double targetTilt = desiredTiltForZoom(state.zoomLevel);
            ignoreNextUpdate = true;
            camera.setOrientationAtTarget(new GeoOrientationUpdate(null, targetTilt));
        }

        void setEnabled(boolean enabled) {
            this.enabled = enabled;
            ignoreNextUpdate = false;
        }

        private double desiredTiltForZoom(double zoom) {
            TiltSample first = samples.get(0);
            TiltSample last = samples.get(samples.size() - 1);

            if (zoom <= first.zoomLevel) {
                return preference == TiltPreference.MAX ? first.maxTiltDeg : first.minTiltDeg;
            }
            if (zoom >= last.zoomLevel) {
                return preference == TiltPreference.MAX ? last.maxTiltDeg : last.minTiltDeg;
            }

            TiltSample lower = first;
            for (int i = 1; i < samples.size(); i++) {
                TiltSample upper = samples.get(i);
                if (zoom < upper.zoomLevel) {
                    double t = interpolationFactor(lower.zoomLevel, upper.zoomLevel, zoom);
                    double minTilt = lerp(lower.minTiltDeg, upper.minTiltDeg, t);
                    double maxTilt = lerp(lower.maxTiltDeg, upper.maxTiltDeg, t);
                    return preference == TiltPreference.MAX ? maxTilt : minTilt;
                }
                lower = upper;
            }

            return preference == TiltPreference.MAX ? last.maxTiltDeg : last.minTiltDeg;
        }

        private double interpolationFactor(double z0, double z1, double z) {
            double log0 = Math.log(z0 + 1.0);
            double log1 = Math.log(z1 + 1.0);
            double logZ = Math.log(z + 1.0);
            double denominator = log1 - log0;
            if (Math.abs(denominator) <= 1e-9) {
                return 0.0;
            }
            return (logZ - log0) / denominator;
        }

        private double lerp(double start, double end, double t) {
            return start + (end - start) * t;
        }
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .show();
    }
}
