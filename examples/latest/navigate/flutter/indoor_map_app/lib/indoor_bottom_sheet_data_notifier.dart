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
import 'package:indoor_map_app/indoor_bottom_sheet_data.dart';
import 'package:indoor_map_app/indoor_map_tokens.dart';

/// Global singleton [ValueNotifier] that drives the venue / geometry bottom
/// sheet. The bottom sheet listens to this notifier and updates its content
/// based on the emitted [IndoorBottomSheetData] snapshot.
class IndoorBottomSheetDataNotifier {
  IndoorBottomSheetDataNotifier._();

  static final ValueNotifier<IndoorBottomSheetData> notifier = ValueNotifier<IndoorBottomSheetData>(
    const IndoorBottomSheetData(
      titleList: <String>[],
      descriptionList: <String?>[],
      rightAccessory: const ImageIcon(AssetImage('assets/right_arrow.png')),
      leftAccessory: const ImageIcon(AssetImage('assets/building.png')),
      activeList: BottomSheetActiveList.venueList,
      isSingleItem: false,
    ),
  );

  static void showVenueList({required List<String> venueNameList, required List<String?> venueIdList}) {
    notifier.value = IndoorBottomSheetData(
      titleList: venueNameList,
      descriptionList: venueIdList,
      rightAccessory: const ImageIcon(AssetImage('assets/right_arrow.png')),
      leftAccessory: const ImageIcon(AssetImage('assets/building.png')),
      activeList: BottomSheetActiveList.venueList,
      isSingleItem: false,
    );
  }

  static void showGeometryList({
    required List<String> geometryList,
    required List<String?> internalAddressList,
    bool isSingleGeometryItem = false,
  }) {
    notifier.value = IndoorBottomSheetData(
      titleList: geometryList,
      descriptionList: internalAddressList,
      rightAccessory: const ImageIcon(AssetImage('assets/north_west_arrow.png'), size: IndoorMapTokens.size20),
      leftAccessory: Image.asset(
        'assets/space_icon.png',
        width: IndoorMapTokens.size32,
        height: IndoorMapTokens.size32,
      ),
      activeList: BottomSheetActiveList.geometryList,
      isSingleItem: isSingleGeometryItem,
    );
  }

  static void clear() {
    notifier.value = const IndoorBottomSheetData(
      titleList: <String>[],
      descriptionList: <String?>[],
      rightAccessory: const ImageIcon(AssetImage('assets/right_arrow.png')),
      leftAccessory: const ImageIcon(AssetImage('assets/building.png')),
      activeList: BottomSheetActiveList.venueList,
      isSingleItem: false,
    );
  }
}
