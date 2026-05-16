/**
 * Project: bigrun-coach
 * File: RunDurationCategory
 * ------------------------------------------------------------------------
 * Description:
 * Categorizes run duration into distinct groups used for nutrition planning.
 * Adding a new category does not require modifying existing calculation logic (OCP).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import java.util.Arrays;

public enum RunDurationCategory {

    SHORT(0,  30),
    MEDIUM(31, 59),
    LONG(60,  89),
    ULTRA(90, Integer.MAX_VALUE);

    private final int minMinutes;
    private final int maxMinutes;

    RunDurationCategory(int minMinutes, int maxMinutes) {
        this.minMinutes = minMinutes;
        this.maxMinutes = maxMinutes;
    }

    public static RunDurationCategory fromDuration(int durationMinutes) {
        return Arrays.stream(values())
                .filter(c -> durationMinutes >= c.minMinutes && durationMinutes <= c.maxMinutes)
                .findFirst()
                .orElse(ULTRA);
    }

    public boolean isAtLeast(RunDurationCategory other) {
        return this.minMinutes >= other.minMinutes;
    }
}
