/*
 * Copyright (C) 2020-2026 HERE Europe B.V.
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

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:here_sdk/core.errors.dart';
import 'package:here_sdk/core.dart';
import 'package:here_sdk/core.engine.dart';
import 'package:indoor_map_app/indoor_map_screen.dart';
import 'package:indoor_map_app/indoor_routing_data_provider.dart';
import 'package:indoor_map_app/venue_data_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Usually, you need to initialize the HERE SDK only once during the lifetime of an application.
  await _initializeHERESDK();

  runApp(const MyApp());
}

Future<void> _initializeHERESDK() async {
  // Needs to be called before accessing SDKOptions to load necessary libraries.
  SdkContext.init(IsolateOrigin.main);

  // Set your credentials for the HERE SDK.
  String accessKeyId = "VENUE_ACCESS_KEY_ID";
  String accessKeySecret = "VENUE_ACCESS_KEY_SECRET";
  AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyId, accessKeySecret);
  SDKOptions sdkOptions = SDKOptions.withAuthenticationMode(authenticationMode);

  try {
    await SDKNativeEngine.makeSharedInstance(sdkOptions);
  } on InstantiationException {
    throw Exception("Failed to initialize the HERE SDK.");
  }
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final AppLifecycleListener _appLifecycleListener;
  @override
  void initState() {
    super.initState();
    _appLifecycleListener = AppLifecycleListener(
      onDetach: () =>
          // Sometimes Flutter may not reliably call dispose(),
          // therefore it is recommended to dispose the HERE SDK
          // also when the AppLifecycleListener is detached.
          // See more details: https://github.com/flutter/flutter/issues/40940
          {print('AppLifecycleListener detached.'), _disposeHERESDK()},
    );
  }

  @override
  void dispose() {
    _disposeHERESDK();
    super.dispose();
  }

  void _disposeHERESDK() async {
    // Free HERE SDK resources before the application shuts down.
    await SDKNativeEngine.sharedInstance?.dispose();
    SdkContext.release();
    _appLifecycleListener.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider<VenueDataProvider>(create: (_) => VenueDataProvider()),
        ChangeNotifierProvider<IndoorRoutingDataProvider>(create: (_) => IndoorRoutingDataProvider()),
      ],
      child: MaterialApp(title: 'HERE SDK for Flutter - Indoor Map', home: const IndoorMapScreen()),
    );
  }
}
