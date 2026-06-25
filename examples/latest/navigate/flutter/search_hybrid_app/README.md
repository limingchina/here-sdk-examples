The SearchHybrid example app shows how to search for places including autosuggestions, for the address that belongs to certain geographic coordinates (_reverse geocoding_) and for the geographic coordinates that belong to an address (_geocoding_). You can find how this is done in [SearchExample.dart](lib/SearchExample.dart).

Map storage in this app relies only on cache, and offline maps are not loaded. Offline maps could also be implemented here, but offline search is out of scope for this app. To learn how to implement it, see the offline_maps_app example and [OfflineMapsExample.dart](../offline_maps_app/lib/OfflineMapsExample.dart).

Build instructions:
-------------------

1) Set your HERE SDK credentials programmatically in `lib/main.dart`.

2) Unzip the HERE SDK plugin to the plugins folder inside this project. Name the folder 'here_sdk': `plugins/here_sdk`.

3) Start an emulator or simulator and execute `flutter run` from the app's directory - or run the app from within your IDE.

More information can be found in the _Get Started_ section of the _Developer Guide_.
