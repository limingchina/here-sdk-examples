package com.here.navigationwarnerskotlin

import android.util.Log
import com.here.sdk.core.Metadata
import com.here.sdk.mapdata.SegmentData
import com.here.sdk.mapdata.SpecialSpeedType
import com.here.sdk.warner.CustomWarning
import com.here.sdk.warner.CustomWarningProvider


/**
 * Provides custom speed bump warnings for WarnerEngine.
 * The provider scans SegmentData for speed bump positions and reports them via startOffsetInMeters.
 * The WarnerEngine handles distance computation and AHEAD/PASSED delivery based on vehicle position.
 */
class SpeedBumpWarningProvider : CustomWarningProvider {

    override fun getCustomWarningType(): Int {
        return SPEED_BUMP_WARNING_ID
    }

    // Called by the WarnerEngine for EVERY segment the vehicle passes through.
    // The WarnerEngine uses startOffsetInMeters to compute distance and determine AHEAD/PASSED.
    override fun getWarnings(
        currentSegment: SegmentData,
        previousSegment: SegmentData?
    ): List<CustomWarning> {
        val result: MutableList<CustomWarning> = ArrayList()
        val spans = currentSegment.spans

        for (span in spans) {
            val specialSituations = span.specialSpeedSituations ?: continue

            for (situation in specialSituations) {
                if (situation.specialSpeedType == SpecialSpeedType.SPEED_BUMPS_PRESENT) {
                    Log.d("SpeedBumpProvider", "Speed bump detected in segment: " + currentSegment.segmentReference)

                    // The position of the speed bump comes directly from SegmentData.
                    val spanStartOffsetMeters = span.startOffsetInMeters

                    val payload = Metadata()
                    payload.setString(PAYLOAD_SEGMENT_REFERENCE, currentSegment.segmentReference.toString())

                    val customWarning = CustomWarning()
                    customWarning.customWarningType = SPEED_BUMP_WARNING_ID
                    // startOffsetInMeters tells the WarnerEngine where in the segment the bump is.
                    customWarning.startOffsetInMeters = spanStartOffsetMeters.toDouble()
                    customWarning.endOffsetInMeters = null
                    customWarning.payload = payload
                    result.add(customWarning)
                }
            }
        }
        return result
    }

    // Helper for decoding payload in warning handler
    fun getSegmentReference(customWarning: CustomWarning): String? {
        return customWarning.payload?.getString(PAYLOAD_SEGMENT_REFERENCE)
    }

    companion object {
        // Custom warning type ID for speed bumps.
        // Avoid using 0: choose a distinct custom warning type id to not conflict with SDK internals.
        const val SPEED_BUMP_WARNING_ID: Int = 1000
        private const val PAYLOAD_SEGMENT_REFERENCE = "segmentReference"
    }
}
