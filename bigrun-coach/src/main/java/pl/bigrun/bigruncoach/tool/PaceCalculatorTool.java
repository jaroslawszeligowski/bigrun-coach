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

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
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
            "Parameters: distanceKm (e.g. 42.195 for marathon, 21.0975 for half marathon, 10.0 for 10km), " +
            "targetTimeMinutes (e.g. 240 for 4 hours, 90 for 1h30m). " +
            "Use this when the user knows the distance and desired finish time and wants to know the required pace.")
    public String calculatePace(Double distanceKm, Double targetTimeMinutes) {
        log.info(">>> TOOL CALLED: calculatePace({}, {})", distanceKm, targetTimeMinutes);
        if (distanceKm == null || targetTimeMinutes == null || isNotPositive(distanceKm) || isNotPositive(targetTimeMinutes)) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds = toPaceSeconds(distanceKm, targetTimeMinutes);
        double speedKmh    = distanceKm / (targetTimeMinutes / SECONDS_PER_MINUTE);
        return String.format("Pace: %s min/km (%.1f km/h) for %.1f km in %.0f min",
                formatter.formatPaceFromSeconds(paceSeconds), speedKmh, distanceKm, targetTimeMinutes);
    }

    @Tool(description = "Calculates finish time for given distance in km and running pace in min/km. " +
            "Parameters: distanceKm (e.g. 42.195 for marathon, 10.0 for 10km), " +
            "paceMinPerKm as decimal where minutes and fraction represent seconds (e.g. 5.5 = 5:30, 4.75 = 4:45, 6.0 = 6:00). " +
            "Use this when the user knows the distance and their pace and wants to know the estimated finish time.")
    public String calculateFinishTime(Double distanceKm, Double paceMinPerKm) {
        log.info(">>> TOOL CALLED: calculateFinishTime({}, {})", distanceKm, paceMinPerKm);
        if (distanceKm == null || paceMinPerKm == null || isNotPositive(distanceKm) || isNotPositive(paceMinPerKm)) {
            return "Invalid input: distance and pace must be positive values.";
        }
        double totalMinutes = distanceKm * paceMinPerKm;
        return String.format("Finish time: %s for %.1f km at %s min/km",
                formatter.formatDuration(totalMinutes), distanceKm, formatter.formatPaceFromDecimal(paceMinPerKm));
    }

    @Tool(description = "Calculates distance covered in km for given running pace in min/km and duration in minutes. " +
            "Parameters: paceMinPerKm as decimal (e.g. 5.5 = 5:30, 6.0 = 6:00 min/km), " +
            "durationMinutes (e.g. 60 for 1 hour, 90 for 1h30m, 30 for 30 minutes). " +
            "Use this when the user knows their pace and how long they will run and wants to know the distance covered.")
    public String calculateDistance(Double paceMinPerKm, Double durationMinutes) {
        log.info(">>> TOOL CALLED: calculateDistance({}, {})", paceMinPerKm, durationMinutes);
        if (paceMinPerKm == null || durationMinutes == null || isNotPositive(paceMinPerKm) || isNotPositive(durationMinutes)) {
            return "Invalid input: pace and duration must be positive values.";
        }
        double distanceKm = durationMinutes / paceMinPerKm;
        return String.format("Distance: %.2f km running %.0f min at %s min/km",
                distanceKm, durationMinutes, formatter.formatPaceFromDecimal(paceMinPerKm));
    }

    @Tool(description = "Calculates training pace in min/km for a given race pace and desired intensity percentage. " +
            "Parameters: racePaceMinPerKm as decimal (e.g. 5.0 = 5:00, 4.5 = 4:30 min/km — the pace achieved in a recent race), " +
            "intensityPercent as integer 1-100 (e.g. 65-75 for easy/recovery run, 80-85 for tempo, 90-95 for intervals, 100 for race pace). " +
            "Use this when the user asks about training zones, how fast to run easy runs, tempo runs, or interval training.")
    public String calculateTrainingPace(Double racePaceMinPerKm, Double intensityPercent) {
        log.info(">>> TOOL CALLED: calculateTrainingPace({}, {})", racePaceMinPerKm, intensityPercent);
        if (racePaceMinPerKm == null || intensityPercent == null) {
            return "Invalid input: pace and intensity are required.";
        }
        int intensity = (int) intensityPercent.doubleValue();
        if (isNotPositive(racePaceMinPerKm) || intensity <= 0 || intensity > 100) {
            return "Invalid input: pace must be positive and intensity must be between 1 and 100.";
        }
        double racePaceSeconds     = racePaceMinPerKm * SECONDS_PER_MINUTE;
        double trainingPaceSeconds = racePaceSeconds / (intensity / 100.0);
        TrainingZone zone          = TrainingZone.fromIntensity(intensity);
        return String.format("Training pace at %d%% intensity: %s min/km%nZone: %s%nBased on race pace: %s min/km",
                intensity,
                formatter.formatPaceFromSeconds(trainingPaceSeconds),
                zone.getLabel(),
                formatter.formatPaceFromDecimal(racePaceMinPerKm));
    }

    @Tool(description = "Returns a pacing strategy divided into four equal quarters for given distance and target finish time. " +
            "Parameters: distanceKm (e.g. 42.195 for marathon, 21.0975 for half marathon, 10.0 for 10km), " +
            "targetTimeMinutes (e.g. 210 for 3h30m, 240 for 4h, 50 for 50 minutes). " +
            "Use this when the user asks about race strategy, how to pace a race, negative splits, or race plan.")
    public String calculateSplitStrategy(Double distanceKm, Double targetTimeMinutes) {
        log.info(">>> TOOL CALLED: calculateSplitStrategy({}, {})", distanceKm, targetTimeMinutes);
        if (distanceKm == null || targetTimeMinutes == null || isNotPositive(distanceKm) || isNotPositive(targetTimeMinutes)) {
            return "Invalid input: distance and time must be positive values.";
        }
        double paceSeconds = toPaceSeconds(distanceKm, targetTimeMinutes);
        double quarter     = distanceKm / QUARTERS;
        double halfwayMin  = targetTimeMinutes / 2.0;

        return String.format(
                "Split strategy for %.1f km in %.0f min:%n" +
                "Target pace: %s min/km%n" +
                "%s%n%s%n%s%n%s%n" +
                "Halfway target: %.0f min",
                distanceKm, targetTimeMinutes,
                formatter.formatPaceFromSeconds(paceSeconds),
                formatter.formatSplitLine("Q1", 0,          quarter,     paceSeconds * CONSERVATIVE_FACTOR, "start conservatively"),
                formatter.formatSplitLine("Q2", quarter,     2 * quarter, paceSeconds,                       "settle into race pace"),
                formatter.formatSplitLine("Q3", 2 * quarter, 3 * quarter, paceSeconds,                       "maintain pace"),
                formatter.formatSplitLine("Q4", 3 * quarter, distanceKm,  paceSeconds * AGGRESSIVE_FACTOR,   "push if you have energy left"),
                halfwayMin);
    }

    private double toPaceSeconds(double distanceKm, double timeMinutes) {
        return (timeMinutes * SECONDS_PER_MINUTE) / distanceKm;
    }

    private boolean isNotPositive(Double value) {
        return Double.isNaN(value) || Double.isInfinite(value) || value <= 0;
    }
}
