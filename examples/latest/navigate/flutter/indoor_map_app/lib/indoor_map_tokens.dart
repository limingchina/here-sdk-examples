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

/// Centralised design tokens for the Indoor Maps module.
/// All spacing, sizing and colour constants live here.
class IndoorMapTokens {
  IndoorMapTokens._();

  // ---------------------------------------------------------------------------
  // Spacing / sizing (mirrors HdsConstants and IndoorDefault values)
  // ---------------------------------------------------------------------------
  static const double size1 = 1.0;
  static const double size2 = 2.0;
  static const double size4 = 4.0;
  static const double size5 = 5.0;
  static const double size6 = 6.0;
  static const double size8 = 8.0;
  static const double size10 = 10.0;
  static const double size12 = 12.0;
  static const double size13 = 13.0;
  static const double size14 = 14.0;
  static const double size15 = 15.0;
  static const double size16 = 16.0;
  static const double size18 = 18.0;
  static const double size20 = 20.0;
  static const double size24 = 24.0;
  static const double size25 = 25.0;
  static const double size28 = 28.0;
  static const double size32 = 32.0;
  static const double size40 = 40.0;
  static const double size48 = 48.0;
  static const double size50 = 50.0;
  static const double size60 = 60.0;
  static const double size80 = 80.0;

  // Padding aliases (mirrors IndoorDefault.paddingX)
  static const double padding0 = 0.0;
  static const double padding10 = 10.0;
  static const double padding20 = 20.0;

  // Elevation
  static const double elevation4 = 4.0;

  // ---------------------------------------------------------------------------
  // Drag handle dimensions
  // ---------------------------------------------------------------------------
  static const double dragHandleWidth = 50.0;
  static const double dragHandleHeight = 5.0;

  // ---------------------------------------------------------------------------
  // Level switcher constants
  // ---------------------------------------------------------------------------
  static const double levelRowHeight = 40.0;
  static const int levelSwitcherMaxVisibleRows = 3;

  // ---------------------------------------------------------------------------
  // Drawing switcher constants
  // ---------------------------------------------------------------------------
  static const double drawingRowHeight = 60.0;
  static const double drawingListWidth = 200.0;
  static const double drawingListWidthGap = 20.0;
  static const int drawingSwitcherMaxVisibleRows = 3;

  // ---------------------------------------------------------------------------
  // Border radii
  // ---------------------------------------------------------------------------
  static const double radiusSmall = 5.0;
  static const double radiusMedium = 12.0;
  static const double radiusLarge = 20.0;
  static const double radiusCircle = 32.0;

  // ---------------------------------------------------------------------------
  // Colours (Flutter-provided only — no HDS theme tokens)
  // ---------------------------------------------------------------------------

  /// Used for selected level / drawing text and the transport mode indicator.
  static const Color selectedColor = Colors.blue;

  /// Background tint on the selected level row.
  static Color get selectedBgColor => Colors.grey.shade200;

  /// Text colour for unselected level / drawing items.
  static Color get unselectedTextColor => Colors.grey.shade700;

  /// Secondary / hint text colour.
  static Color get secondaryTextColor => Colors.grey.shade500;

  /// Card / sheet surface colour.
  static const Color surfaceColor = Colors.white;

  /// Topology toggle icon colour when topology is active.
  static Color get topologyActiveColor => Colors.blue.shade700;

  /// Colour of the animated indicator bar below active transport mode tab.
  static const Color transportModeActiveIndicator = Colors.blue;

  /// Colour of the indicator bar below inactive transport mode tab.
  static const Color transportModeInactiveIndicator = Colors.transparent;
}
