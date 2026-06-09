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

import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:indoor_map_app/indoor_map_tokens.dart';
import 'package:indoor_map_app/venue_data_provider.dart';
import 'package:here_sdk/venue.control.dart';
import 'package:here_sdk/venue.data.dart';

/// Vertical level switcher overlay for the indoor map.
/// Displays the list of levels in the currently selected venue and allows changing the selected level.
class IndoorLevelSwitcher extends StatefulWidget {
  const IndoorLevelSwitcher({super.key});

  @override
  State<IndoorLevelSwitcher> createState() => _IndoorLevelSwitcherState();
}

class _IndoorLevelSwitcherState extends State<IndoorLevelSwitcher> {
  static const double _rowHeight = IndoorMapTokens.levelRowHeight;
  static const int _maxVisibleRows = IndoorMapTokens.levelSwitcherMaxVisibleRows;

  late final ScrollController _controller;
  late VenueDataProvider _dataProvider;
  late VenueLevelSelectionListener _levelChangeListener;
  VenueMap? _venueMap;
  Venue? _selectedVenue;
  int _currDrawingIndex = -1;

  @override
  void initState() {
    super.initState();
    _dataProvider = context.read<VenueDataProvider>();
    _controller = ScrollController();
    _venueMap = _dataProvider.venueEngine?.venueMap;
    _selectedVenue = _venueMap?.selectedVenue;
    if (_selectedVenue == null) {
      debugPrint('IndoorLevelSwitcher: selected venue is null');
      return;
    }
    _levelChangeListener = VenueLevelSelectionListener((
      Venue venue,
      VenueDrawing drawing,
      VenueLevel? oldLevel,
      VenueLevel newLevel,
    ) {
      _smoothScrollToSelectedLevelIndex(_dataProvider.currSelectedLevelIndx);
    });
    _venueMap?.addLevelSelectionListener(_levelChangeListener);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _smoothScrollToSelectedLevelIndex(_dataProvider.currSelectedLevelIndx);
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    _venueMap?.removeLevelSelectionListener(_levelChangeListener);
    _selectedVenue = null;
    _venueMap = null;
    _dataProvider.currSelectedLevelIndx = -1;
    _currDrawingIndex = -1;
    super.dispose();
  }

  void _levelUpAction() {
    final int? current = _venueMap?.selectedVenue?.selectedLevelIndex;
    if (current == null) return;
    final int max = _dataProvider.levelList?.length ?? 0;
    if (current + 1 >= max) return;
    _venueMap!.selectedVenue!.selectedLevelIndex = current + 1;
    _dataProvider.setCurrentSelectedLevelIndex(
      _dataProvider.maxLevelIndex - _venueMap!.selectedVenue!.selectedLevelIndex,
    );
  }

  void _levelDownAction() {
    final int? current = _venueMap?.selectedVenue?.selectedLevelIndex;
    if (current == null || current - 1 < 0) return;
    _venueMap!.selectedVenue!.selectedLevelIndex = current - 1;
    _dataProvider.setCurrentSelectedLevelIndex(
      _dataProvider.maxLevelIndex - _venueMap!.selectedVenue!.selectedLevelIndex,
    );
  }

  void _tapLevelChangeAction(int newLevelIndex) {
    if (_venueMap?.selectedVenue?.selectedLevelIndex == null) return;
    _venueMap?.selectedVenue!.selectedLevelIndex = _dataProvider.maxLevelIndex - newLevelIndex;
    _dataProvider.setCurrentSelectedLevelIndex(newLevelIndex);
  }

  void _smoothScrollToSelectedLevelIndex(int currIndex) {
    if (!_controller.hasClients) return;
    final int itemCount = _dataProvider.levelList?.length ?? 0;
    if (itemCount == 0) return;
    final int visibleRows = math.min(itemCount, _maxVisibleRows);
    final double offset = _controller.offset;
    final int firstVisible = (offset / _rowHeight).floor();
    final int lastVisible = firstVisible + visibleRows - 1;
    if (currIndex < firstVisible) {
      _controller.animateTo(currIndex * _rowHeight, duration: const Duration(milliseconds: 200), curve: Curves.easeOut);
    } else if (currIndex > lastVisible) {
      _controller.animateTo(
        (currIndex - visibleRows + 1) * _rowHeight,
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_venueMap == null || _selectedVenue == null || _dataProvider.levelList == null) {
      return const SizedBox.shrink();
    }
    final int currSelectedIndx = context.select<VenueDataProvider, int>(
      (VenueDataProvider p) => p.currSelectedLevelIndx,
    );
    final int providerDrawingIndx = context.select<VenueDataProvider, int>(
      (VenueDataProvider p) => p.currSelectedDrawingIndx,
    );
    if (providerDrawingIndx != _currDrawingIndex) {
      _smoothScrollToSelectedLevelIndex(_dataProvider.currSelectedLevelIndx);
      _currDrawingIndex = providerDrawingIndx;
    }

    final int visibleRows = math.min(_dataProvider.levelList?.length ?? 1, _maxVisibleRows);

    return Material(
      elevation: IndoorMapTokens.elevation4,
      borderRadius: BorderRadius.circular(IndoorMapTokens.radiusCircle),
      child: SizedBox(
        width: IndoorMapTokens.size40,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            IconButton(
              onPressed: _levelUpAction,
              iconSize: IndoorMapTokens.size25,
              style: IconButton.styleFrom(tapTargetSize: MaterialTapTargetSize.shrinkWrap),
              constraints: const BoxConstraints(minHeight: IndoorMapTokens.size40, minWidth: IndoorMapTokens.size40),
              padding: EdgeInsets.zero,
              splashRadius: IndoorMapTokens.size20,
              icon: const Icon(Icons.keyboard_arrow_up),
            ),
            const Divider(height: IndoorMapTokens.size1, thickness: IndoorMapTokens.size1),
            SizedBox(
              height: _rowHeight * visibleRows,
              child: ListView.builder(
                itemCount: _dataProvider.levelList?.length ?? 0,
                itemExtent: _rowHeight,
                physics: (_dataProvider.levelList?.length ?? 0) > _maxVisibleRows
                    ? const BouncingScrollPhysics()
                    : const NeverScrollableScrollPhysics(),
                controller: _controller,
                itemBuilder: (BuildContext context, int index) {
                  final bool selected = index == currSelectedIndx;
                  return GestureDetector(
                    onTap: () => _tapLevelChangeAction(index),
                    child: Container(
                      alignment: Alignment.center,
                      color: selected ? IndoorMapTokens.selectedBgColor : Colors.transparent,
                      child: Text(
                        _dataProvider.levelList?[index].shortName ?? '',
                        style: TextStyle(
                          fontWeight: selected ? FontWeight.bold : FontWeight.normal,
                          fontSize: IndoorMapTokens.size13,
                          color: selected ? IndoorMapTokens.selectedColor : IndoorMapTokens.unselectedTextColor,
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),
            const Divider(height: IndoorMapTokens.size1, thickness: IndoorMapTokens.size1),
            IconButton(
              onPressed: _levelDownAction,
              iconSize: IndoorMapTokens.size25,
              style: IconButton.styleFrom(tapTargetSize: MaterialTapTargetSize.shrinkWrap),
              constraints: const BoxConstraints(minHeight: 40.0, minWidth: 40.0),
              padding: EdgeInsets.zero,
              splashRadius: IndoorMapTokens.size20,
              icon: const Icon(Icons.keyboard_arrow_down),
            ),
          ],
        ),
      ),
    );
  }
}
