/**
 * Project: bigrun-coach
 * File: PaceCalculatorTool
 * ------------------------------------------------------------------------
 * Description:
 * Spring AI Tool for calculating running pace and split strategy.
 * Used by the AI assistant to answer pace-related questions from runners.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class PaceCalculatorTool {

    @Tool(description = "Calculates running pace in min/km for given distance in km and target finish time in minutes." +
            "Use this when the user asks about pace, speed, or how fast they should run.")
    public String calculatePace(double distanceKm, int targetTimeMinutes) {
        if (distanceKm <= 0 || targetTimeMinutes <= 0) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds = (targetTimeMinutes * 60.0) / distanceKm;
        int minutes = (int) paceSeconds / 60;
        int seconds = (int) paceSeconds % 60;
        double speedKmh = distanceKm / (targetTimeMinutes / 60.0);
        return String.format("Pace: %d:%02d min/km (%.1f km/h) for %.1f km in %d min",
                minutes, seconds, speedKmh, distanceKm, targetTimeMinutes);
    }

    @Tool(description = "Returns split strategy for given distance in km and target finish time in minutes." +
            "Provides recommended splits for each quarter of the race." +
            "Use this when user asks about splits, pacing strategy, or race plan.")
    public String calculateSplitStrategy(double distanceKm, int targetTimeMinutes) {
        if (distanceKm <= 0 || targetTimeMinutes <= 0) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds = (targetTimeMinutes * 60.0) / distanceKm;
        int paceMin = (int) paceSeconds / 60;
        int paceSec = (int) paceSeconds % 60;

        double quarter = distanceKm / 4.0;
        double quarterTimeMin = targetTimeMinutes / 4.0;

        double conservativePaceSeconds = paceSeconds * 1.03;
        double aggressivePaceSeconds = paceSeconds * 0.97;
        int cMin = (int) conservativePaceSeconds / 60;
        int cSec = (int) conservativePaceSeconds % 60;
        int aMin = (int) aggressivePaceSeconds / 60;
        int aSec = (int) aggressivePaceSeconds % 60;

        return String.format(
                "Split strategy for %.1f km in %d min:%n" +
                "Target pace: %d:%02d min/km%n" +
                "Q1 (0-%.1f km): %d:%02d min/km — start conservatively%n" +
                "Q2 (%.1f-%.1f km): %d:%02d min/km — settle into race pace%n" +
                "Q3 (%.1f-%.1f km): %d:%02d min/km — maintain pace%n" +
                "Q4 (%.1f-%.1f km): %d:%02d min/km — push if you have energy left%n" +
                "Halfway target: %.0f min",
                distanceKm, targetTimeMinutes,
                paceMin, paceSec,
                quarter, cMin, cSec,
                quarter, 2 * quarter, paceMin, paceSec,
                2 * quarter, 3 * quarter, paceMin, paceSec,
                3 * quarter, distanceKm, aMin, aSec,
                quarterTimeMin * 2);
    }
}
