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
import 'package:indoor_map_app/indoor_topology_info.dart';
import 'package:here_sdk/venue.data.dart';
import 'package:here_sdk/venue.routing.dart';

/// A bottom-anchored info panel that shows the transport-mode accessibility of
/// the currently selected topology segment.
/// Used as the content of the bottom sheet displayed when tapping on a topology.
class IndoorTopologyInfoWidget extends StatefulWidget {
  const IndoorTopologyInfoWidget({super.key, required this.topologyInfo});

  final IndoorTopologyInfo topologyInfo;

  @override
  State<IndoorTopologyInfoWidget> createState() => _IndoorTopologyInfoWidgetState();
}

class _IndoorTopologyInfoWidgetState extends State<IndoorTopologyInfoWidget> {
  static const double _sheetHeight = 190.0;
  static const int _scrollbarThreshold = 3;

  late final ScrollController _scrollController;

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  String _getIconNameBasedOnTransportMode(VenueTransportMode mode) {
    if (mode == VenueTransportMode.pedestrian) return 'assets/img_pedestrian.png';
    if (mode == VenueTransportMode.car) return 'assets/img_car.png';
    if (mode == VenueTransportMode.taxi) return 'assets/img_taxi.png';
    if (mode == VenueTransportMode.scooter) return 'assets/img_bike.png';
    return 'assets/img_pedestrian.png';
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: _sheetHeight,
      padding: const EdgeInsets.all(IndoorMapTokens.size16),
      decoration: BoxDecoration(
        color: IndoorMapTokens.surfaceColor,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(IndoorMapTokens.radiusMedium)),
        boxShadow: const <BoxShadow>[BoxShadow(color: Colors.black26, blurRadius: IndoorMapTokens.size10)],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            widget.topologyInfo.topologyId,
            style: const TextStyle(fontSize: IndoorMapTokens.size16, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: IndoorMapTokens.size10),
          Expanded(
            child: Scrollbar(
              controller: _scrollController,
              thumbVisibility: widget.topologyInfo.modes.length > _scrollbarThreshold,
              interactive: true,
              child: ListView.separated(
                controller: _scrollController,
                itemCount: widget.topologyInfo.modes.length,
                separatorBuilder: (_, __) => const SizedBox(height: IndoorMapTokens.size10),
                itemBuilder: (BuildContext context, int index) {
                  final VenueTopologyTopologyDirectionality direction = widget.topologyInfo.directions[index];
                  final VenueTransportMode mode = widget.topologyInfo.modes[index];
                  return Row(
                    children: <Widget>[
                      ImageIcon(AssetImage(_getIconNameBasedOnTransportMode(mode)), size: IndoorMapTokens.size24),
                      const SizedBox(width: IndoorMapTokens.size80),
                      Text(direction.name.toUpperCase()),
                    ],
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
