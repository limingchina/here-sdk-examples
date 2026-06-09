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
import 'package:indoor_map_app/indoor_map_tokens.dart';

/// A circular icon button that replaces the HdsButton circle variant
/// (e.g. the drawing-switcher FAB).
///
/// Renders an [ElevatedButton] with [CircleBorder], a white background, and
/// the supplied [icon].  The button size defaults to [IndoorMapTokens.size40].
class AppCircleIconButton extends StatelessWidget {
  const AppCircleIconButton({
    super.key,
    required this.icon,
    required this.onPressed,
    this.size = IndoorMapTokens.size40,
    this.backgroundColor,
    this.iconColor,
    this.elevation = IndoorMapTokens.elevation4,
  });

  final Widget icon;
  final VoidCallback? onPressed;
  final double size;
  final Color? backgroundColor;
  final Color? iconColor;
  final double elevation;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size,
      height: size,
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: backgroundColor ?? IndoorMapTokens.surfaceColor,
          foregroundColor: iconColor ?? Colors.black87,
          padding: EdgeInsets.zero,
          shape: const CircleBorder(),
          elevation: elevation,
          minimumSize: Size(size, size),
        ),
        child: icon,
      ),
    );
  }
}
