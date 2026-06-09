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

enum BottomSheetActiveList { venueList, geometryList }

/// Immutable data snapshot for the venue/geometry bottom sheet.
class IndoorBottomSheetData {
  const IndoorBottomSheetData({
    required this.titleList,
    required this.descriptionList,
    required this.rightAccessory,
    required this.leftAccessory,
    required this.activeList,
    required this.isSingleItem,
  });

  final List<String> titleList;
  final List<String?> descriptionList;
  final Widget? rightAccessory;
  final Widget? leftAccessory;
  final BottomSheetActiveList activeList;
  final bool isSingleItem;

  IndoorBottomSheetData copyWith({
    List<String>? titleList,
    List<String?>? descriptionList,
    Widget? rightAccessory,
    Widget? leftAccessory,
    BottomSheetActiveList? activeList,
    bool? isSingleItem,
  }) {
    return IndoorBottomSheetData(
      titleList: titleList ?? this.titleList,
      descriptionList: descriptionList ?? this.descriptionList,
      rightAccessory: rightAccessory ?? this.rightAccessory,
      leftAccessory: leftAccessory ?? this.leftAccessory,
      activeList: activeList ?? this.activeList,
      isSingleItem: isSingleItem ?? this.isSingleItem,
    );
  }
}
