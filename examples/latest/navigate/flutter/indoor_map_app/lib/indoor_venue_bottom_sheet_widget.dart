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
import 'package:here_sdk/venue.data.dart';
import 'package:provider/provider.dart';
import 'package:indoor_map_app/indoor_bottom_sheet_data.dart';
import 'package:indoor_map_app/indoor_bottom_sheet_data_notifier.dart';
import 'package:indoor_map_app/indoor_events.dart';
import 'package:indoor_map_app/indoor_map_tokens.dart';
import 'package:indoor_map_app/venue_data_provider.dart';
import 'package:indoor_map_app/venue_tap_controller.dart';
import 'package:indoor_map_app/widgets/app_list_tile_detailed.dart';
import 'package:indoor_map_app/widgets/app_search_field.dart';

/// Draggable bottom sheet showing the venue list or the geometry list inside a
/// selected venue. Uses [AppSearchField], [AppListTileDetailed], and [IndoorMapTokens].
/// The sheet can be dragged up to expand or down to collapse, and the search field automatically
/// expands the sheet when focused. The parent screen listens to the sheet's state and calls
/// [sheetCollapseCleanup] when handling back press to ensure the sheet is collapsed and search
/// state is cleared before navigating back.
class IndoorVenueBottomSheetWidget extends StatefulWidget {
  const IndoorVenueBottomSheetWidget({
    super.key,
    required this.dragController,
    required this.minBottomSheetSize,
    required this.maxBottomSheetSize,
    required this.tapController,
  });

  final DraggableScrollableController dragController;
  final double minBottomSheetSize;
  final double maxBottomSheetSize;
  final VenueTapController tapController;

  @override
  State<IndoorVenueBottomSheetWidget> createState() => IndoorVenueBottomSheetWidgetState();
}

class IndoorVenueBottomSheetWidgetState extends State<IndoorVenueBottomSheetWidget> {
  late final double _minSize = widget.minBottomSheetSize;
  late final double _maxSize = widget.maxBottomSheetSize;
  late double _sheetSize = widget.minBottomSheetSize;
  late double _desiredSheetSize = widget.minBottomSheetSize;
  static const double _geometrySelectedSheetSize = 0.22;

  bool _isSnapping = false;
  bool _isAnimatingSheet = false;
  double _listScrollOffset = 0;

  late final FocusNode _searchFocusNode;
  late final TextEditingController _searchController;
  late VenueDataProvider _venueDataProvider;
  late final VoidCallback _dragControllerCallback;

  DraggableScrollableController get _dragController => widget.dragController;
  VenueTapController get _tapController => widget.tapController;

