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

import 'package:flutter/foundation.dart';

/// Global [ValueNotifier] instances that track the full and filtered venue
/// lists returned by the HERE SDK.

class VenueIdListEventHandler with ChangeNotifier {
  ValueNotifier<List<String>> updatedIdList = ValueNotifier<List<String>>(<String>[]);

  @override
  void dispose() {
    updatedIdList.value.clear();
    updatedIdList.dispose();
    super.dispose();
  }
}

class VenueNameListEventHandler with ChangeNotifier {
  ValueNotifier<List<String>> updatedNameList = ValueNotifier<List<String>>(<String>[]);

  @override
  void dispose() {
    updatedNameList.value.clear();
    updatedNameList.dispose();
    super.dispose();
  }
}

// Actual venue list notifiers (populated when venue info list is loaded)
late VenueIdListEventHandler venueIdList;
late VenueNameListEventHandler venueNameList;

// Filtered venue list notifiers (used by search)
late VenueIdListEventHandler filteredVenueIdList;
late VenueNameListEventHandler filteredVenueNameList;
