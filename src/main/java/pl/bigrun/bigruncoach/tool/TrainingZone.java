/**
 * Project: bigrun-coach
 * File: TrainingZone
 * ------------------------------------------------------------------------
 * Description:
 * Represents running training intensity zones.
 * Each zone defines a minimum intensity threshold and a descriptive label.
 * New zones can be added without modifying existing resolution logic (OCP).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import java.util.Arrays;

public enum TrainingZone {

    INTERVALS(95, "Intervals / race pace"),
    TEMPO(85,     "Tempo run"),
    AEROBIC(75,   "Aerobic / marathon pace"),
    EASY(0,       "Easy / recovery run");

    private final int    minIntensity;
    private final String label;

    TrainingZone(int minIntensity, String label) {
        this.minIntensity = minIntensity;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TrainingZone fromIntensity(int intensityPercent) {
        return Arrays.stream(values())
                .filter(zone -> intensityPercent >= zone.minIntensity)
                .findFirst()
                .orElse(EASY);
    }
}
