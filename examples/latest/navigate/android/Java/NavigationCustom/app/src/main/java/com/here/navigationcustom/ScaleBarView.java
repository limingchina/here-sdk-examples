/*
 * Copyright (C) 2019-2025 HERE Europe B.V.
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

package com.here.navigationcustom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class ScaleBarView extends View {

    private static final int MAP_ZOOM_LEVEL_MIN = 0;
    private static final int MAP_ZOOM_LEVEL_MAX = 23;

    // Zoom level to distance and scale table
    public static final int[][] LEVEL_TO_DISTANCE_AND_SCALE = new int[24][2];

    static {
        LEVEL_TO_DISTANCE_AND_SCALE[23] = new int[]{95, 5};
        LEVEL_TO_DISTANCE_AND_SCALE[22] = new int[]{100, 5};
        LEVEL_TO_DISTANCE_AND_SCALE[21] = new int[]{300, 5};
        LEVEL_TO_DISTANCE_AND_SCALE[20] = new int[]{700, 10};
        LEVEL_TO_DISTANCE_AND_SCALE[19] = new int[]{1000, 25};
        LEVEL_TO_DISTANCE_AND_SCALE[18] = new int[]{3000, 50};
        LEVEL_TO_DISTANCE_AND_SCALE[17] = new int[]{6000, 100};
        LEVEL_TO_DISTANCE_AND_SCALE[16] = new int[]{12000, 200};
        LEVEL_TO_DISTANCE_AND_SCALE[15] = new int[]{25000, 500};
        LEVEL_TO_DISTANCE_AND_SCALE[14] = new int[]{50000, 1000};
        LEVEL_TO_DISTANCE_AND_SCALE[13] = new int[]{100000, 1000};
        LEVEL_TO_DISTANCE_AND_SCALE[12] = new int[]{195000, 3000};
        LEVEL_TO_DISTANCE_AND_SCALE[11] = new int[]{390000, 5000};
        LEVEL_TO_DISTANCE_AND_SCALE[10] = new int[]{780000, 10000};
        LEVEL_TO_DISTANCE_AND_SCALE[9] = new int[]{1000000, 30000};
        LEVEL_TO_DISTANCE_AND_SCALE[8] = new int[]{3000000, 50000};
        LEVEL_TO_DISTANCE_AND_SCALE[7] = new int[]{6000000, 100000};
        LEVEL_TO_DISTANCE_AND_SCALE[6] = new int[]{12000000, 300000};
        LEVEL_TO_DISTANCE_AND_SCALE[5] = new int[]{25000000, 300000};
        LEVEL_TO_DISTANCE_AND_SCALE[4] = new int[]{50000000, 500000};
        LEVEL_TO_DISTANCE_AND_SCALE[3] = new int[]{100000000, 5000000};
        LEVEL_TO_DISTANCE_AND_SCALE[2] = new int[]{200000000, 10000000};
        LEVEL_TO_DISTANCE_AND_SCALE[1] = new int[]{400000000, 10000000};
        LEVEL_TO_DISTANCE_AND_SCALE[0] = new int[]{800000000, 10000000};
    }

    private Paint linePaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private int scaleValue = 0;
    private String scaleText = "";
    private float barWidth = 100; // Default width in pixels

    private static final int PADDING = 8;
    private static final int BAR_HEIGHT = 4;
    private static final int TICK_HEIGHT = 12;
    private static final int TEXT_SIZE = 28;

    public ScaleBarView(Context context) {
        super(context);
        init();
    }

    public ScaleBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(0xFF000000); // Black
        linePaint.setStrokeWidth(3);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(0xFF000000); // Black
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xCCFFFFFF); // Semi-transparent white
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
    }

    /**
     * Update the scale bar based on the current zoom level
     *
     * @param zoomLevel The current map zoom level
     */
    public void updateScale(double zoomLevel) {
        int index = (int) Math.round(zoomLevel);
        if (index >= MAP_ZOOM_LEVEL_MIN && index <= MAP_ZOOM_LEVEL_MAX) {
            int[] distanceAndScale = LEVEL_TO_DISTANCE_AND_SCALE[index];
            scaleValue = distanceAndScale[1];
            scaleText = formatScaleText(scaleValue);

            // Calculate bar width based on screen density and zoom level
            barWidth = calculateBarWidth(distanceAndScale[0], scaleValue);
            Log.d(TAG, "updateScale: ");
            invalidate(); // Redraw the view
        }
    }

    private String formatScaleText(int meters) {
        if (meters >= 1000) {
            int km = meters / 1000;
            return km + " km";
        } else {
            return meters + " m";
        }
    }

    private float calculateBarWidth(int maxDistance, int scaleValue) {
        // Calculate proportional width
        // Base width is 100dp, scaled by density
        float density = getResources().getDisplayMetrics().density;
        float baseWidth = 100 * density;

        // Adjust width proportionally to the scale value
        float ratio = (float) scaleValue / (float) maxDistance;
        return Math.max(50 * density, baseWidth * ratio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaleValue == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        // Calculate bounds
        Rect textBounds = new Rect();
        textPaint.getTextBounds(scaleText, 0, scaleText.length(), textBounds);

        float totalWidth = barWidth + PADDING * 2;
        float totalHeight = TICK_HEIGHT + textBounds.height() + PADDING * 2;

        // Draw background
        float bgLeft = width - totalWidth - PADDING;
        float bgTop = height - totalHeight - PADDING;
        float bgRight = width - PADDING;
        float bgBottom = height - PADDING;
        canvas.drawRect(bgLeft, bgTop, bgRight, bgBottom, backgroundPaint);

        // Draw scale bar
        float barLeft = width - barWidth - PADDING * 2;
        float barRight = width - PADDING * 2;
        float barBottom = height - PADDING * 2;
        float barTop = barBottom - BAR_HEIGHT;

        // Draw horizontal bar
        canvas.drawLine(barLeft, barTop + BAR_HEIGHT / 2, barRight, barTop + BAR_HEIGHT / 2, linePaint);

        // Draw left tick
        canvas.drawLine(barLeft, barTop - TICK_HEIGHT / 2, barLeft, barTop + TICK_HEIGHT / 2 + BAR_HEIGHT / 2, linePaint);

        // Draw right tick
        canvas.drawLine(barRight, barTop - TICK_HEIGHT / 2, barRight, barTop + TICK_HEIGHT / 2 + BAR_HEIGHT / 2, linePaint);

        // Draw text
        float textX = (barLeft + barRight) / 2;
        float textY = barTop - TICK_HEIGHT / 2 - PADDING;
        canvas.drawText(scaleText, textX, textY, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set minimum size
        int minWidth = (int) (150 * getResources().getDisplayMetrics().density);
        int minHeight = (int) (60 * getResources().getDisplayMetrics().density);

        setMeasuredDimension(minWidth, minHeight);
    }
}

