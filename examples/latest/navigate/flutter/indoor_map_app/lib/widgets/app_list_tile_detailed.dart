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

/// A list-tile replacement for HdsListTileDetailed.
///
/// Displays an optional leading accessory widget, a title, an optional
/// subtitle, and an optional trailing accessory widget.
class AppListTileDetailed extends StatelessWidget {
  const AppListTileDetailed({
    super.key,
    this.leadingAccessory,
    required this.title,
    this.subtitle,
    this.trailingAccessory,
    this.onTap,
  });

  final Widget? leadingAccessory;
  final String title;
  final String? subtitle;
  final Widget? trailingAccessory;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: IndoorMapTokens.size16, vertical: IndoorMapTokens.size12),
            child: Row(
              children: [
                if (leadingAccessory != null) ...[leadingAccessory!, const SizedBox(width: IndoorMapTokens.size12)],
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(fontSize: IndoorMapTokens.size14, fontWeight: FontWeight.w500),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                      if (subtitle != null)
                        Text(
                          subtitle!,
                          style: TextStyle(fontSize: IndoorMapTokens.size12, color: IndoorMapTokens.secondaryTextColor),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                    ],
                  ),
                ),
                if (trailingAccessory != null) ...[const SizedBox(width: IndoorMapTokens.size8), trailingAccessory!],
              ],
            ),
          ),
          const Divider(height: IndoorMapTokens.size1, thickness: IndoorMapTokens.size1),
        ],
      ),
    );
  }
}