  @override
  void initState() {
    super.initState();
    _searchFocusNode = FocusNode();
    _searchController = TextEditingController();

    _dragControllerCallback = () {
      if (!_dragController.isAttached) return;
      if (mounted) {
        setState(() => _sheetSize = _dragController.size);
      }
    };
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _dragController.addListener(_dragControllerCallback);
    });
    _searchFocusNode.addListener(_onSearchFocusChanged);
    _searchController.addListener(_handleSearch);

    _venueDataProvider = context.read<VenueDataProvider>();
    IndoorBottomSheetDataNotifier.notifier.addListener(_onSheetDataChange);
  }

  void _onSheetDataChange() {
    final IndoorBottomSheetData data = IndoorBottomSheetDataNotifier.notifier.value;
    if (data.activeList == BottomSheetActiveList.venueList) return;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      if (_searchFocusNode.hasFocus || _searchController.text.isNotEmpty) {
        return;
      }
      if (data.isSingleItem) {
        _safeAnimateToDesired(_geometrySelectedSheetSize);
      } else {
        _safeAnimateToDesired(_minSize);
      }
    });
  }

  @override
  void dispose() {
    _searchFocusNode
      ..removeListener(_onSearchFocusChanged)
      ..dispose();
    _searchController
      ..removeListener(_handleSearch)
      ..dispose();
    if (_dragController.isAttached) {
      _dragController.removeListener(_dragControllerCallback);
    }
    IndoorBottomSheetDataNotifier.notifier.removeListener(_onSheetDataChange);
    super.dispose();
  }

  void _onSearchFocusChanged() {
    _safeAnimateTo(_maxSize);
  }

  void _handleSearch() {
    final String query = _searchController.text.toLowerCase();
    final BottomSheetActiveList activeList = IndoorBottomSheetDataNotifier.notifier.value.activeList;

    if (activeList == BottomSheetActiveList.venueList) {
      if (query.isEmpty) {
        filteredVenueIdList.updatedIdList.value = venueIdList.updatedIdList.value;
        filteredVenueNameList.updatedNameList.value = venueNameList.updatedNameList.value;
        IndoorBottomSheetDataNotifier.showVenueList(
          venueNameList: filteredVenueNameList.updatedNameList.value,
          venueIdList: filteredVenueIdList.updatedIdList.value,
        );
        return;
      }
      final List<int> indexList = <int>[];
      for (int i = 0; i < venueNameList.updatedNameList.value.length; i++) {
        final String name = venueNameList.updatedNameList.value[i];
        if (name.toLowerCase().contains(query)) {
          indexList.add(i);
        }
      }
      filteredVenueIdList.updatedIdList.value = indexList.map((int i) => venueIdList.updatedIdList.value[i]).toList();
      filteredVenueNameList.updatedNameList.value = indexList
          .map((int i) => venueNameList.updatedNameList.value[i])
          .toList();
      IndoorBottomSheetDataNotifier.showVenueList(
        venueNameList: filteredVenueNameList.updatedNameList.value,
        venueIdList: filteredVenueIdList.updatedIdList.value,
      );
    } else {
      if (query.isEmpty) {
        _venueDataProvider.updateGeometryListInfo();
      } else {
        _venueDataProvider.filterGeometryListInfo(query);
      }
    }
  }

  Future<void> _safeAnimateTo(double target) async {
    if (!mounted || !_dragController.isAttached) return;
    if (_isAnimatingSheet) return;
    _isAnimatingSheet = true;
    if (target <= _minSize) _collapseCleanup();
    await _dragController.animateTo(target, duration: const Duration(milliseconds: 250), curve: Curves.easeOut);
    _isAnimatingSheet = false;
  }

  Future<void> _safeAnimateToDesired(double newSize) async {
    if (!mounted || !_dragController.isAttached) return;
    _desiredSheetSize = newSize.clamp(_minSize, 0.90);
    if (_isAnimatingSheet) return;
    _isAnimatingSheet = true;
    try {
      while (mounted && _dragController.isAttached && (_dragController.size - _desiredSheetSize).abs() > 0.01) {
        await _dragController.animateTo(
          _desiredSheetSize,
          duration: const Duration(milliseconds: 250),
          curve: Curves.easeOutCubic,
        );
      }
    } catch (_) {
      // ignore animation race
    } finally {
      _isAnimatingSheet = false;
    }
  }

  void _collapseCleanup() {
    _searchFocusNode.unfocus();
    _searchController.clear();
    filteredVenueIdList.updatedIdList.value = venueIdList.updatedIdList.value;
    filteredVenueNameList.updatedNameList.value = venueNameList.updatedNameList.value;
  }

  /// Public helper used by the parent screen while handling back press.
  void sheetCollapseCleanup() {
    _collapseCleanup();
  }

  Future<void> _handleVenueLoad(String venueId) async {
    await _safeAnimateTo(_minSize);
    _venueDataProvider.loadVenueEvent(venueId);
  }

  void _handleGeometryTap(VenueGeometry geometry) {
    _collapseCleanup();
    debugPrint(
      'Geometry tapped: ${geometry.identifier}, level: ${geometry.level.shortName}, drawing: ${geometry.level.drawing.identifier}',
    );
    _tapController
      ..drawingChangeBasedOnGeometrySelection(geometry)
      ..levelChangeBasedOnGeometrySelection(geometry)
      ..selectGeometry(geometry, geometry.center, true);
  }

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      controller: _dragController,
      initialChildSize: _minSize,
      minChildSize: _minSize,
      maxChildSize: _maxSize,
      builder: (BuildContext context, ScrollController scrollController) {
        return SafeArea(
          top: false,
          child: Container(
            decoration: BoxDecoration(
              color: IndoorMapTokens.surfaceColor,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(IndoorMapTokens.radiusMedium)),
            ),
            child: Column(
              children: <Widget>[
                // Header: drag handle + search bar
                GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onVerticalDragUpdate: (DragUpdateDetails d) {
                    final double delta = d.primaryDelta ?? 0.0;
                    final double screenH = MediaQuery.of(context).size.height;
                    double newSize = _dragController.size - (delta / screenH);
                    newSize = newSize.clamp(_minSize, _maxSize);
                    _dragController.jumpTo(newSize);
                  },
                  onVerticalDragEnd: (DragEndDetails d) async {
                    if (_isAnimatingSheet) return;
                    final double velocity = d.primaryVelocity ?? 0;
                    final double size = _dragController.size;
                    final double mid = (_minSize + _maxSize) / 2;
                    if (velocity < -500) {
                      await _safeAnimateTo(_maxSize);
                    } else if (velocity > 500) {
                      await _safeAnimateTo(_minSize);
                    } else if (size < mid) {
                      await _safeAnimateTo(_minSize);
                    } else {
                      await _safeAnimateTo(_maxSize);
                    }
                  },
                  child: Column(
                    children: <Widget>[
                      Container(
                        margin: const EdgeInsets.fromLTRB(0, IndoorMapTokens.size10, 0, 0),
                        child: Container(
                          width: IndoorMapTokens.dragHandleWidth,
                          height: IndoorMapTokens.dragHandleHeight,
                          decoration: BoxDecoration(
                            color: Colors.grey.shade400,
                            borderRadius: BorderRadius.circular(IndoorMapTokens.radiusMedium),
                          ),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(IndoorMapTokens.size10),
                        child: ValueListenableBuilder<IndoorBottomSheetData>(
                          valueListenable: IndoorBottomSheetDataNotifier.notifier,
                          builder: (_, IndoorBottomSheetData data, __) {
                            return AppSearchField(
                              controller: _searchController,
                              focusNode: _searchFocusNode,
                              hintText: data.activeList == BottomSheetActiveList.venueList
                                  ? 'Search for venues'
                                  : 'Search for spaces',
                              onCancel: () => _safeAnimateTo(_minSize),
                            );
                          },
                        ),
                      ),
                    ],
                  ),
                ),
                // Body: loading indicator or list
                Expanded(
                  child: ValueListenableBuilder<IndoorBottomSheetData>(
                    valueListenable: IndoorBottomSheetDataNotifier.notifier,
                    builder: (_, IndoorBottomSheetData list, __) {
                      if (venueIdList.updatedIdList.value.isEmpty) {
                        final bool showLoader = _sheetSize > (_minSize + 0.1);
                        if (!showLoader) {
                          return SingleChildScrollView(controller: scrollController, child: const SizedBox(height: 1));
                        }
                        return LayoutBuilder(
                          builder: (_, BoxConstraints constraints) {
                            return SingleChildScrollView(
                              controller: scrollController,
                              child: SizedBox(
                                height: constraints.maxHeight,
                                child: const Center(child: CircularProgressIndicator(color: Colors.blueAccent)),
                              ),
                            );
                          },
                        );
                      }

                      return NotificationListener<ScrollNotification>(
                        onNotification: (ScrollNotification n) {
                          if (_isAnimatingSheet || _isSnapping) return false;
                          if (n is ScrollUpdateNotification) {
                            _listScrollOffset = n.metrics.pixels;
                            final bool atTop = _listScrollOffset <= 0.5;
                            if (atTop && (n.scrollDelta ?? 0) > 0 && _dragController.size > _minSize) {
                              final double delta = n.scrollDelta!;
                              final double screenH = MediaQuery.of(context).size.height;
                              double newSize = _dragController.size - (delta / screenH);
                              newSize = newSize.clamp(_minSize, _maxSize);
                              _dragController.jumpTo(newSize);
                              return true;
                            }
                          }
                          if (n is ScrollEndNotification && !_isSnapping && !_isAnimatingSheet) {
                            _isSnapping = true;
                            final double size = _dragController.size;
                            final double mid = (_minSize + _maxSize) / 2;
                            final double target = (size < mid) ? _minSize : _maxSize;
                            if ((size - target).abs() > 0.02) {
                              _safeAnimateTo(target).then((_) => _isSnapping = false);
                            } else {
                              _isSnapping = false;
                            }
                          }
                          return false;
                        },
                        child: Scrollbar(
                          controller: scrollController,
                          thumbVisibility: true,
                          interactive: true,
                          child: ListView.builder(
                            controller: scrollController,
                            itemCount: list.titleList.length,
                            itemBuilder: (BuildContext context, int index) {
                              return AppListTileDetailed(
                                title: list.titleList[index],
                                subtitle: list.activeList == BottomSheetActiveList.geometryList
                                    ? list.descriptionList[index] ?? ''
                                    : null,
                                leadingAccessory: list.leftAccessory,
                                trailingAccessory: list.rightAccessory,
                                onTap: () => list.activeList == BottomSheetActiveList.venueList
                                    ? _handleVenueLoad(list.descriptionList[index] ?? '')
                                    : _handleGeometryTap(_venueDataProvider.venueGeometryList[index]),
                              );
                            },
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
