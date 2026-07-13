The Navigation example app shows how to calculate a route from A to B and how to start **turn-by-turn navigation**. You can find how this is done in [NavigationExample.dart](lib/NavigationExample.dart). It also shows how to set a tracking view when navigation is stopped.

Features demonstrated:
----------------------

- **Route calculation**: Calculates a car route between two waypoints using the `RoutingEngine`.
- **Turn-by-turn navigation**: Uses the `VisualNavigator` with `startRendering()` to display a navigation arrow and guide the user along the route.
- **Voice guidance**: Provides localized maneuver instructions via the `EventTextListener` and a text-to-speech engine. The preferred device language is automatically detected and matched against available voice skins.
- **Maneuver notifications**: Delivers `RouteProgressListener` updates including next maneuver action, remaining distance, ETA, and traffic delay. Supports turn angle and roundabout angle information.
- **Dynamic camera behavior**: Enables auto-zoom during guidance via `DynamicCameraBehavior` (or alternatively `SpeedBasedCameraBehavior`). The guidance frame rate is set to 60 fps for smooth rendering.
- **Location simulation**: Supports both real GPS positioning (`HEREPositioningProvider` with `LocationAccuracy.navigation`) and simulated route playback (`HEREPositioningSimulator`) for testing.
- **Map-matched location**: The `NavigableLocationListener` provides map-matched coordinates, wrong-way driving detection, and current speed information.
- **Map data prefetching**: Uses `RoutePrefetcher` (corridor-based) and `PolygonPrefetcher` (area-based) to download map data into the cache in advance for a smoother offline experience.
- **Dynamic routing**: Periodically searches for better traffic-optimized routes during guidance using the `DynamicRoutingEngine`. Configurable poll intervals and time-difference thresholds control how often alternatives are checked.
- **Live traffic updates**: Calls `calculateTrafficOnRoute` periodically to update traffic information displayed on the route polyline.
- **Electronic Horizon**: Retrieves road-ahead information based on the most probable paths. Note that ADASIS is not natively supported by the HERE SDK; this example shows how to use the Electronic Horizon features without conversion to the ADASIS data format.
- **Lane recommendation**: Optionally includes lane recommendation in maneuver notifications for supported roads.
- **Camera tracking toggle**: Allows the user to enable/disable camera tracking during guidance.

Build instructions:
-------------------

1) Set your HERE SDK credentials programmatically in `lib/main.dart`.

2) Unzip the HERE SDK plugin to the plugins folder inside this project. Name the folder 'here_sdk': `plugins/here_sdk`.

3) Start an emulator or simulator and execute `flutter run` from the app's directory - or run the app from within your IDE.

More information can be found in the _Get Started_ section of the _Developer Guide_.
