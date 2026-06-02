/**
 * Project: bigrun-coach
 * File: PaceFormatter
 * ------------------------------------------------------------------------
 * Description:
 * Responsible for formatting pace, speed, and duration values into
 * human-readable strings. Extracted from PaceCalculatorTool to satisfy SRP.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import org.springframework.stereotype.Component;

@Component
public class PaceFormatter {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR   = 60;

    public String formatPaceFromSeconds(double paceSeconds) {
        int minutes = (int) paceSeconds / SECONDS_PER_MINUTE;
        int seconds = (int) paceSeconds % SECONDS_PER_MINUTE;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String formatPaceFromDecimal(double paceMinPerKm) {
        int minutes = (int) paceMinPerKm;
        int seconds = (int) Math.round((paceMinPerKm - minutes) * SECONDS_PER_MINUTE);
        return String.format("%d:%02d", minutes, seconds);
    }

    public String formatSplitLine(String label, double from, double to, double paceSeconds, String note) {
        return String.format("%s (%.1f-%.1f km): %s min/km — %s",
                label, from, to, formatPaceFromSeconds(paceSeconds), note);
    }

    public String formatDuration(double totalMinutes) {
        int hours   = (int) totalMinutes / MINUTES_PER_HOUR;
        int minutes = (int) totalMinutes % MINUTES_PER_HOUR;
        int seconds = (int) ((totalMinutes - (int) totalMinutes) * SECONDS_PER_MINUTE);
        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, seconds)
                : String.format("%d:%02d min", minutes, seconds);
    }
}
