# ScaleBar Integration Guide

## Changes needed in MainActivity.java

### 1. Add ScaleBarView field (after line 90, after `private MapView mapView;`)
```java
private ScaleBarView scaleBarView;
```

### 2. Initialize ScaleBarView in onCreate() method (after line 128, after `mapView.onCreate(savedInstanceState);`)
```java
// Initialize ScaleBarView
scaleBarView = findViewById(R.id.scale_bar);
```

### 3. Add MapCameraListener in loadMapScene() method (after line 225, in the onLoadScene callback where the map loads successfully)

Add this code after `switchToPedestrianLocationIndicator();`:

```java
// Setup scale bar to update on zoom level changes
mapView.getCamera().addObserver(new MapCameraListener() {
    @Override
    public void onCameraUpdated(@NonNull com.here.sdk.mapview.MapCamera.State state) {
        // Update scale bar based on current zoom level
        scaleBarView.updateScale(state.zoomLevel);
    }
});

// Initialize scale bar with current zoom level
scaleBarView.updateScale(mapView.getCamera().getState().zoomLevel);
```

## Summary
The ScaleBarView has been created and added to the layout at the bottom-right corner. 
You need to add the above three code snippets to MainActivity.java to complete the integration.

