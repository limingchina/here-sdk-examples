/*
 * Copyright (C) 2019-2026 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import 'package:here_sdk/core.dart';
import 'package:here_sdk/mapdata.dart';
import 'package:here_sdk/warner.dart';

// This example shows how to add custom warning types such as for speed bumps.
// The provider scans SegmentData for speed bump positions and reports them via startOffsetInMeters.
// The WarnerEngine handles distance computation and AHEAD/PASSED delivery based on vehicle position.
class SpeedBumpWarningProvider {
  // Custom warning type ID for speed bumps.
  // Avoid using 0: choose a distinct custom warning type ID to not conflict with SDK internals.
  static const int speedBumpWarningId = 1000;

  static const String _payloadSegmentReference = "segmentReference";

  int getCustomWarningType() => speedBumpWarningId;

  // Called by the WarnerEngine for EVERY segment the vehicle passes through.
  // The WarnerEngine uses startOffsetInMeters to compute distance and determine AHEAD/PASSED.
  List<CustomWarning> getWarnings(SegmentData currentSegment, SegmentData? previousSegment) {
    List<CustomWarning> result = [];

    for (SegmentSpanData span in currentSegment.spans) {
      List<SegmentSpecialSpeedSituation>? specialSituations = span.specialSpeedSituations;
      if (specialSituations == null) continue;

      for (SegmentSpecialSpeedSituation situation in specialSituations) {
        if (situation.specialSpeedType == SpecialSpeedType.speedBumpsPresent) {
          // The position of the speed bump comes directly from SegmentData.
          int startOffset = span.startOffsetInMeters;

          Metadata payload = Metadata();
          payload.setString(_payloadSegmentReference, currentSegment.segmentReference.toString());

          CustomWarning customWarning = CustomWarning();
          customWarning.id = span.hashCode;
          customWarning.customWarningType = speedBumpWarningId;
          // startOffsetInMeters tells the WarnerEngine where in the segment the bump is.
          customWarning.startOffsetInMeters = startOffset.toDouble();
          customWarning.endOffsetInMeters = null;
          customWarning.payload = payload;
          result.add(customWarning);
        }
      }
    }
    return result;
  }

  // Helper for decoding payload in the warning handler.
  String? getSegmentReference(CustomWarning customWarning) {
    return customWarning.payload?.getString(_payloadSegmentReference);
  }
}
