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

    private static final double CONSERVATIVE_FACTOR = 1.03;
    private static final double AGGRESSIVE_FACTOR   = 0.97;
    private static final int    SECONDS_PER_MINUTE  = 60;
    private static final int    QUARTERS            = 4;

    private final PaceFormatter formatter;

    public PaceCalculatorTool(PaceFormatter formatter) {
        this.formatter = formatter;
    }

    @Tool(description = "Calculates running pace in min/km for given distance in km and target finish time in minutes. " +
            "Use this when the user asks about pace, speed, or how fast they should run.")
    public String calculatePace(double distanceKm, int targetTimeMinutes) {
        if (distanceKm <= 0 || targetTimeMinutes <= 0) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds = (targetTimeMinutes * (double) SECONDS_PER_MINUTE) / distanceKm;
        double speedKmh = distanceKm / (targetTimeMinutes / 60.0);
        return String.format("Pace: %s min/km (%.1f km/h) for %.1f km in %d min",
                formatter.formatPaceFromSeconds(paceSeconds), speedKmh, distanceKm, targetTimeMinutes);
    }

    @Tool(description = "Calculates finish time for given distance in km and running pace in min/km (as decimal, e.g. 5.5 means 5:30 min/km). " +
            "Use this when the user asks what time they will finish, how long the run will take, or what their finish time will be.")
    public String calculateFinishTime(double distanceKm, double paceMinPerKm) {
        if (distanceKm <= 0 || paceMinPerKm <= 0) {
            return "Invalid input: distance and pace must be positive values.";
        }
        double totalMinutes = distanceKm * paceMinPerKm;
        return String.format("Finish time: %s for %.1f km at %s min/km",
                formatter.formatDuration(totalMinutes), distanceKm, formatter.formatPaceFromDecimal(paceMinPerKm));
    }

    @Tool(description = "Calculates distance covered for given running pace in min/km (as decimal) and duration in minutes. " +
            "Use this when the user asks how far they will run, what distance they will cover, or how many km at a given pace and time.")
    public String calculateDistance(double paceMinPerKm, int durationMinutes) {
        if (paceMinPerKm <= 0 || durationMinutes <= 0) {
            return "Invalid input: pace and duration must be positive values.";
        }
        double distanceKm = durationMinutes / paceMinPerKm;
        return String.format("Distance: %.2f km running %d min at %s min/km",
                distanceKm, durationMinutes, formatter.formatPaceFromDecimal(paceMinPerKm));
    }

    @Tool(description = "Calculates training pace zones based on race pace in min/km (as decimal) and desired intensity percentage (e.g. 75 for easy run, 85 for tempo, 95 for intervals). " +
            "Use this when the user asks about training pace, easy run pace, tempo pace, intervals pace, or training zones.")
    public String calculateTrainingPace(double racePaceMinPerKm, int intensityPercent) {
        if (racePaceMinPerKm <= 0 || intensityPercent <= 0 || intensityPercent > 100) {
            return "Invalid input: pace must be positive and intensity must be between 1 and 100.";
        }
        double racePaceSeconds     = racePaceMinPerKm * SECONDS_PER_MINUTE;
        double trainingPaceSeconds = racePaceSeconds / (intensityPercent / 100.0);
        TrainingZone zone          = TrainingZone.fromIntensity(intensityPercent);
        return String.format("Training pace at %d%% intensity: %s min/km%nZone: %s%nBased on race pace: %s min/km",
                intensityPercent,
                formatter.formatPaceFromSeconds(trainingPaceSeconds),
                zone.getLabel(),
                formatter.formatPaceFromDecimal(racePaceMinPerKm));
    }

    @Tool(description = "Returns split strategy for given distance in km and target finish time in minutes. " +
            "Provides recommended splits for each quarter of the race. " +
            "Use this when user asks about splits, pacing strategy, or race plan.")
    public String calculateSplitStrategy(double distanceKm, int targetTimeMinutes) {
        if (distanceKm <= 0 || targetTimeMinutes <= 0) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds  = (targetTimeMinutes * (double) SECONDS_PER_MINUTE) / distanceKm;
        double quarter      = distanceKm / QUARTERS;
        double halfwayMin   = targetTimeMinutes / 2.0;

        return String.format(
                "Split strategy for %.1f km in %d min:%n" +
                "Target pace: %s min/km%n" +
                "%s%n%s%n%s%n%s%n" +
                "Halfway target: %.0f min",
                distanceKm, targetTimeMinutes,
                formatter.formatPaceFromSeconds(paceSeconds),
                buildSplitLine("Q1", 0,          quarter,     paceSeconds * CONSERVATIVE_FACTOR, "start conservatively"),
                buildSplitLine("Q2", quarter,     2 * quarter, paceSeconds,                       "settle into race pace"),
                buildSplitLine("Q3", 2 * quarter, 3 * quarter, paceSeconds,                       "maintain pace"),
                buildSplitLine("Q4", 3 * quarter, distanceKm,  paceSeconds * AGGRESSIVE_FACTOR,   "push if you have energy left"),
                halfwayMin);
    }

    private String buildSplitLine(String label, double from, double to, double paceSeconds, String note) {
        return String.format("%s (%.1f-%.1f km): %s min/km — %s",
                label, from, to, formatter.formatPaceFromSeconds(paceSeconds), note);
    }
}
