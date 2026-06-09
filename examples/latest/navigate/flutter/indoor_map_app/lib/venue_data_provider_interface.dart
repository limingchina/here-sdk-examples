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

import 'package:indoor_map_app/indoor_topology_info.dart';
import 'package:indoor_map_app/venue_error_data.dart';
import 'package:here_sdk/venue.control.dart';
import 'package:here_sdk/venue.data.dart';
import 'package:here_sdk/venue.service.dart';
import 'package:here_sdk/venue.style.dart';

abstract class VenueDataProviderInterface {
  void onGetVenueCompleted(String venueIdentifier, VenueModel? venueModel, bool online, VenueStyle? venueStyle);
  void onSelectedVenueChanged(Venue? deselectedVenue, Venue? selectedVenue);
  void onVenueServiceInitializationFailure(VenueServiceInitStatus result);
  void onVenueInfoListLoadWithError(VenueErrorCode? venueLoadError, String? errorMsg);
  void onVenueInfoListLoadSuccess();
  void showTappedGeometryInfo(VenueGeometry geometry);
  void onDeselectGeometryFromTapController();
  void onLevelChangeAfterGeometrySelection(int newLevelIndex);
  void onDrawingChangeAfterGeometrySelection(int newDrawingIndex);
  void showTappedTopologyInfo(IndoorTopologyInfo info);
  void onDeselectTopologyFromTapController();
  void showTopologyButtonOnMap(bool val);
  void setVenueErrorData(VenueErrorData? errorData);
}
