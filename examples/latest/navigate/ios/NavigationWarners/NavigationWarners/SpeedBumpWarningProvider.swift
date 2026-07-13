/*
 * Copyright (C) 2019-2026 HERE Europe B.V.
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

import heresdk
import Foundation

// This example shows how to add custom warning types such as for speed bumps.
// The provider scans SegmentData for speed bump positions and reports them via startOffsetInMeters.
// The WarnerEngine handles distance computation and AHEAD/PASSED delivery based on vehicle position.
class SpeedBumpWarningProvider: CustomWarningProvider {

    // Custom warning type ID for speed bumps.
    // Avoid using 0: choose a distinct custom warning type ID to not conflict with SDK internals.
    static let speedBumpWarningID: Int32 = 1000

    private let payloadSegmentReference = "segmentReference"

    func getCustomWarningType() -> Int32 {
        return SpeedBumpWarningProvider.speedBumpWarningID
    }

    // Called by the WarnerEngine for EVERY segment the vehicle passes through.
    // The WarnerEngine uses startOffsetInMeters to compute distance and determine AHEAD/PASSED.
    func getWarnings(currentSegment: SegmentData, previousSegment: SegmentData?) -> [CustomWarning] {
        var result: [CustomWarning] = []

        for span in currentSegment.spans {
            guard let specialSituations = span.specialSpeedSituations else { continue }

            for situation in specialSituations {
                if situation.specialSpeedType == .speedBumpsPresent {
                    // The position of the speed bump comes directly from SegmentData.
                    let startOffset = span.startOffsetInMeters

                    let payload = Metadata()
                    payload.setString(key: payloadSegmentReference,
                                      value: "\(currentSegment.segmentReference)")

                    let customWarning = CustomWarning(
                        customWarningType: SpeedBumpWarningProvider.speedBumpWarningID,
                        // startOffsetInMeters tells the WarnerEngine where in the segment the bump is.
                        startOffsetInMeters: Double(startOffset),
                        endOffsetInMeters: nil,
                        payload: payload
                    )
                    result.append(customWarning)
                }
            }
        }
        return result
    }

    // Helper for decoding payload in the warning handler.
    func getSegmentReference(_ customWarning: CustomWarning) -> String? {
        return customWarning.payload?.getString(key: payloadSegmentReference)
    }
}
