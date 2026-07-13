package com.here.navigationwarners;


import android.util.Log;

import androidx.annotation.NonNull;

import com.here.sdk.core.Metadata;
import com.here.sdk.mapdata.SegmentData;
import com.here.sdk.mapdata.SegmentSpanData;
import com.here.sdk.mapdata.SegmentSpecialSpeedSituation;
import com.here.sdk.mapdata.SpecialSpeedType;
import com.here.sdk.warner.CustomWarning;
import com.here.sdk.warner.CustomWarningProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to add custom warning types such as for speed bumps.
 * The provider scans SegmentData for speed bump positions and reports them via startOffsetInMeters.
 * The WarnerEngine handles distance computation and AHEAD/PASSED delivery based on vehicle position.
 */
public class SpeedBumpWarningProvider implements CustomWarningProvider {
    // Custom warning type ID for speed bumps.
    // Avoid using 0: choose a distinct custom warning type id to not conflict with SDK internals.
    public static final int SPEED_BUMP_WARNING_ID = 1000;
    private static final String PAYLOAD_SEGMENT_REFERENCE = "segmentReference";

    @Override
    public int getCustomWarningType() {
        return SPEED_BUMP_WARNING_ID;
    }

    // Called by the WarnerEngine for EVERY segment the vehicle passes through.
    // The WarnerEngine uses startOffsetInMeters to compute distance and determine AHEAD/PASSED.
    @Override
    public List<CustomWarning> getWarnings(@NonNull SegmentData currentSegment, SegmentData previousSegment) {
        List<CustomWarning> result = new ArrayList<>();
        List<SegmentSpanData> spans = currentSegment.getSpans();

        for (SegmentSpanData span : spans) {
            List<SegmentSpecialSpeedSituation> specialSituations = span.getSpecialSpeedSituations();
            if (specialSituations == null) continue;

            for (SegmentSpecialSpeedSituation situation : specialSituations) {
                if (situation.specialSpeedType == SpecialSpeedType.SPEED_BUMPS_PRESENT) {
                    Log.d("SpeedBumpProvider", "Speed bump detected in segment: " + currentSegment.getSegmentReference());

                    // The position of the speed bump comes directly from SegmentData.
                    int spanStartOffsetMeters = span.getStartOffsetInMeters();

                    Metadata payload = new Metadata();
                    payload.setString(PAYLOAD_SEGMENT_REFERENCE, currentSegment.getSegmentReference().toString());

                    CustomWarning customWarning = new CustomWarning();
                    customWarning.customWarningType = SPEED_BUMP_WARNING_ID;
                    // startOffsetInMeters tells the WarnerEngine where in the segment the bump is.
                    customWarning.startOffsetInMeters = spanStartOffsetMeters;
                    customWarning.endOffsetInMeters = null;
                    customWarning.payload = payload;
                    result.add(customWarning);
                }
            }
        }
        return result;
    }

    // Helper for decoding payload in warning handler
    public String getSegmentReference(CustomWarning customWarning) {
        if (customWarning.payload != null) {
            return customWarning.payload.getString(PAYLOAD_SEGMENT_REFERENCE);
        }
        return null;
    }
}
